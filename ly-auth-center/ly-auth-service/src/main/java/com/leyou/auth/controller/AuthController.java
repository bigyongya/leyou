package com.leyou.auth.controller;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entiy.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties prop;

    /**
     * 登录授权
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("accredit")
    public ResponseEntity<Void> authentication(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response) {
        // 登录校验
        String token = this.authService.authentication(username, password,prop);
        if (StringUtils.isBlank(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // 将token写入cookie,并指定httpOnly为true，防止通过JS获取和修改
        Cookie cookie = new Cookie(prop.getCookieName(),token);
        cookie.setMaxAge(prop.getCookieMaxAge());
        response.addCookie(cookie);

       /* CookieUtils.setCookie(request, response, prop.getCookieName(),
                token, prop.getCookieMaxAge(), null, true);*/
        return ResponseEntity.ok().build();
    }

    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN")String token, HttpServletResponse response){

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            
            //刷新token
            String newToken = JwtUtils.generateToken(userInfo, prop.getPrivateKey(), prop.getExpire());

            Cookie cookie = new Cookie("LY_TOKEN",newToken);
            cookie.setMaxAge(prop.getCookieMaxAge());
            response.addCookie(cookie);


            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}
