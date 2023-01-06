
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

import org.springframework.cglib.beans.BeanMap;
import xin.altitude.cms.common.model.KVModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * MapUtils工具类
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/06/03 22:49
 **/
public class MapUtils {
    private MapUtils() {
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        if (map == null) {
            return true;
        } else {
            return map.isEmpty();
        }
    }


    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    public static <K, V> void ifNotEmpty(Map<K, V> map, Consumer<Map<K, V>> consumer) {
        Objects.requireNonNull(consumer);
        if (isNotEmpty(map)) {
            consumer.accept(map);
        }
    }


    /**
     * 批量取出Map中的值
     *
     * @param map  map实例
     * @param keys 键的集合
     * @param <K>  key的泛型
     * @param <V>  value的泛型
     * @return value的泛型的集合
     */
    @SafeVarargs
    public static <K, V> List<V> getCollection(Map<K, V> map, K... keys) {
        Objects.requireNonNull(keys);
        return getCollection(map, Arrays.asList(keys));
    }

    /**
     * 批量取出Map中的值
     *
     * @param map  map实例
     * @param keys 键的集合
     * @param <K>  key的泛型
     * @param <V>  value的泛型
     * @return value的泛型的集合
     */
    public static <K, V> List<V> getCollection(Map<K, V> map, Collection<K> keys) {
        Objects.requireNonNull(keys);
        List<V> result = new ArrayList<>();
        ifNotEmpty(map, e -> keys.forEach(key -> Optional.ofNullable(e.get(key)).ifPresent(result::add)));
        return result;
    }

    /**
     * 批量取出Map中的值
     *
     * @param source     map实例
     * @param keys       键key集合
     * @param comparator 排序器
     * @param <K>        key的泛型
     * @param <V>        value的泛型
     * @return value的泛型的集合
     */
    public static <K, V> List<V> getCollection(Map<K, V> source, Collection<K> keys, Comparator<V> comparator) {
        List<V> result = getCollection(source, keys);
        Optional.ofNullable(comparator).ifPresent(result::sort);
        return result;
    }

    /**
     * 将Map转化成List
     *
     * @param source 原始Map实例
     * @param <K>    Key类型
     * @param <V>    Value类型
     * @return 返回KVModel类型集合
     */
    public static <K, V> List<KVModel<K, V>> mapToList(Map<K, V> source) {
        Objects.requireNonNull(source);
        List<KVModel<K, V>> result = source.entrySet().stream()
            .map(e -> new KVModel<>(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
        return result;
    }


    /**
     * 讲Map中 value进行转换
     *
     * @param map         原始Map实例
     * @param valueAction value转换的行为
     * @param <K>         Key的类型
     * @param <V>         原始value的类型
     * @param <R>         目标value类型
     * @return 转换后的Map
     */
    public static <K, V, R> Map<K, R> transMap(Map<K, V> map, Function<? super V, ? extends R> valueAction) {
        Objects.requireNonNull(valueAction);
        Map<K, R> hashMap = new HashMap<>(16);
        ifNotEmpty(map, e -> e.forEach((key, value) -> hashMap.put(key, EntityUtils.toObj(value, valueAction))));
        return hashMap;
    }

    public static <K, V, NK, NV> Map<NK, NV> transMap(Map<K, V> map, Function<? super K, ? extends NK> keyAction, Function<? super V, ? extends NV> valueAction) {
        Objects.requireNonNull(valueAction);
        Map<NK, NV> hashMap = new HashMap<>(16);
        ifNotEmpty(map, e -> e.forEach((key, value) -> hashMap.put(EntityUtils.toObj(key, keyAction), EntityUtils.toObj(value, valueAction))));
        return hashMap;
    }


    /**
     * 将{@link Map.Entry}集合实例转化成{@link Map}实例
     *
     * @param list {@code Map.Entry}集合实例
     * @param <K>  {@code Map}的Key类型
     * @param <V>  {@code Map}的Value类型
     * @return {@code Map}实例 如果输入集合为null或者空集合 则返回空{@code Map}实例
     */
    public static <E extends Map.Entry<K, V>, K, V> Map<K, V> transMap(Collection<E> list) {
        return transMap(list, (Predicate<E>) e -> true);
    }

    /**
     * 将{@link Map.Entry}集合实例转化成{@link Map}实例
     *
     * @param list {@code Map.Entry}集合实例
     * @param pred 断言器 用以辅助过滤数据
     * @param <K>  {@code Map}的Key类型
     * @param <V>  {@code Map}的Value类型
     * @return {@code Map}实例 如果输入集合为null或者空集合 则返回空{@code Map}实例
     */
    public static <E extends Map.Entry<K, V>, K, V> Map<K, V> transMap(Collection<E> list, Predicate<E> pred) {
        Objects.requireNonNull(pred);
        if (ColUtils.isNotEmpty(list)) {
            Map<K, V> map = new HashMap<>(16);
            list.stream().filter(pred).forEach(e -> map.put(e.getKey(), e.getValue()));
            return map;
        }
        return Collections.emptyMap();
    }


    /**
     * <p>将普通的集合 按照一定的映射关系转换后 生成新{@code Map}实例</p>
     *
     * @param list  {@code E}集合实例
     * @param acton 转换规则
     * @param <E>   {@code E}集合类型
     * @param <K>   {@code Map}的Key类型
     * @param <V>   {@code Map}的Value类型
     * @return {@code Map}实例 如果输入集合为null或者空集合 则返回空{@code Map}实例
     */
    public static <E, K, V> Map<K, V> transMap(Collection<E> list, Function<E, ? extends Map.Entry<K, V>> acton) {
        return transMap(EntityUtils.toList(list, acton));
    }


    /**
     * <p>从{@code Map}实例中取值 防止因{@code Map}实例为<code>null</code>而发生运行时空指针异常</p>
     * <p>如果{@code Map}实例为<code>null</code>，则返回<code>null</code></p>
     *
     * @param map {@code Map}实例 允许为<code>null</code>
     * @param key Key的值 允许为<code>null</code>
     * @param <K> Key的类型
     * @param <V> Value的类型
     * @return 从{@code Map}实例通过Key取出的Value值
     */
    public static <K, V> V getObj(Map<K, V> map, K key) {
        return Optional.ofNullable(map).map(e -> e.get(key)).orElse(null);
    }

    /**
     * <p>从{@code Map}实例中取值</p>
     * <p>防止因{@code Map}实例为<code>null</code>而发生运行时空指针异常</p>
     *
     * @param map          {@code Map}实例 允许为<code>null</code>
     * @param key          Key的值 允许为<code>null</code>
     * @param defaultValue 默认值 允许为<code>null</code>
     * @param <K>          Key的类型
     * @param <V>          Value的类型
     * @return 从{@code Map}实例通过Key取出的Value值
     */
    public static <K, V> V getObj(Map<K, V> map, K key, V defaultValue) {
        return Optional.ofNullable(map).map(e -> e.get(key)).orElse(defaultValue);
    }


    /**
     * <p>从{@code Map}实例中取值 并将Value从{@code V}类型转化为{@code R}类型实例</p>
     * <p>防止因{@code Map}实例为<code>null</code>而发生运行时空指针异常</p>
     *
     * @param map    {@code Map}实例 允许为<code>null</code>
     * @param key    Key的值 允许为<code>null</code>
     * @param action Value转换规则
     * @param <K>    Key的类型
     * @param <V>    Value的类型
     * @return 从{@code Map}实例通过Key取出的Value值
     */
    public static <K, V, R> R getObj(Map<K, V> map, K key, Function<V, R> action) {
        Objects.requireNonNull(action);
        return Optional.ofNullable(map).map(e -> e.get(key)).map(action).orElse(null);
    }

}
