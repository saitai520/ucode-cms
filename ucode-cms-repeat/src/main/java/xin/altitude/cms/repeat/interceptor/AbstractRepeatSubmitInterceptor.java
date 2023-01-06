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

package xin.altitude.cms.repeat.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import xin.altitude.cms.common.entity.AjaxResult;
import xin.altitude.cms.common.util.ServletUtils;
import xin.altitude.cms.repeat.annotation.RepeatSubmit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * 防止重复提交拦截器
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
public abstract class AbstractRepeatSubmitInterceptor implements HandlerInterceptor {
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 请求处理链
     *
     * @param request  请求
     * @param response 响应
     * @param handler
     * @return boolean
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            RepeatSubmit annotation = Optional.of((HandlerMethod) handler).map(HandlerMethod::getMethod).map(e -> e.getAnnotation(RepeatSubmit.class)).orElse(null);
            if (annotation != null && isRepeatSubmit(request, annotation)) {
                AjaxResult ajaxResult = AjaxResult.error(annotation.message());
                ServletUtils.renderString(response, objectMapper.writeValueAsString(ajaxResult));
                return false;
            }
            return true;
        } else {
            return true;
        }
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     *
     * @param request
     * @return
     */
    public abstract boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation) throws JsonProcessingException;
}
