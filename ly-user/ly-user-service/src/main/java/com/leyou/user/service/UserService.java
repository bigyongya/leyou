package com.leyou.user.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    public Boolean check(String data, Integer type) {
        User user = new User();
        if(type==1){
            user.setUsername(data);
        }else if(type==2){
            user.setPhone(data);
        }

        return this.userMapper.selectCount(user)!=1;
    }

    public Boolean sendSms(String phone) {

        try {
            HashMap<String, String> msg = new HashMap<>();

            String code = NumberUtils.generateCode(6);

            msg.put("phone",phone);
            msg.put("code",code);
            this.amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",msg);

            //将验证码存入redis保存中五分钟
            this.redisTemplate.opsForValue().set(phone,code,300, TimeUnit.SECONDS);

        } catch (AmqpException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean register(User user, String code) {

        if(StringUtils.isNotBlank(code)){

            String redisCode = (String) redisTemplate.opsForValue().get(user.getPhone());

            if(code.equals(redisCode)){

                user.setCreated(new Date());

                String salt = CodecUtils.generateSalt();

                user.setSalt(salt);

                user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));

                this.userMapper.insertSelective(user);

                return true;
            }

        }

        return null;
    }

    public User query(String username, String password) {
        if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)){

            User record = new User();
            record.setUsername(username);

            User user = this.userMapper.selectOne(record);

            if(user!=null){
                String dataPass = CodecUtils.md5Hex(password, user.getSalt());
                if(dataPass.equals(user.getPassword())){
                    return user;
                }
            }

        }

        return null;
    }
}
