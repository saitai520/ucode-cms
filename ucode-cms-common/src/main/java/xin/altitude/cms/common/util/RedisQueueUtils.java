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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import xin.altitude.cms.common.lang.IQueue;
import xin.altitude.cms.common.support.RedisQueue;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * {@link RedisQueueUtils}工具类
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 1.6.2
 */
public class RedisQueueUtils {
    private static final Logger logger = LoggerFactory.getLogger(RedisQueueUtils.class);
    private static final StringRedisTemplate STRING_REDIS_TEMPLATE = SpringUtils.getBean(StringRedisTemplate.class);

    private static final IQueue<String> QUEUE = new RedisQueue<>(STRING_REDIS_TEMPLATE.opsForList());

    /**
     * 向队列中添加单个元素
     *
     * @param key Key键
     * @param e   元素
     * @return 添加成功返回 <code>true</code>
     */
    public static <T> boolean add(String key, T e) {
        if (e instanceof String) {
            return QUEUE.add(key, (String) e);
        } else {
            String value = JacksonUtils.writeValueAsString(e);
            return QUEUE.add(key, value);
        }
    }

    /**
     * 批量向队列中添加元素
     *
     * @param key    Key键
     * @param values 元素集合
     */
    public static <T> boolean addAll(String key, Collection<T> values) {
        if (ColUtils.toObj(values) instanceof String) {
            List<String> newValue = EntityUtils.toList(values, e -> (String) e);
            return QUEUE.addAll(key, newValue);
        } else {
            List<String> newValue = EntityUtils.toList(values, JacksonUtils::writeValueAsString);
            return QUEUE.addAll(key, newValue);
        }
    }

    /**
     * 从队列中取出队首元素
     *
     * @param key Key键
     * @return 元素
     */
    public static String remove(String key) {
        return QUEUE.remove(key);
    }

    /**
     * 从队列中取出队首元素
     *
     * @param key Key键
     * @return 元素
     */
    public static <T> T remove(String key, Class<T> clazz) {
        String value = QUEUE.remove(key);
        return JacksonUtils.readObjectValue(value, clazz);
    }

    /**
     * 从队列中取出<code>count</code>个队首元素(最多)
     *
     * @return 元素
     */
    public static List<String> remove(String key, long count) {
        return QUEUE.remove(key, count);
    }

    /**
     * 从队列中取出<code>count</code>个队首元素(最多)
     *
     * @return 元素
     */
    public static <T> List<T> remove(String key, long count, Class<T> clazz) {
        List<String> values = QUEUE.remove(key, count);
        return EntityUtils.toList(values, e -> JacksonUtils.readObjectValue(e, clazz));
    }

    /**
     * 阻塞从队列中取出队首元素
     *
     * @return 元素
     */
    public static String remove(String key, long timeout, TimeUnit unit) {
        return QUEUE.remove(key, timeout, unit);
    }

    /**
     * 清空队列
     *
     * @param key Key键
     */
    public static void clear(String key) {
        QUEUE.clear(key);
    }

    /**
     * 获取队列中实际元素的个数
     *
     * @param key Key键
     * @return 元素个数
     */
    public static int size(String key) {
        return QUEUE.size(key);
    }

}
