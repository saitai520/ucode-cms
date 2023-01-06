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

package xin.altitude.cms.code.entity.vo;

import xin.altitude.cms.code.constant.enums.ColumnDataEnum;
import xin.altitude.cms.code.domain.MetaColumn;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/06/19 18:27
 **/
public class MetaColumnVo extends MetaColumn {
    /**
     * 字段类型
     */
    private String fieldType;
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 是否是主键
     */
    private Boolean pkColumn;
    /**
     * 列数据类型
     */
    private ColumnDataEnum columnDataType;

    public MetaColumnVo(MetaColumn metaColumn) {
        super(metaColumn);
    }

    public Boolean getPkColumn() {
        // return "PRI".equals(this.getColumnKey());
        return pkColumn;
    }

    public void setPkColumn(Boolean pkColumn) {
        this.pkColumn = pkColumn;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public ColumnDataEnum getColumnDataType() {
        return columnDataType;
    }

    public void setColumnDataType(ColumnDataEnum columnDataType) {
        this.columnDataType = columnDataType;
    }
}
