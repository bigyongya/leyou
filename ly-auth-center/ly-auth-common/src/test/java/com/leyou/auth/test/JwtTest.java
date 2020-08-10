package com.leyou.auth.test;

import com.leyou.auth.entiy.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "D:/heima/rsa/rsa.pub";

    private static final String priKeyPath = "D:/heima/rsa/rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    //@Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MzIsInVzZXJuYW1lIjoiYmlneW9uZyIsImV4cCI6MTU4ODkyMzQyNH0.a09-8HhQtQgrll6H4wxmoq2iGU7_LIp5qL3PXZtoc8u2OoOEMAUCPsOKp-9LheTEEsuRSupR88TY_A6xg2fJlNu6hTqwqgD6ofZusYwVgpo9_vR66JbZ2SqIlnsD7oGtmckJ8of6ydx2y0Lw6lPN6DxRsucjeTK50znmIAbK-PE";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
