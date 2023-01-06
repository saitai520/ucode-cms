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

package xin.altitude.cms.quartz.service.impl;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import xin.altitude.cms.common.util.SpringUtils;
import xin.altitude.cms.quartz.constant.ScheduleConstants;
import xin.altitude.cms.quartz.model.JobModel;
import xin.altitude.cms.quartz.service.IQuartzJobService;
import xin.altitude.cms.quartz.util.CronUtils;
import xin.altitude.cms.quartz.util.QuartzUtils;

import javax.annotation.PostConstruct;

/**
 * 定时任务调度信息 服务层
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
public class QuartzJobServiceImpl implements IQuartzJobService {
    /**
     * 调度器（核心参数）
     */
    private final Scheduler scheduler = SpringUtils.getBean(ScheduleConstants.SCHEDULE_NAME);

    /**
     * 初始化并启动任务列表
     */
    @PostConstruct
    public void init() {
        SpringUtils.getBeans(Job.class).forEach(e -> QuartzUtils.createScheduleJob(scheduler, e.getClass()));
    }

    /**
     * 暂停任务
     *
     * @param job 调度信息
     */
    public void pauseJob(JobModel job) throws SchedulerException {
        Long jobId = job.getJobId();
        scheduler.pauseJob(QuartzUtils.createJobKey(jobId));
    }

    /**
     * 恢复任务
     *
     * @param job 调度信息
     */
    public void resumeJob(JobModel job) throws SchedulerException {
        Long jobId = job.getJobId();
        scheduler.resumeJob(QuartzUtils.createJobKey(jobId));
    }

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     * @param job 调度信息
     */
    public void deleteJob(JobModel job) throws SchedulerException {
        Long jobId = job.getJobId();
        scheduler.deleteJob(QuartzUtils.createJobKey(jobId));
    }

    /**
     * 立即运行任务
     *
     * @param job 调度信息
     */
    public void run(JobModel job) {
        Long jobId = job.getJobId();
        // 参数
        JobDataMap dataMap = new JobDataMap();
        // dataMap.put(ScheduleConstants.TASK_PROPERTIES, sysJob);
        try {
            /* 立即运行任务 */
            scheduler.triggerJob(QuartzUtils.createJobKey(jobId), dataMap);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 立即运行任务
     *
     * @param jobId 任务ID
     */
    @Override
    public void run(long jobId) {
        run(jobId, null);
    }

    /**
     * 立即运行任务
     *
     * @param jobId      任务ID
     * @param jobDataMap 参数列表
     */
    @Override
    public void run(long jobId, JobDataMap jobDataMap) {
        try {
            /* 立即运行任务 */
            scheduler.triggerJob(QuartzUtils.createJobKey(jobId), jobDataMap);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增任务（新增任务注意暂停）
     *
     * @param job 调度信息 调度信息
     */
    public void insertJob(JobModel job) {
        QuartzUtils.createScheduleJob(scheduler, job);
    }

    /**
     * 校验cron表达式是否有效
     *
     * @param cronExp 表达式
     * @return 结果
     */
    public boolean checkCronExpressionIsValid(String cronExp) {
        return CronUtils.isValid(cronExp);
    }
}
