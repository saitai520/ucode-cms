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

import org.redisson.api.RLock;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 此类数据分布式Redis场景下的工具类
 * 目前有效方法是ifLocked
 * 用以替换如下语句
 * if(locl.isLock()){
 * locl.unlocked();
 * }
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 1.4
 */
public final class LockOptional<T extends RLock> {
    private static final LockOptional<?> EMPTY = new LockOptional<>();

    private final T value;

    private LockOptional() {
        this.value = null;
    }

    private LockOptional(T value) {
        this.value = Objects.requireNonNull(value);
    }

    public static <T extends RLock> LockOptional<T> empty() {
        @SuppressWarnings("unchecked")
        LockOptional<T> t = (LockOptional<T>) EMPTY;
        return t;
    }

    public static <T extends RLock> LockOptional<T> of(T value) {
        return new LockOptional<>(value);
    }

    public static <T extends RLock> LockOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * 如果当前锁尚未释放，则执行后续操作
     *
     * @param consumer 消费者lambda表达式
     */
    public void ifLocked(Consumer<? super T> consumer) {
        if (value != null && value.isLocked()) {
            consumer.accept(value);
        }
    }

    public T orElse(T other) {
        return value != null ? value : other;
    }


    public T orElseGet(Supplier<? extends T> other) {
        return value != null ? value : other.get();
    }


    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof LockOptional)) {
            return false;
        }

        LockOptional<?> other = (LockOptional<?>) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value != null
            ? String.format("LockOptional[%s]", value)
            : "LockOptional.empty";
    }
}
