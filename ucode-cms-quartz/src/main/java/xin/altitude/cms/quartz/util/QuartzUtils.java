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

package xin.altitude.cms.quartz.util;

import lombok.SneakyThrows;
import org.quartz.*;
import xin.altitude.cms.common.util.SpringUtils;
import xin.altitude.cms.quartz.annotation.CronExp;
import xin.altitude.cms.quartz.constant.ScheduleConstants;
import xin.altitude.cms.quartz.model.CmsJobBean;

/**
 * 定时任务工具类
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
public class QuartzUtils {
    /**
     * 构建任务触发对象
     */
    public static TriggerKey createTriggerKey(Long jobId) {
        return TriggerKey.triggerKey(String.valueOf(jobId), null);
    }

    public static JobKey createJobKey(Long jobId) {
        return JobKey.jobKey(String.valueOf(jobId), null);
    }

    /**
     * 创建定时任务
     *
     * @param scheduler 任务调度器
     * @param cmsJobBean  任务模型
     */
    @SneakyThrows
    public static void createScheduleJob(Scheduler scheduler, CmsJobBean cmsJobBean) {
        createScheduleJob(scheduler, cmsJobBean.getJobId(), cmsJobBean.getJobClass(), cmsJobBean.getCron());
    }

    public static void createScheduleJob(Scheduler scheduler, Class<? extends Job> jobClass) {
        CronExp annotation = jobClass.getAnnotation(CronExp.class);
        long jobId = annotation.id() == 0 ? System.currentTimeMillis() : annotation.id();
        String cron = annotation.cron();
        createScheduleJob(scheduler, jobId, jobClass, cron);
    }

    /**
     * 如果不执行调度器，则默认从容器中获取
     * 如果不指定任务ID，则使用当前时间戳
     * 如果不指定调度策略，则从任务类注解处获取
     *
     * @param jobClass 任务的类对象
     */
    public static void createScheduleJob(Class<? extends Job> jobClass) {
        Scheduler scheduler = SpringUtils.getBean(ScheduleConstants.SCHEDULE_NAME);
        CronExp annotation = jobClass.getAnnotation(CronExp.class);
        long jobId = annotation.id() == 0 ? System.currentTimeMillis() : annotation.id();
        String cron = annotation.cron();
        createScheduleJob(scheduler, jobId, jobClass, cron);
    }

    /**
     * 如果不执行调度器，则默认从容器中获取
     * 如果不指定任务ID，则使用当前时间戳
     *
     * @param jobClass 任务的类对象
     * @param cron     调度策略
     */
    public static void createScheduleJob(Class<? extends Job> jobClass, String cron) {
        Scheduler scheduler = SpringUtils.getBean(ScheduleConstants.SCHEDULE_NAME);
        long jobId = System.currentTimeMillis();
        createScheduleJob(scheduler, jobId, jobClass, cron);
    }


    /**
     * 如果不执行调度器，则默认从容器中获取
     * 如果不指定调度策略，则从任务类注解处获取
     *
     * @param jobId    任务ID，确保唯一
     * @param jobClass 任务的类对象
     */
    public static void createScheduleJob(Long jobId, Class<? extends Job> jobClass) {
        Scheduler scheduler = SpringUtils.getBean(ScheduleConstants.SCHEDULE_NAME);
        String cron = jobClass.getAnnotation(CronExp.class).cron();
        createScheduleJob(scheduler, jobId, jobClass, cron);
    }


    /**
     * 如果不执行调度器，则默认从容器中获取
     *
     * @param jobId    任务ID，确保唯一
     * @param jobClass 任务的类对象
     * @param cron     调度策略
     */
    public static void createScheduleJob(Long jobId, Class<? extends Job> jobClass, String cron) {
        Scheduler scheduler = SpringUtils.getBean(ScheduleConstants.SCHEDULE_NAME);
        createScheduleJob(scheduler, jobId, jobClass, cron);
    }

    /**
     * 创建定时任务
     *
     * @param scheduler 调度器
     * @param jobId     任务ID，确保唯一
     * @param jobClass  任务的类对象
     * @param cron      调度策略
     */
    public static void createScheduleJob(Scheduler scheduler, Long jobId, Class<? extends Job> jobClass, String cron) {
        JobKey jobKey = createJobKey(jobId);
        /* 创建JobDetail */
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobKey).build();

        /* 表达式调度构建器 */
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        /* 如果任务错过了执行时间，则以当前时间立即触发执行一次，下一次按正常频率执行 */
        cronScheduleBuilder.withMisfireHandlingInstructionFireAndProceed();

        /* 构建trigger */
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(createTriggerKey(jobId)).withSchedule(cronScheduleBuilder).build();

        try {
            // 判断是否存在
            if (scheduler.checkExists(jobKey)) {
                // 防止创建时存在数据问题 先移除，然后在执行创建操作
                scheduler.deleteJob(jobKey);
            }
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
