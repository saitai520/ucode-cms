/*
 *
 * Copyright (c) 2020-2023, Java知识图谱 (http://www.altitude.xin).
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

package xin.altitude.cms.plus.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import xin.altitude.cms.common.util.BeanCopyUtils;
import xin.altitude.cms.common.util.EntityUtils;
import xin.altitude.cms.common.util.GuavaUtils;
import xin.altitude.cms.common.util.RefUtils;
import xin.altitude.cms.common.util.SpringUtils;
import xin.altitude.cms.common.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
public class FieldInjectUtils {
    private FieldInjectUtils() {
    }

    /**
     * <p>适用于一对一查询VO属性注入</p>
     * <p>
     * 通过反射的方式给关联表查询注入属性值
     * 需要说明的是 相较于手动编码 反射的执行效率略微差点
     * 本方法优点是能够提高开发效率
     * 后续考虑逆向工程优化性能
     * </p>
     * <p>主副表的定义：主表是指包含外键的表</p>
     *
     * @param <V>           主表对应的实体类VO
     * @param <S>           副表对应的实体类DO
     * @param <R>           主表外键、副表主键对应的数据类型
     * @param data          主表对应的VO集合实例
     * @param fkColumn      主表关联外键列（字段） 方法引用表示
     * @param clazz         副表对应的IService实现类Class对象
     * @param pkColumn      副表对应的主键（字段）方法引用表示
     * @param injectColumns 需要注入的列（字段） 方法引用表示
     */
    @SafeVarargs
    public static <V, S, R> void injectField(List<V> data, final SFunction<V, R> fkColumn, Class<? extends IService<S>> clazz, final SFunction<S, R> pkColumn, SFunction<V, ? extends Serializable>... injectColumns) {
        IService<S> iService = SpringUtils.getBean(clazz);
        // 获取主表关联外键的集合（去重）
        Set<? extends R> ids = EntityUtils.toSet(data, fkColumn);
        // 如果集合元素个数不为空 则进行后续操作
        if (ids.size() > 0 && injectColumns.length > 0) {
            // 主表关联外键对应的字段名字符串
            String fieldName = RefUtils.getFiledName(fkColumn);
            List<String> injectFiledNames = RefUtils.getFiledNames(injectColumns);
            String[] selectField = {fieldName, String.join(",", injectFiledNames)};
            // 数据库查询字段需要下划线表示
            String selectStr = Arrays.stream(selectField).map(GuavaUtils::toUnderScoreCase).collect(Collectors.joining(","));
            // 构造副表查询条件(查询指定列元素)
            QueryWrapper<S> wrapper = Wrappers.query(RefUtils.newInstance(iService.getEntityClass())).select(selectStr).in(GuavaUtils.toUnderScoreCase(fieldName), ids);
            // 通过主表的外键查询关联副表符合条件的数据
            List<S> list = iService.getBaseMapper().selectList(wrapper);
            // 将list转换为map 其中Key为副表主键 Value为副表类型实例本身
            Map<R, S> map = EntityUtils.toMap(list, pkColumn, e -> e);
            doInjectField(data, fieldName, map, RefUtils.getFiledNames(injectColumns));
        }
    }


    /**
     * <p>此方法适用于一对多查询列表属性注入</p>
     * <p>主副表的定义：主表是指包含外键的表</p>
     *
     * @param data         副表实体类VO的集合实例
     * @param pkColumn     副表实体类主键列（方法引用表示）
     * @param clazz        主表对应的IService实现类Class对象
     * @param fkColumn     主表实体类外键列（方法引用表示）
     * @param injectColumn 需要注入的列（字段） 方法引用表示
     * @param <V>          副表实体类VO泛型
     * @param <R>          主表外键、副表主键对应的数据类型
     * @param <S>          主表对应的实体类DO
     * @param <W>          需要注入属性的列泛型
     */
    public static <V, R, S, W> void injectListField(List<V> data, SFunction<V, R> pkColumn, Class<? extends IService<S>> clazz, SFunction<S, R> fkColumn, SFunction<V, W> injectColumn) {
        Set<R> deptIds = EntityUtils.toSet(data, pkColumn);
        if (deptIds.size() > 0) {
            IService<S> service = SpringUtils.getBean(clazz);
            LambdaQueryWrapper<S> in = Wrappers.lambdaQuery(service.getEntityClass()).in(fkColumn, deptIds);

            List<S> list = service.getBaseMapper().selectList(in);
            // 以部门ID为单位对用户数据分组
            Map<R, List<S>> map = list.stream().collect(Collectors.groupingBy(fkColumn));
            String injectFiledName = RefUtils.getFiledName(injectColumn);
            String filedName = RefUtils.getFiledName(pkColumn);
            doInjectField(data, filedName, map, injectFiledName);
        }
    }


    /**
     * 通过反射的方式给关联表查询注入属性值
     * 需要说明的是 相较于手动编码 反射的执行效率略差
     * 本方法能够提高开发效率 后续考虑逆向工程优化性能
     *
     * @param <V>           主表对应的实体类VO
     * @param <S>           副表对应的实体类DO
     * @param <R>           主表外键副表主键对应的数据类型
     * @param mData         主表对应的VO集合实例
     * @param fkColumn      主表关联外键列（字段） 方法引用表示
     * @param vData         副表对应DO集合实例
     * @param pkColumn      副表对应的主键（字段）方法引用表示
     * @param injectColumns 需要注入的列（字段） 方法引用表示
     */
    @SafeVarargs
    public static <V, S, R> void injectField(List<V> mData, SFunction<V, R> fkColumn, List<S> vData, SFunction<S, R> pkColumn, SFunction<V, ? extends Serializable>... injectColumns) {
        // 如果集合元素个数不为空 则进行后续操作
        if (vData.size() > 0 && injectColumns.length > 0) {
            // 主表关联外键对应的字段名字符串
            String fieldName = RefUtils.getFiledName(fkColumn);
            // 将list转换为map 其中Key为副表主键 Value为副表类型实例本身
            Map<R, S> map = EntityUtils.toMap(vData, pkColumn, e -> e);
            doInjectField(mData, fieldName, map, RefUtils.getFiledNames(injectColumns));
        }
    }

    /**
     * @param vData        副表对应的VO集合实例
     * @param pkColumn     副表对应的主键（字段）方法引用表示
     * @param mData        主表对应的DO集合实例
     * @param fkColumn     主表关联外键列（字段） 方法引用表示
     * @param injectColumn 需要注入的列（字段） 方法引用表示
     * @param <V>          副表表对应的实体类VO
     * @param <S>          主表对应的实体类DO
     * @param <R>          主表外键副表主键对应的数据类型
     * @param <W>          需要注入属性的列泛型
     */
    public static <V, S, R, W> void injectListField(List<V> vData, SFunction<V, R> pkColumn, List<S> mData, SFunction<S, R> fkColumn, SFunction<V, W> injectColumn) {
        // 如果集合元素个数不为空 则进行后续操作
        if (mData.size() > 0 && injectColumn != null) {
            // 主表关联外键对应的字段名字符串
            String fieldName = RefUtils.getFiledName(pkColumn);
            // 将list转换为map 其中Key为副表主键 Value为副表类型实例本身
            Map<R, List<S>> map = EntityUtils.groupBy(mData, fkColumn);

            doInjectField(vData, fieldName, map, RefUtils.getFiledName(injectColumn));
        }
    }


    /**
     * 通过反射的方式给关联表查询注入属性值
     * 需要说明的是 相较于手动编码 反射的执行效率略差
     * 本方法能够提高开发效率 后续考虑逆向工程优化性能
     *
     * @param page          主表对应的VO分页实例
     * @param fkColumn      主表关联外键列（字段） 方法引用表示
     * @param clazz         副表对应的IService实例
     * @param pkColumn      副表对应的主键（字段）方法引用表示
     * @param injectColumns 需要注入的列（字段） 方法引用表示
     * @param <T>           主表对应的实体类VO
     * @param <S>           副表对应的实体类DO
     * @param <R>           主表外键副表主键对应的数据类型
     */
    @SafeVarargs
    public static <T, S, R> void injectField(IPage<T> page, final SFunction<T, R> fkColumn, Class<? extends IService<S>> clazz, final SFunction<S, R> pkColumn, SFunction<T, ? extends Serializable>... injectColumns) {
        List<T> records = page.getRecords();
        injectField(records, fkColumn, clazz, pkColumn, injectColumns);
    }

    /**
     * <p>此方法适用于一对多查询列表属性注入</p>
     * <p>主副表的定义：主表是指包含外键的表</p>
     *
     * @param page         副表实体类VO的分页实例
     * @param pkColumn     副表实体类主键列（方法引用表示）
     * @param clazz        主表对应的IService实现类Class对象
     * @param fkColumn     主表实体类外键列（方法引用表示）
     * @param injectColumn 需要注入的列（字段） 方法引用表示
     * @param <V>          副表实体类VO泛型
     * @param <R>          主表外键、副表主键对应的数据类型p
     * @param <S>          主表对应的实体类DO
     * @param <W>          需要注入属性的列泛型
     */
    public static <V, R, S, W> void injectListField(IPage<V> page, SFunction<V, R> pkColumn, Class<? extends IService<S>> clazz, SFunction<S, R> fkColumn, SFunction<V, W> injectColumn) {
        List<V> records = page.getRecords();
        injectListField(records, pkColumn, clazz, fkColumn, injectColumn);
    }


    @SafeVarargs
    public static <T, S, R, W> void injectField2(List<T> data, final SFunction<T, R> fkColumn, Class<? extends IService<S>> clazz, final SFunction<S, R> pkColumn, SFunction<T, ? extends Serializable>... injectColumns) {
        IService<S> iService = SpringUtils.getBean(clazz);
        // 获取主表关联外键的集合（去重）
        Set<? extends R> ids = EntityUtils.toSet(data, fkColumn);
        // 如果集合元素个数不为空 则进行后续操作
        if (ids.size() > 0 && injectColumns.length > 0) {
            // 主表关联外键对应的字段名字符串
            String fieldName = RefUtils.getFiledName(fkColumn);
            // 构造副表查询条件(查询整行元素)
            LambdaQueryWrapper<S> wrapper = Wrappers.lambdaQuery(iService.getEntityClass()).in(pkColumn, ids);
            // 通过主表的外键查询关联副表符合条件的数据
            List<S> list = iService.getBaseMapper().selectList(wrapper);
            // 将list转换为map 其中Key为副表主键 Value为副表类型实例本身
            Map<R, S> map = EntityUtils.toMap(list, pkColumn, e -> e);
            doInjectField(data, fieldName, map, RefUtils.getFiledNames(injectColumns));
        }
    }


    /**
     * 具体执行属性复制逻辑 原理使用反射 确保仅复制指定字段 严格保证数据正确性
     *
     * @param data       含有空值属性的对象集合
     * @param fieldName  主表关联外键字段名
     * @param map        副表主键与当前对象Map
     * @param filedNames 需要注入属性的字段（驼峰表示）
     * @param <T>        主表VO泛型
     * @param <S>        副表DO泛型
     * @param <R>        主表关联外键、副表主键的类型泛型
     */
    private static <T, S, R> void doInjectField(List<T> data, String fieldName, Map<R, S> map, List<String> filedNames) {
        if (fieldName != null && filedNames.size() > 0) {
            for (T t : data) {
                // 获取当前主表VO对象实例关联外键的值
                R r = RefUtils.getFieldValue(t, fieldName);
                // 从map中取出关联副表的对象实例
                S s = map.get(r);
                // 使用Spring内置的属性复制方法
                BeanCopyUtils.copyProperties(s, t, filedNames);
            }
        }
    }


    private static <V, S, R> void doInjectField(List<V> data, String pkFieldName, Map<R, List<S>> map, String injectFieldName) {
        if (pkFieldName != null && injectFieldName != null) {
            for (V v : data) {
                // 获取当前主表VO对象实例关联外键的值
                R r = RefUtils.getFieldValue(v, pkFieldName);
                // 从map中取出关联副表的对象实例
                List<S> s = map.get(r);
                // 使用反射给属性赋值
                RefUtils.setFiledValue(v, injectFieldName, s);
            }
        }
    }


    /**
     * 从参数map中取值，然后将指定的属性字段injectFieldName的值 注入到目标对象t中
     *
     * @param t               待注入属性值的对象
     * @param map             Value值为原复制对象的{@code Map}
     * @param keyFieldName    {@code Map}中Key对应的字段名称（非字段值）
     * @param injectFieldName 待注入属性名称
     * @param <T>             目标复制对象泛型
     * @param <K>             {@code Map}中Key对应的类型泛型
     * @param <V>             {@code Map}中Value对应的类型泛型
     */
    private static <T, K extends Serializable, V extends Serializable> void doInjectField(T t, Map<K, V> map, String keyFieldName, String injectFieldName) {
        // 使用反射 通过属性名 取出属性值
        K k = RefUtils.getFieldValue(t, keyFieldName);
        // 通过Key属性值 从map中取出Value值
        V v = map.get(k);
        // 使用反射 将@{code v}的值复制到 复制给目标对象@{code t}的@{code injectFieldName}字段中
        RefUtils.setFiledValue(t, injectFieldName, v);
    }


    /**
     * 聚合查询 属性注入 重载方法一
     *
     * @param <AR>        用户表聚合实体类泛型
     * @param <K>         用户表聚合实体类（AR）Key字段数据类型 泛型
     * @param <V>         用户表聚合实体类（AR）Value字段数据类型 泛型
     * @param <VO>        部门VO实体类泛型
     * @param keyAction   用户表聚合实体类（AR）Key字段（方法引用表示）
     * @param valueAction 用户表聚合实体类（AR）Value字段（方法引用表示）
     * @return 部门VO实体类集合
     */
    public static <AR, K extends Serializable, V extends Serializable, VO> List<VO> injectField(List<VO> voData, Map<K, V> map, SFunction<AR, K> keyAction, SFunction<AR, V> valueAction) {
        return ArWrapper.injectField(voData, map, keyAction, valueAction);
    }

    /**
     * 聚合查询 属性注入 重载方法二
     *
     * @param <AR>        用户表聚合实体类泛型
     * @param <K>         用户表聚合实体类（AR）Key字段数据类型 泛型
     * @param <V>         用户表聚合实体类（AR）Value字段数据类型 泛型
     * @param <VO>        部门VO实体类泛型
     * @param arData      用户表聚合实体类（AR）集合实例
     * @param keyAction   用户表聚合实体类（AR）Key字段（方法引用表示）
     * @param valueAction 用户表聚合实体类（AR）Value字段（方法引用表示）
     * @return 部门VO实体类集合
     */
    public static <AR, K extends Serializable, V extends Serializable, VO> List<VO> injectField(List<VO> voData, List<AR> arData, SFunction<AR, K> keyAction, SFunction<AR, V> valueAction) {
        return ArWrapper.injectField(voData, arData, keyAction, valueAction);
    }

    /**
     * 聚合查询 属性注入 重载方法三
     *
     * @param <AR>        用户表聚合实体类泛型
     * @param <K>         用户表聚合实体类（AR）Key字段数据类型 泛型
     * @param <V>         用户表聚合实体类（AR）Value字段数据类型 泛型
     * @param <S>         部门实体类泛型
     * @param <VO>        部门VO实体类泛型
     * @param voClazz     部门VO Class对象实例
     * @param arData      用户表聚合实体类（AR）集合实例
     * @param keyAction   用户表聚合实体类（AR）Key字段（方法引用表示）
     * @param valueAction 用户表聚合实体类（AR）Value字段（方法引用表示）
     * @return 部门VO实体类集合
     */
    public static <AR, K extends Serializable, V extends Serializable, S, VO> List<VO> injectField(List<S> sData, Class<VO> voClazz, List<AR> arData, SFunction<AR, K> keyAction, SFunction<AR, V> valueAction) {
        return ArWrapper.injectField(sData, voClazz, arData, keyAction, valueAction);
    }

    /**
     * 聚合查询 属性注入 重载方法四
     *
     * @param <AR>         用户表聚合实体类泛型
     * @param <K>          用户表聚合实体类（AR）Key字段数据类型 泛型
     * @param <V>          用户表聚合实体类（AR）Value字段数据类型 泛型
     * @param <S>          部门实体类泛型
     * @param <VO>         部门VO实体类泛型
     * @param serviceClazz 部门表Service Class对象实例
     * @param voClazz      部门VO Class对象实例
     * @param arData       用户表聚合实体类（AR）集合实例
     * @param keyAction    用户表聚合实体类（AR）Key字段（方法引用表示）
     * @param valueAction  用户表聚合实体类（AR）Value字段（方法引用表示）
     * @return 部门VO实体类集合
     */
    public static <AR, K extends Serializable, V extends Serializable, S, VO extends AR> List<VO> injectField(Class<? extends IService<S>> serviceClazz, Class<VO> voClazz, List<AR> arData, SFunction<AR, K> keyAction, SFunction<AR, V> valueAction) {
        return ArWrapper.injectField(serviceClazz, voClazz, arData, keyAction, valueAction);
    }

    /**
     * 聚合查询属性注入具体实现
     */
    private static class ArWrapper {
        /**
         * 重载方法一
         *
         * @param <AR>        用户表聚合实体类泛型
         * @param <K>         用户表聚合实体类（AR）Key字段数据类型 泛型
         * @param <V>         用户表聚合实体类（AR）Value字段数据类型 泛型
         * @param <VO>        部门VO实体类泛型
         * @param keyAction   用户表聚合实体类（AR）Key字段（方法引用表示）
         * @param valueAction 用户表聚合实体类（AR）Value字段（方法引用表示）
         * @return 部门VO实体类集合
         */
        public static <AR, K extends Serializable, V extends Serializable, VO> List<VO> injectField(List<VO> voData, Map<K, V> map, SFunction<AR, K> keyAction, SFunction<AR, V> valueAction) {
            List<VO> rs = new ArrayList<>();
            String keyFieldName = RefUtils.getFiledName(keyAction);
            String injectFieldName = RefUtils.getFiledName(valueAction);
            for (VO vo : voData) {
                doInjectField(vo, map, keyFieldName, injectFieldName);
                rs.add(vo);
            }
            return rs;
        }


        /**
         * 重载方法二
         *
         * @param <AR>        用户表聚合实体类泛型
         * @param <K>         用户表聚合实体类（AR）Key字段数据类型 泛型
         * @param <V>         用户表聚合实体类（AR）Value字段数据类型 泛型
         * @param <VO>        部门VO实体类泛型
         * @param arData      用户表聚合实体类（AR）集合实例
         * @param keyAction   用户表聚合实体类（AR）Key字段（方法引用表示）
         * @param valueAction 用户表聚合实体类（AR）Value字段（方法引用表示）
         * @return 部门VO实体类集合
         */
        public static <AR, K extends Serializable, V extends Serializable, VO> List<VO> injectField(List<VO> voData, List<AR> arData, SFunction<AR, K> keyAction, SFunction<AR, V> valueAction) {
            Map<K, V> map = EntityUtils.toMap(arData, keyAction, valueAction);
            return injectField(voData, map, keyAction, valueAction);
        }

        /**
         * 重载方法三
         *
         * @param <AR>        用户表聚合实体类泛型
         * @param <K>         用户表聚合实体类（AR）Key字段数据类型 泛型
         * @param <V>         用户表聚合实体类（AR）Value字段数据类型 泛型
         * @param <S>         部门实体类泛型
         * @param <VO>        部门VO实体类泛型
         * @param voClazz     部门VO Class对象实例
         * @param arData      用户表聚合实体类（AR）集合实例
         * @param keyAction   用户表聚合实体类（AR）Key字段（方法引用表示）
         * @param valueAction 用户表聚合实体类（AR）Value字段（方法引用表示）
         * @return 部门VO实体类集合
         */
        public static <AR, K extends Serializable, V extends Serializable, S, VO> List<VO> injectField(List<S> sData, Class<VO> voClazz, List<AR> arData, SFunction<AR, K> keyAction, SFunction<AR, V> valueAction) {
            List<VO> voList = EntityUtils.toList(sData, e -> BeanCopyUtils.copyProperties(e, voClazz));
            return injectField(voList, arData, keyAction, valueAction);
        }

        /**
         * 重载方法四
         *
         * @param <AR>         用户表聚合实体类泛型
         * @param <K>          用户表聚合实体类（AR）Key字段数据类型 泛型
         * @param <V>          用户表聚合实体类（AR）Value字段数据类型 泛型
         * @param <S>          部门实体类泛型
         * @param <VO>         部门VO实体类泛型
         * @param serviceClazz 部门表Service Class对象实例
         * @param voClazz      部门VO Class对象实例
         * @param arData       用户表聚合实体类（AR）集合实例
         * @param keyAction    用户表聚合实体类（AR）Key字段（方法引用表示）
         * @param valueAction  用户表聚合实体类（AR）Value字段（方法引用表示）
         * @return 部门VO实体类集合
         */
        public static <AR, K extends Serializable, V extends Serializable, S, VO extends AR> List<VO> injectField(Class<? extends IService<S>> serviceClazz, Class<VO> voClazz, List<AR> arData, SFunction<AR, K> keyAction, SFunction<AR, V> valueAction) {
            Set<K> deptIds = EntityUtils.toSet(arData, keyAction);
            if (deptIds.size() > 0) {
                IService<S> service = SpringUtils.getBean(serviceClazz);
                List<S> sData = service.getBaseMapper().selectBatchIds(deptIds);
                return injectField(sData, voClazz, arData, keyAction, valueAction);
            }
            return null;
        }


        /**
         * 从参数map中取值，然后将指定的属性字段injectFieldName的值 注入到目标对象t中
         *
         * @param t               待注入属性值的对象
         * @param map             Value值为原复制对象的{@code Map}
         * @param keyFieldName    {@code Map}中Key对应的字段名称（非字段值）
         * @param injectFieldName 待注入属性名称
         * @param <T>             目标复制对象泛型
         * @param <K>             {@code Map}中Key对应的类型泛型
         * @param <V>             {@code Map}中Value对应的类型泛型
         */
        public static <T, K extends Serializable, V extends Serializable> void doInjectField(T t, Map<K, V> map, String keyFieldName, String injectFieldName) {
            // 使用反射 通过属性名 取出属性值
            K k = RefUtils.getFieldValue(t, keyFieldName);
            // 通过Key属性值 从map中取出Value值
            V v = map.get(k);
            // 使用反射 将@{code v}的值复制到 复制给目标对象@{code t}的@{code injectFieldName}字段中
            RefUtils.setFiledValue(t, injectFieldName, v);
        }
    }

}
