package com.yamada.five.handler;

import com.yamada.five.exception.FiveException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class FiveExceptionHandler {

    @ExceptionHandler(FiveException.class)
    public ModelAndView error(FiveException e) {
        Map<String, Object> map = new HashMap<>();
        map.put("msg", e.getMessage());
        map.put("url", e.getUrl());
        return new ModelAndView("common/error", map);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView error(Exception e) {
        if (e instanceof FiveException) {
            return error((FiveException)e);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "发生错误！");
        map.put("url", "/index");
        return new ModelAndView("common/error", map);
    }
}
