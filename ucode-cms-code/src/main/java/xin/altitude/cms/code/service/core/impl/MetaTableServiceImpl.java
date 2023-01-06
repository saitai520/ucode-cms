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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import xin.altitude.cms.code.config.property.CodeProperties;
import xin.altitude.cms.code.constant.CodeConstant;
import xin.altitude.cms.code.domain.MetaTable;
import xin.altitude.cms.code.entity.vo.MetaTableBo;
import xin.altitude.cms.code.mapper.MetaTableMapper;
import xin.altitude.cms.code.service.core.IMetaTableService;
import xin.altitude.cms.code.service.core.IThirdSqlSessionService;
import xin.altitude.cms.common.util.ColUtils;
import xin.altitude.cms.common.util.EntityUtils;

import java.util.List;

/**
 * 信息Service业务层处理
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2021-06-22
 */
// @Service
public class MetaTableServiceImpl implements IMetaTableService {
    @Autowired
    private IThirdSqlSessionService sessionService;

    @Autowired
    private CodeProperties codeProperties;


    /**
     * 查询当前数据库所有列信息
     *
     * @return 列元数据集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MetaTable> listTables() {
        try (SqlSession sqlSession = sessionService.getSqlSession()) {
            LambdaQueryWrapper<MetaTable> wrapper = Wrappers.lambdaQuery(MetaTable.class)
                .apply("table_schema = database()");
            return sqlSession.getMapper(MetaTableMapper.class).selectList(wrapper);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MetaTable> listTables(MetaTable metaTable) {
        try (SqlSession sqlSession = sessionService.getSqlSession()) {
            LambdaQueryWrapper<MetaTable> wrapper = Wrappers.lambdaQuery(metaTable)
                .apply("table_schema = database()");
            if (codeProperties.getFilterSysTable()) {
                /* 不包含系统表 */
                wrapper.notLike(MetaTable::getTableName, CodeConstant.SYS_TABLE_PREFIX);
                wrapper.notLike(MetaTable::getTableName, CodeConstant.QRTZ_TABLE_PREFIX);
            }
            return sqlSession.getMapper(MetaTableMapper.class).selectList(wrapper);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MetaTableBo> selectTableList(String tableName) {
        try (SqlSession sqlSession = sessionService.getSqlSession()) {
            LambdaQueryWrapper<MetaTable> eq = Wrappers.lambdaQuery(MetaTable.class)
                .select(MetaTable::getTableName, MetaTable::getTableComment)
                .apply("table_schema = database()");
            List<MetaTable> list = sqlSession.getMapper(MetaTableMapper.class).selectList(eq);
            return EntityUtils.toList(list, e -> new MetaTableBo(e.getTableName(), e.getTableName().equalsIgnoreCase(tableName)));
        }
    }

    /**
     * 列表查询元数据表数据
     *
     * @param metaTable
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MetaTableBo> selectTableList(MetaTable metaTable) {
        try (SqlSession sqlSession = sessionService.getSqlSession()) {
            LambdaQueryWrapper<MetaTable> eq = Wrappers.lambdaQuery(MetaTable.class)
                .select(MetaTable::getTableName, MetaTable::getTableComment)
                .apply("table_schema = database()");
            List<MetaTable> list = sqlSession.getMapper(MetaTableMapper.class).selectList(eq);
            return EntityUtils.toList(list, e -> new MetaTableBo(e.getTableName(), e.getTableName().equalsIgnoreCase(metaTable.getTableName())));
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPage<MetaTable> pageMetaTable(Page<MetaTable> page, MetaTable metaTable) {
        page.setOptimizeCountSql(false);
        try (SqlSession sqlSession = sessionService.getSqlSession()) {
            LambdaQueryWrapper<MetaTable> wrapper = Wrappers.lambdaQuery(metaTable).apply("table_schema = database()");
            String tableName = metaTable.getTableName();
            wrapper.like(tableName != null, MetaTable::getTableName, tableName).getEntity().setTableName(null);
            if (codeProperties.getFilterSysTable()) {
                /* 不包含系统表 */
                wrapper.notLike(MetaTable::getTableName, CodeConstant.SYS_TABLE_PREFIX);
                wrapper.notLike(MetaTable::getTableName, CodeConstant.QRTZ_TABLE_PREFIX);
            }
            return sqlSession.getMapper(MetaTableMapper.class).selectPage(page, wrapper);
        }
    }

    @Override
    public MetaTable getMetaTable(String tableName) {
        try (SqlSession sqlSession = sessionService.getSqlSession()) {
            LambdaQueryWrapper<MetaTable> queryWrapper = Wrappers.lambdaQuery(MetaTable.class)
                .select(MetaTable::getTableName, MetaTable::getTableComment)
                .eq(MetaTable::getTableName, tableName)
                .apply("table_schema = database()");
            List<MetaTable> tables = sqlSession.getMapper(MetaTableMapper.class).selectList(queryWrapper);
            return ColUtils.toObj(tables);
        }
    }
}
