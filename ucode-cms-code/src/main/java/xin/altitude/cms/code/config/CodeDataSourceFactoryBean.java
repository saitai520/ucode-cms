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

package xin.altitude.cms.code.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2021/03/22 22:29
 **/
public class CodeDataSourceFactoryBean implements FactoryBean<DataSource> {
    @Value("${spring.datasource.druid.slave.enable:false}")
    private boolean enabled;
    @Value("${spring.datasource.druid.slave.url:url}")
    private String url;
    @Value("${spring.datasource.druid.slave.username:username}")
    private String username;
    @Value("${spring.datasource.druid.slave.password:password}")
    private String password;


    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Override
    public DataSource getObject() {
        DruidDataSource defaultDruidDataSource = new DruidDataSource();
        BeanUtils.copyProperties(dataSourceProperties, defaultDruidDataSource);
        if (enabled) {
            DruidDataSource codeDruidDataSource = new DruidDataSource();
            codeDruidDataSource.setUrl(url);
            codeDruidDataSource.setUsername(username);
            codeDruidDataSource.setPassword(password);
            return codeDruidDataSource;
        }
        return defaultDruidDataSource;
    }

    @Override
    public Class<?> getObjectType() {
        return DataSource.class;
    }
}
