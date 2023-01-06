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

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.context.annotation.Bean;
import xin.altitude.cms.common.util.SpringUtils;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * Mybatis支持*匹配扫描包
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
public class MyBatisConfig extends AbstractMyBatisConfig {

    /**
     * 构造SqlSession会话工厂
     *
     * @param dataSource 数据源
     * @return SqlSessionFactory
     * @throws Exception Exception
     */
    @Bean
    // @Primary
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        /* 注入MybatisPlus会话工厂 */
        final MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        /* 注入MyBatis全局配置 */
        Optional.ofNullable(env.getProperty("mybatis.mapperLocations")).ifPresent(e -> sessionFactory.setMapperLocations(getMapperLocations(e)));
        // /* 注入mapper扫描地址 */
        // Optional.ofNullable(env.getProperty("mybatis.configLocation")).ifPresent(e -> sessionFactory.setConfigLocation(getConfigLocation(e)));
        VFS.addImplClass(SpringBootVFS.class);
        sessionFactory.setConfiguration(SpringUtils.getBean(MybatisPlusProperties.class).getConfiguration());
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPlugins(interceptor());
        return sessionFactory.getObject();
    }

}
