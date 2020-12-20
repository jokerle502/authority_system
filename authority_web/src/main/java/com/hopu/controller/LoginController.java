package com.hopu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hopu.domain.Menu;
import com.hopu.domain.User;
import com.hopu.service.IMenuService;
import com.hopu.utils.IconFontUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
@RequestMapping("")
public class LoginController {

    @Autowired
    private IMenuService menuService;

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

            //左侧菜单
            QueryWrapper<Menu> menuQueryWrapper = new QueryWrapper<>();
            List<Menu> leftMenu = menuService.list(menuQueryWrapper.eq("pid", "0"));
            findChildren(leftMenu);
            List<String> iconFont = IconFontUtils.getIconFont();

            request.setAttribute("iconFont",iconFont);

            session.setAttribute("leftMenu",leftMenu);
            session.setAttribute("user",user1);
            return "admin/index";
        } catch (Exception e) {
            String msg = "账户"+ token.getPrincipal() + "的用户名或密码错误！";
            request.setAttribute("msg", msg);
            return "forward:/login.jsp";
        }
    }

    @PostMapping("/user/refresh")
    @ResponseBody
    public String refresh(HttpServletRequest request,HttpSession session){
        //左侧菜单
        QueryWrapper<Menu> menuQueryWrapper = new QueryWrapper<>();
        List<Menu> leftMenu = menuService.list(menuQueryWrapper.eq("pid", "0"));
        findChildren(leftMenu);
        List<String> iconFont = IconFontUtils.getIconFont();

        request.setAttribute("iconFont",iconFont);

        session.setAttribute("leftMenu",leftMenu);
        return "admin/index";
    }


    //私有方法，查询父级菜单下的子菜单
    private void findChildren(List<Menu> leftMenu){
        for (Menu menu : leftMenu) {
            List<Menu> list = menuService.list(new QueryWrapper<Menu>(new Menu()).eq("pid", menu.getId()));
            if (list!=null) {
                menu.setNodes(list);
            }
        }
    }

}