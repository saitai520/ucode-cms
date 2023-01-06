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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import xin.altitude.cms.auth.util.SecurityUtils;
import xin.altitude.cms.security.util.UserUtils;
import xin.altitude.cms.common.util.EntityUtils;
import xin.altitude.cms.common.util.SpringUtils;
import xin.altitude.cms.common.util.StringUtil;
import xin.altitude.cms.framework.annotation.DataScope;
import xin.altitude.cms.framework.constant.UserConstants;
import xin.altitude.cms.framework.core.domain.SysDept;
import xin.altitude.cms.framework.core.domain.SysRole;
import xin.altitude.cms.framework.core.domain.SysUser;
import xin.altitude.cms.framework.exception.ServiceException;
import xin.altitude.cms.system.domain.SysPost;
import xin.altitude.cms.system.domain.SysUserPost;
import xin.altitude.cms.system.domain.SysUserRole;
import xin.altitude.cms.system.mapper.SysUserMapper;
import xin.altitude.cms.system.service.ISysConfigService;
import xin.altitude.cms.system.service.ISysDeptService;
import xin.altitude.cms.system.service.ISysPostService;
import xin.altitude.cms.system.service.ISysRoleService;
import xin.altitude.cms.system.service.ISysUserPostService;
import xin.altitude.cms.system.service.ISysUserRoleService;
import xin.altitude.cms.system.service.ISysUserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户 业务层处理
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
// @Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private ISysPostService sysPostService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ISysUserPostService sysUserPostService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISysDeptService sysDeptService;


    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUserList(SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery(user);
        if (user.getDeptId() != null) {
            /*  只查询当前部分，孩子并未包含 */
            wrapper.eq(SysUser::getDeptId, user.getDeptId());
            HashSet<Long> deptIds = new HashSet<>();
            sysDeptService.getChildIds(deptIds, user.getDeptId());
            wrapper.or().in(SysUser::getDeptId, deptIds);
            user.setDeptId(null);
        }
        List<SysUser> sysUsers = list(wrapper);
        Set<Long> deptIds = EntityUtils.toSet(sysUsers, SysUser::getDeptId);
        if (deptIds.size() > 0) {
            Map<Long, SysDept> map = EntityUtils.toMap(sysDeptService.listByIds(deptIds), SysDept::getDeptId, e -> e);
            sysUsers.forEach(e -> e.setDept(map.get(e.getDeptId())));
        }
        return sysUsers;
        // return userMapper.selectUserList(user);
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectAllocatedList(SysUser user) {
        Set<Long> userIds = EntityUtils.toSet(sysUserRoleService.list(Wrappers.lambdaQuery(SysUserRole.class)), SysUserRole::getUserId);
        if (userIds.size() > 0) {
            return list(Wrappers.lambdaQuery(user).in(SysUser::getUserId, userIds));
        }
        return new ArrayList<>();
        // return userMapper.selectAllocatedList(user);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUnallocatedList(SysUser user) {
        // return userMapper.selectUnallocatedList(user);
        Set<Long> userIds = EntityUtils.toSet(sysUserRoleService.list(Wrappers.lambdaQuery(SysUserRole.class)), SysUserRole::getUserId);
        return list(Wrappers.lambdaQuery(user).in(userIds.size() > 0, SysUser::getUserId, userIds));
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {
        // return userMapper.selectUserByUserName(userName);
        // 需要补充部门、角色等信息
        SysUser sysUser = getOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUserName, userName));
        sysUser.setDept(sysDeptService.getById(sysUser.getDeptId()));
        return sysUser;
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId) {
        SysUser sysUser = getById(userId);
        sysUser.setDept(sysDeptService.getById(sysUser.getDeptId()));
        return sysUser;
        // return userMapper.selectUserById(userId);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName) {
        // List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        List<SysRole> list = sysRoleService.selectRolesByUserName(userName);
        StringBuffer idsStr = new StringBuffer();
        for (SysRole role : list) {
            idsStr.append(role.getRoleName()).append(",");
        }
        if (StringUtil.isNotEmpty(idsStr.toString())) {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName) {
        // List<SysPost> list = postMapper.selectPostsByUserName(userName);
        List<SysPost> list = sysPostService.selectPostsByUserName(userName);
        StringBuffer idsStr = new StringBuffer();
        for (SysPost post : list) {
            idsStr.append(post.getPostName()).append(",");
        }
        if (StringUtil.isNotEmpty(idsStr.toString())) {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    @Override
    public String checkUserNameUnique(String userName) {
        // int count = userMapper.checkUserNameUnique(userName);
        long count = count(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUserName, userName));
        if (count > 0) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(SysUser user) {
        Long userId = StringUtil.isNull(user.getUserId()) ? -1L : user.getUserId();
        // SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber());
        SysUser info = getOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getPhonenumber, user.getPhonenumber()));
        if (StringUtil.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(SysUser user) {
        Long userId = StringUtil.isNull(user.getUserId()) ? -1L : user.getUserId();
        // SysUser info = userMapper.checkEmailUnique(user.getEmail());
        SysUser info = getOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getEmail, user.getEmail()));
        if (StringUtil.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user) {
        if (StringUtil.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId) {
        if (!SysUser.isAdmin(UserUtils.getUserId())) {
            SysUser user = new SysUser();
            user.setUserId(userId);
            List<SysUser> users = SpringUtils.getAopProxy(this).selectUserList(user);
            if (StringUtil.isEmpty(users)) {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public boolean insertUser(SysUser user) {
        // 新增用户信息
        // int rows = userMapper.insertUser(user);
        boolean bool = save(user);
        // 新增用户岗位关联
        insertUserPost(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return bool;
    }

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean registerUser(SysUser user) {
        // return userMapper.insertUser(user) > 0;
        return save(user);
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public boolean updateUser(SysUser user) {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        // userRoleMapper.deleteUserRoleByUserId(userId);
        sysUserRoleService.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        // 删除用户与岗位关联
        // userPostMapper.deleteUserPostByUserId(userId);
        sysUserPostService.deleteUserPostByUserId(userId);
        // 新增用户与岗位管理
        insertUserPost(user);
        // return userMapper.updateUser(user);
        return updateById(user);
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional
    public void insertUserAuth(Long userId, Long[] roleIds) {
        // userRoleMapper.deleteUserRoleByUserId(userId);
        sysUserRoleService.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds);
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean updateUserStatus(SysUser user) {
        // return userMapper.updateUser(user);
        return updateById(user);
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean updateUserProfile(SysUser user) {
        // return userMapper.updateUser(user);
        return updateById(user);
    }

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar   头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String userName, String avatar) {
        // return userMapper.updateUserAvatar(userName, avatar) > 0;
        SysUser sysUser = new SysUser();
        sysUser.setAvatar(avatar);
        return update(sysUser, Wrappers.lambdaUpdate(SysUser.class).eq(SysUser::getUserName, userName));
    }

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean resetPwd(SysUser user) {
        // return userMapper.updateUser(user);
        return updateById(user);
    }

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public boolean resetUserPwd(String userName, String password) {
        // return userMapper.resetUserPwd(userName, password);
        SysUser sysUser = new SysUser();
        sysUser.setAvatar(password);
        return update(sysUser, Wrappers.lambdaUpdate(SysUser.class).eq(SysUser::getUserName, userName));
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user) {
        Long[] roles = user.getRoleIds();
        if (StringUtil.isNotNull(roles)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<>();
            for (Long roleId : roles) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getUserId());
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0) {
                // userRoleMapper.batchUserRole(list);
                sysUserRoleService.saveBatch(list);
            }
        }
    }

    /**
     * 新增用户岗位信息
     *
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user) {
        Long[] posts = user.getPostIds();
        if (StringUtil.isNotNull(posts)) {
            // 新增用户与岗位管理
            List<SysUserPost> list = new ArrayList<>();
            for (Long postId : posts) {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }
            if (list.size() > 0) {
                // userPostMapper.batchUserPost(list);
                sysUserPostService.saveBatch(list);
            }
        }
    }

    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds) {
        if (StringUtil.isNotNull(roleIds)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0) {
                // userRoleMapper.batchUserRole(list);
                sysUserRoleService.saveBatch(list);
            }
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public boolean deleteUserById(Long userId) {
        // 删除用户与角色关联
        // userRoleMapper.deleteUserRoleByUserId(userId);
        sysUserRoleService.deleteUserRoleByUserId(userId);
        // 删除用户与岗位表
        // userPostMapper.deleteUserPostByUserId(userId);
        sysUserPostService.deleteUserPostByUserId(userId);
        // return userMapper.deleteUserById(userId);
        return removeById(userId);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public boolean deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            checkUserAllowed(new SysUser(userId));
        }
        // 删除用户与角色关联
        // userRoleMapper.deleteUserRole(userIds);
        sysUserRoleService.deleteUserRoleByUserIds(Arrays.asList(userIds));
        // 删除用户与岗位关联
        // sysUserPostService.deleteUserPost(userIds);
        sysUserPostService.deleteUserPostByUserIds(Arrays.asList(userIds));
        // return userMapper.deleteUserByIds(userIds);
        return removeByIds(Arrays.asList(userIds));
    }

    /**
     * 导入用户数据
     *
     * @param userList        用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName) {
        if (StringUtil.isNull(userList) || userList.size() == 0) {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        String password = configService.selectConfigByKey("sys.user.initPassword");
        for (SysUser user : userList) {
            try {
                // 验证是否存在这个用户
                SysUser u = selectUserByUserName(user.getUserName());
                if (StringUtil.isNull(u)) {
                    user.setPassword(SecurityUtils.encryptPassword(password));
                    user.setCreateBy(operName);
                    this.insertUser(user);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getUserName()).append(" 导入成功");
                } else if (isUpdateSupport) {
                    user.setUpdateBy(operName);
                    this.updateUser(user);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getUserName()).append(" 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(user.getUserName()).append(" 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
                failureMsg.append(msg).append(e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }
}
