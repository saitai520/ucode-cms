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

package xin.altitude.cms.code.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.Objects;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/06/19 14:26
 **/
@TableName(schema = "information_schema", value = "columns")
public class MetaColumn extends Model<MetaColumn> {
    // private String tableCatalog;
    /**
     * 数据库
     */
    private String tableSchema;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 列名
     */
    private String columnName;
    /**
     * 列排序
     */
    private Long ordinalPosition;
    // private String columnDefault;
    private String isNullable;
    private String dataType;
    // private Long characterMaximumLength;
    // private Long numericPrecision;
    // private Long numericScale;
    // private Long datetimePrecision;
    // private String collationName;
    // private String columnType;
    // private String columnKey;
    // private String extra;
    // private String privileges;
    /**
     * 列备注
     */
    private String columnComment;
    // private String generationExpression;

    public MetaColumn() {
    }

    public MetaColumn(MetaColumn metaColumn) {
        // this.tableCatalog = metaColumn.getTableCatalog();
        this.tableSchema = metaColumn.getTableSchema();
        this.tableName = metaColumn.getTableName();
        this.columnName = metaColumn.getColumnName();
        this.ordinalPosition = metaColumn.getOrdinalPosition();
        // this.columnDefault = metaColumn.getColumnDefault();
        this.isNullable = metaColumn.getIsNullable();
        this.dataType = metaColumn.getDataType();
        // this.characterMaximumLength = metaColumn.getCharacterMaximumLength();
        // this.numericPrecision = metaColumn.getNumericPrecision();
        // this.numericScale = metaColumn.getNumericScale();
        // this.datetimePrecision = metaColumn.getDatetimePrecision();
        // this.collationName = metaColumn.getCollationName();
        // this.columnType = metaColumn.getColumnType();
        // this.columnKey = metaColumn.columnKey;
        // this.extra = metaColumn.extra;
        // this.privileges = metaColumn.privileges;
        this.columnComment = metaColumn.columnComment;
        // this.generationExpression = metaColumn.generationExpression;
    }


    // public String getTableCatalog() {
    //     return tableCatalog;
    // }
    //
    // public void setTableCatalog(String tableCatalog) {
    //     this.tableCatalog = tableCatalog;
    // }

    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Long getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(Long ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    // public String getColumnDefault() {
    //     return columnDefault;
    // }
    //
    // public void setColumnDefault(String columnDefault) {
    //     this.columnDefault = columnDefault;
    // }

    public String getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(String isNullable) {
        this.isNullable = isNullable;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    // public Long getCharacterMaximumLength() {
    //     return characterMaximumLength;
    // }
    //
    // public void setCharacterMaximumLength(Long characterMaximumLength) {
    //     this.characterMaximumLength = characterMaximumLength;
    // }


    // public Long getNumericPrecision() {
    //     return numericPrecision;
    // }
    //
    // public void setNumericPrecision(Long numericPrecision) {
    //     this.numericPrecision = numericPrecision;
    // }
    //
    // public Long getNumericScale() {
    //     return numericScale;
    // }
    //
    // public void setNumericScale(Long numericScale) {
    //     this.numericScale = numericScale;
    // }
    //
    // public Long getDatetimePrecision() {
    //     return datetimePrecision;
    // }
    //
    // public void setDatetimePrecision(Long datetimePrecision) {
    //     this.datetimePrecision = datetimePrecision;
    // }
    //
    // public String getCollationName() {
    //     return collationName;
    // }
    //
    // public void setCollationName(String collationName) {
    //     this.collationName = collationName;
    // }
    //
    // public String getColumnType() {
    //     return columnType;
    // }
    //
    // public void setColumnType(String columnType) {
    //     this.columnType = columnType;
    // }
    //
    // public String getColumnKey() {
    //     return columnKey;
    // }
    //
    // public void setColumnKey(String columnKey) {
    //     this.columnKey = columnKey;
    // }
    //
    // public String getExtra() {
    //     return extra;
    // }
    //
    // public void setExtra(String extra) {
    //     this.extra = extra;
    // }
    //
    // public String getPrivileges() {
    //     return privileges;
    // }
    //
    // public void setPrivileges(String privileges) {
    //     this.privileges = privileges;
    // }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

    // public String getGenerationExpression() {
    //     return generationExpression;
    // }
    //
    // public void setGenerationExpression(String generationExpression) {
    //     this.generationExpression = generationExpression;
    // }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MetaColumn)) {
            return false;
        }
        return getColumnName().equals(((MetaColumn) o).getColumnName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getColumnName());
    }
}
