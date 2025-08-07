package com.intelijake.mall.admin.inteceptor;

import com.intelijake.mall.common.util.JwtUtil;
import com.intelijake.mall.common.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //得到请求头里面的token
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }

        try {
            Map<String, Object> map = JwtUtil.parseToken(token);

            ThreadLocalUtil.set(map);

            return true;
        } catch (Exception e) {
            //http响应状态码401
            //response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setStatus(401);
            return false;
        }

    }
}
