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

package xin.altitude.cms.limiter.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import xin.altitude.cms.common.constant.Constants;
import xin.altitude.cms.common.entity.AjaxResult;
import xin.altitude.cms.common.util.ColUtils;
import xin.altitude.cms.common.util.ServletUtils;
import xin.altitude.cms.common.util.SpringUtils;
import xin.altitude.cms.limiter.annotation.RateLimiter;
import xin.altitude.cms.limiter.config.LimitRedisTemplate;
import xin.altitude.cms.limiter.config.RedisScriptConfig;
import xin.altitude.cms.limiter.config.RedisTemplateConfig;
import xin.altitude.cms.limiter.enums.LimitType;
import xin.altitude.cms.common.util.IpUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2021/03/19 10:10
 **/
@Import({RedisScriptConfig.class, RedisTemplateConfig.class})
public class LimitInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LimitInterceptor.class);

    private LimitRedisTemplate redisTemplate = SpringUtils.getBean(LimitRedisTemplate.class);

    @Autowired
    private RedisScript<Boolean> redisScript;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            /* 把handler强转为HandlerMethod */
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            /* 从handlerMethod中获取本次请求的接口方法对象然后判断该方法上是否标有我们自定义的注解@RateLimiter */
            RateLimiter rateLimiter = handlerMethod.getMethod().getAnnotation(RateLimiter.class);
            if (null != rateLimiter) {
                int threshold = rateLimiter.threshold();
                int ttl = rateLimiter.ttl();

                List<String> keys = ColUtils.toCol(getCombineKey(rateLimiter, handlerMethod.getMethod()));
                List<String> args = Arrays.asList(String.valueOf(threshold), String.valueOf(ttl));
                Boolean result = redisTemplate.execute(redisScript, keys, args.toArray());
                /* 服务被限流 */
                if (result != null && result) {
                    String msg = String.format("访问过于频繁，请稍候再试，当前请求允许访问速率为{%d次/%d秒}", threshold, ttl);
                    log.warn(msg);
                    AjaxResult ajaxResult = AjaxResult.error(msg);
                    ServletUtils.renderString(response, objectMapper.writeValueAsString(ajaxResult));
                    return false;
                }
            }
        }
        return true;
    }

    private String getCombineKey(RateLimiter rateLimiter, Method method) {
        HttpServletRequest request = ServletUtils.getRequest();
        StringBuilder sb = new StringBuilder(rateLimiter.key());
        if (rateLimiter.limitType() == LimitType.IP) {
            sb.append(IpUtils.getIpAddr(request)).append("-");
        } else if (rateLimiter.limitType() == LimitType.USER) {
            String userId = request.getHeader(Constants.JWT_USERID);
            if (userId != null) {
                sb.append(userId).append("-");
            } else {
                sb.append(request.getSession().getId()).append("-");
            }
        }
        Class<?> targetClass = method.getDeclaringClass();
        sb.append(targetClass.getName()).append("-").append(method.getName());
        return sb.toString();
    }
}
