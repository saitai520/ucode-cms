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
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import xin.altitude.cms.code.constant.CodeConstant;
import xin.altitude.cms.code.domain.KeyColumnUsage;
import xin.altitude.cms.code.mapper.KeyColumnUsageMapper;
import xin.altitude.cms.code.service.core.IKeyColumnUsage;
import xin.altitude.cms.code.service.core.IThirdSqlSessionService;

import java.util.List;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/12/18 19:37
 **/
// @Service
public class KeyColumnUsageImpl implements IKeyColumnUsage {

    @Autowired
    private IThirdSqlSessionService sessionService;

    /**
     * 查询当前数据库所有主键、外键信息
     *
     * @return 主键、外键元数据集合
     */
    @Override
    public List<KeyColumnUsage> listKeyColumns() {
        try (SqlSession sqlSession = sessionService.getSqlSession()) {
            LambdaQueryWrapper<KeyColumnUsage> wrapper = Wrappers.lambdaQuery(KeyColumnUsage.class)
                .apply("table_schema = database()");
            return sqlSession.getMapper(KeyColumnUsageMapper.class).selectList(wrapper);
        }
    }

    /**
     * 判断当前表是否存在主键
     *
     * @param tableName 表名
     * @return 有且仅有一列标记为主键返回true
     */
    @Override
    public boolean existPk(String tableName) {
        try (SqlSession sqlSession = sessionService.getSqlSession()) {
            final LambdaQueryWrapper<KeyColumnUsage> eq = Wrappers.lambdaQuery(KeyColumnUsage.class)
                .apply("table_schema=database()")
                .eq(KeyColumnUsage::getTableName, tableName)
                .eq(KeyColumnUsage::getConstraintName, CodeConstant.PRIMARY);
            final Long count = sqlSession.getMapper(KeyColumnUsageMapper.class).selectCount(eq);
            return count == 1;
        }
    }

    /**
     * 判断当前列是不是主键
     *
     * @param tableName  表名
     * @param columnName 列名
     * @return 有且仅有一列标记为主键返回true
     */
    @Override
    public boolean isPk(String tableName, String columnName) {
        List<KeyColumnUsage> list = listPrimaryKeyColumns(tableName);
        return list.stream().anyMatch(e -> e.getColumnName().equalsIgnoreCase(columnName));
    }

    /**
     * 查询主键键索引信息
     *
     * @param tableName 当前表名
     * @return 主键集合
     */
    @Override
    public List<KeyColumnUsage> listPrimaryKeyColumns(String tableName) {
        try (SqlSession sqlSession = sessionService.getSqlSession()) {
            LambdaQueryWrapper<KeyColumnUsage> eq = Wrappers.lambdaQuery(KeyColumnUsage.class)
                .apply("table_schema = database()")
                .eq(KeyColumnUsage::getTableName, tableName)
                .eq(KeyColumnUsage::getConstraintName, CodeConstant.PRIMARY);
            return sqlSession.getMapper(KeyColumnUsageMapper.class).selectList(eq);
        }
    }

    /**
     * 查询外键索引信息
     *
     * @param tableName 当前表名
     * @return 外键集合
     */
    @Override
    public List<KeyColumnUsage> listKeyColumns(String tableName) {
        try (SqlSession sqlSession = sessionService.getSqlSession()) {
            LambdaQueryWrapper<KeyColumnUsage> eq = Wrappers.lambdaQuery(KeyColumnUsage.class)
                .apply("table_schema = database()")
                .eq(KeyColumnUsage::getTableName, tableName)
                .ne(KeyColumnUsage::getConstraintName, CodeConstant.PRIMARY)
                .isNotNull(KeyColumnUsage::getReferencedColumnName);
            return sqlSession.getMapper(KeyColumnUsageMapper.class).selectList(eq);
        }
    }
}
