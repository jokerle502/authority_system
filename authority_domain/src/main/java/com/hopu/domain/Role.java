package com.hopu.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色表对应实体类
 */
@TableName("t_role")
@Data
public class Role extends BaseEntity{
    private static final long serialVersionUID = 1L;

    private String role; //角色名称
    private String remark; //备注

    @TableField(exist=false)
    private boolean LAY_CHECKED=false;

    public Role(String role, String remark) {
        this.role = role;
        this.remark = remark;
    }

    public Role() {
    }

    @Override
    public String toString() {
        return "role{" +
                "role='" + role + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
