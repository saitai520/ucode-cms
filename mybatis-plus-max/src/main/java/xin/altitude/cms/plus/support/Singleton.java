/*
 *
 * Copyright (c) 2020-2023, Java知识图谱 (http://www.altitude.xin).
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

package xin.altitude.cms.plus.support;

import xin.altitude.cms.common.constant.RedisConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 单例模式类
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
public class Singleton {
    private static final Map<String, Lock> LOCK_MAP = new HashMap<>();

    private Singleton() {
    }

    /**
     * 使用双重检查·单例模式为每个Service服务类创建一个表锁
     *
     * @param entityClass 实体类Class对象
     * @return 本地锁实例
     */
    public static Lock getLocalLock(Class<?> entityClass) {
        Objects.requireNonNull(entityClass);
        return getLocalLock(entityClass.getName());
    }

    /**
     * 使用双重检查·单例模式为每个Service服务类创建一个表锁
     *
     * @param className 实体类名称
     * @return 本地锁实例
     */
    public static Lock getLocalLock(String className) {
        Objects.requireNonNull(className);
        String key = RedisConstants.createLockKey(className);
        if (LOCK_MAP.get(key) == null) {
            synchronized (Singleton.class) {
                if (LOCK_MAP.get(key) == null) {
                    // 创建一个可重入锁
                    LOCK_MAP.put(key, new ReentrantLock());
                }
            }
        }
        return LOCK_MAP.get(key);
    }
}
