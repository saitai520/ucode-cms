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

package xin.altitude.cms.code.config.property;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import xin.altitude.cms.code.constant.enums.CodeModeEnum;
import xin.altitude.cms.code.constant.enums.LayerEnum;
import xin.altitude.cms.code.entity.bo.ControllerConfig;
import xin.altitude.cms.code.entity.bo.DomainConfig;
import xin.altitude.cms.code.entity.bo.MapperConfig;
import xin.altitude.cms.code.entity.bo.XmlConfig;

import java.util.List;

import static java.util.Objects.nonNull;

/**
 * 动态配置实体类
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/07/07 13:41
 **/
@ConfigurationProperties(prefix = "ucode.code")
public class CodeProperties {
    /**
     * 项目的相对路径，如果为空则使用默认值
     */
    private String projectDir;
    /**
     * 代码生成模式
     */
    private CodeModeEnum codeMode = CodeModeEnum.LOCAL;
    /**
     * 是否去除表前缀
     */
    private Boolean removeTablePrefix = false;
    /**
     * 表前缀
     */
    private String tablePrefix;
    /**
     * 基础包名
     */
    private String packageName = "xin.altitude.front";
    /**
     * 模块名
     */
    private String moduleName;
    /**
     * 作者
     */
    private String functionAuthor = "explore";
    /**
     * 是否使用Lombok
     */
    private Boolean useLombok = true;
    /**
     * 是否使用MybatisPlus
     */
    private Boolean useMybatisPlus = true;
    /**
     * 是否使用Swagger
     */
    private Boolean useSwagger = false;
    /**
     * 是否过滤系统表
     */
    private Boolean filterSysTable = true;
    /**
     * 是否开启连接查询
     */
    private Boolean joinQuery = false;
    /**
     * 实体类配置
     */
    @NestedConfigurationProperty
    private DomainConfig domain = new DomainConfig();
    /**
     * 控制器配置
     */
    @NestedConfigurationProperty
    private ControllerConfig controller = new ControllerConfig();
    /**
     * xml文件
     */
    @NestedConfigurationProperty
    private XmlConfig xml = new XmlConfig();
    /**
     * Mapper文件
     */
    @NestedConfigurationProperty
    private MapperConfig mapper = new MapperConfig();
    /**
     * 代码生成层次配置
     */
    @JsonIgnore
    private List<String> layerTypes = LayerEnum.toList(this.getXml());

    public Boolean getFilterSysTable() {
        return filterSysTable;
    }

    public void setFilterSysTable(Boolean filterSysTable) {
        this.filterSysTable = filterSysTable;
    }

    public Boolean getRemoveTablePrefix() {
        return removeTablePrefix;
    }

    public void setRemoveTablePrefix(Boolean removeTablePrefix) {
        this.removeTablePrefix = removeTablePrefix;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFunctionAuthor() {
        return functionAuthor;
    }

    public void setFunctionAuthor(String functionAuthor) {
        this.functionAuthor = functionAuthor;
    }

    public Boolean getUseLombok() {
        return useLombok;
    }

    public void setUseLombok(Boolean useLombok) {
        this.useLombok = useLombok;
    }

    public Boolean getUseMybatisPlus() {
        return useMybatisPlus;
    }

    public void setUseMybatisPlus(Boolean useMybatisPlus) {
        this.useMybatisPlus = useMybatisPlus;
    }

    public Boolean getUseSwagger() {
        return useSwagger;
    }

    public void setUseSwagger(Boolean useSwagger) {
        this.useSwagger = useSwagger;
    }

    public DomainConfig getDomain() {
        return domain;
    }

    public void setDomain(DomainConfig domain) {
        this.domain = domain;
    }

    public ControllerConfig getController() {
        return controller;
    }

    public void setController(ControllerConfig controller) {
        this.controller = controller;
    }

    public XmlConfig getXml() {
        return xml;
    }

    public void setXml(XmlConfig xml) {
        this.xml = xml;
    }

    public MapperConfig getMapper() {
        return mapper;
    }

    public void setMapper(MapperConfig mapper) {
        this.mapper = mapper;
    }

    public List<String> getLayerTypes() {
        return layerTypes;
    }

    public void setLayerTypes(List<String> layerTypes) {
        this.layerTypes = layerTypes;
    }

    public CodeModeEnum getCodeMode() {
        return codeMode;
    }

    public void setCodeMode(CodeModeEnum codeMode) {
        this.codeMode = codeMode;
    }

    public String getProjectDir() {
        return projectDir;
    }

    public void setProjectDir(String projectDir) {
        this.projectDir = projectDir;
    }

    public Boolean getJoinQuery() {
        return joinQuery;
    }

    public void setJoinQuery(Boolean joinQuery) {
        this.joinQuery = joinQuery;
    }

    /**
     * 获取模块名
     *
     * @return 模块名
     */
    public String getModuleName() {
        final int index = packageName.lastIndexOf(".");
        if (index > 0) {
            return nonNull(moduleName) ? moduleName : packageName.substring(index + 1);
        }
        return nonNull(moduleName) ? moduleName : "front";
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
