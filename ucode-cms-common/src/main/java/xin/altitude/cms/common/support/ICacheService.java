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

import com.baomidou.mybatisplus.extension.service.IService;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.transaction.annotation.Transactional;
import xin.altitude.cms.common.util.PlusUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * 增强{@link IService} 用于实现Redis分布式缓存功能
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
public interface ICacheService<T> extends IService<T> {
    /**
     * 重载{@link IService#removeById(Serializable)}方法 增加Redis分布式缓存功能
     * 使用事务是为了保证数据库与Redis双写数据的一致性
     *
     * @param id    主键ID
     * @param clazz 实体类Class对象（参数为了方法的重载 不参与实际业务逻辑）
     * @return true表示删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean removeById(Serializable id, Class<?> clazz) {
        return PlusUtils.removeById(id, getEntityClass(), this::removeById);
    }

    /**
     * 重载{@link IService#removeById(Object)}方法 增加Redis分布式缓存功能
     * 使用事务是为了保证数据库与Redis双写数据的一致性
     *
     * @param entity {@code T}实体类对象
     * @param clazz  实体类Class对象（参数为了方法的重载 不参与实际业务逻辑）
     * @return true表示删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean removeById(T entity, Class<?> clazz) {
        return PlusUtils.removeById(entity, getEntityClass(), this::removeById);
    }

    /**
     * 重载{@link IService#removeByIds(Collection)}方法 增加Redis分布式缓存功能
     * 使用事务是为了保证数据库与Redis双写数据的一致性
     *
     * @param idList 批主键ID
     * @param clazz  实体类Class对象（参数为了方法的重载 不参与实际业务逻辑）
     * @return true表示删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean removeByIds(Collection<? extends Serializable> idList, Class<?> clazz) {
        return PlusUtils.removeByIds(idList, getEntityClass(), this::removeByIds);
    }

    /**
     * <p>重载{@link IService#updateById(Object)}方法 增加Redis分布式缓存功能</p>
     * <p>使用事务是为了保证数据库与Redis缓存双写数据的一致性</p>
     * <p>使用JVM锁默认并发不高，否则请使用分布式锁</p>
     * <p>此处使用表锁 即为每一个服务类分配一把可重入锁 采用懒汉单例模式</p>
     *
     * @param entity DO实体类对象
     * @param clazz  实体类Class对象（参数为了方法的重载 不参与实际业务逻辑）
     * @return true表示更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean updateById(T entity, Class<?> clazz) {
        // 此处使用表锁 并发更新时 写与读均阻塞
        Class<T> entityClass = getEntityClass();
        return PlusUtils.updateById(entity, entityClass, this::updateById, this::getById);
    }

    /**
     * <p>重载{@link IService#updateById(Object)}方法 增加Redis分布式缓存功能</p>
     * <p>使用事务是为了保证数据库与Redis缓存双写数据的一致性</p>
     * <p>使用JVM锁默认并发不高，否则请使用分布式锁</p>
     * <p>此处使用表锁 即为每一个服务类分配一把可重入锁 采用懒汉单例模式</p>
     *
     * @param entity DO实体类对象
     * @param clazz  实体类Class对象（参数为了方法的重载 不参与实际业务逻辑）
     * @param lock   如果置空则使用默认可重入锁，否则使用参数给定的锁
     * @return true表示更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean updateById(T entity, Class<?> clazz, Lock lock) {
        Lock optLock = lock != null ? lock : getJvmLock();
        // 此处使用表锁 并发更新时 写与读均阻塞
        Class<T> entityClass = getEntityClass();
        return PlusUtils.updateById(entity, entityClass, this::updateById, this::getById, optLock);
    }

    /**
     * <p>重载{@link IService#getById(Serializable)}方法 增加Redis分布式缓存功能</p>
     * <p>有缓存；无过期时间；适合并发较低的场景</p>
     *
     * @param id    主键ID
     * @param clazz 实体类Class对象（参数为了方法的重载 不参与实际业务逻辑）
     * @return {@code T}实体类对象实例
     */
    default T getById(Serializable id, Class<?> clazz) {
        return PlusUtils.ifNotCache(id, getEntityClass(), this::getById, getJvmLock());
    }

    /**
     * 重载{@link IService#getById(Serializable)}方法 增加Redis分布式缓存功能
     * <p>有缓存；有过期时间；适合并发较低的场景</p>
     *
     * @param id 主键ID
     * @param ms 过期时间 毫秒
     * @return {@code T}实体类对象实例
     */
    default T getById(Serializable id, long ms) {
        return PlusUtils.ifNotCache(id, getEntityClass(), ms, this::getById, getJvmLock());
    }

    /**
     * <p>重载{@link IService#getById(Serializable)}方法 增加Redis分布式缓存功能</p>
     * <p>增加分布式锁的支持 防止高并发场景下<i>缓存穿透</i></p>
     *
     * @param id       主键ID
     * @param ms       过期时间 毫秒
     * @param redisson {@link Redisson}实例
     * @return {@code T}实体类对象实例
     */
    default T getById(Serializable id, long ms, RedissonClient redisson) {
        return PlusUtils.ifNotCache(id, getEntityClass(), ms, this::getById, redisson);
    }

    /**
     * 重载{@link IService#getById(Serializable)}方法 增加Redis分布式缓存功能
     *
     * @param id    主键ID
     * @param ms    过期时间 毫秒
     * @param clazz 实体类Class对象（参数为了方法的重载 不参与实际业务逻辑）
     * @return {@code T}实体类对象实例
     */
    default T getById(Serializable id, long ms, Class<?> clazz) {
        return PlusUtils.ifNotCache(id, getEntityClass(), ms, this::getById, getJvmLock());
    }


    /**
     * 重载{@link IService#listByIds(Collection)}方法 增加Redis分布式缓存功能
     *
     * @param ids   批主键ID
     * @param clazz 实体类Class对象（参数为了方法的重载 不参与实际业务逻辑）
     * @return 以{@code T}实体类为元素的集合实例
     */
    default List<T> listByIds(Collection<? extends Serializable> ids, Class<?> clazz) {
        return PlusUtils.ifNotCache(ids, getEntityClass(), -1, this::listByIds);
    }

    /**
     * 重载{@link IService#listByIds(Collection)}方法 增加Redis分布式缓存功能
     *
     * @param ids 批主键ID
     * @param ms  过期时间 毫秒
     * @return 以{@code T}实体类为元素的集合实例
     */
    default List<T> listByIds(Collection<? extends Serializable> ids, long ms) {
        return PlusUtils.ifNotCache(ids, getEntityClass(), ms, this::listByIds);
    }

    /**
     * 重载{@link IService#listByIds(Collection)}方法 增加Redis分布式缓存功能
     *
     * @param ids   批主键ID
     * @param clazz 实体类Class对象（参数为了方法的重载 不参与实际业务逻辑）
     * @param ms    过期时间 毫秒
     * @return 以{@code T}实体类为元素的集合实例
     */
    default List<T> listByIds(Collection<? extends Serializable> ids, long ms, Class<?> clazz) {
        return PlusUtils.ifNotCache(ids, getEntityClass(), ms, this::listByIds);
    }


    /**
     * 获取实体类表锁
     *
     * @return 重入锁实例
     */
    default Lock getJvmLock() {
        return Singleton.getLocalLock(getEntityClass());
    }
}
