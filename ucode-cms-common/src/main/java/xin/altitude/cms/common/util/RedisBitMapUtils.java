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
import org.springframework.data.redis.core.ValueOperations;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * {@link RedisBitMapUtils}工具类
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 1.6.0
 */
public class RedisBitMapUtils {
    private static final Logger logger = LoggerFactory.getLogger(RedisBitMapUtils.class);

    private static final StringRedisTemplate STRING_REDIS_TEMPLATE = SpringUtils.getBean(StringRedisTemplate.class);
    private static final ValueOperations<String, String> OPS_FOR_VALUE = STRING_REDIS_TEMPLATE.opsForValue();

    /**
     * <p>本方法在首次初始化以{@code key}为参数的BitMap时执行</p>
     * <p>首先删除Key 然后重新构建BitMap</p>
     *
     * @param <T> 主键类型
     * @param key 每种业务分别对应不同的Key名称
     * @param ids 主键ID
     */
    public static <T extends Serializable> void init(String key, Collection<T> ids) {
        remove(key);
        setBit(key, ids);
    }

    /**
     * <p>本方法在首次初始化以{@code key}为参数的BitMap时执行</p>
     * <p>首先删除Key 然后重新构建BitMap</p>
     *
     * @param key    每种业务分别对应不同的Key名称
     * @param list   实体类对象集合
     * @param action 主键列（方法引用表示）
     * @param <T>    实体类泛型
     * @param <R>    主键列泛型
     */
    public static <T, R extends Serializable> void init(String key, Collection<T> list, Function<T, R> action) {
        List<R> ids = EntityUtils.toList(list, action);
        init(key, ids);
    }

    /**
     * 检查当前主键ID在Redis BitMap中是否存在 如果存在则执行函数式回调
     *
     * @param key 每种业务分别对应不同的Key名称
     * @param id  主键ID
     * @return {@code R}实例
     */
    public static <T extends Serializable, R> R ifPresent(String key, T id, Function<T, R> action) {
        if (getBit(key, id)) {
            return action.apply(id);
        }
        return null;
    }

    /**
     * 检查当前主键ID在Redis BitMap中是否存在 如果存在则执行函数式回调
     *
     * @param key 每种业务分别对应不同的Key名称
     * @param id  主键ID
     * @return {@code R}实例
     */
    public static <T extends Serializable, R> R ifPresent(String key, T id, Supplier<R> supplier) {
        if (getBit(key, id)) {
            return supplier.get();
        }
        return null;
    }

    /**
     * 检查当前主键ID在Redis BitMap中是否存在 如果存在则返回<code>true</code>
     *
     * @param key 每种业务分别对应不同的Key名称
     * @param id  主键ID
     * @return 如果存在则返回<code>true</code>
     */
    public static <T extends Serializable> boolean isPresent(String key, T id) {
        return getBit(key, id);
    }

    /**
     * 检查当前主键ID在Redis BitMap中是否存在 如果存在则返回<code>true</code>
     * 本方法是{@link RedisBitMapUtils#getBit(String, Serializable)}的别名方法 方便对外调用
     *
     * @param key 每种业务分别对应不同的Key名称
     * @param id  主键ID
     * @return 如果存在则返回<code>true</code>
     */
    public static <T extends Serializable> boolean checkId(String key, T id) {
        return getBit(key, id);
    }

    /**
     * 向Redis BitMap中保存主键ID
     *
     * @param key 每种业务分别对应不同的Key名称
     * @param id  主键ID
     */
    public static <T extends Serializable> void setBit(String key, T id) {
        ifOffsetValid(Objects.hash(id), e -> OPS_FOR_VALUE.setBit(key, e, true));
    }

    /**
     * 向Redis BitMap中批量保存主键ID
     *
     * @param <T> 主键类型
     * @param key 每种业务分别对应不同的Key名称
     * @param ids 主键ID
     */
    public static <T extends Serializable> void setBit(String key, Collection<T> ids) {
        ids.forEach(id -> ifOffsetValid(Objects.hash(id), e -> OPS_FOR_VALUE.setBit(key, e, true)));
    }

    /**
     * 检查当前主键ID在Redis BitMap中是否存在 如果存在则返回<code>true</code>
     *
     * @param key 每种业务分别对应不同的Key名称
     * @param id  主键ID
     * @return 如果存在则返回<code>true</code>
     */
    public static <T extends Serializable> boolean getBit(String key, T id) {
        return ifOffsetValid(Objects.hash(id), e -> OPS_FOR_VALUE.getBit(key, e));
    }


    /**
     * 从Redis BitMap中删除当前主键ID
     *
     * @param key 每种业务分别对应不同的Key名称
     * @param id  主键ID
     */
    public static <T extends Serializable> void removeBit(String key, T id) {
        ifOffsetValid(Objects.hash(id), e -> OPS_FOR_VALUE.setBit(key, e, false));
    }

    /**
     * 从Redis BitMap中批量删除主键ID
     *
     * @param key 每种业务分别对应不同的Key名称
     * @param ids 主键ID
     * @param <T> 主键类型
     */
    public static <T extends Serializable> void removeBit(String key, Collection<T> ids) {
        ids.forEach(id -> ifOffsetValid(Objects.hash(id), e -> OPS_FOR_VALUE.setBit(key, e, false)));
    }


    /**
     * 将当前分类下的BitMap Key删除
     * 清空该Key下所有数据
     */
    public static void remove(String key) {
        RedisUtils.remove(key);
    }

    /**
     * <p>检查偏移量是否合法</p>
     * <p>Redis字符串支持字符串最大长度512M，因此支持offset的最大值为(2^32)-1</p>
     *
     * @param offset 偏移量
     * @param action 映射规则
     */
    private static <N extends Number> Boolean ifOffsetValid(N offset, Function<N, Boolean> action) {
        Objects.requireNonNull(action);

        //如果ID用整型表示 那么正整数范围内所有的ID均有效 最大正整数值为2147483647 约为20亿
        long max = (1L << 32) - 1;
        if (offset.intValue() >= 0 && offset.intValue() < Integer.MAX_VALUE) {
            return action.apply(offset);
        } else {
            if (Integer.MAX_VALUE >= 0 && offset.longValue() <= max) {
                return action.apply(offset);
            } else {
                logger.info(String.format("偏移量{%d}越界[0,%s]，本次操作不成功！", offset.longValue(), max));
                return false;
            }
        }

    }
}
