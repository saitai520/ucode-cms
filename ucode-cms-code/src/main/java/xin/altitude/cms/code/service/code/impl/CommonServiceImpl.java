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

package xin.altitude.cms.code.service.code.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import xin.altitude.cms.code.config.property.CodeProperties;
import xin.altitude.cms.code.constant.MysqlToJava;
import xin.altitude.cms.code.domain.KeyColumnUsage;
import xin.altitude.cms.code.domain.MetaColumn;
import xin.altitude.cms.code.domain.MetaTable;
import xin.altitude.cms.code.entity.vo.KeyColumnUsageVo;
import xin.altitude.cms.code.entity.vo.MetaColumnVo;
import xin.altitude.cms.code.service.code.ICommonService;
import xin.altitude.cms.code.service.core.IKeyColumnUsage;
import xin.altitude.cms.code.service.core.IMetaColumnService;
import xin.altitude.cms.code.service.core.IMetaTableService;
import xin.altitude.cms.code.util.CodeUtils;
import xin.altitude.cms.code.util.TemplateMethod;
import xin.altitude.cms.code.util.VelocityInitializer;
import xin.altitude.cms.common.util.EntityUtils;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;


/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
// @Import({KeyColumnUsageImpl.class})
public abstract class CommonServiceImpl implements ICommonService {
    @Autowired
    protected CodeProperties config;

    @Autowired
    private IKeyColumnUsage keyColumnUsage;

    @Autowired
    private IMetaTableService metaTableService;

    @Autowired
    private IMetaColumnService metaColumnService;

    /**
     * 查询表信息
     */
    @Override
    public MetaTable getTableInfo(String tableName) {
        return metaTableService.getMetaTable(tableName);
    }

    /**
     * 查询列信息
     */
    @Override
    public List<MetaColumnVo> getColumnVos(String tableName) {
        return metaColumnService.getColumnVos(tableName);
    }

    /**
     * 获取两表列名信息的差集
     *
     * @param tableNameA 表名A
     * @param tableNameB 表名B
     * @return 差集合
     */
    @Override
    public List<MetaColumn> listColumns(String tableNameA, String tableNameB) {
        List<MetaColumn> a = metaColumnService.listColumns(tableNameA);
        List<MetaColumn> b = metaColumnService.listColumns(tableNameB);
        return CollectionUtil.subtractToList(a, b);
    }

    /**
     * 通过连接ID，判断所有权，获取连接，查询列信息
     *
     * @return 列实体VO
     */
    @Override
    public MetaColumnVo getPkColumn(String tableName) {
        List<MetaColumnVo> columnsVoList = getColumnVos(tableName);
        columnsVoList.forEach(CodeUtils::handleColumnField);
        return columnsVoList.stream().filter(MetaColumnVo::getPkColumn).findFirst().orElse(null);
    }

    /**
     * 查询外键索引信息
     *
     * @param tableName 当前表名
     * @return 外键集合
     */
    @Override
    public List<KeyColumnUsage> listKeyColumns(String tableName) {
        return keyColumnUsage.listKeyColumns(tableName);
    }


    /**
     * 处理一对一引用主键问题
     *
     * @param keyColumnUsage
     * @return
     */
    @Override
    public KeyColumnUsageVo toKeyColumnUsageVo(KeyColumnUsage keyColumnUsage) {
        KeyColumnUsageVo vo = EntityUtils.toObj(keyColumnUsage, KeyColumnUsageVo::new);
        /* 先处理两表名 */
        vo.setClassName(CodeUtils.getClassName(vo.getTableName()));
        vo.setReferencedClassName(CodeUtils.getClassName(vo.getReferencedTableName()));

        /* 处理列名及列类型 */
        vo.setFieldName(CodeUtils.getFieldName(vo.getColumnName()));
        vo.setReferencedFieldName(CodeUtils.getFieldName(vo.getReferencedColumnName()));

        vo.setFieldType(MysqlToJava.getJavaType(EntityUtils.toObj(metaColumnService.getOneColumn(vo.getTableName(), vo.getColumnName()), MetaColumn::getDataType)));
        vo.setReferencedFieldType(MysqlToJava.getJavaType(EntityUtils.toObj(metaColumnService.getOneColumn(vo.getReferencedTableName(), vo.getReferencedColumnName()), MetaColumn::getDataType)));
        return vo;
    }

    public List<MetaColumnVo> getMetaColumnVoList(String tableNameA, String tableNameB) {
        List<MetaColumn> columns = listColumns(tableNameA, tableNameB);
        List<MetaColumnVo> columnsVoList = EntityUtils.toList(columns, MetaColumnVo::new);
        columnsVoList.forEach(CodeUtils::handleColumnField);
        return columnsVoList;
    }

    /**
     * 创建全局Context
     *
     * @return Context
     */
    public VelocityContext createContext() {
        VelocityContext context = new VelocityContext();
        context.put("t", new TemplateMethod());
        context.put("configEntity", config);
        context.put("packageName", config.getPackageName());
        return context;
    }

    /**
     * 将模板渲染
     *
     * @param context  数据
     * @param template 模版
     * @return StringWriter
     */
    public StringWriter renderTemplate(VelocityContext context, String template) {
        StringWriter sw = new StringWriter();
        VelocityInitializer.initVelocity();
        Template tpl = Velocity.getTemplate(template, Charset.defaultCharset().displayName());
        tpl.merge(context, sw);
        return sw;
    }
}
