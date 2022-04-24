package com.example.springbootwebsocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller("web_Scoket_system")
@RequestMapping("/api/socket")
public class TestContorller {

    //页面请求
    @GetMapping("/index/{user}")
    public String socket(@PathVariable String user, Model model) {
        model.addAttribute("user",user);
        return "index";
    }


}
