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
import xin.altitude.cms.common.util.EntityUtils;
import xin.altitude.cms.common.util.StringUtil;
import xin.altitude.cms.framework.constant.UserConstants;
import xin.altitude.cms.framework.core.domain.SysUser;
import xin.altitude.cms.framework.exception.ServiceException;
import xin.altitude.cms.system.domain.SysPost;
import xin.altitude.cms.system.domain.SysUserPost;
import xin.altitude.cms.system.mapper.SysPostMapper;
import xin.altitude.cms.system.service.ISysPostService;
import xin.altitude.cms.system.service.ISysUserPostService;
import xin.altitude.cms.system.service.ISysUserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 岗位信息 服务层处理
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
// @Service
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements ISysPostService {
    // @Autowired
    // private SysPostMapper postMapper;

    // @Autowired
    // private SysUserPostMapper userPostMapper;
    @Autowired
    private ISysUserPostService sysUserPostService;

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 查询岗位信息集合
     *
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    @Override
    public List<SysPost> selectPostList(SysPost post) {
        // return postMapper.selectPostList(post);
        return list(Wrappers.lambdaQuery(post));
    }

    /**
     * 查询所有岗位
     *
     * @return 岗位列表
     */
    @Override
    public List<SysPost> selectPostAll() {
        // return postMapper.selectPostAll();
        return list();
    }

    /**
     * 通过岗位ID查询岗位信息
     *
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    @Override
    public SysPost selectPostById(Long postId) {
        // return postMapper.selectPostById(postId);
        return getById(postId);
    }

    /**
     * 根据用户ID获取岗位选择框列表
     *
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    @Override
    public List<Long> selectPostListByUserId(Long userId) {
        List<SysUserPost> userPosts = sysUserPostService.list(Wrappers.lambdaQuery(SysUserPost.class).eq(SysUserPost::getUserId, userId));
        return EntityUtils.toList(userPosts, SysUserPost::getPostId);
        // return postMapper.selectPostListByUserId(userId);
    }

    /**
     * 校验岗位名称是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public String checkPostNameUnique(SysPost post) {
        Long postId = StringUtil.isNull(post.getPostId()) ? -1L : post.getPostId();
        // SysPost info = postMapper.checkPostNameUnique(post.getPostName());
        SysPost info = getOne(Wrappers.lambdaQuery(SysPost.class).eq(SysPost::getPostName, post.getPostName()));
        if (StringUtil.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验岗位编码是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public String checkPostCodeUnique(SysPost post) {
        Long postId = StringUtil.isNull(post.getPostId()) ? -1L : post.getPostId();
        // SysPost info = postMapper.checkPostCodeUnique(post.getPostCode());
        SysPost info = getOne(Wrappers.lambdaQuery(SysPost.class).eq(SysPost::getPostCode, post.getPostCode()));
        if (StringUtil.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 通过岗位ID查询岗位使用数量
     *
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public long countUserPostById(Long postId) {
        // return userPostMapper.countUserPostById(postId);
        return sysUserPostService.count(Wrappers.lambdaQuery(SysUserPost.class).eq(SysUserPost::getPostId, postId));
    }

    /**
     * 删除岗位信息
     *
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public boolean deletePostById(Long postId) {
        // return postMapper.deletePostById(postId);
        return removeById(postId);
    }

    /**
     * 批量删除岗位信息
     *
     * @param postIds 需要删除的岗位ID
     * @return 结果
     */
    @Override
    public boolean deletePostByIds(Long[] postIds) {
        for (Long postId : postIds) {
            SysPost post = getById(postId);
            if (countUserPostById(postId) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", post.getPostName()));
            }
        }
        // return postMapper.deletePostByIds(postIds);
        return removeByIds(Arrays.asList(postIds));
    }

    /**
     * 新增保存岗位信息
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean insertPost(SysPost post) {
        // return postMapper.insertPost(post);
        return save(post);
    }

    /**
     * 修改保存岗位信息
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean updatePost(SysPost post) {
        // return postMapper.updatePost(post);
        return updateById(post);
    }

    @Override
    public List<SysPost> selectPostsByUserName(String userName) {
        SysUser sysUser = sysUserService.getOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUserName, userName));
        Long userId = EntityUtils.toObj(sysUser, SysUser::getUserId);
        List<SysUserPost> userPosts = sysUserPostService.list(Wrappers.lambdaQuery(SysUserPost.class).eq(SysUserPost::getUserId, userId));
        List<Long> postIds = EntityUtils.toList(userPosts, SysUserPost::getPostId);
        if (postIds.size() > 0) {
            return list(Wrappers.lambdaQuery(SysPost.class).in(SysPost::getPostId, postIds));
        }
        return new ArrayList<>();
    }
}
