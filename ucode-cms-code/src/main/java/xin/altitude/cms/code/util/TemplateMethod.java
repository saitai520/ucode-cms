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

import java.util.Optional;

/**
 * beetl模版针对word拓展函数
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
public class TemplateMethod {
    /**
     * 首字母大小写切换
     *
     * @param s 输入英文字符
     * @return 转化后的英文字符
     */
    public String sc(String s) {
        return Optional.ofNullable(s).map(e -> {
            char[] chars = e.toCharArray();
            chars[0] ^= 32;
            return String.valueOf(chars);
        }).orElse(null);

    }

    /**
     * 根据表名创建类名
     *
     * @param tableName 原始表名（与数据库一致）
     * @return 类名（首字母大写）
     */
    public String getClassName(String tableName) {
        return CodeUtils.getClassName(tableName);
    }

    /**
     * 将下划线连接符转化为首字母小写驼峰
     *
     * @param columnName 列名
     * @return 字段名（首字母小写）
     */
    public String getFieldName(String columnName) {
        return CodeUtils.getFieldName(columnName);
    }

}
