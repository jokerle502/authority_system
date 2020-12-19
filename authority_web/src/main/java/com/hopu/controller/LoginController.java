package com.hopu.controller;

import com.hopu.domain.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("")
public class LoginController {
    // 用户登录
    @PostMapping("/user/login")
    public String login(User user, HttpServletRequest request, HttpServletResponse response){
        // 登录用户校验
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUserName(), user.getPassword());
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            // 登录成功后，把用户放在session与对象中
            HttpSession session = WebUtils.toHttp(request).getSession();
            User user1 = (User) subject.getPrincipal();
            session.setAttribute("user",user1);
            return "admin/index";
//            return "redirect:/admin/index";
        } catch (Exception e) {
            String msg = "账户["+ token.getPrincipal() + "]的用户名或密码错误！";
            request.setAttribute("msg", msg);
            return "forward:/login.jsp";
        }
    }

//    @GetMapping("")
//    public String toLogin(){
//        return "admin/index";
//    }

}