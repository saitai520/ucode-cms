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

package xin.altitude.cms.code.constant;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/07/12 17:23
 **/
public class MysqlToJava {
    private static final Map<String, String> HASH_MAPS = new HashMap<>();


    static {
        HASH_MAPS.put("bigint", Long.class.getSimpleName());
        HASH_MAPS.put("int", Integer.class.getSimpleName());
        HASH_MAPS.put("tinyint", Integer.class.getSimpleName());
        HASH_MAPS.put("smallint", Integer.class.getSimpleName());
        HASH_MAPS.put("mediumint", Integer.class.getSimpleName());
        HASH_MAPS.put("year", Integer.class.getSimpleName());
        HASH_MAPS.put("char", String.class.getSimpleName());
        HASH_MAPS.put("varchar", String.class.getSimpleName());
        HASH_MAPS.put("json", String.class.getSimpleName());
        HASH_MAPS.put("tinytext", String.class.getSimpleName());
        HASH_MAPS.put("text", String.class.getSimpleName());
        HASH_MAPS.put("mediumtext", String.class.getSimpleName());
        HASH_MAPS.put("longtext", String.class.getSimpleName());
        HASH_MAPS.put("bit", Boolean.class.getSimpleName());
        HASH_MAPS.put("float", Float.class.getSimpleName());
        HASH_MAPS.put("date", LocalDate.class.getSimpleName());
        HASH_MAPS.put("datetime", LocalDateTime.class.getSimpleName());
        HASH_MAPS.put("timestamp", LocalDateTime.class.getSimpleName());
        HASH_MAPS.put("time", LocalTime.class.getSimpleName());
        HASH_MAPS.put("double", Double.class.getSimpleName());
        HASH_MAPS.put("decimal", BigDecimal.class.getSimpleName());
    }


    /**
     * 通过Mysql字段类型，返回Java类型
     *
     * @param dataType Mysql字段类型
     * @return Java数据类型
     */
    public static String getJavaType(String dataType) {
        return HASH_MAPS.get(dataType);
    }

    /**
     * 判断字段是否存在
     *
     * @param key 键值
     * @return Boolean
     */
    public static Boolean containsKey(String key) {
        return HASH_MAPS.containsKey(key);
    }
}
