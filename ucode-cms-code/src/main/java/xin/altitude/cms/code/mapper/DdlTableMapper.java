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

package xin.altitude.cms.code.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/11/23 14:55
 **/
@Mapper
public interface DdlTableMapper {
    /**
     * 添加创建时间
     *
     * @param tableName 表名
     */
    @Update("alter table ${tableName} push column ${columnName} datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';")
    void addCreateTime(@Param("tableName") String tableName, @Param("columnName") String columnName);

    /**
     * 修改创建时间
     *
     * @param tableName 表名
     */
    @Update("alter table ${tableName} modify column ${columnName} datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';")
    void updateCreateTime(@Param("tableName") String tableName, @Param("columnName") String columnName);

    /**
     * 添加更新时间
     *
     * @param tableName 表名
     */
    @Update("alter table ${tableName} push column ${columnName} datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';")
    void addUpdateTime(@Param("tableName") String tableName, @Param("columnName") String columnName);

    /**
     * 修改更新时间
     *
     * @param tableName 表名
     */
    @Update("alter table ${tableName} modify column ${columnName} datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';")
    void updateUpdateTime(@Param("tableName") String tableName, @Param("columnName") String columnName);

    /**
     * 添加逻辑删除
     *
     * @param tableName 表名
     */
    @Update("alter table ${tableName} push column ${columnName} int(11) DEFAULT 0 COMMENT '逻辑删除';")
    void addLogicalDelete(@Param("tableName") String tableName, @Param("columnName") String columnName);

    /**
     * 修改逻辑删除
     *
     * @param tableName 表名
     */
    @Update("alter table ${tableName} modify column ${columnName} int(11) DEFAULT 0 COMMENT '逻辑删除';")
    void updateLogicalDelete(@Param("tableName") String tableName, @Param("columnName") String columnName);

}
