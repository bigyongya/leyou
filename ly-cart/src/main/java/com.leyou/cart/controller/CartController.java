package com.leyou.cart.controller;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<Cart>> findCart(@RequestParam("id")Long id){
        List<Cart> carts = this.cartService.findCart(id);
        if (carts != null && carts.size()!=0) {
            return ResponseEntity.ok(carts);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
    @PutMapping
    public ResponseEntity<Void> updateCart(@RequestBody Cart cart){
        this.cartService.updateCart(cart);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{userId}/{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("userId")Long userId,@PathVariable("skuId")Long skuId){
        this.cartService.deleteCart(userId,skuId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
