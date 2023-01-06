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

package xin.altitude.cms.db.datasource.constant;

/**
 * 数据源名称常量名称
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/12/23 16:30
 **/
public interface DataSourceName {
    /**
     * 主数据源名称
     */
    String MASTER_DATA_SOURCE = "masterDataSource";
    /**
     * 从数据源名称
     */
    String SLAVE_DATA_SOURCE = "slaveDataSource";
    /**
     * 动态数据源名称
     */
    String DYNAMIC_DATA_SOURCE = "dynamicDataSource";
}
