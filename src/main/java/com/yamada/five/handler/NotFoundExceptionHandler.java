package com.yamada.five.handler;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class NotFoundExceptionHandler implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(value = "/error")
    public ModelAndView error() {
        Map<String, String> map = new HashMap<>();
        map.put("msg", "你访问的资源不存在！");
        map.put("url", "/index");
        return new ModelAndView("common/error", map);
    }
}
