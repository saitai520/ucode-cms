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

package xin.altitude.cms.common.constant;

import xin.altitude.cms.common.util.EntityUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
public class RedisConstants {
    /**
     * 缓存前缀
     */
    public static String CACHE_PREFIX = "CACHE";
    /**
     * 锁前缀
     */
    public static String LOCK_PREFIX = "LOCK";
    /**
     * 消息前缀
     */
    public static String MSG_PREFIX = "MSG";
    /**
     * 队列前缀
     */
    public static String QUEUE_PREFIX = "QUEUE";

    private RedisConstants() {
    }

    /**
     * 创建缓存Key
     *
     * @param clazz 被缓存数据的Class对象
     * @param id    被缓存数据的主键ID
     * @param <T>   被缓存数据的类型
     * @return 字符串
     */
    public static <T> String createCacheKey(Class<T> clazz, Serializable id) {
        return String.format("%s:%s:%s", CACHE_PREFIX, clazz.getName(), id);
    }

    /**
     * 创建缓存Key
     *
     * @param ids   被缓存数据的主键ID
     * @param <T>   被缓存数据的类型
     * @param clazz 被缓存数据的Class对象
     * @return 字符串
     */
    public static <T> List<String> createCacheKey(Class<T> clazz, Collection<? extends Serializable> ids) {
        return EntityUtils.toList(ids, e -> createCacheKey(clazz, e));
    }

    /**
     * 创建表锁Key
     *
     * @param className 被锁实体类名
     * @param <T>       被锁数据的类型
     * @return 字符串
     */
    public static <T> String createLockKey(String className) {
        return String.format("%s:%s", LOCK_PREFIX, className);
    }

    /**
     * 创建行锁Key
     *
     * @param clazz 被锁数据的Class对象
     * @param id    被锁数据的主键ID
     * @param <T>   被锁数据的类型
     * @return 字符串
     */
    public static <T> String createLockKey(Class<T> clazz, Serializable id) {
        return String.format("%s:%s:%s", LOCK_PREFIX, clazz.getName(), id);
    }

    /**
     * 创建队列Key
     *
     * @param clazz 被队列数据的Class对象
     * @param <T>   被队列数据的类型
     * @return 字符串
     */
    public static <T> String createQueueKey(Class<T> clazz) {
        return String.format("%s:%s", QUEUE_PREFIX, clazz.getName());
    }
}
