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

package xin.altitude.cms.merge;

import com.baomidou.mybatisplus.core.metadata.TableInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
class MergeHelper {

    /**
     * <p>从队列中取出指定数量的{@code AsynFuture}元素，返回到集合中</p>
     * <p>如果队列{@code queue}的实际元素个数小于{@code maxRequestSize}阈值，则使用实际大小</p>
     *
     * @param queue          队列实例
     * @param maxRequestSize 指定数量
     * @return 集合实例
     */
    public static <E> List<E> pollFuture(Queue<E> queue, int maxRequestSize) {
        Objects.requireNonNull(queue);
        int size = Math.min(queue.size(), maxRequestSize);
        List<E> lists = IntStream.range(0, size).mapToObj(i -> queue.poll())
            .collect(Collectors.toCollection(() -> new ArrayList<>(size)));
        return lists;
    }

    public static Serializable transformKey(Class<?> keyType, Serializable key) {
        if (keyType.isAssignableFrom(String.class)) {
            return String.valueOf(key);
        } else if (keyType.isAssignableFrom(Long.class) && key instanceof Number) {
            return ((Number) key).longValue();
        } else if (keyType.isAssignableFrom(Integer.class) && key instanceof Number) {
            return ((Number) key).intValue();
        }

        return key;
    }

    public static <E> E fetchMapValue(Map<Serializable, E> map, Class<?> keyType, Serializable key) {
        return map.get(transformKey(keyType, key));
    }
}
