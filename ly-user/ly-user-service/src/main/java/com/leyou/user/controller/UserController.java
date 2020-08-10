package com.leyou.user.controller;


import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> check(
            @PathVariable("data")String data,
            @PathVariable("type")Integer type){
        Boolean boo = this.userService.check(data,type);
        if (boo != null) {
            return ResponseEntity.ok(boo);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/code")
    public ResponseEntity<Void> code(@RequestParam("phone")String phone){
        Boolean boo = this.userService.sendSms(phone);
        if (boo != null && boo) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("register")
    public ResponseEntity<Void> register(User user,@RequestParam("code")String code){
        Boolean boo = this.userService.register(user,code);
        if (boo == null || !boo) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("query")
    public ResponseEntity<User> query(
            @RequestParam("username")String username,
            @RequestParam("password")String password){
        User user = this.userService.query(username,password);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
