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

import xin.altitude.cms.common.function.PlainCaller;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 用于批处理结果处理
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
public class BooleanUtils {
    private BooleanUtils() {
    }

    /**
     * <p>存在一个<code>false</code>即为<code>false</code></p>
     * <p>只有全部为<code>true</code>才为<code>true</code></p>
     *
     * @param coll {@link Boolean}集合实例
     */
    public static boolean isTrue(Collection<Boolean> coll) {
        if (ColUtils.isNotEmpty(coll)) {
            boolean bool = true;
            for (Boolean b : coll) {
                if (b == null) {
                    return false;
                }
                bool = Boolean.logicalAnd(bool, b);
            }
            return bool;
        }
        return false;
    }

    public static boolean notNull(Object... objs) {
        boolean r = true;
        for (Object obj : objs) {
            r &= Objects.nonNull(obj);
        }
        return r;
    }

    public static void ifTrue(boolean bool, PlainCaller caller) {
        Objects.requireNonNull(caller);
        if (bool) {
            caller.handle();
        }
    }

    public static <T> void ifTrue(T t, Predicate<T> predicate, Consumer<T> consumer) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(consumer);
        if (predicate.test(t)) {
            consumer.accept(t);
        }
    }
}
