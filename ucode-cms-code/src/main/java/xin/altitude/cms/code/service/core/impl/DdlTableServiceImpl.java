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

package xin.altitude.cms.code.service.core.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import xin.altitude.cms.code.config.property.CodeProperties;
import xin.altitude.cms.code.constant.CodeConstant;
import xin.altitude.cms.code.constant.DdlEnum;
import xin.altitude.cms.code.domain.MetaColumn;
import xin.altitude.cms.code.domain.MetaTable;
import xin.altitude.cms.code.mapper.DdlTableMapper;
import xin.altitude.cms.code.mapper.MetaColumnMapper;
import xin.altitude.cms.code.mapper.MetaTableMapper;
import xin.altitude.cms.code.service.core.IDdlTableService;
import xin.altitude.cms.code.service.core.IThirdSqlSessionService;
import xin.altitude.cms.common.util.EntityUtils;
import xin.altitude.cms.common.util.SpringUtils;

import java.util.List;

/**
 * 处理字段业务逻辑
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/11/23 15:29
 **/
// @Service
public class DdlTableServiceImpl implements IDdlTableService {

    public static final String BASE_TABLE = "BASE TABLE";

    @Autowired
    private IThirdSqlSessionService sessionService;

    /**
     * 处理通用字段入口
     *
     * @param tableNames 表名集合
     */
    @Override
    public void handleAllColumn(List<String> tableNames) {
        if (tableNames.isEmpty()) {
            try (SqlSession sqlSession = sessionService.getSqlSession()) {
                LambdaQueryWrapper<MetaTable> wrapper = Wrappers.lambdaQuery(MetaTable.class)
                    .select(MetaTable::getTableName)
                    .eq(MetaTable::getTableType, BASE_TABLE)
                    .apply("table_schema = database()");
                /* 是否过滤系统表系统表 */
                if (SpringUtils.getBean(CodeProperties.class).getFilterSysTable()) {
                    wrapper.notLike(MetaTable::getTableName, CodeConstant.SYS_TABLE_PREFIX);
                    wrapper.notLike(MetaTable::getTableName, CodeConstant.QRTZ_TABLE_PREFIX);
                }
                tableNames.addAll(EntityUtils.toSet(sqlSession.getMapper(MetaTableMapper.class).selectList(wrapper), MetaTable::getTableName));
            }
        }
        List<String> tableNameList = viewTable(tableNames);
        /* 遍历处理通用字段 */
        for (String tableName : tableNameList) {
            handleCreateTime(tableName);
            handleUpdateTime(tableName);
            handleLogicalDelete(tableName);
        }
    }

    /**
     * 处理创建时间字段
     *
     * @param tableName 表名
     */
    @Override
    public void handleCreateTime(String tableName) {
        String columnName = DdlEnum.create_time.toString();
        try (SqlSession sqlSession = sessionService.getSqlSession()) {
            if (checkColumn(tableName, columnName)) {
                sqlSession.getMapper(DdlTableMapper.class).updateCreateTime(tableName, columnName);
            } else {
                sqlSession.getMapper(DdlTableMapper.class).addCreateTime(tableName, columnName);
            }
        }
    }

    /**
     * 处理更新时间字段
     *
     * @param tableName 表名
     */
    @Override
    public void handleUpdateTime(String tableName) {
        String columnName = DdlEnum.update_time.toString();
        try (SqlSession sqlSession = sessionService.getSqlSession()) {
            if (checkColumn(tableName, columnName)) {
                sqlSession.getMapper(DdlTableMapper.class).updateUpdateTime(tableName, columnName);
            } else {
                sqlSession.getMapper(DdlTableMapper.class).addUpdateTime(tableName, columnName);
            }
        }
    }

    /**
     * 处理逻辑删除字段
     *
     * @param tableName 表名
     */
    @Override
    public void handleLogicalDelete(String tableName) {
        String columnName = DdlEnum.deleted.toString();
        try (SqlSession sqlSession = sessionService.getSqlSession()) {
            if (checkColumn(tableName, columnName)) {
                sqlSession.getMapper(DdlTableMapper.class).updateLogicalDelete(tableName, columnName);
            } else {
                sqlSession.getMapper(DdlTableMapper.class).addLogicalDelete(tableName, columnName);
            }
        }
    }

    /**
     * 检查列是否存在
     *
     * @param tableName  表名
     * @param columnName 列名
     * @return 是否存在该列
     */
    @Options(useCache = false)
    public boolean checkColumn(String tableName, String columnName) {
        LambdaQueryWrapper<MetaColumn> eq = Wrappers.lambdaQuery(MetaColumn.class)
            .eq(MetaColumn::getColumnName, columnName)
            .eq(MetaColumn::getTableName, tableName)
            .apply("table_schema = database()");
        try (SqlSession sqlSession = sessionService.getSqlSession()) {
            return sqlSession.getMapper(MetaColumnMapper.class).selectCount(eq) > 0;
        }
    }

    /**
     * 检测表是不是视图
     * 视图不需要编辑表
     *
     * @param tableNames 表名
     * @return 返回过滤后的实体表名
     */
    @Override
    @Options(useCache = false)
    public List<String> viewTable(List<String> tableNames) {
        LambdaQueryWrapper<MetaTable> eq = Wrappers.lambdaQuery(MetaTable.class)
            .in(MetaTable::getTableName, tableNames)
            .eq(MetaTable::getTableType, BASE_TABLE)
            .select(MetaTable::getTableName)
            .apply("table_schema = database()");
        try (SqlSession sqlSession = sessionService.getSqlSession()) {
            return EntityUtils.toList(sqlSession.getMapper(MetaTableMapper.class).selectList(eq), MetaTable::getTableName);
        }
    }
}
