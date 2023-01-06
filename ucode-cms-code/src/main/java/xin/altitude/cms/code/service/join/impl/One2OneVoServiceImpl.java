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
import xin.altitude.cms.code.domain.KeyColumnUsage;
import xin.altitude.cms.code.service.code.impl.CommonServiceImpl;
import xin.altitude.cms.code.service.join.IOne2OneVoService;
import xin.altitude.cms.code.util.CodeUtils;
import xin.altitude.cms.code.util.VelocityInitializer;
import xin.altitude.cms.code.util.format.JavaFormat4Domain;

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
public class One2OneVoServiceImpl extends CommonServiceImpl implements IOne2OneVoService {
    private final static String TEMPLATE = "vm10/java/one2one/domainVo.java.vm";

    /**
     * 代码实时预览
     */
    @Override
    public String realtimePreview(String tableName, KeyColumnUsage keyColumnUsage, VelocityContext context) {
        StringWriter sw = new StringWriter();
        VelocityInitializer.initVelocity();
        Template tpl = Velocity.getTemplate(TEMPLATE, Charset.defaultCharset().displayName());
        tpl.merge(context, sw);
        return JavaFormat4Domain.formJava(sw.toString());
    }

    /**
     * 写到本地
     *
     * @param tableName
     * @param className
     * @param keyColumnUsage
     */
    @Override
    public void writeToLocalFile(String tableName, String className, KeyColumnUsage keyColumnUsage) {
        String fileName = String.format("%sVo.java", className);
        VelocityContext context = createContext(tableName, keyColumnUsage);
        String value = realtimePreview(tableName, keyColumnUsage, context);
        String parentDirPath = CodeUtils.createRelativJavaDirFilePath(LayerEnum.DOMAINVO.getValue());
        String filePath = FilenameUtils.concat(parentDirPath, fileName);
        CodeUtils.genDirAndFile(value, parentDirPath, filePath);
    }


    /**
     * 构建VelocityContext
     */
    @Override
    public VelocityContext createContext(String tableName, KeyColumnUsage keyColumnUsage) {
        VelocityContext context = createContext();
        context.put("ClassName", CodeUtils.getClassName(tableName));
        context.put("className", CodeUtils.getInstanceName(tableName));

        context.put("columns", getMetaColumnVoList(keyColumnUsage.getReferencedTableName(), keyColumnUsage.getTableName()));
        // 添加导包列表
        context.put("importList", getImportList(tableName));
        // 添加表备注
        context.put("tableComment", getTableInfo(tableName).getTableComment());
        return context;
    }

    /**
     * 获取导包列表
     *
     * @param tableName 表名
     */
    @Override
    public List<String> getImportList(String tableName) {
        ArrayList<String> rs = new ArrayList<>();
        // 如果配置需要导包，方才进行真正的导包列表构建
        if (config.getDomain().getAddImportList()) {
            rs.add(String.format("import %s;", LocalDateTime.class.getName()));
            rs.add(String.format("import %s;", LocalDate.class.getName()));
            rs.add(String.format("import %s;", Date.class.getName()));
            rs.add(String.format("import %s.domain.%s;", config.getPackageName(), CodeUtils.getClassName(tableName)));
            if (config.getDomain().getDateFormat()) {
                rs.add("import com.fasterxml.jackson.annotation.JsonFormat;");
            }
            if (config.getDomain().getDateSerialize()) {
                rs.add("import com.fasterxml.jackson.databind.annotation.JsonDeserialize;");
                rs.add("import com.fasterxml.jackson.databind.annotation.JsonSerialize;");
                rs.add("import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;");
                rs.add("import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;");
            }
            if (config.getUseLombok()) {
                rs.add("import lombok.AllArgsConstructor;");
                rs.add("import lombok.Builder;");
                rs.add("import lombok.Data;");
                rs.add("import lombok.NoArgsConstructor;");
                rs.add("import lombok.experimental.Accessors;");
            }
            if (config.getUseMybatisPlus() && config.getMapper().getUseCache()) {
                rs.add("import java.io.Serializable;");
            }
        }
        rs.sort(Comparator.naturalOrder());
        return rs;
    }
}


