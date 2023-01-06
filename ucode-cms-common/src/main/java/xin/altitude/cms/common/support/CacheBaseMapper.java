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

package xin.altitude.cms.common.support;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import xin.altitude.cms.common.constant.RedisConstants;
import xin.altitude.cms.common.util.EntityUtils;
import xin.altitude.cms.common.util.PlusUtils;
import xin.altitude.cms.common.util.RedisUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static xin.altitude.cms.common.util.PlusUtils.pkVal;

/**
 * 增强{@link BaseMapper} 用于实现Redis分布式缓存功能
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
public interface CacheBaseMapper<T> extends BaseMapper<T> {

    /**
     * 重载{@link BaseMapper#selectById(Serializable)}方法 增加Redis分布式缓存功能
     *
     * @param id    主键ID
     * @param clazz {@code T}实体类Class对象
     * @return {@code T}实体类对象
     */
    default T selectById(Serializable id, Class<T> clazz) {
        return PlusUtils.ifNotCache(id, clazz, this::selectById);
    }

    /**
     * 重载{@link BaseMapper#selectById(Serializable)}方法 增加Redis分布式缓存功能
     *
     * @param id    主键ID
     * @param clazz {@code T}实体类Class对象
     * @param ms    过期时间
     * @return {@code T}实体类对象
     */
    default T selectById(Serializable id, Class<T> clazz, long ms) {
        return PlusUtils.ifNotCache(id, clazz, ms, this::selectById);
    }

    /**
     * 重载{@link BaseMapper#selectBatchIds(Collection)}方法 增加Redis分布式缓存功能
     *
     * @param idList 批主键ID
     * @param clazz  {@code T}实体类Class对象
     * @return 以{@code T}实体类为元素的集合实例
     */
    default List<T> selectBatchIds(Collection<? extends Serializable> idList, Class<T> clazz) {
        return PlusUtils.ifNotCache(idList, clazz, -1, this::selectBatchIds);
    }

    /**
     * 重载{@link BaseMapper#selectBatchIds(Collection)}方法 增加Redis分布式缓存功能
     *
     * @param idList 批主键ID
     * @param clazz  {@code T}实体类Class对象
     * @param ms     过期时间
     * @return 以{@code T}实体类为元素的集合实例
     */
    default List<T> selectBatchIds(Collection<? extends Serializable> idList, Class<T> clazz, long ms) {
        return PlusUtils.ifNotCache(idList, clazz, ms, this::selectBatchIds);
    }

    /**
     * 重载{@link BaseMapper#updateById(Object)} 增加Redis分布式缓存功能
     *
     * @param entity DO实体类对象
     * @param clazz  {@code T}实体类Class对象
     * @return 大于0表示更新成功
     */
    default int updateById(T entity, Class<T> clazz) {
        String key = RedisConstants.createCacheKey(clazz, pkVal(entity, clazz));
        RedisUtils.remove(key);
        int result = updateById(entity);
        RedisUtils.remove(key);
        return result;
    }

    /**
     * 重载{@link BaseMapper#deleteById(Object)}方法 增加Redis分布式缓存功能
     *
     * @param entity DO实体类对象
     * @param clazz  {@code T}实体类Class对象
     * @return 大于0表示删除成功
     */
    default int deleteById(T entity, Class<T> clazz) {
        String key = RedisConstants.createCacheKey(clazz, pkVal(entity, clazz));
        int result = deleteById(entity);
        RedisUtils.remove(key);
        return result;
    }

    /**
     * 重载{@link BaseMapper#deleteBatchIds(Collection)}方法 增加Redis分布式缓存功能
     *
     * @param idList 主键ID列表
     * @param clazz  {@code T}实体类Class对象
     * @return 大于0表示删除成功
     */
    default int deleteBatchIds(Collection<? extends Serializable> idList, Class<T> clazz) {
        Set<String> keys = EntityUtils.toSet(idList, e -> RedisConstants.createCacheKey(clazz, e));
        int result = deleteBatchIds(keys);
        RedisUtils.remove(keys);
        return result;
    }
}
