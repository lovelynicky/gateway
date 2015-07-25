package com.spring.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/test")
public class TestController {


    @RequestMapping(value = "/velocity-test", method = RequestMethod.GET)
    public ModelAndView printWelcome(@RequestParam("message") String message) {
        Map map = new HashMap();
        map.put("hello", message);
        return new ModelAndView("hello", map);
    }
}