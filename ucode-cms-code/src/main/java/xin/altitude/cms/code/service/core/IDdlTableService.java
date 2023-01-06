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

import java.util.List;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/11/23 16:07
 **/
public interface IDdlTableService {
    void handleAllColumn(List<String> tableNames);

    void handleCreateTime(String tableName);

    void handleUpdateTime(String tableName);

    void handleLogicalDelete(String tableName);

    /**
     * 检测表是不是视图
     * 视图不需要编辑表
     *
     * @param tableNames 表名
     * @return 返回过滤后的实体表名
     */
    List<String> viewTable(List<String> tableNames);
}
