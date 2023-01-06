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
import org.springframework.beans.factory.annotation.Autowired;
import xin.altitude.cms.security.util.UserUtils;
import xin.altitude.cms.common.util.ConvertUtils;
import xin.altitude.cms.common.util.EntityUtils;
import xin.altitude.cms.common.util.SpringUtils;
import xin.altitude.cms.common.util.StringUtil;
import xin.altitude.cms.framework.annotation.DataScope;
import xin.altitude.cms.framework.constant.UserConstants;
import xin.altitude.cms.framework.core.domain.SysDept;
import xin.altitude.cms.framework.core.domain.SysRole;
import xin.altitude.cms.framework.core.domain.SysUser;
import xin.altitude.cms.framework.entity.TreeSelect;
import xin.altitude.cms.framework.exception.ServiceException;
import xin.altitude.cms.system.domain.SysRoleDept;
import xin.altitude.cms.system.mapper.SysDeptMapper;
import xin.altitude.cms.system.service.ISysDeptService;
import xin.altitude.cms.system.service.ISysRoleDeptService;
import xin.altitude.cms.system.service.ISysRoleService;
import xin.altitude.cms.system.service.ISysUserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 部门管理 服务实现
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
// @Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {
    // @Autowired
    // private SysDeptMapper deptMapper;
    //
    // @Autowired
    // private SysRoleMapper roleMapper;

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private ISysRoleDeptService sysRoleDeptService;

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<SysDept> selectDeptList(SysDept dept) {
        // return deptMapper.selectDeptList(dept);
        return list(Wrappers.lambdaQuery(dept));
    }

    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> returnList = new ArrayList<SysDept>();
        List<Long> tempList = new ArrayList<Long>();
        for (SysDept dept : depts) {
            tempList.add(dept.getDeptId());
        }
        for (SysDept dept : depts) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts) {
        List<SysDept> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    @Override
    public List<Long> selectDeptListByRoleId(Long roleId) {
        // SysRole role = roleMapper.selectRoleById(roleId);
        SysRole role = sysRoleService.getById(roleId);
        LambdaQueryWrapper<SysRoleDept> wrapper = Wrappers.lambdaQuery(SysRoleDept.class);
        /* 是否只包含叶子节点 */
        if (role.isDeptCheckStrictly()) {
            Set<Long> deptIds = getParentIds();
            if (deptIds.size() > 0) {
                wrapper.notIn(SysRoleDept::getDeptId, deptIds);
            }
        }
        return EntityUtils.toList(sysRoleDeptService.list(wrapper), SysRoleDept::getDeptId);
        // return deptMapper.selectDeptListByRoleId(roleId, role.isDeptCheckStrictly());
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Override
    public SysDept selectDeptById(Long deptId) {
        // return deptMapper.selectDeptById(deptId);
        return getById(deptId);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    @Override
    public int selectNormalChildrenDeptById(Long deptId) {
        // return deptMapper.selectNormalChildrenDeptById(deptId);
        HashSet<Long> deptIds = new HashSet<>();
        /* 没有限制部门状态 */
        getChildIds(deptIds, deptId);
        deptIds.remove(deptId);
        return deptIds.size();
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(Long deptId) {
        // int result = deptMapper.hasChildByDeptId(deptId);
        long result = count(Wrappers.lambdaQuery(SysDept.class).eq(SysDept::getParentId, deptId));
        return result > 0;
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(Long deptId) {
        // int result = deptMapper.checkDeptExistUser(deptId);
        long result = sysUserService.count(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getDeptId, deptId));
        return result > 0;
    }

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public String checkDeptNameUnique(SysDept dept) {
        Long deptId = StringUtil.isNull(dept.getDeptId()) ? -1L : dept.getDeptId();
        // SysDept info = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
        SysDept info = getOne(Wrappers.lambdaQuery(SysDept.class).eq(SysDept::getDeptName, dept.getDeptName()));
        if (StringUtil.isNotNull(info) && info.getDeptId().longValue() != deptId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    @Override
    public void checkDeptDataScope(Long deptId) {
        if (!SysUser.isAdmin(UserUtils.getUserId())) {
            SysDept dept = new SysDept();
            dept.setDeptId(deptId);
            List<SysDept> depts = SpringUtils.getAopProxy(this).selectDeptList(dept);
            if (StringUtil.isEmpty(depts)) {
                throw new ServiceException("没有权限访问部门数据！");
            }
        }
    }

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public boolean insertDept(SysDept dept) {
        SysDept info = getById(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus())) {
            throw new ServiceException("部门停用，不允许新增");
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        // return deptMapper.insertDept(dept);
        return save(dept);
    }

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public boolean updateDept(SysDept dept) {
        // SysDept newParentDept = deptMapper.selectDeptById(dept.getParentId());
        SysDept newParentDept = getById(dept.getParentId());
        SysDept oldDept = getById(dept.getDeptId());
        if (StringUtil.isNotNull(newParentDept) && StringUtil.isNotNull(oldDept)) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        // int result = deptMapper.updateDept(dept);
        boolean result = updateById(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()) && StringUtil.isNotEmpty(dept.getAncestors())
            && !StringUtil.equals("0", dept.getAncestors())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(SysDept dept) {
        String ancestors = dept.getAncestors();
        Long[] deptIds = ConvertUtils.toLongArray(ancestors);
        List<SysDept> sysDepts = EntityUtils.toList(Arrays.asList(deptIds), e -> new SysDept(e, "0"));
        updateBatchById(sysDepts);
        // deptMapper.updateDeptStatusNormal(deptIds);
    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        // List<SysDept> children = deptMapper.selectChildrenDeptById(deptId);
        HashSet<Long> deptIds = new HashSet<>();
        getChildIds(deptIds, deptId);
        deptIds.remove(deptId);
        List<SysDept> children = listByIds(deptIds);
        // List<SysDept> children = getChildList(deptId);
        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0) {
            // deptMapper.updateDeptChildren(children);
            updateBatchById(children);
        }
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean deleteDeptById(Long deptId) {
        // return deptMapper.deleteDeptById(deptId);
        return removeById(deptId);
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, SysDept t) {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
        List<SysDept> tlist = new ArrayList<SysDept>();
        for (SysDept n : list) {
            if (StringUtil.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getDeptId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t) {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 查询当前节点的孩子ID（包含自己）
     *
     * @param deptIds
     * @param deptId
     */
    @Override
    public void getChildIds(Set<Long> deptIds, Long deptId) {
        deptIds.add(deptId);
        LambdaQueryWrapper<SysDept> wrapper = Wrappers.lambdaQuery(SysDept.class)
            .eq(SysDept::getParentId, deptId).select(SysDept::getDeptId);
        List<Long> ids = EntityUtils.toList(list(wrapper), SysDept::getDeptId);
        for (Long id : ids) {
            getChildIds(deptIds, id);
        }
    }

    /**
     * 查询当前节点所有父亲ID（不包含自己）
     *
     * @param deptIds
     * @param deptId
     */
    @Override
    public void getParentIds(Set<Long> deptIds, Long deptId) {
        Long parentId = EntityUtils.toObj(getById(deptId), SysDept::getParentId);
        if (parentId != null) {
            deptIds.add(parentId);
            getParentIds(deptIds, parentId);
        }
    }

    /**
     * 获取非叶子节点的集合
     *
     * @return
     */
    @Override
    public Set<Long> getParentIds() {
        Set<Long> deptIds = new HashSet<>();
        EntityUtils.toList(list(), SysDept::getAncestors).forEach(e -> Arrays.stream(e.split(",")).forEach(f -> deptIds.add(Long.valueOf(f))));
        return deptIds;
    }
}
