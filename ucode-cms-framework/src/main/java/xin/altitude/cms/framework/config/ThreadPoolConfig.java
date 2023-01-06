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

package xin.altitude.cms.framework.config;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import xin.altitude.cms.common.util.SpringUtils;
import xin.altitude.cms.core.config.CmsConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
// @ConditionalOnProperty(value = "ucode.thread.enabled", havingValue = "true")
public class ThreadPoolConfig {
    /**
     * 定时任务线程池名称
     */
    public static final String SCHEDULED_POOL_NAME = "CMS_SCHEDULED_POOL";
    /**
     * 定时任务线程池名称
     */
    public static final String FIXED_POOL_NAME = "CMS_FIXED_POOL";
    /**
     * 定时任务线程池名称
     */
    public static final String CACHED_POOL_NAME = "CMS_CACHED_POOL";

    /**
     * 外部配置线程池对象
     */
    private final CmsConfig.Thread thread = SpringUtils.getBean(CmsConfig.class).getThread();


    /**
     * 执行周期性或定时任务
     *
     * @return ScheduledExecutorService
     */
    @Bean(name = SCHEDULED_POOL_NAME)
    // @ConditionalOnProperty(value = "ucode.thread.scheduleEnabled", havingValue = "true")
    protected ScheduledExecutorService scheduledThreadPool() {
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("scheduled-thread-pool-%d").daemon(true).build();
        return new ScheduledThreadPoolExecutor(thread.getCorePoolSize(), threadFactory);
    }

    /**
     * 固定大小线程池
     *
     * @return ThreadPoolExecutor
     */
    @Bean(FIXED_POOL_NAME)
    @ConditionalOnProperty(value = "ucode.thread.fixEnabled", havingValue = "true")
    public ThreadPoolExecutor fixedThreadPool() {
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("fixed-thread-pool-%d").daemon(true).build();
        return new ThreadPoolExecutor(thread.getCorePoolSize(), thread.getCorePoolSize(), 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), threadFactory);
    }

    /**
     * 可伸缩大小线程池
     *
     * @return ExecutorService
     */
    @Bean(CACHED_POOL_NAME)
    @ConditionalOnProperty(value = "ucode.thread.cacheEnabled", havingValue = "true")
    public ExecutorService cachedThreadPool() {
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("cached-thread-pool-%d").daemon(true).build();
        return new ThreadPoolExecutor(thread.getCorePoolSize(), thread.getMaxPoolSize(), thread.getKeepAliveSeconds(), TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactory);
    }
}
