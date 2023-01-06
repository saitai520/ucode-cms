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

package xin.altitude.cms.log.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import xin.altitude.cms.common.util.JacksonUtils;
import xin.altitude.cms.log.domain.OperateLog;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * 此类须由子类继承，并注入到Spring容器中
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2021/03/21 13:28
 **/
public class DefaultOperateLogListener implements MessageListener {
    protected static final Logger log = LoggerFactory.getLogger(DefaultOperateLogListener.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String s = new String(message.getBody(), StandardCharsets.UTF_8);
        OperateLog operateLog = JacksonUtils.readObjectValue(s, OperateLog.class);
        /* 如果数据存在，则保存数据 */
        Optional.ofNullable(operateLog).ifPresent(this::saveData);
    }

    /**
     * 实现入库等其它操作
     * 默认情况下空消费
     *
     * @param operateLog
     */
    protected void saveData(OperateLog operateLog) {
        log.debug(operateLog.toString());
    }
}
