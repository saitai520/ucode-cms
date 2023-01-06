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

package xin.altitude.cms.code.entity.bo;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/07/07 13:53
 **/
// @Data
public class DomainConfig {
    /**
     * 是否添加超类
     */
    private Boolean addSuperClass = false;
    /**
     * 是否重写toString方法
     */
    private Boolean overrideToString = false;
    /**
     * 是否添加构造方法
     */
    private Boolean addConstructionMethod = true;
    /**
     * 是否使用MybatisPlus AR模式
     */
    private Boolean useActiveRecord = true;
    /**
     * 是否需要import列表
     */
    private Boolean addImportList = true;
    /**
     * 字段注释
     */
    private Boolean addNoteInfo = false;
    /**
     * 是否强制主键自增
     */
    private Boolean pkInc = true;
    /**
     * 日期强制格式化
     */
    private Boolean dateFormat = true;
    /**
     * 日期强制序列化
     */
    private Boolean dateSerialize = false;

    public Boolean getAddSuperClass() {
        return addSuperClass;
    }

    public void setAddSuperClass(Boolean addSuperClass) {
        this.addSuperClass = addSuperClass;
    }

    public Boolean getOverrideToString() {
        return overrideToString;
    }

    public void setOverrideToString(Boolean overrideToString) {
        this.overrideToString = overrideToString;
    }

    public Boolean getAddConstructionMethod() {
        return addConstructionMethod;
    }

    public void setAddConstructionMethod(Boolean addConstructionMethod) {
        this.addConstructionMethod = addConstructionMethod;
    }

    public Boolean getUseActiveRecord() {
        return useActiveRecord;
    }

    public void setUseActiveRecord(Boolean useActiveRecord) {
        this.useActiveRecord = useActiveRecord;
    }

    public Boolean getAddImportList() {
        return addImportList;
    }

    public void setAddImportList(Boolean addImportList) {
        this.addImportList = addImportList;
    }

    public Boolean getAddNoteInfo() {
        return addNoteInfo;
    }

    public void setAddNoteInfo(Boolean addNoteInfo) {
        this.addNoteInfo = addNoteInfo;
    }

    public Boolean getPkInc() {
        return pkInc;
    }

    public void setPkInc(Boolean pkInc) {
        this.pkInc = pkInc;
    }

    public Boolean getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(Boolean dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Boolean getDateSerialize() {
        return dateSerialize;
    }

    public void setDateSerialize(Boolean dateSerialize) {
        this.dateSerialize = dateSerialize;
    }
}
