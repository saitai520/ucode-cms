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
 * 控制器部分代码配置类
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/07/08 15:17
 **/
// @Data
public class ControllerConfig {
    /**
     * 业务名(如果不指定业务名，则由表名去除下划线替换而来)
     */
    private String businessName;
    /**
     * 是否需要import列表
     */
    private Boolean addImportList = true;
    /**
     * 是否需要方法注释
     */
    private Boolean addNoteInfo = false;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
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
}
