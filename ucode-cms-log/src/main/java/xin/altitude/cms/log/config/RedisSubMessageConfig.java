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

package xin.altitude.cms.log.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import xin.altitude.cms.common.util.ColUtils;
import xin.altitude.cms.common.util.SpringUtils;
import xin.altitude.cms.log.aspectj.LogAspect;
import xin.altitude.cms.log.listener.DefaultOperateLogListener;

import java.util.List;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2021/03/21 13:37
 **/
@Import({DefaultOperateLogListener.class})
public class RedisSubMessageConfig {

    // @Autowired
    // private DefaultRedisOperateLogListener listener;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        /* 订阅topic - subscribe */
        redisMessageListenerContainer.addMessageListener(dynamicListener(), new ChannelTopic(LogAspect.CHANNEL_NAME));
        return redisMessageListenerContainer;
    }

    /**
     * 动态选择监听器
     *
     * @return MessageListener
     */
    private MessageListener dynamicListener() {
        List<MessageListener> beans = SpringUtils.getBeans(MessageListener.class);
        if (beans.size() != 1) {
            for (MessageListener bean : beans) {
                if (!DefaultOperateLogListener.class.equals(bean.getClass())) {
                    return bean;
                }
            }
        }
        return ColUtils.toObj(beans);
    }
}


