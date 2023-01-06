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

package xin.altitude.cms.common.lang;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
public interface IQueue<E> {

    /**
     * 向队列中添加单个元素
     *
     * @param key Key键
     * @param e   元素
     * @return 添加成功返回 <code>true</code>
     */
    boolean add(String key, E e);

    /**
     * 批量向队列中添加元素
     *
     * @param key    Key键
     * @param values 元素集合
     * @return
     */
    boolean addAll(String key, Collection<E> values);

    /**
     * 从队列中取出队首元素
     *
     * @param key Key键
     * @return 元素
     */
    E remove(String key);

    /**
     * 从队列中取出<code>count</code>个队首元素(最多)
     *
     * @return 元素
     */
    List<E> remove(String key, long count);

    /**
     * 阻塞从队列中取出队首元素
     *
     * @return 元素
     */
    E remove(String key, long timeout, TimeUnit unit);

    /**
     * 清空队列
     *
     * @param key Key键
     */
    void clear(String key);

    /**
     * 获取队列中实际元素的个数
     *
     * @param key Key键
     * @return 元素个数
     */
    int size(String key);
}
