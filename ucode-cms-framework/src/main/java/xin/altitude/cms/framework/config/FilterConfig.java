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

package xin.altitude.cms.framework.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import xin.altitude.cms.common.util.SpringUtils;
import xin.altitude.cms.common.util.StringUtil;
import xin.altitude.cms.core.config.CmsConfig;
import xin.altitude.cms.framework.filter.XssFilter;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter配置
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
// @Configuration
public class FilterConfig {
    private final CmsConfig.Xss xss = SpringUtils.getBean(CmsConfig.class).getXss();

    @Bean
    @ConditionalOnProperty(value = "ucode.xss.enabled", havingValue = "true")
    public FilterRegistrationBean<Filter> xssFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns(StringUtil.split(xss.getUrlPatterns(), ","));
        registration.setName("xssFilter");
        registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("excludes", xss.getExcludes());
        registration.setInitParameters(initParameters);
        return registration;
    }

    // @Bean
    // public FilterRegistrationBean<Filter> someFilterRegistration() {
    //     FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
    //     // registration.setFilter(new RepeatableFilter());
    //     registration.addUrlPatterns("/*");
    //     registration.setName("repeatableFilter");
    //     registration.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);
    //     return registration;
    // }

}
