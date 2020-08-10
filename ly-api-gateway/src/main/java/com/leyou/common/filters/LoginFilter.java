package com.leyou.common.filters;

import com.leyou.auth.entiy.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.config.FilterProperties;
import com.leyou.common.config.JwtProperties;
import com.leyou.common.utils.CookieUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.http.protocol.RequestContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CookieValue;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties prop;

    @Autowired
    private FilterProperties filterProperties;

    //前置拦截器
    @Override
    public String filterType() {
        return "pre";
    }

    //拦截器的优先级
    @Override
    public int filterOrder() {
        return 0;
    }

    //返回ture 拦截生效  返回false 不生效
    @Override
    public boolean shouldFilter() {


        List<String> allowPaths = filterProperties.getAllowPaths();

        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String requestURI = request.getRequestURI();

        for (String allowPath : allowPaths) {
            if(requestURI.startsWith(allowPath)){
                return false;
            }
        }

        return true;
    }

    @Override
    public Object run() throws ZuulException {


        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        try {
           /* Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if(prop.getCookieName().equals(cookie.getName())){
                    JwtUtils.getInfoFromToken(cookie.getValue(), prop.getPublicKey());
                }
            }*/
            String token = CookieUtils.getCookieValue(request, prop.getCookieName());

            JwtUtils.getInfoFromToken(token, prop.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
        }

        return null;
    }
}
