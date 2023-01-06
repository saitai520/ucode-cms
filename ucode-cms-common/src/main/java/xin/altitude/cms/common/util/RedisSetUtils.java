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
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.util.Pair;

import java.util.Collection;
import java.util.Set;

/**
 * {@link RedisSetUtils}工具类
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 1.6.2
 */
public class RedisSetUtils {
    private static final Logger logger = LoggerFactory.getLogger(RedisSetUtils.class);
    private static final StringRedisTemplate STRING_REDIS_TEMPLATE = SpringUtils.getBean(StringRedisTemplate.class);
    private static final ZSetOperations<String, String> OPS_FOR_ZSET = STRING_REDIS_TEMPLATE.opsForZSet();

    private RedisSetUtils() {
    }

    /**
     * 向ZSET中添加单条元素
     *
     * @param key   ZSET对应的Key
     * @param value Value对象
     * @param score 分值
     * @return 成功true；失败false；异常null
     */
    public static Boolean add(String key, String value, double score) {
        return OPS_FOR_ZSET.add(key, value, score);
    }

    /**
     * 向ZSET中添加单条元素
     *
     * @param key   ZSET对应的Key
     * @param value Value对象
     * @param score 分值
     * @return 成功true；失败false；异常null
     */
    public static Boolean add(String key, String value, long score) {
        return add(key, value, new Double(score));
    }


    public static Long add(String key, Collection<Pair<String, Double>> pairs) {
        Set<ZSetOperations.TypedTuple<String>> tuples = EntityUtils.toSet(pairs, e -> ZSetOperations.TypedTuple.of(e.getFirst(), e.getSecond()));
        return OPS_FOR_ZSET.add(key, tuples);
    }

    public static Long add(String key, Set<ZSetOperations.TypedTuple<String>> tuples) {
        return OPS_FOR_ZSET.add(key, tuples);
    }

    /**
     * 按照分数从低到高的顺序取出指定范围内的Value对象集合
     *
     * @param key ZSET对应的Key
     * @param min score最小值
     * @param max score最大值
     * @return Value对象集合
     */
    public static Set<String> rangeByScore(String key, double min, double max) {
        return OPS_FOR_ZSET.rangeByScore(key, min, max);
    }

    /**
     * 批量移除ZSET中多个member
     *
     * @param key    ZSET对应的Key
     * @param values Value对象
     * @return 成功移除的个数
     */
    public static Long remove(String key, Object... values) {
        return OPS_FOR_ZSET.remove(key, values);
    }


    /**
     * 清空ZSET当前Key
     *
     * @param key ZSET对应的Key
     */
    public static Boolean remove(String key) {
        return RedisUtils.remove(key);
    }

}
