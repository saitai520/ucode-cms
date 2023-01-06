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

import xin.altitude.cms.code.domain.MetaColumn;
import xin.altitude.cms.code.entity.vo.MetaColumnVo;

import java.util.List;

/**
 * 字段信息Service接口
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
public interface IMetaColumnService {

    List<MetaColumn> listColumns();

    /**
     * 查询表的列信息集合
     *
     * @param tableName 表名
     * @return 列信息集合
     */
    List<MetaColumn> listColumns(String tableName);

    /**
     * 根据表名、列名查询详细数据
     *
     * @param tableName  表名
     * @param columnName 列名
     * @return 列详细数据
     */
    MetaColumn getOneColumn(String tableName, String columnName);

    List<MetaColumnVo> getColumnVos(String tableName);
}
