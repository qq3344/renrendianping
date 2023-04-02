package com.sky.system.handler;

import com.sky.common.result.Result;
import com.sky.common.result.ResultCodeEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public Result error(ArithmeticException e){
        e.printStackTrace();
        return Result.fail().message("数学错误");
    }

    @ExceptionHandler(MyException.class)
    @ResponseBody
    public Result error(MyException e){
        e.printStackTrace();
        return Result.fail().message(e.getMessage()).code(e.getCode());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    public Result error(BadCredentialsException e){
        return Result.fail().message("用户名或密码错误！").code(ResultCodeEnum.FAIL.getCode());
    }


    @ResponseBody
    @ExceptionHandler(value = AccessDeniedException.class)
    public Result handleAccessDenied(AccessDeniedException e) {
        return Result.fail().message("没有权限，请联系管理员").code(ResultCodeEnum.PERMISSION.getCode());
    }

}
