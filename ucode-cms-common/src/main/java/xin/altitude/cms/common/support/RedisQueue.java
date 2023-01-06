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

import org.springframework.data.redis.core.ListOperations;
import xin.altitude.cms.common.lang.IQueue;
import xin.altitude.cms.common.util.RedisUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 定义以Redis为底层实现的队列
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
public class RedisQueue<E> implements IQueue<E> {
    private final ListOperations<String, E> list;


    public RedisQueue(ListOperations<String, E> list) {
        this.list = list;
    }

    /**
     * 向队列中添加单个元素
     *
     * @param key Key键
     * @param e   元素
     * @return 添加成功返回 <code>true</code>
     */
    @Override
    public boolean add(String key, E e) {
        Long result = list.leftPush(key, e);
        return result != null && result > 0;
    }


    /**
     * 批量向队列中添加元素
     *
     * @param key    Key键
     * @param values 元素集合
     * @return
     */
    @Override
    public boolean addAll(String key, Collection<E> values) {
        Objects.requireNonNull(values);
        Long result = list.leftPushAll(key, values);
        return result != null && result > 0;
    }

    /**
     * 从队列中取出队首元素
     *
     * @param key Key键
     * @return 元素
     */
    @Override
    public E remove(String key) {
        return list.rightPop(key);
    }

    /**
     * 从队列中取出<code>count</code>个队首元素(最多)
     *
     * @return 元素
     */
    @Override
    public List<E> remove(String key, long count) {
        return list.rightPop(key, count);
    }

    /**
     * 阻塞从队列中取出队首元素
     *
     * @return 元素
     */
    @Override
    public E remove(String key, long timeout, TimeUnit unit) {
        return list.rightPop(key, timeout, unit);
    }

    /**
     * 清空队列
     *
     * @param key Key键
     */
    @Override
    public void clear(String key) {
        RedisUtils.remove(key);
    }

    /**
     * 获取队列中实际元素的个数
     *
     * @param key Key键
     * @return 元素个数
     */
    @Override
    public int size(String key) {
        return Optional.ofNullable(list.size(key)).map(Long::intValue).orElse(0);
    }
}
