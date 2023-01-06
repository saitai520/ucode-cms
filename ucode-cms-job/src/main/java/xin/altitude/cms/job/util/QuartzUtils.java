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

package xin.altitude.cms.job.util;

import org.quartz.*;
import xin.altitude.cms.job.constant.JobStatus;
import xin.altitude.cms.job.constant.ScheduleConstants;
import xin.altitude.cms.job.domain.SysJob;
import xin.altitude.cms.job.exception.TaskException;

/**
 * 定时任务工具类
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
public class QuartzUtils {
    /**
     * 得到quartz任务类
     *
     * @param sysJob 执行计划
     * @return 具体执行任务类
     */
    private static Class<? extends Job> getQuartzJobClass(SysJob sysJob) {
        boolean isConcurrent = "0".equals(sysJob.getConcurrent());
        return isConcurrent ? QuartzJobExecution.class : QuartzDisallowConcurrentExecution.class;
    }

    /**
     * 构建任务触发对象
     */
    public static TriggerKey createTriggerKey(Long jobId, String jobGroup) {
        return TriggerKey.triggerKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 构建任务键对象
     *
     * @param jobId    任务ID（用户自定义编号（一般唯一））
     * @param jobGroup 任务组别
     * @return JobKey
     */
    public static JobKey createJobKey(Long jobId, String jobGroup) {
        return JobKey.jobKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 创建定时任务
     *
     * @param scheduler
     * @param sysJob
     */
    public static void createScheduleJob(Scheduler scheduler, SysJob sysJob) {
        /* 构造QZ Job */
        Class<? extends Job> jobClass = getQuartzJobClass(sysJob);
        // 构建job信息
        Long jobId = sysJob.getJobId();
        String jobGroup = sysJob.getJobGroup();
        JobKey jobKey = createJobKey(jobId, jobGroup);
        /* 创建JobDetail */
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobKey).build();

        // 表达式调度构建器
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(sysJob.getCronExpression());

        handleCronScheduleMisfirePolicy(sysJob, cronScheduleBuilder);

        // 按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(createTriggerKey(jobId, jobGroup))
            .withSchedule(cronScheduleBuilder).build();

        // 放入参数，运行时的方法可以获取
        jobDetail.getJobDataMap().put(ScheduleConstants.TASK_PROPERTIES, sysJob);

        // 判断是否存在
        try {
            if (scheduler.checkExists(jobKey)) {
                // 防止创建时存在数据问题 先移除，然后在执行创建操作
                scheduler.deleteJob(jobKey);
            }
            scheduler.scheduleJob(jobDetail, trigger);

            if (sysJob.getStatus().equals(JobStatus.PAUSE.getValue())) {
                /* 暂停任务 */
                scheduler.pauseJob(jobKey);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置定时任务策略
     * 默认
     * 立即触发执行
     * 触发一次执行
     * 不触发立即执行
     */
    public static void handleCronScheduleMisfirePolicy(SysJob sysJob, CronScheduleBuilder cb) {
        switch (sysJob.getMisfirePolicy()) {
            case ScheduleConstants.MISFIRE_DEFAULT:
                return;
            case ScheduleConstants.MISFIRE_IGNORE_MISFIRES:
                cb.withMisfireHandlingInstructionIgnoreMisfires();
                return;
            case ScheduleConstants.MISFIRE_FIRE_AND_PROCEED:
                cb.withMisfireHandlingInstructionFireAndProceed();
                return;
            case ScheduleConstants.MISFIRE_DO_NOTHING:
                cb.withMisfireHandlingInstructionDoNothing();
                return;
            default:
        }
    }
}
