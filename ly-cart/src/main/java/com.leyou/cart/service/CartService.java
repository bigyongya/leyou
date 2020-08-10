package com.leyou.cart.service;

import com.leyou.cart.pojo.Cart;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {


    @Autowired
    private StringRedisTemplate redisTemplate;

    public void addCart(Cart cart) {
        // 获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(cart.getUserId().toString());
        // 查询是否存在
        Long skuId = cart.getSkuId();
        Integer num = cart.getNum();
        Boolean boo = hashOps.hasKey(skuId.toString());
        if (boo) {
            // 存在，获取购物车数据
            String json = hashOps.get(skuId.toString()).toString();
            cart = JsonUtils.parse(json, Cart.class);
            // 修改购物车数量
            cart.setNum(cart.getNum() + num);
        }
        // 将购物车数据写入redis
        hashOps.put(cart.getSkuId().toString(), JsonUtils.serialize(cart));

    }

    public List<Cart> findCart(Long id) {

        // 判断是否存在购物车
        if(!this.redisTemplate.hasKey(id.toString())){
            // 不存在，直接返回
            return null;
        }
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(id.toString());
        List<Object> carts = hashOps.values();
        // 判断是否有数据
        if(CollectionUtils.isEmpty(carts)){
            return null;
        }
        // 查询购物车数据
        return carts.stream().map(o -> JsonUtils.parse(o.toString(), Cart.class)).collect(Collectors.toList());
    }

    public void updateCart(Cart cart) {


        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(cart.getUserId().toString());
        // 获取购物车
        String json = hashOps.get(cart.getSkuId().toString()).toString();
        Cart car = JsonUtils.parse(json, Cart.class);
        car.setNum(cart.getNum());
        // 写入购物车
        hashOps.put(cart.getSkuId().toString(), JsonUtils.serialize(car));
    }

    public void deleteCart(Long userId,Long skuId) {
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(userId.toString());
        hashOps.delete(skuId.toString());
    }
}
