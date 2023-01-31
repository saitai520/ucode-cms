package xin.altitude.cms.quartz.demo;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import xin.altitude.cms.quartz.annotation.CronExp;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
@DisallowConcurrentExecution
@CronExp(id = 1, cron = "0/5 * * * * ?")
public class DemoTask implements org.quartz.Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 具体执行任务细节
    }
}
