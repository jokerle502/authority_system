package com.hopu.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hopu.domain.Menu;
import com.hopu.domain.Role;
import com.hopu.domain.RoleMenu;
import com.hopu.domain.UserRole;
import com.hopu.result.PageEntity;
import com.hopu.result.ResponseEntity;
import com.hopu.service.IRoleMenuService;
import com.hopu.service.IRoleService;
import com.hopu.service.IUserRoleService;
import com.hopu.utils.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.hopu.result.ResponseEntity.success;

@Controller
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private IRoleService roleService;
    @Autowired
    private IRoleMenuService roleMenuService;
    @Autowired
    private IUserRoleService userRoleService;

    @RequestMapping("/roleList")
    @ResponseBody
    public PageEntity list(String userId){
        // 先查询指定用户已经有哪些角色
        List<UserRole> userRoleList = userRoleService.list(new QueryWrapper<UserRole>().eq("user_id", userId));

        // 查询所有角色信息
        List<Role> list = roleService.list();

//        // 判断用户哪些角色已经绑定，添加LAY_CHECKED字段为true
//        list.forEach(role -> {
//            List<String> roleIds = userRoleList.stream().map(userRole -> userRole.getRoleId()).collect(Collectors.toList());
//            if(roleIds.contains(role.getId())){
//                role.setLAY_CHECKED(true);
//            }
//        });
//        return new PageEntity(list.size(),list);

        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        list.forEach(role -> {
            // 先需要把对象转换为JSON格式
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(role));
            // 判断是否已经有了对应的权限
            List<String> roleIds = userRoleList.stream().map(userRole -> userRole.getRoleId()).collect(Collectors.toList());
            if(roleIds.contains(role.getId())){
                jsonObject.put("LAY_CHECKED",true);
            }
            jsonObjects.add(jsonObject);
        });

        return new PageEntity(jsonObjects.size(),jsonObjects);

    }

    /**
     * 跳转角色分配权限界面
     * @param roleId
     * @param request
     * @return
     */
    @RequestMapping("/toRoleSetMenu")
    public String toRoleSetMenu(@RequestParam(value = "id") String roleId, HttpServletRequest request){
        request.setAttribute("roleId",roleId);
        return "admin/role/role_setMenu";
    }

    @RequestMapping("/setMenu")
    @ResponseBody
    public ResponseEntity roleSetMenu(String roleId,@RequestBody List<Menu> menus){
        roleService.setMenu(roleId,menus);
        return success();

    }


    @RequestMapping("/toList")
    public String toList(){
        return "admin/role/role_list";
    }

    /**
     * 角色分页异步展示
     * @param pageNum   当前页，value = "page"——因为在layui框架里面分页展示返回的是page和limit，
     * @param pageSize  每页展示数
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public PageEntity list(@RequestParam(value = "page",defaultValue = "1") Integer pageNum,
                           @RequestParam(value = "limit",defaultValue = "5") Integer pageSize,
                           Role role){
        //设置分页条件
        Page<Role> page = new Page<>(pageNum,pageSize);
        //封装查询条件
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>(new Role());
        //条件查询
        if (role != null){
            if (!StringUtils.isEmpty(role.getRole())){
                roleQueryWrapper.like("role",role.getRole());
            }
        }
        IPage<Role> iPage = roleService.page(page, roleQueryWrapper);
        return new PageEntity(iPage);
    }

    @RequestMapping("/toRoleAdd")
    public String toRoleAdd(){
        return "admin/role/role_add";
    }

    @RequestMapping("/roleAdd")
    @ResponseBody
    public ResponseEntity roleAdd(Role role){
        //封装查询条件
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>(new Role());
        //角色名重名校验
        roleQueryWrapper.eq("role",role.getRole());
        Role one = roleService.getOne(roleQueryWrapper);
        if (one != null){
            return ResponseEntity.error("此角色已经存在");
        }
        //添加角色
        role.setId(UUIDUtils.getID());
        role.setCreateTime(new Date());

        roleService.save(role);

        return success();
    }

    @RequestMapping("/toRoleUpdate")
    public String toRoleUpdate(String id, Model model){
        Role role = roleService.getById(id);
        model.addAttribute("role",role);
        return "admin/role/role_update";
    }

    @RequestMapping("/roleUpdate")
    @ResponseBody
    public ResponseEntity roleUpdate(Role role){
        role.setUpdateTime(new Date());

        roleService.updateById(role);
        return success();
    }

    @RequestMapping("/roleDelete")
    @ResponseBody
    public ResponseEntity roleDelete(@RequestBody List<Role> roles){

        for (Role role:roles){
            if ("root".equals(role.getRole())){
                return ResponseEntity.error("管理员角色不可修改");
            }
            roleService.removeById(role.getId());
        }

        return success();
    }


}
