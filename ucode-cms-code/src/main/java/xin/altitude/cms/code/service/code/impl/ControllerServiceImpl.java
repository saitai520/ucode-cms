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

import org.apache.commons.io.FilenameUtils;
import org.apache.velocity.VelocityContext;
import xin.altitude.cms.code.config.property.CodeProperties;
import xin.altitude.cms.code.constant.enums.LayerEnum;
import xin.altitude.cms.code.domain.KeyColumnUsage;
import xin.altitude.cms.code.entity.vo.KeyColumnUsageVo;
import xin.altitude.cms.code.util.CodeUtils;
import xin.altitude.cms.code.util.format.JavaFormat4Controller;
import xin.altitude.cms.common.entity.AjaxResult;
import xin.altitude.cms.common.entity.PageEntity;
import xin.altitude.cms.common.util.EntityUtils;
import xin.altitude.cms.common.util.SpringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * 处理domain部分代码生成的业务逻辑
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/07/07 14:11
 **/
public class ControllerServiceImpl extends CommonServiceImpl {
    private final static String TEMPLATE = "vm10/java/controller.java.vm";

    public void writeToLocalFile(String tableName, String className, Integer flagCode, List<KeyColumnUsage> keyColumnUsages) {
        List<KeyColumnUsageVo> keyColumnUsageVos = EntityUtils.toList(keyColumnUsages, this::toKeyColumnUsageVo);
        String fileName = String.format("%sController.java", className);
        VelocityContext context = createContext(tableName, flagCode, keyColumnUsageVos);
        // 渲染后并格式化代码字符串
        String value = JavaFormat4Controller.formJava(renderTemplate(context, TEMPLATE).toString());
        String parentDirPath = CodeUtils.createRelativJavaDirFilePath(LayerEnum.CONTROLLER.getValue());
        String filePath = FilenameUtils.concat(parentDirPath, fileName);
        CodeUtils.genDirAndFile(value, parentDirPath, filePath);
    }

    public void writeToLocalFile(String tableName, Integer flagCode, KeyColumnUsage keyColumnUsage) {
        // List<KeyColumnUsageVo> keyColumnUsageVos = EntityUtils.toList(keyColumnUsages, this::toKeyColumnUsageVo);
        KeyColumnUsageVo keyColumnUsageVo = EntityUtils.toObj(keyColumnUsage, this::toKeyColumnUsageVo);
        String fileName = String.format("%sController.java", keyColumnUsageVo.getReferencedClassName());
        VelocityContext context = createContext(keyColumnUsageVo.getReferencedTableName(), flagCode, keyColumnUsageVo);
        // 渲染后并格式化代码字符串
        String value = JavaFormat4Controller.formJava(renderTemplate(context, TEMPLATE).toString());
        String parentDirPath = CodeUtils.createRelativJavaDirFilePath(LayerEnum.CONTROLLER.getValue());
        String filePath = FilenameUtils.concat(parentDirPath, fileName);
        CodeUtils.genDirAndFile(value, parentDirPath, filePath, true);
    }


    public VelocityContext createContext(String tableName) {
        VelocityContext context = createContext();
        context.put("tableName", tableName);
        context.put("ClassName", CodeUtils.getClassName(tableName));
        // 主键列
        context.put("pkColumn", getPkColumn(tableName));
        // 添加导包列表
        context.put("importList", getImportList(tableName));
        // 添加表备注
        context.put("tableComment", getTableInfo(tableName).getTableComment());
        // 处理业务名
        context.put("businessName", CodeUtils.getBusinessName(tableName));
        return context;
    }

    /**
     * 构建VelocityContext
     */
    public VelocityContext createContext(String tableName, Integer flagCode) {
        VelocityContext context = createContext(tableName);
        // 标识是否增强Vo
        context.put("flagCode", flagCode);
        context.put("joinQuery", SpringUtils.getBean(CodeProperties.class).getJoinQuery());
        return context;
    }

    public VelocityContext createContext(String tableName, Integer flagCode, List<KeyColumnUsageVo> keyColumnUsageVos) {
        VelocityContext context = createContext(tableName, flagCode);
        if (keyColumnUsageVos.size() == 2 && !keyColumnUsageVos.get(0).getReferencedTableName().equalsIgnoreCase(keyColumnUsageVos.get(1).getReferencedTableName())) {
            context.put("LKeyColumn", keyColumnUsageVos.get(0));
            context.put("RKeyColumn", keyColumnUsageVos.get(1));
        }
        // 添加导包列表
        context.put("importList", new HashSet<>(getImportList(tableName, keyColumnUsageVos)));
        return context;
    }

    public VelocityContext createContext(String tableName, Integer flagCode, KeyColumnUsageVo keyColumnUsageVo) {
        VelocityContext context = createContext(tableName, flagCode);

        // 添加导包列表
        context.put("importList", new HashSet<>(getImportList(tableName, keyColumnUsageVo)));
        return context;
    }

    /**
     * 获取导包列表
     */
    public List<String> getImportList(String tableName) {
        ArrayList<String> rs = new ArrayList<>();
        // 如果配置需要导包，方才进行真正的导包列表构建
        if (config.getController().getAddImportList()) {
            rs.add("import org.springframework.beans.factory.annotation.Autowired;");
            rs.add("import org.springframework.web.bind.annotation.DeleteMapping;");
            rs.add("import org.springframework.web.bind.annotation.GetMapping;");
            rs.add("import org.springframework.web.bind.annotation.PathVariable;");
            rs.add("import org.springframework.web.bind.annotation.PostMapping;");
            rs.add("import org.springframework.web.bind.annotation.PutMapping;");
            rs.add("import org.springframework.web.bind.annotation.RequestBody;");
            rs.add("import org.springframework.web.bind.annotation.RequestMapping;");
            rs.add("import org.springframework.web.bind.annotation.RestController;");
            rs.add("import java.util.Arrays;");
            rs.add("import java.util.List;");
            rs.add(String.format("import %s;", AjaxResult.class.getName()));
            rs.add(String.format("import %s;", PageEntity.class.getName()));
            rs.add(String.format("import %s.domain.%s;", config.getPackageName(), CodeUtils.getClassName(tableName)));
            rs.add(String.format("import %s.service.I%sService;", config.getPackageName(), CodeUtils.getClassName(tableName)));
            rs.add(String.format("import %s.mapper.%sMapper;", config.getPackageName(), CodeUtils.getClassName(tableName)));
            if (config.getUseMybatisPlus()) {
                rs.add("import com.baomidou.mybatisplus.core.toolkit.Wrappers;");
            }
            if (config.getUseSwagger()) {
                rs.add("import io.swagger.annotations.ApiOperation;");
            }
        }
        rs.sort(Comparator.naturalOrder());
        return rs;
    }

    public List<String> getImportList(String tableName, List<KeyColumnUsageVo> keyColumnUsageVos) {
        List<String> rs = getImportList(tableName);
        Boolean joinQuery = SpringUtils.getBean(CodeProperties.class).getJoinQuery();
        if (joinQuery && keyColumnUsageVos.size() == 2) {
            rs.add(String.format("import %s.domain.%s;", config.getPackageName(), keyColumnUsageVos.get(0).getReferencedClassName()));
            rs.add(String.format("import %s.domain.%s;", config.getPackageName(), keyColumnUsageVos.get(1).getReferencedClassName()));
        }
        rs.sort(Comparator.naturalOrder());
        return rs;
    }

    public List<String> getImportList(String tableName, KeyColumnUsageVo keyColumnUsageVo) {
        List<String> rs = getImportList(tableName);
        Boolean joinQuery = config.getJoinQuery();
        rs.sort(Comparator.naturalOrder());
        return rs;
    }
}


