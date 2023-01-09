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

package xin.altitude.cms.common.entity;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 锁元数据
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
public class JvmLockMeta {
    /**
     * JVM锁实例
     */
    private Lock lock;
    /**
     * 排队等待加锁时间
     */
    private long waitTime;
    /**
     * 时间单位
     */
    private TimeUnit timeUnit;

    public JvmLockMeta() {
    }

    public JvmLockMeta(Lock lock, long waitTime, TimeUnit timeUnit) {
        this.lock = lock;
        this.waitTime = waitTime;
        this.timeUnit = timeUnit;
    }

    public static JvmLockMeta of(Lock lock, long waitTime, TimeUnit timeUnit) {
        return new JvmLockMeta(lock, waitTime, timeUnit);
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

}
