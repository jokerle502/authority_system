package com.hopu.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hopu.domain.Menu;
import com.hopu.domain.RoleMenu;
import com.hopu.result.PageEntity;
import com.hopu.result.ResponseEntity;
import com.hopu.service.IMenuService;
import com.hopu.service.IRoleMenuService;
import com.hopu.utils.IconFontUtils;
import com.hopu.utils.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.hopu.result.ResponseEntity.success;


@Controller
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private IMenuService menuService;
    @Autowired
    private IRoleMenuService roleMenuService;

    @RequestMapping("/menuList")
    @ResponseBody
    public PageEntity menuList(String roleId){
        // 查询当前角色已经关联了的权限
        QueryWrapper<RoleMenu> roleMenuQueryWrapper = new QueryWrapper<>();
        List<RoleMenu> roleMenuList = roleMenuService.list(roleMenuQueryWrapper.eq("role_id", roleId));

        // 如果不涉及到子菜单关联
        List<Menu> list = menuService.list();

        //  此处循环的作用就是为了判断角色已有权限，然后添加一个LAY_CHECKED字段，前端layui表格才能自动勾选
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        list.forEach(menu -> {
            // 先需要把对象转换为JSON格式
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(menu));
            // 判断是否已经有了对应的权限
            List<String> menuIds = roleMenuList.stream().map(roleMenu -> roleMenu.getMenuId()).collect(Collectors.toList());
            if(menuIds.contains(menu.getId())){
                jsonObject.put("LAY_CHECKED",true);
            }
            jsonObjects.add(jsonObject);
        });

        return new PageEntity(jsonObjects.size(),jsonObjects);
    }

    /**
     * 跳转菜单展示界面
     * @return
     */
    @RequestMapping("/toList")
    public String toList(){
        return "admin/menu/menu_list";
    }

    /**
     * 菜单分页异步展示,不需要分页展示
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public PageEntity list(){
        //封装查询条件
        QueryWrapper<Menu> menuQueryWrapper = new QueryWrapper<>();
        //查询父菜单,在这里“0”是为了直接找到顶级目录，方便后面父菜单找到其子菜单
        List<Menu> pList = menuService.list(menuQueryWrapper.eq("pid","0"));
        //根据父菜单id查询对应的所有子菜单，把子菜单封装到父菜单对象的属性nodes中
        //需求：最终返回的是各个菜单集合
        ArrayList<Menu> menus = new ArrayList<>();

        findChildrenMenu(pList,menus);

        return new PageEntity(menus.size(),menus);
    }

    //私有方法，循环查询儿子菜单列表
    private List<Menu> findChildrenMenu(List<Menu> pList,List<Menu> menus){
        for (Menu menu:pList){
            //在这判断菜单是否已经查过了，为了防止2次循环找到相同的菜单
            if (!menus.contains(menu)){
                menus.add(menu);
            }

            String pId = menu.getId();
            //封装查询条件
            QueryWrapper<Menu> menuQueryWrapper = new QueryWrapper<>();
            List<Menu> childrenList = menuService.list(menuQueryWrapper.eq("pid", pId));
            menu.setNodes(childrenList);
            //判断是否有儿子菜单
            if (childrenList.size()>0){
                //递归调用
                menus = findChildrenMenu(childrenList,menus);
            }
        }
        return  menus;
    }

    /**
     * 添加菜单
     * 菜单列表跳转的toAddPage方法中，还做了2个重要的功能：查询父级菜单、查询所有iconFont图标。
     * 这是因为菜单添加页面中父级菜单选项和菜单图标选项需要根据已有的进行选择。
     * @param model
     * @return
     */
    @RequestMapping("/toMenuAdd")
    public String toMenuAdd(Model model){
        QueryWrapper<Menu> menuQueryWrapper = new QueryWrapper<>();
        //父菜单
        List<Menu> list = menuService.list(menuQueryWrapper.eq("pid", '0'));
        findChildren(list);

        List<String> iconFont = IconFontUtils.getIconFont();
        model.addAttribute("list",list);
        model.addAttribute("iconFont",iconFont);
        return "admin/menu/menu_add";
    }

    //私有方法，查询父级菜单下的子菜单
    private void findChildren(List<Menu> list){
        for (Menu menu : list) {
            List<Menu> list2 = menuService.list(new QueryWrapper<Menu>(new Menu()).eq("pid", menu.getId()));
            if (list2!=null) {
                menu.setNodes(list2);
            }
        }
    }

    @RequestMapping("/add")
    @ResponseBody
    public ResponseEntity add(Menu menu){
        menu.setId(UUIDUtils.getID());
        menu.setCreateTime(new Date());
        menuService.save(menu);
        return success();
    }

    /**
     * 菜单修改链接跳转，修改前需要回显信息；通过所选中的菜单id、跳转修改页面时request域中保存的信息，来进行修改
     * @param id
     * @param request
     * @return
     */
    @RequestMapping("/toMenuUpdate")
    public String toMenuUpdate(String id, HttpServletRequest request){
        //找到所选id的信息
        Menu menu = menuService.getById(id);

        List<Menu> list = menuService.list(new QueryWrapper<Menu>().eq("pid", "0").orderByAsc("seq"));
        findChildren(list);

        //查询iconFont图标
        List<String> iconFont = IconFontUtils.getIconFont();

        request.setAttribute("list",list);
        request.setAttribute("iconFont",iconFont);
        request.setAttribute("menu",menu);
        return "admin/menu/menu_update";
    }

    @RequestMapping("/update")
    @ResponseBody
    public ResponseEntity menuUpdate(Menu menu){
        menu.setUpdateTime(new Date());
        menuService.updateById(menu);
        return success();
    }

    /**
     * 菜单删除没有条件并且可以批量删除
     * @param menus
     * @return
     */
    @RequestMapping("/menuDelete")
    @ResponseBody
    public ResponseEntity menuDelete(@RequestBody ArrayList<Menu> menus){
        List<String> list = new ArrayList<>();
        for (Menu menu:menus){
            list.add(menu.getId());
        }
        menuService.removeByIds(list);
        return success();
    }

}
