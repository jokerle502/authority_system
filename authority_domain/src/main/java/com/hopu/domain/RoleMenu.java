package com.hopu.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色菜单表
 */
@TableName("t_role_menu")
@Data
public class RoleMenu{

    private static final long serialVersionUID = 1L;

    private String menuId;  // 菜单id
    private String roleId; // 角色id

    public RoleMenu(String menuId, String roleId) {
        this.menuId = menuId;
        this.roleId = roleId;
    }

    public RoleMenu() {
    }

    @Override
    public String toString() {
        return "RoleMenu{" +
                "menuId='" + menuId + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }
}
