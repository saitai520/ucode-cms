/*
 * Copyright (c) 2021. 北京流深数据科技有限公司
 */

package xin.altitude.cms.quartz.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;


/**
 * 耗时统计，借助AOP统一处理切片逻辑
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue">UCode CMS</a>
 */
@Aspect
public class QuartzAspect {
    /**
     * 统计请求的处理时间
     */
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 带有注解的方法
     */
    @Pointcut("execution(* org.quartz.Job.execute(..))")
    public void time() {

    }


    @Around("time()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        /* 方法被调用执行前 */
        startTime.set(System.currentTimeMillis());
        point.proceed();
        /* 方法被调用执行后 */
        Long a = startTime.get();
        long b = System.currentTimeMillis();
        startTime.remove();
        System.out.println("l = " + (b - a));
        return null;
    }
}
