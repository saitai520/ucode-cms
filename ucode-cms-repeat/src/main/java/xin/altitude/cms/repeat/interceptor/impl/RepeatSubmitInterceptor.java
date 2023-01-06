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

package xin.altitude.cms.repeat.interceptor.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import xin.altitude.cms.common.constant.Constants;
import xin.altitude.cms.common.util.RedisUtils;
import xin.altitude.cms.repeat.annotation.RepeatSubmit;
import xin.altitude.cms.repeat.filter.RepeateRequestWrapper;
import xin.altitude.cms.repeat.interceptor.AbstractRepeatSubmitInterceptor;
import xin.altitude.cms.repeat.util.HttpHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 判断请求url和数据是否和上一次相同，
 * 如果和上次相同，则是重复提交表单。 有效时间为10秒内。
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
public class RepeatSubmitInterceptor extends AbstractRepeatSubmitInterceptor {
    private static final Logger log = LoggerFactory.getLogger(RepeatSubmitInterceptor.class);
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation) throws JsonProcessingException {
        String nowParams = null;
        if (request instanceof RepeateRequestWrapper) {
            RepeateRequestWrapper repeatedlyRequest = (RepeateRequestWrapper) request;
            nowParams = HttpHelper.getBodyString(repeatedlyRequest);
        }

        // body参数为空，获取Parameter的数据
        if (nowParams == null) {
            nowParams = objectMapper.writeValueAsString(request.getParameterMap());
        }

        String submitKey = request.getRequestURI() + "-" + nowParams;
        // 唯一标识（指定key + 消息头）
        String cacheRepeatKey = Constants.REPEAT_SUBMIT_KEY + submitKey;

        Boolean bool = RedisUtils.saveIfAbsent(cacheRepeatKey, Constants.LOGIN_SUCCESS, annotation.interval(), TimeUnit.SECONDS);
        boolean result = bool != null && !bool;
        if (result) {
            log.warn(String.format("当前请求重复提交，缓存Key为【%s】", cacheRepeatKey));
        }
        return result;
    }
}
