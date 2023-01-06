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

package xin.altitude.cms.common.util;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import xin.altitude.cms.common.constant.RedisConstants;
import xin.altitude.cms.common.entity.JvmLockMeta;
import xin.altitude.cms.common.entity.LockMeta;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

/**
 * MybatisPlus增强工具类
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
public class PlusUtils {


    /**
     * <p>增强MybatisPlus主键查询方法{@link com.baomidou.mybatisplus.core.mapper.BaseMapper#selectById(Serializable)}</p>
     * <p>增强MybatisPlus主键查询方法{@link com.baomidou.mybatisplus.extension.service.IService#getById(Serializable)}</p>
     * <p>使其具备Redis数据缓存功能 提高数据访问效率</p>
     * 普通无缓存查询数据库
     * <pre>
     *     User user = baseMapper.selectById(id);
     *     User user = service.getById(id);
     * </pre>
     * 使用缓存查询数据库
     * <pre>
     *     User user = PlusUtils.ifNotCache(id, User.class, baseMapper::selectById);
     *     User user = PlusUtils.ifNotCache(id, User.class, service::getById);
     * </pre>
     *
     * @param id     主键查询ID
     * @param clazz  DO实体类Class对象
     * @param action 访问数据库的回调
     * @param <E>    主键ID的数据类型
     * @param <T>    DO实体类数据类型
     * @return DO实体类实例
     */
    public static <E extends Serializable, T> T ifNotCache(E id, Class<T> clazz, Function<E, T> action) {
        String key = RedisConstants.createCacheKey(clazz, id);
        T value = RedisUtils.getObject(key, clazz);
        if (value == null) {
            T newValue = action.apply(id);
            Optional.ofNullable(newValue).ifPresent(e -> RedisUtils.save(key, e));
            return newValue;
        }
        return value;
    }

    /**
     * <p>增强MybatisPlus主键查询方法{@link com.baomidou.mybatisplus.core.mapper.BaseMapper#selectById(Serializable)}</p>
     * <p>增强MybatisPlus主键查询方法{@link com.baomidou.mybatisplus.extension.service.IService#getById(Serializable)}</p>
     * <p>使其具备Redis数据缓存功能 提高数据访问效率</p>
     * 普通无缓存查询数据库
     * <pre>
     *     User user = baseMapper.selectById(id);
     *     User user = service.getById(id);
     * </pre>
     * 使用缓存查询数据库
     * <pre>
     *     User user = PlusUtils.ifNotCache(id, User.class, baseMapper::selectById);
     *     User user = PlusUtils.ifNotCache(id, User.class, service::getById);
     * </pre>
     *
     * @param id     主键查询ID
     * @param clazz  DO实体类Class对象
     * @param action 访问数据库的回调
     * @param <E>    主键ID的数据类型
     * @param <T>    DO实体类数据类型
     * @return DO实体类实例
     */
    public static <E extends Serializable, T> T ifNotCache(E id, Class<T> clazz, Function<E, T> action, Lock lock) {
        T value = RedisUtils.getObject(id, clazz);
        if (value == null) {
            T newValue = doVisitDb(id, -1L, clazz, action, lock);
            Optional.ofNullable(newValue).ifPresent(e -> RedisUtils.save(id, e));
            return newValue;
        }
        return value;
    }

    /**
     * <p>增强MybatisPlus主键查询方法{@link com.baomidou.mybatisplus.core.mapper.BaseMapper#selectById(Serializable)}</p>
     * <p>增强MybatisPlus主键查询方法{@link com.baomidou.mybatisplus.extension.service.IService#getById(Serializable)}</p>
     * <p>使其具备Redis数据缓存功能 提高数据访问效率</p>
     * 普通无缓存查询数据库
     * <pre>
     *     User user = baseMapper.selectById(id);
     *     User user = service.getById(id);
     * </pre>
     * 使用缓存查询数据库
     * <pre>
     *     User user = PlusUtils.ifNotCache(id, User.class, baseMapper::selectById);
     *     User user = PlusUtils.ifNotCache(id, User.class, service::getById);
     * </pre>
     *
     * @param id     主键查询ID
     * @param clazz  DO实体类Class对象
     * @param ms     过期时间 毫秒
     * @param action 访问数据库的回调
     * @param <E>    主键ID的数据类型
     * @param <T>    DO实体类数据类型
     * @return DO实体类实例
     */
    public static <E extends Serializable, T> T ifNotCache(E id, Class<T> clazz, long ms, Function<E, T> action) {
        T value = RedisUtils.getObject(id, clazz);
        if (value == null) {
            T newValue = action.apply(id);
            Optional.ofNullable(newValue).ifPresent(e -> RedisUtils.save(id, e, ms, TimeUnit.MILLISECONDS));
            return newValue;
        }
        return value;
    }

    /**
     * <p>增强MybatisPlus主键查询方法{@link com.baomidou.mybatisplus.core.mapper.BaseMapper#selectById(Serializable)}</p>
     * <p>增强MybatisPlus主键查询方法{@link com.baomidou.mybatisplus.extension.service.IService#getById(Serializable)}</p>
     * <p>使其具备Redis数据缓存功能 提高数据访问效率</p>
     * 普通无缓存查询数据库
     * <pre>
     *     User user = baseMapper.selectById(id);
     *     User user = service.getById(id);
     * </pre>
     * 使用缓存查询数据库
     * <pre>
     *     User user = PlusUtils.ifNotCache(id, User.class, baseMapper::selectById);
     *     User user = PlusUtils.ifNotCache(id, User.class, service::getById);
     * </pre>
     *
     * @param id     主键查询ID
     * @param clazz  DO实体类Class对象
     * @param ms     过期时间 毫秒
     * @param action 访问数据库的回调
     * @param <E>    主键ID的数据类型
     * @param <T>    DO实体类数据类型
     * @return DO实体类实例
     */
    public static <E extends Serializable, T> T ifNotCache(E id, Class<T> clazz, long ms, Function<E, T> action, Lock lock) {
        T value = RedisUtils.getObject(id, clazz);
        if (value == null) {
            T newValue = doVisitDb(id, ms, clazz, action, lock);
            Optional.ofNullable(newValue).ifPresent(e -> RedisUtils.save(id, e, ms, TimeUnit.MILLISECONDS));
            return newValue;
        }
        return value;
    }

    /**
     * <p>增强MybatisPlus主键查询方法{@link com.baomidou.mybatisplus.core.mapper.BaseMapper#selectById(Serializable)}</p>
     * <p>增强MybatisPlus主键查询方法{@link com.baomidou.mybatisplus.extension.service.IService#getById(Serializable)}</p>
     * <p>使其具备Redis数据缓存功能 提高数据访问效率</p>
     * 普通无缓存查询数据库
     * <pre>
     *     User user = baseMapper.selectById(id);
     *     User user = service.getById(id);
     * </pre>
     * 使用无锁缓存查询数据库
     * <pre>
     *     User user = PlusUtils.ifNotCache(id, User.class, baseMapper::selectById);
     *     User user = PlusUtils.ifNotCache(id, User.class, service::getById);
     * </pre>
     * 使用有锁缓存查询数据库 解决缓存穿透问题
     * <pre>
     *     User user = PlusUtils.ifNotCache(id, User.class, 10000, service::getById, redisson);
     * </pre>
     *
     * @param id       主键查询ID
     * @param clazz    DO实体类Class对象
     * @param ms       过期时间 毫秒
     * @param action   访问数据库的回调
     * @param redisson {@link RedissonClient}实例
     * @param <E>      主键ID的数据类型
     * @param <T>      DO实体类数据类型
     * @return DO实体类实例
     */
    public static <E extends Serializable, T> T ifNotCache(E id, Class<T> clazz, long ms, Function<E, T> action, RedissonClient redisson) {
        T value = RedisUtils.getObject(id, clazz);
        if (value == null) {
            return doVisitDb(id, ms, clazz, action, redisson);
        }
        return value;
    }


    /**
     * <p>增强MybatisPlus主键查询方法{@link com.baomidou.mybatisplus.core.mapper.BaseMapper#selectBatchIds(Collection)}</p>
     * <p>增强MybatisPlus主键查询方法{@link com.baomidou.mybatisplus.extension.service.IService#listByIds(Collection)}</p>
     * <p>使其具备Redis数据缓存功能 提高数据访问效率</p>
     * 普通无缓存查询数据库
     * <pre>
     *     User[] users = baseMapper.selectBatchIds(ids);
     *     User[] users = service.listByIds(ids);
     * </pre>
     * 使用缓存查询数据库
     * <pre>
     *     User[] users = PlusUtils.ifNotCache(ids, User.class, baseMapper::selectBatchIds);
     *     User[] users = PlusUtils.ifNotCache(ids, User.class, service::listByIds);
     * </pre>
     *
     * @param ids    批主键查询ID
     * @param clazz  DO实体类Class对象
     * @param ms     过期时间 毫秒 当参数小于0代表不过期
     * @param action 访问数据库的回调
     * @param <T>    DO实体类数据类型
     * @return DO实体类实例
     */
    public static <T> List<T> ifNotCache(Collection<? extends Serializable> ids, Class<T> clazz, long ms, Function<Collection<? extends Serializable>, ? extends List<T>> action) {
        List<String> keys = RedisConstants.createCacheKey(clazz, ids);
        List<T> redisResults = RedisUtils.multiGet(keys, clazz);
        if (redisResults.size() < ids.size()) {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
            if (tableInfo == null) {
                throw new RuntimeException("当前实体类不属于DO层");
            }
            List<? extends Serializable> remainIds = getRemainIds(ids, redisResults, tableInfo);
            if (!remainIds.isEmpty()) {
                List<T> newValue = action.apply(remainIds);
                if (!newValue.isEmpty()) {
                    Map<String, T> map = EntityUtils.toMap(newValue, e -> RedisConstants.createCacheKey(clazz, (String) pkVal(tableInfo, e)), Function.identity());
                    RedisUtils.multiSet(map, clazz);
                    redisResults.addAll(newValue);
                    if (ms > 0) {
                        map.forEach((k, v) -> RedisUtils.expire(k, ms, TimeUnit.MILLISECONDS));
                    }
                }
            }
        }
        return redisResults;
    }

    /**
     * <p>有缓存主键更新数据 重载{@link com.baomidou.mybatisplus.extension.service.IService#updateById(Object)}方法</p>
     *
     * @param entity 实体类对象
     * @param clazz  实体类Class对象
     * @param action 更新操作回调
     * @param <T>    实体类泛型
     * @return 更新操作状态
     */
    public static <T> boolean updateById(T entity, Class<T> clazz, Function<T, Boolean> action) {
        Serializable id = pkVal(entity, clazz);
        String key = RedisConstants.createCacheKey(clazz, id);
        RedisUtils.remove(key);
        boolean result = action.apply(entity);
        RedisUtils.remove(key);
        return result;
    }

    /**
     * <p>有缓存主键更新数据 重载{@link com.baomidou.mybatisplus.extension.service.IService#updateById(Object)}方法</p>
     *
     * @param entity       实体类对象
     * @param clazz        实体类Class对象
     * @param updateAction 更新操作回调
     * @param <T>          实体类泛型
     * @return 更新操作状态
     */
    public static <T> boolean updateById(T entity, Class<T> clazz, Function<T, Boolean> updateAction, Function<Serializable, T> selectAction) {
        Serializable id = pkVal(entity, clazz);

        boolean result = updateAction.apply(entity);
        BooleanUtils.ifTrue(result, () -> RedisUtils.save(id, selectAction.apply(id)));
        return result;
    }

    /**
     * <p>有缓存主键更新数据 重载{@link com.baomidou.mybatisplus.extension.service.IService#updateById(Object)}方法</p>
     *
     * @param entity       实体类对象
     * @param clazz        实体类Class对象
     * @param updateAction 更新操作回调
     * @param selectAction 主键查询操作回调
     * @param <T>          实体类泛型
     * @return 更新操作状态
     */
    public static <T> boolean updateById(T entity, Class<T> clazz, Function<T, Boolean> updateAction, Function<Serializable, T> selectAction, Lock lock) {
        // 获取实体类主键值
        Serializable id = pkVal(entity, clazz);
        JvmLockMeta meta = JvmLockMeta.of(lock, 3, TimeUnit.SECONDS);
        return LockUtils.tryLock(meta, () -> {
            boolean result = updateAction.apply(entity);
            BooleanUtils.ifTrue(result, () -> RedisUtils.save(id, selectAction.apply(id)));
            return result;
        });

    }

    public static <T> boolean updateById(T entity, Class<T> clazz, Function<T, Boolean> action, RedissonClient redisson) {
        Objects.requireNonNull(redisson);
        Serializable id = pkVal(entity, clazz);
        String cacheKey = RedisConstants.createCacheKey(clazz, id);
        String lockKey = RedisConstants.createLockKey(clazz, id);
        RLock lock = redisson.getLock(lockKey);
        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                RedisUtils.remove(cacheKey);
                boolean result = action.apply(entity);
                RedisUtils.remove(cacheKey);
                return result;
            }
            return false;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            Optional.ofNullable(lock).ifPresent(RLock::unlock);
        }
    }

    /**
     * <p>有缓存主键删除数据</p>
     */
    public static <T> boolean removeById(Serializable id, Class<T> clazz, Function<Serializable, Boolean> action) {
        String key = RedisConstants.createCacheKey(clazz, id);
        boolean result = action.apply(id);
        RedisUtils.remove(key);
        return result;
    }

    /**
     * <p>有缓存主键删除</p>
     */
    public static <T> boolean removeById(T entity, Class<T> clazz, Function<T, Boolean> action) {
        String key = RedisConstants.createCacheKey(clazz, pkVal(entity, clazz));
        boolean result = action.apply(entity);
        RedisUtils.remove(key);
        return result;
    }

    /**
     * <p>有缓存批量删除</p>
     */
    public static <T> boolean removeByIds(Collection<? extends Serializable> idList, Class<T> clazz, Function<Collection<String>, Boolean> action) {
        Set<String> keys = EntityUtils.toSet(idList, e -> RedisConstants.createCacheKey(clazz, e));
        boolean result = action.apply(keys);
        RedisUtils.remove(keys);
        return result;
    }


    /**
     * 具体查询数据库逻辑
     */
    private static <E extends Serializable, T> T doVisitDb(E id, long ms, Class<T> clazz, Function<E, T> action, RedissonClient redisson) {
        Objects.requireNonNull(redisson);
        String cacheKey = RedisConstants.createCacheKey(clazz, id);
        String lockKey = RedisConstants.createLockKey(clazz, id);
        RLock lock = redisson.getLock(lockKey);
        LockMeta meta = LockMeta.of(lock, 5, 10, TimeUnit.SECONDS);
        return LockUtils.tryLock(meta, () -> noLockVisitDb(id, ms, clazz, action, cacheKey));
    }


    /**
     * 具体查询数据库逻辑
     */
    private static <E extends Serializable, T> T doVisitDb(E id, long ms, Class<T> clazz, Function<E, T> action, Lock lock) {
        String cacheKey = RedisConstants.createCacheKey(clazz, id);
        JvmLockMeta meta = JvmLockMeta.of(lock, 5, TimeUnit.SECONDS);
        return LockUtils.tryLock(meta, () -> noLockVisitDb(id, ms, clazz, action, cacheKey));
    }

    private static <E extends Serializable, T> T noLockVisitDb(E id, long ms, Class<T> clazz, Function<E, T> action, String cacheKey) {
        // 再次检查一下Redis里面有没有数据 有的话直接返回 没有再取查询DB
        T value = RedisUtils.getObject(cacheKey, clazz);
        // 如果Redis中查询到数据 直接返回 否则执行查询数据库操作
        if (value != null) {
            return value;
        } else {
            T t = action.apply(id);
            if (ms < 0) {
                Optional.ofNullable(t).ifPresent(e -> RedisUtils.save(cacheKey, e));
            } else {
                Optional.ofNullable(t).ifPresent(e -> RedisUtils.save(cacheKey, e, ms, TimeUnit.MILLISECONDS));
            }
            return t;
        }
    }


    /**
     * 获取当前DO实体类主键值
     *
     * @param tableInfo 表信息实例
     * @param e         DO实体类
     * @param <E>       DO实体类类型
     * @param <R>       主键的类型
     * @return 主键值
     */
    @SuppressWarnings("unchecked")
    public static <E, R extends Serializable> R pkVal(TableInfo tableInfo, E e) {
        String keyProperty = tableInfo.getKeyProperty();
        return (R) tableInfo.getPropertyValue(e, keyProperty);
    }

    /**
     * 获取当前DO实体类主键值
     *
     * @param entity 实体类实例
     * @param clazz  实体类Class对象
     * @param <T>    实体类类型
     * @return 主键值
     */
    public static <T> Serializable pkVal(T entity, Class<T> clazz) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        return pkVal(tableInfo, entity);
    }

    /**
     * 找出差异值 类型不确定 具体实现还是挺复杂的
     */
    private static <T> List<? extends Serializable> getRemainIds(Collection<? extends Serializable> ids, List<T> ts, TableInfo tableInfo) {
        Serializable id = ColUtils.toObj(ids);
        if (id instanceof String) {
            List<String> tsIds = EntityUtils.toList(ts, e -> pkVal(tableInfo, e));
            List<String> remainIds = EntityUtils.toList(ids, e -> (String) e);
            remainIds.removeAll(tsIds);
            return remainIds;
        } else if (id instanceof Number) {
            List<String> tsIds = EntityUtils.toList(ts, e -> String.format("%s", (Serializable) pkVal(tableInfo, e)));
            List<String> remainIds = EntityUtils.toList(ids, String::valueOf);
            remainIds.removeAll(tsIds);
            return remainIds;
        }

        return Collections.emptyList();
    }

}

