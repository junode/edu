package com.lagou.edu.authority.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lagou.edu.auth.client.dto.AllocateRoleMenuDTO;
import com.lagou.edu.auth.client.dto.MenuNodeDTO;
import com.lagou.edu.auth.client.param.MenuQueryParam;
import com.lagou.edu.authority.common.contant.PubContants;
import com.lagou.edu.authority.entity.po.Menu;
import com.lagou.edu.authority.entity.po.RoleMenu;
import com.lagou.edu.authority.mapper.MenuMapper;
import com.lagou.edu.authority.service.IMenuService;
import com.lagou.edu.authority.service.IRoleMenuService;
import com.lagou.edu.common.util.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MenuService extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private IRoleMenuService roleMenuService;

    @Override
    public List<Menu> queryByParentId(Integer id) {
        return this.list(new QueryWrapper<Menu>().eq("parent_id", id));
    }

    @Override
    public List<Menu> queryByIds(List<Integer> ids) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        return this.list(queryWrapper);
    }

    @Override
    public List<Menu> queryByRoleIds(Set<Integer> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Lists.newArrayList();
        }
        return menuMapper.queryByRoleIds(roleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteWithAssociation(Integer id) {
        deleteMenuCascade(id);
        return true;
    }

    /**
     * ???????????????????????????????????????????????????????????????
     *
     * @param id
     */
    private void deleteMenuCascade(Integer id) {
        List<Menu> menus = this.queryByParentId(id);
        if (CollectionUtils.isNotEmpty(menus)) {
            menus.stream().forEach(menu -> deleteMenuCascade(menu.getId()));
        }
        roleMenuService.removeByMenuId(id);
        this.removeById(id);
    }

    @Override
    public Page<Menu> getMenuPages(MenuQueryParam menuQueryParam) {
        Page<Menu> page = new Page<>(menuQueryParam.getCurrent(), menuQueryParam.getSize());
        QueryWrapper<Menu> queryWrapper = new QueryWrapper();
        // ??????????????????????????????
        if (Objects.nonNull(menuQueryParam.getId()) && menuQueryParam.isQuerySubLevel()) {
            queryWrapper.eq("parent_id", menuQueryParam.getId());
        } else if (Objects.nonNull(menuQueryParam.getId()) && !menuQueryParam.isQuerySubLevel()) {
            queryWrapper.eq("id", menuQueryParam.getId());
        }
        queryWrapper
                .like(StringUtils.isNotBlank(menuQueryParam.getName()), "name", menuQueryParam.getName())
                .eq(Objects.nonNull(menuQueryParam.getShown()), "shown", menuQueryParam.getShown())
                .ge(Objects.nonNull(menuQueryParam.getStartCreateTime()), "created_time", menuQueryParam.getStartCreateTime())
                .le(Objects.nonNull(menuQueryParam.getEndCreateTime()), "created_time", menuQueryParam.getEndCreateTime())
                .orderByDesc("id");
        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean switchShown(Menu menu) {
        Menu dbMenu = this.getById(menu.getId());
        if (Objects.isNull(dbMenu)) {
            return false;
        }
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("shown", !dbMenu.isShown());
        updateWrapper.set("updated_by", menu.getUpdatedBy());
        updateWrapper.set("updated_time", menu.getUpdatedTime());
        updateWrapper.eq("id", menu.getId());
        return this.update(updateWrapper);
    }

    @Override
    public List<MenuNodeDTO> getMenuNodeList() {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper();
        queryWrapper.eq("level", PubContants.MENU_TOP_LEVEL);
        List<Menu> topLevelMenuList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(topLevelMenuList)) {
            return Lists.newArrayList();
        }
        List<MenuNodeDTO> menuNodeList = topLevelMenuList.stream().map(menu -> fillMenuNode(menu)).collect(Collectors.toList());
        Collections.sort(menuNodeList);
        return menuNodeList;
    }

    /**
     * ???????????????
     *
     * @param menu
     */
    @Override
    public MenuNodeDTO fillMenuNode(Menu menu) {
        MenuNodeDTO menuNodeDTO = ConvertUtils.convert(menu, MenuNodeDTO.class);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("parent_id", menuNodeDTO.getId());
        List<Menu> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return menuNodeDTO;
        }
        List<MenuNodeDTO> subMenuList = list.stream().map(subMenu -> fillMenuNode(subMenu)).collect(Collectors.toList());
        Collections.sort(subMenuList);
        menuNodeDTO.setSubMenuList(subMenuList);
        return menuNodeDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void allocateRoleMenus(AllocateRoleMenuDTO allocateRoleMenuDTO) {
        if (CollectionUtils.isEmpty(allocateRoleMenuDTO.getMenuIdList())) {
            // ??????????????????????????????
            allocateRoleMenuDTO.setMenuIdList(Lists.newArrayList());
        }
        // ????????????????????????
        Set<Integer> roleMenuIds = roleMenuService.queryByRoleIds(Sets.newHashSet(allocateRoleMenuDTO.getRoleId()));
        // ?????????????????????????????????????????? -1(-1???????????????id????????????????????????ID)
        Set<Integer> allocateRoleMenuIds = allocateRoleMenuDTO.getMenuIdList().stream()
                .filter(menuId -> !Objects.equals(menuId, PubContants.MENU_TOP_LEVEL_PARENT_ID)).collect(Collectors.toSet());

        // ?????????????????????
        Set<Integer> needToDelMenus = roleMenuIds.stream().filter(id -> !allocateRoleMenuIds.contains(id)).collect(Collectors.toSet());
        // ?????????????????????
        Set<Integer> needToInsertMenus = allocateRoleMenuIds.stream().filter(id -> !roleMenuIds.contains(id)).collect(Collectors.toSet());

        if (CollectionUtils.isNotEmpty(needToDelMenus)) {
            roleMenuService.removeByRoleIdAndMenuIds(allocateRoleMenuDTO.getRoleId(), needToDelMenus);
        }

        if (CollectionUtils.isNotEmpty(needToInsertMenus)) {
            List<RoleMenu> roleMenus = needToInsertMenus.stream().map(menuId -> {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(allocateRoleMenuDTO.getRoleId());
                roleMenu.setMenuId(menuId);
                roleMenu.setCreatedBy(allocateRoleMenuDTO.getCreatedBy());
                roleMenu.setUpdatedBy(allocateRoleMenuDTO.getUpdatedBy());
                roleMenu.setCreatedTime(new Date());
                roleMenu.setUpdatedTime(new Date());
                return roleMenu;
            }).collect(Collectors.toList());
            roleMenuService.saveBatch(roleMenus);
        }
    }

    @Override
    public List<Menu> getByRoleIdIgnoreIsShown(Integer roleId) {
        return menuMapper.queryByRoleIdIgnoreIsShown(roleId);
    }
}
