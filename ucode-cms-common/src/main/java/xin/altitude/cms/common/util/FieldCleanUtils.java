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

package xin.altitude.cms.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * <p>清洗<i>POJO</i>属性工具类</p>
 * <p>主要两有两种模式：一种是清空指定的字段；一种是保留指定的字段</p>
 * <p>主要用于对<i>DO</i>等实体类做数据清洗</p>
 * <p>{@link FieldCleanUtils}工具类尽管底层使用了反射技术，通过添加缓存，即使频繁调用，依旧具备优秀的性能</p>
 * <p>{@link FieldCleanUtils}和{@link FieldFilterUtils}的主要区别是
 * 前者将字段置空，后者将字段移除；前者适用于数据模型各个阶段，后者通常在控制器返回前调用</p>
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 1.6.1
 **/
public class FieldCleanUtils {

    /**
     * <p>将{@code T}对象实例中指定的字段置<code>null</code></p>
     *
     * @param obj     对象实例
     * @param columns 选中的列 如果为<code>null</code> 则忽略清洗操作
     * @param <T>     {@code T}对象类型
     */
    @SafeVarargs
    public static <T> void cleanFields(T obj, final SFunction<T, ? extends Serializable>... columns) {
        Objects.requireNonNull(obj);
        if (ColUtils.isNotEmpty(columns)) {
            Set<String> set = new HashSet<>(RefUtils.getFiledNames(columns));
            List<Field> fieldList = ReflectionKit.getFieldList(obj.getClass());
            handleField(obj, fieldList, e -> set.contains(e.getName()));
        }
    }

    /**
     * <p>将{@code T}对象实例中指定的字段置<code>null</code></p>
     *
     * @param list    集合实例
     * @param columns 选中的列 如果为<code>null</code> 则忽略清洗操作
     * @param <T>     {@code T}对象类型
     */
    @SafeVarargs
    public static <T> void cleanFields(List<T> list, final SFunction<T, ? extends Serializable>... columns) {
        if (ColUtils.isNotEmpty(columns) && ColUtils.isNotEmpty(list)) {
            Set<String> set = new HashSet<>(RefUtils.getFiledNames(columns));
            List<Field> fieldList = ReflectionKit.getFieldList(ColUtils.toObj(list).getClass());
            list.forEach(t -> handleField(t, fieldList, e -> set.contains(e.getName())));
        }
    }

    /**
     * <p>将{@code T}对象实例中指定的字段置<code>null</code></p>
     *
     * @param page    分页对象实例
     * @param columns 选中的列 如果为<code>null</code> 则忽略清洗操作
     * @param <T>     {@code T}对象类型
     */
    @SafeVarargs
    public static <T> void cleanFields(IPage<T> page, final SFunction<T, ? extends Serializable>... columns) {
        Optional.ofNullable(page).ifPresent(e -> cleanFields(e.getRecords(), columns));
    }


    /**
     * <p>保留{@code T}对象中指定的字段，剩余字段置<code>null</code></p>
     *
     * @param obj     对象实例
     * @param columns 选中的列 如果为<code>null</code> 则忽略清洗操作
     * @param <T>     {@code T}对象类型
     */
    @SafeVarargs
    public static <T> void retainFields(T obj, final SFunction<T, ? extends Serializable>... columns) {
        Objects.requireNonNull(obj);
        if (ColUtils.isNotEmpty(columns)) {
            Set<String> set = new HashSet<>(RefUtils.getFiledNames(columns));
            List<Field> fieldList = ReflectionKit.getFieldList(obj.getClass());
            handleField(obj, fieldList, e -> !set.contains(e.getName()));
        }
    }

    /**
     * <p>保留{@code T}对象中指定的字段，剩余字段置<code>null</code></p>
     *
     * @param list    集合实例
     * @param columns 选中的列 如果为<code>null</code> 则忽略清洗操作
     * @param <T>     {@code T}对象类型
     */
    @SafeVarargs
    public static <T> void retainFields(List<T> list, final SFunction<T, ? extends Serializable>... columns) {
        if (ColUtils.isNotEmpty(columns) && ColUtils.isNotEmpty(list)) {
            Set<String> set = new HashSet<>(RefUtils.getFiledNames(columns));
            List<Field> fieldList = ReflectionKit.getFieldList(ColUtils.toObj(list).getClass());
            list.forEach(t -> handleField(t, fieldList, e -> !set.contains(e.getName())));
        }
    }

    /**
     * <p>保留{@code T}对象中指定的字段，剩余字段置<code>null</code></p>
     *
     * @param page    分页对象实例
     * @param columns 选中的列 如果为<code>null</code> 则忽略清洗操作
     * @param <T>     {@code T}对象类型
     */
    @SafeVarargs
    public static <T> void retainFields(IPage<T> page, final SFunction<T, ? extends Serializable>... columns) {
        Optional.ofNullable(page).ifPresent(e -> retainFields(e.getRecords(), columns));
    }


    /**
     * 具体执行方法 将断言器过滤出的字段Value值置<code>null</code>
     *
     * @param t         {@code T} 类型的实例对象
     * @param fieldList {@code T} 类中的字段列表
     * @param predicate 断言器
     * @param <T>       {@code T} 类型
     */
    private static <T> void handleField(T t, List<Field> fieldList, Predicate<Field> predicate) {
        fieldList.stream().filter(predicate).forEach(e -> RefUtils.setFiledValue(t, e, null));
    }
}
