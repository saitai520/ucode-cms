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

package xin.altitude.cms.code.service.join.impl;

import org.apache.commons.io.FilenameUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import xin.altitude.cms.code.constant.enums.LayerEnum;
import xin.altitude.cms.code.service.code.impl.CommonServiceImpl;
import xin.altitude.cms.code.util.CodeUtils;
import xin.altitude.cms.code.util.VelocityInitializer;
import xin.altitude.cms.code.util.format.JavaFormat4Domain;
import xin.altitude.cms.common.util.ColUtils;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 处理domain部分代码生成的业务逻辑
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/07/07 14:11
 **/
public class More2MoreVoServiceImpl extends CommonServiceImpl {
    private final static String TEMPLATE = "vm10/java/more2more/domainVo.java.vm";

    public String realtimePreview(VelocityContext context, String... tableNames) {
        StringWriter sw = new StringWriter();
        VelocityInitializer.initVelocity();
        // VelocityContext context = createContext(tableNames);
        Template tpl = Velocity.getTemplate(TEMPLATE, Charset.defaultCharset().displayName());
        tpl.merge(context, sw);
        return JavaFormat4Domain.formJava(sw.toString());
    }

    public void writeToLocalFile(List<String> tableNames, String midClassName) {
        String fileName = String.format("%sVo.java", CodeUtils.getClassName(ColUtils.toObj(tableNames)));
        List<String> importList = getImportList(tableNames, midClassName);
        VelocityContext context = createContext(tableNames, importList, midClassName);
        String value = realtimePreview(context, tableNames.toArray(new String[0]));
        // 文件路径增加关联表关联
        String parentDirPath = CodeUtils.createRelativJavaDirFilePath(FilenameUtils.concat(LayerEnum.DOMAINVO.getValue(), midClassName));
        String filePath = FilenameUtils.concat(parentDirPath, fileName);
        CodeUtils.genDirAndFile(value, parentDirPath, filePath);
    }


    public VelocityContext createContext(List<String> tableNames, List<String> importList, String midClassName) {
        VelocityContext context = createContext();
        context.put("MidClassName", midClassName);
        context.put("leftClassName", CodeUtils.getClassName(tableNames.get(0)));
        context.put("rightClassName", CodeUtils.getClassName(tableNames.get(1)));

        // 添加导包列表
        context.put("importList", importList);
        // 添加表备注
        // context.put("tableComment", getTableInfo(tableName).getTableComment());
        return context;
    }

    public List<String> getImportList(List<String> tableNames, String midClassName) {
        ArrayList<String> rs = new ArrayList<>();
        // 如果配置需要导包，方才进行真正的导包列表构建
        if (config.getDomain().getAddImportList()) {
            rs.add(String.format("import %s;", List.class.getName()));
            rs.add("import lombok.AllArgsConstructor;");
            rs.add("import lombok.Data;");
            rs.add(String.format("import %s;", LocalDateTime.class.getName()));
            rs.add(String.format("import %s;", LocalDate.class.getName()));
            rs.add(String.format("import %s;", Date.class.getName()));
            rs.add(String.format("import %s.domain.%s;", config.getPackageName(), CodeUtils.getClassName(tableNames.get(0))));
            rs.add(String.format("import %s.entity.bo.%s.%sBo;", config.getPackageName(), midClassName, CodeUtils.getClassName(tableNames.get(1))));
        }
        rs.sort(Comparator.naturalOrder());
        return rs;
    }
}


