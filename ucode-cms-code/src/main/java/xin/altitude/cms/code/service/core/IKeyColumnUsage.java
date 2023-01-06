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

package xin.altitude.cms.code.service.core;

import xin.altitude.cms.code.domain.KeyColumnUsage;

import java.util.List;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/12/18 19:38
 **/
public interface IKeyColumnUsage {
    /**
     * 查询当前数据库所有列信息
     *
     * @return 列元数据集合
     */
    List<KeyColumnUsage> listKeyColumns();

    /**
     * 判断当前表是否存在主键
     *
     * @param tableName 表名
     * @return 有且仅有一列标记为主键返回true
     */
    boolean existPk(String tableName);

    boolean isPk(String tableName, String columnName);

    /**
     * 查询主键键索引信息
     *
     * @param tableName 当前表名
     * @return 主键集合
     */
    List<KeyColumnUsage> listPrimaryKeyColumns(String tableName);

    /**
     * 查询外键索引信息
     *
     * @param tableName 当前表名
     * @return 外键集合
     */
    List<KeyColumnUsage> listKeyColumns(String tableName);
}
