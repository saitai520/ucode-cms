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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * <p>配置类</p>
 * <p>业务端通过重写{@link QueueServiceImpl#createReqConfig}方法来修改配置信息，否则使用默认值</p>
 * <p>
 * 属性{@link ReqConfig#maxReqSize}和{@link ReqConfig#reqInterval}控制请求合并与拆分访问数据库的行为 核心参数
 * </p>
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @see QueueServiceImpl#createReqConfig()
 */
public class ReqConfig {
    /**
     * 单次合并最大请求大小 默认值100 最大不要超过1000
     */
    private Integer maxReqSize = 100;
    /**
     * 合并请求间隔（单位毫秒）
     */
    private Integer reqInterval = 100;
    /**
     * 线程工厂核心线程数
     */
    private Integer corePoolSize = 5;

    private ScheduledExecutorService threadPool;

    private ThreadFactory threadFactory = Executors.defaultThreadFactory();

    public ReqConfig() {
    }

    public ReqConfig(Integer maxReqSize, Integer reqInterval) {
        this.maxReqSize = maxReqSize;
        this.reqInterval = reqInterval;
    }

    public ReqConfig(Integer maxReqSize, Integer reqInterval, ScheduledExecutorService threadPool) {
        this.maxReqSize = maxReqSize;
        this.reqInterval = reqInterval;
        this.threadPool = threadPool;
    }

    public ReqConfig(Integer maxReqSize, Integer reqInterval, Integer corePoolSize) {
        this.maxReqSize = maxReqSize;
        this.reqInterval = reqInterval;
        this.corePoolSize = corePoolSize;
    }

    public ReqConfig(Integer maxReqSize, Integer reqInterval, Integer corePoolSize, ThreadFactory threadFactory) {
        this.maxReqSize = maxReqSize;
        this.reqInterval = reqInterval;
        this.corePoolSize = corePoolSize;
        this.threadFactory = threadFactory;
    }

    public Integer getMaxReqSize() {
        return maxReqSize;
    }

    public void setMaxReqSize(Integer maxReqSize) {
        this.maxReqSize = maxReqSize;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getReqInterval() {
        return reqInterval;
    }

    public void setReqInterval(Integer reqInterval) {
        this.reqInterval = reqInterval;
    }

    public ScheduledExecutorService getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ScheduledExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }
}
