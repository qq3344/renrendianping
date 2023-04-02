package com.sky.system.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.common.result.ResultCodeEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        int code = ResultCodeEnum.PERMISSION.getCode();
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        String msg = "权限不足，无法访问系统资源";
        Map<String, Object> result = new HashMap<>();
        result.put("message", msg);
        result.put("code", code);
        String s = new ObjectMapper().writeValueAsString(result);
        response.getWriter().println(s);
    }
}
