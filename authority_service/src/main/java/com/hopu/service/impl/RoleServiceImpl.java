package com.hopu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hopu.domain.Menu;
import com.hopu.domain.Role;
import com.hopu.domain.RoleMenu;
import com.hopu.mapper.RoleMapper;
import com.hopu.service.IRoleMenuService;
import com.hopu.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Autowired
    private IRoleMenuService roleMenuService;

    @Override
    public void setMenu(String roleId, List<Menu> menus) {
        //首先移除原有的权限，避免重复添加
        roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("role_id",roleId));
        //再重新添加权限，注意：角色和权限是一对多的关系
        menus.forEach(menu -> {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setMenuId(menu.getId());
            roleMenu.setRoleId(roleId);
            roleMenuService.save(roleMenu);
        });

    }
}
