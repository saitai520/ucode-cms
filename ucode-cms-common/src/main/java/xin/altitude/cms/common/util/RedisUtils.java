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

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import xin.altitude.cms.common.constant.RedisConstants;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * {@link RedisUtils}工具类
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 1.4
 */
public class RedisUtils {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    private static final StringRedisTemplate STRING_REDIS_TEMPLATE = SpringUtils.getBean(StringRedisTemplate.class);
    private static final ValueOperations<String, String> OPS_FOR_VALUE = STRING_REDIS_TEMPLATE.opsForValue();

    /**
     * 缓存基本的对象
     *
     * @param id    缓存的主键值 使用{@link RedisConstants#createCacheKey(Class, Serializable)}转换成Key
     * @param value 缓存的值
     */
    public static <T> void save(final Serializable id, final T value) {
        String key = RedisConstants.createCacheKey(value.getClass(), id);
        if (value instanceof String) {
            OPS_FOR_VALUE.set(key, (String) value);
        } else {
            Optional.ofNullable(JacksonUtils.writeValue(value)).ifPresent(e -> OPS_FOR_VALUE.set(key, e));
        }
    }

    /**
     * <p>缓存基本的对象，Integer、String、实体类等 如果value是String类型，直接直接保存 如果不是，则转化序列化承JSON字符串后保存</p>
     * <p>缓存对象值支持多种类型：普通单个实体类对象、{@link List}实体类集合、{@link IPage}分页对象、{@link Map}实例对象</p>
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public static void save(final String key, final Object value) {
        if (value instanceof String) {
            OPS_FOR_VALUE.set(key, (String) value);
        } else {
            Optional.ofNullable(JacksonUtils.writeValue(value)).ifPresent(e -> OPS_FOR_VALUE.set(key, e));
        }
    }

    /**
     * <p>缓存基本的对象，Integer、String、实体类等 如果value是String类型，直接直接保存 如果不是，则转化序列化承JSON字符串后保存</p>
     * <p>支持指定过期时间</p>
     * <p>缓存对象值支持多种类型：普通单个实体类对象、{@link List}实体类集合、{@link IPage}分页对象、{@link Map}实例对象</p>
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @param sec   过期时间 单位秒
     */
    public static void save(final String key, final Object value, final int sec) {
        save(key, value, sec, TimeUnit.SECONDS);
    }


    /**
     * <p>缓存基本的对象</p>
     *
     * @param id       缓存的主键ID用以形成唯一key
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public static <T> void save(final Serializable id, final T value, final long timeout, final TimeUnit timeUnit) {
        String key = RedisConstants.createCacheKey(value.getClass(), id);
        if (value instanceof String) {
            OPS_FOR_VALUE.set(key, (String) value, timeout, timeUnit);
        } else {
            Optional.ofNullable(JacksonUtils.writeValue(value)).ifPresent(e -> OPS_FOR_VALUE.set(key, e, timeout, timeUnit));
        }
    }

    /**
     * <p>缓存基本的对象，Integer、String、实体类等 如果value是String类型，直接直接保存 如果不是，则转化序列化承JSON字符串后保存</p>
     * <p>支持指定过期时间</p>
     * <p>缓存对象值支持多种类型：普通单个实体类对象、{@link List}实体类集合、{@link IPage}分页对象、{@link Map}实例对象</p>
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public static void save(final String key, final Object value, final long timeout, final TimeUnit timeUnit) {
        if (value instanceof String) {
            OPS_FOR_VALUE.set(key, (String) value, timeout, timeUnit);
        } else {
            Optional.ofNullable(JacksonUtils.writeValue(value)).ifPresent(e -> OPS_FOR_VALUE.set(key, e, timeout, timeUnit));
        }
    }

    /**
     * 批量保存数据
     */
    public static <V> void saveBatch(final Map<String, V> map) {
        Map<String, String> transMap = MapUtils.transMap(map, JacksonUtils::writeValue);
        OPS_FOR_VALUE.multiSet(transMap);
    }

    /**
     * 批量保存数据 设置过期时间
     */
    public static <V> void saveBatch(final Map<String, V> map, int timeout, TimeUnit timeUnit) {
        Map<String, String> transMap = MapUtils.transMap(map, JacksonUtils::writeValue);
        OPS_FOR_VALUE.multiSet(transMap);

        if (timeUnit.equals(TimeUnit.SECONDS)) {
            RedisAsyncUtils.expire(map.keySet(), timeout);
        } else if (timeUnit.equals(TimeUnit.MICROSECONDS)) {
            RedisAsyncUtils.pExpire(map.keySet(), timeout);
        }
    }

    /**
     * <p>缓存基本的对象，Integer、String、实体类等 如果value是String类型，直接直接保存 如果不是，则转化序列化承JSON字符串后保存</p>
     * <p>缓存对象值支持多种类型：普通单个实体类对象、{@link List}实体类集合、{@link IPage}分页对象、{@link Map}实例对象</p>
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @return 如果保存成功，则返回true
     */
    public static Boolean saveIfAbsent(final String key, final Object value) {
        if (value instanceof String) {
            return OPS_FOR_VALUE.setIfAbsent(key, (String) value);
        } else {
            return Optional.ofNullable(JacksonUtils.writeValue(value)).map(e -> OPS_FOR_VALUE.setIfAbsent(key, e)).orElse(false);
        }
    }


    /**
     * <p>缓存基本的对象，Integer、String、实体类等 如果value是String类型，直接直接保存 如果不是，则转化序列化承JSON字符串后保存</p>
     * <p>缓存对象值支持多种类型：普通单个实体类对象、{@link List}实体类集合、{@link IPage}分页对象、{@link Map}实例对象</p>
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @param sec   过期时间 单位秒
     * @return 如果保存成功，则返回true
     */
    public static Boolean saveIfAbsent(final String key, final Object value, final Integer sec) {
        return saveIfAbsent(key, value, sec, TimeUnit.SECONDS);
    }


    /**
     * <p>缓存基本的对象，Integer、String、实体类等 如果value是String类型，直接直接保存 如果不是，则转化序列化承JSON字符串后保存</p>
     * <p>缓存对象值支持多种类型：普通单个实体类对象、{@link List}实体类集合、{@link IPage}分页对象、{@link Map}实例对象</p>
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     * @return 如果保存成功，则返回true
     */
    public static Boolean saveIfAbsent(final String key, final Object value, final long timeout, final TimeUnit timeUnit) {
        // 参数检查 保证参数有意义
        long min = Long.min(timeout, 0);
        if (value instanceof String) {
            return OPS_FOR_VALUE.setIfAbsent(key, (String) value, min, timeUnit);
        } else {
            return Optional.ofNullable(JacksonUtils.writeValue(value)).map(e -> OPS_FOR_VALUE.setIfAbsent(key, e, min, timeUnit)).orElse(false);
        }
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param sec 超时时间
     * @return true=设置成功；false=设置失败
     */
    public static Boolean expire(final String key, final Long sec) {
        return expire(key, sec, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public static Boolean expire(final String key, final Long timeout, final TimeUnit unit) {
        return STRING_REDIS_TEMPLATE.expire(key, timeout, unit);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public static <T> T getObject(final String key, final Class<T> clazz) {
        return Optional.ofNullable(OPS_FOR_VALUE.get(key)).map(e -> JacksonUtils.readObjectValue(e, clazz)).orElse(null);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param id 主键值 使用{@link RedisConstants#createCacheKey(Class, Serializable)}转换成Key
     * @return 缓存键值对应的数据
     */
    public static <T> T getObject(final Serializable id, final Class<T> clazz) {
        String key = RedisConstants.createCacheKey(clazz, id);
        return getObject(key, clazz);
    }

    /**
     * 将JSON字符串转化成集合对象
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> getList(final String key, final Class<T> clazz) {
        return Optional.ofNullable(OPS_FOR_VALUE.get(key)).map(e -> JacksonUtils.readListValue(e, clazz)).orElse(Collections.emptyList());
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public static Boolean remove(final String key) {
        return STRING_REDIS_TEMPLATE.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param keys 多个Key
     * @return
     */
    public static Long remove(final Collection<String> keys) {
        return STRING_REDIS_TEMPLATE.delete(keys);
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public static Set<String> getCacheSet(final String key) {
        return STRING_REDIS_TEMPLATE.opsForSet().members(key);
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public static Long setCacheList(final String key, final List<String> dataList) {
        Long count = STRING_REDIS_TEMPLATE.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public static BoundSetOperations<String, String> setCacheSet(final String key, final Set<String> dataSet) {
        BoundSetOperations<String, String> setOperation = STRING_REDIS_TEMPLATE.boundSetOps(key);
        for (String s : dataSet) {
            setOperation.add(s);
        }
        return setOperation;
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public static void setCacheMap(final String key, final Map<String, String> dataMap) {
        if (dataMap != null) {
            STRING_REDIS_TEMPLATE.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public static Map<Object, Object> getCacheMap(final String key) {
        return STRING_REDIS_TEMPLATE.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public static void setCacheMapValue(final String key, final String hKey, final String value) {
        STRING_REDIS_TEMPLATE.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public static String getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, String> opsForHash = STRING_REDIS_TEMPLATE.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 删除Hash中的数据
     *
     * @param key
     * @param hkey
     */
    public static void delCacheMapValue(final String key, final String hkey) {
        HashOperations<String, String, Object> hashOperations = STRING_REDIS_TEMPLATE.opsForHash();
        hashOperations.delete(key, hkey);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public static Collection<String> keys(final String pattern) {
        return STRING_REDIS_TEMPLATE.keys(pattern);
    }


    /**
     * 批量保存
     *
     * @param map
     */
    public static void multiSet(Map<String, String> map) {
        OPS_FOR_VALUE.multiSet(map);
    }

    /**
     * 批量保存
     *
     * @param map
     * @param clazz
     * @param <T>
     */
    public static <T> void multiSet(Map<String, T> map, Class<T> clazz) {
        Map<String, String> stringMap = MapUtils.transMap(map, e -> e, JacksonUtils::writeValue);
        OPS_FOR_VALUE.multiSet(stringMap);
    }

    /**
     * 批量查询Key对应的Value值
     *
     * @param keys 批量Keys
     * @return Value值集合
     */
    public static List<String> multiGet(List<String> keys) {
        return EntityUtils.toList(OPS_FOR_VALUE.multiGet(keys), Function.identity(), Objects::nonNull);
    }

    /**
     * 批量查询Key对应的Value值
     *
     * @param keys 批量Keys
     * @return Value值集合
     */
    public static <T> List<T> multiGet(List<String> keys, Class<T> clazz) {
        return EntityUtils.toList(multiGet(keys), e -> JacksonUtils.readObjectValue(e, clazz), Objects::nonNull);
    }

    /**
     * 向channel发布消息
     * 如果消息是String类型，直接发送消息；
     * 如果不是，则转化序列化承JSON后发送消息
     *
     * @param channelName channel名称
     * @param msg         消息
     * @since 1.4.5
     */
    public static <T> void publishMsg(final String channelName, T msg) {
        if (msg instanceof String) {
            STRING_REDIS_TEMPLATE.convertAndSend(channelName, msg);
        } else {
            /* 现将对象格式化为JSON字符串，然后保存 */
            String json = JacksonUtils.writeValueAsString(msg);
            Optional.ofNullable(json).ifPresent(e -> STRING_REDIS_TEMPLATE.convertAndSend(channelName, e));
        }
    }
}
