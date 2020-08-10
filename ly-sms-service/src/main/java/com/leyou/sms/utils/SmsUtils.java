package com.leyou.sms.utils;

import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

import com.leyou.sms.config.SmsProperties;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsUtils {

    @Autowired
    private SmsProperties prop;



    public Boolean sendSms(String phone,String code, String signName, String template) {

        DefaultProfile profile=DefaultProfile.getProfile("cn-hangzhou",prop.getAccessKeyId(),prop.getAccessKeySecret());
        IAcsClient client=new DefaultAcsClient(profile);



        CommonRequest request=new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId","cn-hangzhou");
        request.putQueryParameter("PhoneNumbers",phone);
        request.putQueryParameter("SignName",signName);
        request.putQueryParameter("TemplateCode",template);
        request.putQueryParameter("TemplateParam","{\"code\":"+code+"}");
        try{
            CommonResponse response= client.getCommonResponse(request);
            System.out.println(response.getData());
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}