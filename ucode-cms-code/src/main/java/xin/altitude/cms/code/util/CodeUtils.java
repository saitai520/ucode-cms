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

package xin.altitude.cms.code.util;

import com.google.common.base.CaseFormat;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;
import xin.altitude.cms.code.config.property.CodeProperties;
import xin.altitude.cms.code.constant.CodeConstant;
import xin.altitude.cms.code.constant.MysqlToJava;
import xin.altitude.cms.code.constant.enums.ColumnDataEnum;
import xin.altitude.cms.code.entity.bo.ControllerConfig;
import xin.altitude.cms.code.entity.vo.MetaColumnVo;
import xin.altitude.cms.common.util.SpringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * 代码生成自定义工具类
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/07/07 14:32
 **/
public class CodeUtils {
    private static final Set<String> SET = new HashSet<>();

    /* Java关键字列表 */
    static {
        SET.addAll(Arrays.asList("abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "if", "implements", "import", "int", "interface", "instanceof", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"));
    }

    /**
     * 判断是否是Java关键字
     *
     * @param s 字符串
     * @return Boolean
     */
    public static Boolean notJavaKey(String s) {
        return !SET.contains(s);
    }

    /**
     * 根据表名创建业务名
     *
     * @param tableName 表名
     * @return 获取业务控制器前缀
     */
    public static String getBusinessName(String tableName) {
        CodeProperties configEntity = SpringUtils.getBean(CodeProperties.class);
        String newTableName = newTableName(tableName);
        return Optional.of(configEntity).map(CodeProperties::getController).map(ControllerConfig::getBusinessName).orElse(newTableName.replace("_", "/"));
    }

    /**
     * 根据表名创建类名
     *
     * @param tableName 原始表名（与数据库一致）
     * @return 类名
     */
    public static String getClassName(String tableName) {
        String newTableName = newTableName(tableName);
        // 小写下划线转化为首字母大写驼峰
        return CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL).convert(newTableName);
    }

    /**
     * 将下划线连接符转化为首字母大写驼峰
     *
     * @param tableName
     * @return
     */
    public static String getUpperFieldName(String tableName) {
        // 小写下划线转化为首字母大写驼峰
        return CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL).convert(tableName.toLowerCase(Locale.getDefault()));
    }

    /**
     * 根据表名创建类实例
     *
     * @param tableName 表名
     * @return 类实例名
     */
    public static String getInstanceName(String tableName) {
        String newTableName = newTableName(tableName);
        // 小写下划线转化为首字母小写驼峰
        return CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL).convert(newTableName);
    }

    /**
     * 将下划线连接符转化为首字母小写驼峰
     *
     * @param tableName
     * @return
     */
    public static String getFieldName(String tableName) {
        // String newTableName = newTableName(tableName);
        // 小写下划线转化为首字母小写驼峰
        return CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL).convert(tableName.toLowerCase(Locale.getDefault()));
    }


    /**
     * 处理表名(统一变成小写字母，以及处理前缀)
     *
     * @param tableName 表名
     * @return 新表名
     */
    public static String newTableName(String tableName) {
        CodeProperties configEntity = SpringUtils.getBean(CodeProperties.class);
        // 将表名转化为小写字母
        String tmp = tableName.toLowerCase(Locale.getDefault());
        if (configEntity.getRemoveTablePrefix()) {
            Optional<String> optional = Optional.ofNullable(configEntity.getTablePrefix());
            if (optional.isPresent()) {
                tmp = tmp.replace(configEntity.getTablePrefix(), "");
                if (tmp.startsWith("_")) {
                    tmp = tmp.replace(configEntity.getTablePrefix(), "");
                }
            } else {
                /* 当前缀仅有3个字符时方去除 */
                int index = tmp.indexOf("_");
                if (index > 0 && index <= 3) {
                    return tmp.substring(index + 1);
                }
            }
        }
        return tmp;
    }

    /**
     * 初始化列属性字段
     *
     * @param column 表列属性
     */
    public static void handleColumnField(MetaColumnVo column) {
        String dataType = column.getDataType();
        String columnName = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL).convert(column.getColumnName());

        // 设置java字段名
        column.setFieldName(uncapitalize(columnName));
        // 设置默认类型
        column.setFieldType(String.class.getSimpleName());
        // 默认是字符串类型
        column.setColumnDataType(ColumnDataEnum.STRING);
        if (MysqlToJava.containsKey(dataType)) {
            column.setFieldType(MysqlToJava.getJavaType(dataType));
        }
    }

    public static String uncapitalize(final String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        final int strLen = str.length();
        final int[] newCodePoints = new int[strLen];
        int outOffset = 0;

        boolean uncapitalizeNext = true;
        for (int index = 0; index < strLen; ) {
            final int codePoint = str.codePointAt(index);
            if (uncapitalizeNext) {
                final int titleCaseCodePoint = Character.toLowerCase(codePoint);
                newCodePoints[outOffset++] = titleCaseCodePoint;
                index += Character.charCount(titleCaseCodePoint);
                uncapitalizeNext = false;
            } else {
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            }
        }
        return new String(newCodePoints, 0, outOffset);
    }

    /**
     * 给定根目录，相对父目录，文件名，写入文件内容
     *
     * @param value         文件内容
     * @param parentDirPath 结对父目录
     * @param filePath      结对文件
     */
    public static void genDirAndFile(String value, String parentDirPath, String filePath) {
        genDirAndFile(value, parentDirPath, filePath, false);
    }

    /**
     * 给定根目录，相对父目录，文件名，写入文件内容
     *
     * @param value         文件内容
     * @param parentDirPath 结对父目录
     * @param filePath      结对文件
     * @param force         是否强写
     */
    public static void genDirAndFile(String value, String parentDirPath, String filePath, boolean force) {
        File parentDirFile = new File(FilenameUtils.concat(CodeConstant.PROJECT_ROOT_ABSOLUTE_PATH, parentDirPath));
        if (!parentDirFile.exists()) {
            parentDirFile.mkdirs();
        }
        File file = new File(FilenameUtils.concat(CodeConstant.PROJECT_ROOT_ABSOLUTE_PATH, filePath));
        if (force && file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                IOUtils.write(value, new FileOutputStream(file), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件的父目录的相对路径
     *
     * @param layer
     * @return
     */
    public static String createRelativJavaDirFilePath(String layer) {
        CodeProperties config = SpringUtils.getBean(CodeProperties.class);
        // 包路径
        String replace = config.getPackageName().replace(".", "/");
        String s = FilenameUtils.concat(getProjectDir() + "/" + CodeConstant.PROJECT_RELATIVE_JAVA_PATH, replace);

        return FilenameUtils.concat(s, layer.replace(".", "/"));
    }

    public static String createRelativXmlDirFilePath(String layer) {
        return FilenameUtils.concat(getProjectDir() + "/" + CodeConstant.PROJECT_RELATIVE_MAPPER_PATH, layer);
    }


    /**
     * 获取当前运行项目的根目录
     *
     * @return 当前运行服务的根目录
     */
    public static String getProjectDir() {
        String path = System.getProperty("user.dir");
        Optional<String> optional = Optional.ofNullable(SpringUtils.getBean(CodeProperties.class))
            .map(CodeProperties::getProjectDir);
        if (optional.isPresent()) {
            String value = optional.get();
            if (new File(value).exists()) {
                return value;
            } else {
                String s = FilenameUtils.concat(path, value);
                if (new File(s).exists()) {
                    return s;
                }
            }
        }
        return path;
    }
}
