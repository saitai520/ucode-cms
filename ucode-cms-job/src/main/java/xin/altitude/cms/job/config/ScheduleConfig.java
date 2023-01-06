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

package xin.altitude.cms.job.config;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 定时任务配置
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
public class ScheduleConfig {
    public static final String SCHEDULER_FACTORYBEAN = "schedulerFactoryBean";

    /**
     * SchedulerFactoryBean
     *
     * @param dataSource 数据库表
     * @return SchedulerFactoryBean
     */
    @Bean(SCHEDULER_FACTORYBEAN)
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        /* 获取定时任务配置 */
        // CmsConfig.Job job = SpringUtils.getBean(CmsConfig.class).getJob();
        // if (job.getPersist()) {
        //     factory.setDataSource(dataSource);
        // }
        //
        Properties prop = getProperties(false);
        factory.setQuartzProperties(prop);

        factory.setSchedulerName("UCodeCmsScheduler");
        // 延时启动
        factory.setStartupDelay(1);
        factory.setApplicationContextSchedulerContextKey("applicationContextKey");
        // 可选，QuartzScheduler
        // 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        factory.setOverwriteExistingJobs(true);
        // 设置自动启动，默认为true
        factory.setAutoStartup(true);

        return factory;
    }

    /**
     * 获取Quartz属性配置
     *
     * @param persist 是否持久化到数据库
     * @return Properties
     */
    private Properties getProperties(boolean persist) {
        /* quartz参数 */
        Properties prop = new Properties();
        prop.put("org.quartz.scheduler.instanceName", "UCodeCmsScheduler");
        prop.put("org.quartz.scheduler.instanceId", "AUTO");
        // 线程池配置
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        prop.put("org.quartz.threadPool.threadCount", "20");
        prop.put("org.quartz.threadPool.threadPriority", "5");
        if (persist) {
            // JobStore配置
            prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
            // 集群配置
            prop.put("org.quartz.jobStore.isClustered", "true");
            prop.put("org.quartz.jobStore.clusterCheckinInterval", "15000");
            prop.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1");
            prop.put("org.quartz.jobStore.txIsolationLevelSerializable", "true");

            prop.put("org.quartz.jobStore.misfireThreshold", "12000");
            prop.put("org.quartz.jobStore.tablePrefix", "qrtz_");
        } else {
            // JobStore配置
            prop.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        }

        return prop;
    }
}
