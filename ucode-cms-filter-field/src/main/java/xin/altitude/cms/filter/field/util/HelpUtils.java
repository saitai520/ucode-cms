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

package xin.altitude.cms.filter.field.util;


import com.baomidou.mybatisplus.core.metadata.IPage;
import xin.altitude.cms.common.util.EntityUtils;
import xin.altitude.cms.common.util.FieldCleanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>{@link FieldCleanUtils}和{@link HelpUtils}的主要区别是
 * 前者将字段置空，后者将字段移除；前者适用于数据模型各个阶段，后者通常在控制器返回前调用</p>
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
public class HelpUtils {
    private HelpUtils() {
    }


    /**
     * 过滤属性 操作单个对象 默认情况为排除属性（字段）
     *
     * @param <T>        目标对象泛型
     * @param obj        泛型对象实例
     * @param fieldNames 字段集合实例
     * @return JSONObject实例
     */
    public static <T> Map<?, ?> filterFields(T obj, List<String> fieldNames) {
        return filterFields(obj, false, fieldNames);
    }

    /**
     * 过滤属性 操作单个对象
     *
     * @param <T>        目标对象泛型
     * @param obj        泛型对象实例
     * @param isInclude  如果是true代表保留字段、false代表排除字段
     * @param fieldNames 字段集合实例
     * @return JSONObject实例
     */
    public static <T> Map<?, ?> filterFields(T obj, boolean isInclude, List<String> fieldNames) {
        return Optional.of(obj).map(e -> {
            Map<String, Object> map = EntityUtils.toMap(obj);
            return doFilter(map, fieldNames, isInclude);
        }).orElse(null);
    }


    /**
     * 过滤属性 操作列表对象 默认情况为排除属性（字段）
     *
     * @param <T>        目标对象泛型
     * @param list       泛型列表集合实例
     * @param fieldNames 字段集合实例
     * @return Map集合实例
     */
    public static <T> List<? extends Map<?, ?>> filterFields(List<T> list, List<String> fieldNames) {
        return filterFields(list, false, fieldNames);
    }


    /**
     * 过滤属性 操作列表对象
     *
     * @param <T>        目标对象泛型
     * @param list       泛型列表集合实例
     * @param isInclude  如果是true代表保留字段、false代表排除字段
     * @param fieldNames 字段集合实例
     * @return Map集合实例
     */
    public static <T> List<? extends Map<?, ?>> filterFields(List<T> list, boolean isInclude, List<String> fieldNames) {
        return Optional.of(list).map(f -> EntityUtils.toList(f, e -> filterFields(e, isInclude, fieldNames))).orElse(null);
    }

    /**
     * 过滤属性 操作分页对象
     *
     * @param <T>        目标对象泛型
     * @param page       泛型分页实例
     * @param fieldNames 字段集合实例
     * @return Map分页实例
     */
    public static <T> IPage<? extends Map<?, ?>> filterFields(IPage<T> page, List<String> fieldNames) {
        return filterFields(page, false, fieldNames);
    }

    /**
     * 过滤属性 操作分页对象
     *
     * @param <T>        目标对象泛型
     * @param page       泛型分页实例
     * @param isInclude  如果是true代表保留字段、false代表排除字段
     * @param fieldNames 字段集合实例
     * @return Map分页实例
     */
    public static <T> IPage<? extends Map<?, ?>> filterFields(IPage<T> page, boolean isInclude, List<String> fieldNames) {
        return Optional.of(page).map(f -> EntityUtils.toPage(f, e ->
            filterFields(e, isInclude, fieldNames))).orElse(null);
    }


    /**
     * 具体执行过滤
     */
    private static Map<String, Object> doFilter(Map<String, Object> map, List<String> fieldNames, boolean contains) {
        Map<String, Object> result = new HashMap<>();
        Set<String> set = map.entrySet().stream().map(e -> e.getKey()).collect(Collectors.toSet());
        Set<String> fieldNameSet = fieldNames.stream().filter(e -> set.contains(e)).collect(Collectors.toSet());
        if (contains) {
            fieldNameSet.forEach(e -> result.put(e, map.get(e)));
        } else {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!fieldNameSet.contains(entry.getKey())) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return result;
    }

}


