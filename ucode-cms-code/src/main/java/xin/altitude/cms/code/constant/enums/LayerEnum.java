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

package xin.altitude.cms.code.constant.enums;

import xin.altitude.cms.code.entity.bo.XmlConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/07/08 19:32
 **/
public enum LayerEnum {
    /**
     * 实体类
     */
    DOMAIN("domain"),
    /**
     * 实体类VO
     */
    DOMAINVO("entity.vo"),
    /**
     * 实体类BO
     */
    DOMAINBO("entity.bo"),
    /**
     * 控制器
     */
    CONTROLLER("controller"),
    /**
     * 服务接口层
     */
    ISERVICE("iservice"),
    /**
     * 服务实现层
     */
    SERVICEIMPL("serviceimpl"),
    /**
     * MyBatis接口层
     */
    MAPPER("mapper"),
    /**
     * XML文件
     */
    XML("xml");
    private String value;

    LayerEnum(String value) {
        this.value = value;
    }

    public static List<String> toList(XmlConfig codeProperties) {
        List<String> list = new ArrayList<>();
        list.add(LayerEnum.DOMAIN.getValue());
        list.add(LayerEnum.DOMAINVO.getValue());
        list.add(LayerEnum.DOMAINBO.getValue());
        list.add(LayerEnum.CONTROLLER.getValue());
        list.add(LayerEnum.ISERVICE.getValue());
        list.add(LayerEnum.SERVICEIMPL.getValue());
        list.add(LayerEnum.MAPPER.getValue());
        if (codeProperties.getAddXml()) {
            list.add(LayerEnum.XML.getValue());
        }
        return list;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
