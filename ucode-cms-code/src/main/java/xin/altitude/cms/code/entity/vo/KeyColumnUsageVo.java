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


import xin.altitude.cms.code.domain.KeyColumnUsage;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/12/19 14:40
 **/
public class KeyColumnUsageVo extends KeyColumnUsage {
    /**
     * 当前表类名
     */
    private String className;
    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 引用表类名
     */
    private String referencedClassName;

    /**
     * 引用列类型
     */
    private String referencedFieldType;
    /**
     * 引用字段名称
     */
    private String referencedFieldName;

    public KeyColumnUsageVo() {
    }

    public KeyColumnUsageVo(KeyColumnUsage keyColumnUsage) {
        super(keyColumnUsage);
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getReferencedFieldType() {
        return referencedFieldType;
    }

    public void setReferencedFieldType(String referencedFieldType) {
        this.referencedFieldType = referencedFieldType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    // public String getInstanceName() {
    //     return instanceName;
    // }
    //
    // public void setInstanceName(String instanceName) {
    //     this.instanceName = instanceName;
    // }

    public String getReferencedClassName() {
        return referencedClassName;
    }

    public void setReferencedClassName(String referencedClassName) {
        this.referencedClassName = referencedClassName;
    }

    // public String getReferencedInstanceName() {
    //     return referencedInstanceName;
    // }
    //
    // public void setReferencedInstanceName(String referencedInstanceName) {
    //     this.referencedInstanceName = referencedInstanceName;
    // }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getReferencedFieldName() {
        return referencedFieldName;
    }

    public void setReferencedFieldName(String referencedFieldName) {
        this.referencedFieldName = referencedFieldName;
    }

    // public String getUpperFieldName() {
    //     return upperFieldName;
    // }
    //
    // public void setUpperFieldName(String upperFieldName) {
    //     this.upperFieldName = upperFieldName;
    // }
    //
    // public String getReferencedUpperFieldName() {
    //     return referencedUpperFieldName;
    // }
    //
    // public void setReferencedUpperFieldName(String referencedUpperFieldName) {
    //     this.referencedUpperFieldName = referencedUpperFieldName;
    // }
}
