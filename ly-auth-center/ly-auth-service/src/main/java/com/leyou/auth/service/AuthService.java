package com.leyou.auth.service;

import com.leyou.auth.clients.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entiy.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.user.pojo.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    public String authentication(String username, String password, JwtProperties prop) {

        User user = this.userClient.query(username, password);

        if (user != null) {

            UserInfo userInfo = new UserInfo();
            BeanUtils.copyProperties(user,userInfo);

            String token = null;
            try {
                token = JwtUtils.generateToken(userInfo, prop.getPrivateKey(), prop.getExpire());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return token;
        }

        return null;
    }
}
