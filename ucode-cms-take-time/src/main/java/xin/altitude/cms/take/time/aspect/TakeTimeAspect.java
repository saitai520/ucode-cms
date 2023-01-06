/*
 * Copyright (c) 2021. 北京流深数据科技有限公司
 */

package xin.altitude.cms.take.time.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import xin.altitude.cms.common.entity.AjaxResult;


/**
 * 耗时统计，借助AOP统一处理切片逻辑
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
@Aspect
public class TakeTimeAspect {
    /**
     * 统计请求的处理时间
     */
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 带有@TakeTime注解的方法
     */
    @Pointcut("@annotation(xin.altitude.cms.take.time.annotation.TakeTime)")
    public void time() {

    }

    @Before("time()")
    public void doBefore(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
    }

    @AfterReturning(returning = "ajaxResult", pointcut = "time()")
    public void doAfterReturning(AjaxResult ajaxResult) {
        long l = System.currentTimeMillis() - startTime.get();
        ajaxResult.put("time", String.format("当前API接口耗时{%s}", calTime(l)));
        startTime.remove();
    }

    private String calTime(long time) {
        if (time < 100) {
            return String.format("%s毫秒", time);
        } else {
            return String.format("%s秒", time*1.0 / 1000);
        }
    }
}
