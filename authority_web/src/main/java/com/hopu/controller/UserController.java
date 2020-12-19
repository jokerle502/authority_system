package com.hopu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hopu.domain.Role;
import com.hopu.domain.User;
import com.hopu.result.ResponseEntity;
import com.hopu.result.ShiroUtils;
import com.hopu.service.IUserService;
import com.hopu.utils.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hopu.result.ResponseEntity.success;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    // 跳转到用户角色分配页面
    @RequestMapping("/toUserSetRole")
    public String toSetRolePage(@RequestParam(value = "id") String userId, HttpServletRequest request){
        request.setAttribute("userId",userId);

        return "admin/user/user_setRole";
    }

    // 分配并保存角色
    @RequestMapping("/setRole")
    @ResponseBody
    public ResponseEntity setRole(String userId,@RequestBody ArrayList<Role> roles){
        userService.setRole(userId,roles);
        return success();
    }


    /**
     * 向用户分页界面跳转
     * @return
     */
    @RequiresPermissions("user:list")
    @RequestMapping("/toList")
    public String toList(){
        return "admin/user/user_list";
    }

    /**
     * 异步用户分页展示
     * @param pageNum   当前页
     * @param pageSize  每页显示条数(因为在前台固定了limit，因此defaultValue失效了)
     * @param user      查询参数对象user
     * @return
     */
    @GetMapping("/list")
    @ResponseBody
    public IPage<User> list(@RequestParam(value = "page",defaultValue = "1") Integer pageNum,
                            @RequestParam(value = "limit",defaultValue = "5") Integer pageSize,
                            User user){
        //设置分页条件
        Page<User> page = new Page<>(pageNum, pageSize);
        //QueryWrapper封装查询条件
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>(new User());
        if (user != null){
            if (!StringUtils.isEmpty(user.getUserName())){
                userQueryWrapper.like("user_name",user.getUserName());
            }
            if (!StringUtils.isEmpty(user.getTel())){
                userQueryWrapper.like("tel",user.getTel());
            }
            if (!StringUtils.isEmpty(user.getEmail())){
                userQueryWrapper.like("email",user.getEmail());
            }
        }
        //分页条件查询时，带上分页数据以及查询条件对象
        IPage<User> userList = userService.page(page,userQueryWrapper);
        return userList;
    }

    @RequiresPermissions("user:add")
    @RequestMapping("/toUserAdd")
    public String toUserAdd(){
        return "admin/user/user_add";
    }

    /**
     * 异步添加用户
     * @param user  添加参数对象user
     * @return
     */
    @RequestMapping("/userAdd")
    @ResponseBody
    public ResponseEntity userAdd(User user){
        //封装查询条件
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>(new User());
        //用户名重名校验
        userQueryWrapper.eq("user_name",user.getUserName());
        User one = userService.getOne(userQueryWrapper);
        if (one != null){
            return ResponseEntity.error("用户名已存在，请重新输入");
        }

        //在这里添加用户ID用的是工具类UUIDUtils，随机获取ID来注册
        //加盐操作在后面实施
        user.setId(UUIDUtils.getID());
        user.setSalt(UUIDUtils.getID());
        ShiroUtils.encPass(user);
        user.setCreateTime(new Date());
        if (user.getStatus()==null){
            user.setStatus("off");
        }
        userService.save(user);
        return ResponseEntity.success("添加用户成功！");
    }

    @RequestMapping("/toUserUpdate")
    public String toUserUpdate(String id, Model model){
        User user = userService.getById(id);
        model.addAttribute("user",user);
        return "admin/user/user_update";
    }

    @RequestMapping("/userUpdate")
    @ResponseBody
    public ResponseEntity userUpdate(User user){
        ShiroUtils.encPass(user);
        user.setUpdateTime(new Date());

        if (user.getStatus() == null){
            user.setStatus("off");
        }
        userService.updateById(user);

        return ResponseEntity.success();
    }

    @RequestMapping("/userDelete")
    @ResponseBody
    //@responseBody注解的作用：
    // 是将controller的方法返回的对象通过适当的转换器转换为指定的格式之后，写入到response对象的body区，通常用来返回JSON数据或者是XML
    //@RequestBody的作用其实是将json格式的数据转为java对象
    public ResponseEntity userDelete(@RequestBody List<User> users){

        for (User user:users){
            if ("root".equals(user.getUserName())){
                return ResponseEntity.error("管理员账户不可删除");
            }
            userService.removeById(user.getId());
        }
        return ResponseEntity.success();
    }

}
