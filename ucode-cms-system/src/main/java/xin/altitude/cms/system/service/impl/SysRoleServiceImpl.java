/*
 *
 * Copyright (c) 2020-2022, Java知识图谱 (http://www.altitude.xin).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package xin.altitude.cms.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import xin.altitude.cms.security.util.UserUtils;
import xin.altitude.cms.common.util.EntityUtils;
import xin.altitude.cms.common.util.SpringUtils;
import xin.altitude.cms.common.util.StringUtil;
import xin.altitude.cms.framework.annotation.DataScope;
import xin.altitude.cms.framework.constant.UserConstants;
import xin.altitude.cms.framework.core.domain.SysRole;
import xin.altitude.cms.framework.core.domain.SysUser;
import xin.altitude.cms.framework.exception.ServiceException;
import xin.altitude.cms.system.domain.SysRoleDept;
import xin.altitude.cms.system.domain.SysRoleMenu;
import xin.altitude.cms.system.domain.SysUserRole;
import xin.altitude.cms.system.mapper.SysRoleMapper;
import xin.altitude.cms.system.service.ISysRoleDeptService;
import xin.altitude.cms.system.service.ISysRoleMenuService;
import xin.altitude.cms.system.service.ISysRoleService;
import xin.altitude.cms.system.service.ISysUserRoleService;
import xin.altitude.cms.system.service.ISysUserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 角色 业务层处理
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
// @Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {
    // @Autowired
    // private SysRoleMapper roleMapper;

    // @Autowired
    // private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private ISysRoleMenuService sysRoleMenuService;

    // @Autowired
    // private SysUserRoleMapper userRoleMapper;

    // @Autowired
    // private SysRoleDeptMapper roleDeptMapper;
    @Autowired
    private ISysRoleDeptService sysRoleDeptService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<SysRole> selectRoleList(SysRole role) {
        // return roleMapper.selectRoleList(role);
        return list(Wrappers.lambdaQuery(role));
    }

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        // List<SysRole> userRoles = roleMapper.selectRolePermissionByUserId(userId);
        Set<Long> roleIds = EntityUtils.toSet(sysUserRoleService.list(Wrappers.lambdaQuery(SysUserRole.class).eq(SysUserRole::getUserId, userId)), SysUserRole::getRoleId);
        List<SysRole> userRoles = listByIds(roleIds);
        List<SysRole> roles = selectRoleAll();
        for (SysRole role : roles) {
            for (SysRole userRole : userRoles) {
                if (role.getRoleId().longValue() == userRole.getRoleId().longValue()) {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        // List<SysRole> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<Long> roleIds = EntityUtils.toSet(sysUserRoleService.list(Wrappers.lambdaQuery(SysUserRole.class).eq(SysUserRole::getUserId, userId)), SysUserRole::getRoleId);
        List<SysRole> perms = listByIds(roleIds);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (StringUtil.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRoleAll() {
        return SpringUtils.getAopProxy(this).selectRoleList(new SysRole());
    }

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    @Override
    public List<Long> selectRoleListByUserId(Long userId) {
        return EntityUtils.toList(sysUserRoleService.list(Wrappers.lambdaQuery(SysUserRole.class).eq(SysUserRole::getUserId, userId)), SysUserRole::getRoleId);
        // return roleMapper.selectRoleListByUserId(userId);
    }

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public SysRole selectRoleById(Long roleId) {
        // return roleMapper.selectRoleById(roleId);
        return getById(roleId);
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public String checkRoleNameUnique(SysRole role) {
        Long roleId = StringUtil.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        // SysRole info = roleMapper.checkRoleNameUnique(role.getRoleName());
        SysRole info = getOne(Wrappers.lambdaQuery(SysRole.class).eq(SysRole::getRoleName, role.getRoleName()));
        if (StringUtil.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public String checkRoleKeyUnique(SysRole role) {
        Long roleId = StringUtil.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        // SysRole info = roleMapper.checkRoleKeyUnique(role.getRoleKey());
        SysRole info = getOne(Wrappers.lambdaQuery(SysRole.class).eq(SysRole::getRoleKey, role.getRoleKey()));
        if (StringUtil.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(SysRole role) {
        if (StringUtil.isNotNull(role.getRoleId()) && role.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     */
    @Override
    public void checkRoleDataScope(Long roleId) {
        if (!SysUser.isAdmin(UserUtils.getUserId())) {
            SysRole role = new SysRole();
            role.setRoleId(roleId);
            List<SysRole> roles = SpringUtils.getAopProxy(this).selectRoleList(role);
            if (StringUtil.isEmpty(roles)) {
                throw new ServiceException("没有权限访问角色数据！");
            }
        }
    }

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public long countUserRoleByRoleId(Long roleId) {
        // return userRoleMapper.countUserRoleByRoleId(roleId);
        return sysUserRoleService.count(Wrappers.lambdaQuery(SysUserRole.class).eq(SysUserRole::getRoleId, roleId));
    }

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertRole(SysRole role) {
        // 新增角色信息
        // roleMapper.insertRole(role);
        save(role);
        return insertRoleMenu(role);
    }

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateRole(SysRole role) {
        // 修改角色信息
        // roleMapper.updateRole(role);
        updateById(role);
        // 删除角色与菜单关联
        // roleMenuMapper.deleteRoleMenuByRoleId(role.getRoleId());
        sysRoleMenuService.remove(Wrappers.lambdaQuery(SysRoleMenu.class).eq(SysRoleMenu::getRoleId, role.getRoleId()));
        return insertRoleMenu(role);
    }

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public boolean updateRoleStatus(SysRole role) {
        // return roleMapper.updateRole(role);
        return updateById(role);
    }

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int authDataScope(SysRole role) {
        // 修改角色信息
        // roleMapper.updateRole(role);
        updateById(role);
        // 删除角色与部门关联
        // roleDeptMapper.deleteRoleDeptByRoleId(role.getRoleId());
        sysRoleDeptService.remove(Wrappers.lambdaQuery(SysRoleDept.class).eq(SysRoleDept::getRoleId, role.getRoleId()));

        // 新增角色和部门信息（数据权限）
        return insertRoleDept(role);
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    public int insertRoleMenu(SysRole role) {
        int rows = 1;
        // 新增用户与角色管理
        List<SysRoleMenu> list = new ArrayList<SysRoleMenu>();
        for (Long menuId : role.getMenuIds()) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0) {
            // rows = roleMenuMapper.batchRoleMenu(list);
            sysRoleMenuService.saveBatch(list);
        }
        return rows;
    }

    /**
     * 新增角色部门信息(数据权限)
     *
     * @param role 角色对象
     */
    public int insertRoleDept(SysRole role) {
        int rows = 1;
        // 新增角色与部门（数据权限）管理
        List<SysRoleDept> list = new ArrayList<SysRoleDept>();
        for (Long deptId : role.getDeptIds()) {
            SysRoleDept rd = new SysRoleDept();
            rd.setRoleId(role.getRoleId());
            rd.setDeptId(deptId);
            list.add(rd);
        }
        if (list.size() > 0) {
            // rows = roleDeptMapper.batchRoleDept(list);
            sysRoleDeptService.saveBatch(list);
        }
        return rows;
    }

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    @Transactional
    public boolean deleteRoleById(Long roleId) {
        // 删除角色与菜单关联
        // roleMenuMapper.deleteRoleMenuByRoleId(roleId);
        sysRoleMenuService.remove(Wrappers.lambdaQuery(SysRoleMenu.class).eq(SysRoleMenu::getRoleId, roleId));
        // 删除角色与部门关联
        // roleDeptMapper.deleteRoleDeptByRoleId(roleId);
        sysRoleDeptService.remove(Wrappers.lambdaQuery(SysRoleDept.class).eq(SysRoleDept::getRoleId, roleId));
        // return roleMapper.deleteRoleById(roleId);
        return removeById(roleId);
    }

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @Override
    @Transactional
    public boolean deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            checkRoleAllowed(new SysRole(roleId));
            SysRole role = selectRoleById(roleId);
            if (countUserRoleByRoleId(roleId) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", role.getRoleName()));
            }
        }
        // 删除角色与菜单关联
        // roleMenuMapper.deleteRoleMenu(roleIds);
        sysRoleMenuService.remove(Wrappers.lambdaQuery(SysRoleMenu.class).in(SysRoleMenu::getRoleId, Arrays.asList(roleIds)));
        // 删除角色与部门关联
        // roleDeptMapper.deleteRoleDept(roleIds);
        sysRoleDeptService.remove(Wrappers.lambdaQuery(SysRoleDept.class).in(SysRoleDept::getRoleId, Arrays.asList(roleIds)));
        // return roleMapper.deleteRoleByIds(roleIds);
        return removeByIds(Arrays.asList(roleIds));
    }

    /**
     * 取消授权用户角色
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    @Override
    public boolean deleteAuthUser(SysUserRole userRole) {
        // return userRoleMapper.deleteUserRoleInfo(userRole);
        return sysUserRoleService.remove(Wrappers.lambdaQuery(userRole));
    }

    /**
     * 批量取消授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    @Override
    public boolean deleteAuthUsers(Long roleId, Long[] userIds) {
        // return userRoleMapper.deleteUserRoleInfos(roleId, userIds);
        return sysUserRoleService.remove(Wrappers.lambdaQuery(SysUserRole.class).eq(SysUserRole::getRoleId, roleId).in(SysUserRole::getUserId, userIds));
    }

    /**
     * 批量选择授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    @Override
    public boolean insertAuthUsers(Long roleId, Long[] userIds) {
        // 新增用户与角色管理
        List<SysUserRole> list = new ArrayList<>();
        for (Long userId : userIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        // return userRoleMapper.batchUserRole(list);
        return sysUserRoleService.saveBatch(list);
    }

    @Override
    public List<SysRole> selectRolesByUserName(String userName) {
        SysUser sysUser = sysUserService.getOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUserName, userName));
        Long userId = EntityUtils.toObj(sysUser, SysUser::getUserId);
        List<SysUserRole> userRoles = sysUserRoleService.list(Wrappers.lambdaQuery(SysUserRole.class).eq(SysUserRole::getUserId, userId));
        List<Long> roleIds = EntityUtils.toList(userRoles, SysUserRole::getRoleId);
        if (roleIds.size() > 0) {
            return list(Wrappers.lambdaQuery(SysRole.class).in(SysRole::getRoleId, roleIds));
        }
        return new ArrayList<>();
    }
}
