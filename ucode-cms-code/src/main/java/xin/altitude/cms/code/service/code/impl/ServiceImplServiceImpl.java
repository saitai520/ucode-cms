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
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import xin.altitude.cms.code.config.property.CodeProperties;
import xin.altitude.cms.code.service.code.IServiceImplService;
import xin.altitude.cms.common.util.SpringUtils;
import xin.altitude.cms.code.util.CodeUtils;
import xin.altitude.cms.code.util.VelocityInitializer;
import xin.altitude.cms.code.util.format.JavaFormat4Controller;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 处理domain部分代码生成的业务逻辑
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/07/07 14:11
 **/
// @Service
public class ServiceImplServiceImpl extends CommonServiceImpl implements IServiceImplService {
    private final static String TEMPLATE = "vm10/java/serviceImpl.java.vm";


    @Override
    public void writeToLocalFile(String tableName, String className) {
        String fileName = String.format("%sServiceImpl.java", className);
        String value = realtimePreview(tableName);
        String parentDirPath = CodeUtils.createRelativJavaDirFilePath("service/impl");
        String filePath = FilenameUtils.concat(parentDirPath, fileName);
        CodeUtils.genDirAndFile(value, parentDirPath, filePath);
    }

    /**
     * 代码实时预览
     */
    @Override
    public String realtimePreview(String tableName) {
        CodeProperties configEntity = SpringUtils.getBean(CodeProperties.class);
        StringWriter sw = new StringWriter();
        VelocityInitializer.initVelocity();
        VelocityContext context = createContext(tableName);
        Template tpl = Velocity.getTemplate(TEMPLATE, Charset.defaultCharset().displayName());
        tpl.merge(context, sw);
        return JavaFormat4Controller.formJava(sw.toString());
        // return sw.toString();
    }

    /**
     * 构建VelocityContext
     */
    @Override
    public VelocityContext createContext(String tableName) {
        VelocityContext context = new VelocityContext();
        context.put("configEntity", config);
        context.put("packageName", config.getPackageName());
        context.put("tableName", tableName);
        context.put("ClassName", CodeUtils.getClassName(tableName));
        context.put("className", CodeUtils.getInstanceName(tableName));
        // 添加导包列表
        context.put("importList", getImportList(tableName));
        // 添加表备注
        return context;
    }

    /**
     * 获取导包列表
     */
    @Override
    public List<String> getImportList(String tableName) {
        ArrayList<String> rs = new ArrayList<>();
        rs.add("import org.springframework.beans.factory.annotation.Autowired;");
        rs.add("import org.springframework.stereotype.Service;");
        if (config.getUseMybatisPlus()) {
            rs.add("import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;");
            // rs.push(String.format("import %s.domain.%s;", configEntity.getPackageName(), CodeUtils.getClassName(tableName, configEntity)));
            rs.add(String.format("import %s.domain.%s;", config.getPackageName(), CodeUtils.getClassName(tableName)));
            rs.add(String.format("import %s.mapper.%sMapper;", config.getPackageName(), CodeUtils.getClassName(tableName)));
            rs.add(String.format("import %s.service.I%sService;", config.getPackageName(), CodeUtils.getClassName(tableName)));
        }
        rs.sort(Comparator.naturalOrder());
        return rs;
    }
}


