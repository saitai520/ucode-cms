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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import xin.altitude.cms.code.domain.MetaTable;
import xin.altitude.cms.code.entity.vo.MetaTableBo;

import java.util.List;

/**
 * 信息Service接口
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
public interface IMetaTableService {

    /**
     * 查询当前数据库所有列信息
     *
     * @return 列元数据集合
     */
    List<MetaTable> listTables();

    List<MetaTableBo> selectTableList(MetaTable metaTable);

    List<MetaTableBo> selectTableList(String tableName);

    List<MetaTable> listTables(MetaTable metaTable);

    IPage<MetaTable> pageMetaTable(Page<MetaTable> page, MetaTable metaTable);

    MetaTable getMetaTable(String tableName);
}
