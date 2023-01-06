package xin.altitude.cms.common.util;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author 赛先生和泰先生
 * @author 笔者专题技术博客 —— http://www.altitude.xin
 * @author B站视频 —— https://space.bilibili.com/1936685014
 **/
public class RefUtils {
    private RefUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <T, R> R getFiledValue(T t, SFunction<T, R> action) {
        String fieldName = Optional.ofNullable(getFiledName(action)).orElse("");
        try {
            Field field = t.getClass().getField(fieldName);
            return (R) field.get(t);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 通过反射 给属性赋值
     *
     * @param t
     * @param action
     * @param value
     * @param <S>
     * @param <RR>
     */
    public static <T, S, RR> void setFiledValue(T t, SFunction<S, RR> action, Object value) {
        String fieldName = Optional.ofNullable(getFiledName(action)).orElse("");
        try {
            Field field = t.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(t, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void setFiledValue(T t, String fieldName, Object value) {
        try {
            Field field = t.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(t, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void setFiledValue(T t, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(t, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 通过方法引用获取指定实体类的字段名（属性名）
     *
     * @param action
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> String getFiledName(SFunction<T, R> action) {
        return Optional.ofNullable(action).map(LambdaUtils::extract)
            .map(LambdaMeta::getImplMethodName)
            .map(PropertyNamer::methodToProperty).orElse(null);
    }

    // /**
    //  * 通过方法引用获取指定实体类的字段名（属性名）
    //  *
    //  * @param <T>
    //  * @param <R>
    //  * @param action
    //  * @return
    //  */
    // @SafeVarargs
    // public static <T, R> List<String> getFiledNames(SFunction<T, R>... action) {
    //     return Arrays.stream(action).map(LambdaUtils::extract)
    //         .map(LambdaMeta::getImplMethodName)
    //         .map(PropertyNamer::methodToProperty)
    //         .collect(Collectors.toList());
    // }


    @SafeVarargs
    public static <T> List<String> getFiledNames(SFunction<T, ? extends Serializable>... action) {
        return Arrays.stream(action).map(LambdaUtils::extract)
            .map(LambdaMeta::getImplMethodName)
            .map(PropertyNamer::methodToProperty)
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object entity, String fieldName) {
        return (T) ReflectionKit.getFieldValue(entity, fieldName);
    }


    public static Object getFieldValue(Object entity, Field field) {
        field.setAccessible(true);
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 通过Clazz对象创建实例
     *
     * @param clazz CLass对象
     * @param <T>   泛型
     * @return 泛型实例
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得参数为{@code doClazz}的构造器
     *
     * @param doClazz DO实体类的CLass对象实例
     * @param voClazz VO实体类的CLass对象实例
     * @param <VO>    VO实体类泛型
     * @return VO实体类的构造器
     */
    // @SafeVarargs
    public static <VO> Constructor<VO> getConstructor(Class<VO> voClazz, Class<?>... doClazz) {
        Objects.requireNonNull(doClazz);
        try {
            return voClazz.getConstructor(doClazz);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过构造器创建对象
     *
     * @param constructor 以泛型{@code VO}为类型的构造器实例
     * @param initargs    以泛型{@code DO}为类型的参数实例
     * @param <DO>        {@code DO}泛型
     * @param <VO>        {@code VO}泛型
     * @return 以泛型{@code VO}为类型的对象实例
     */
    @SafeVarargs
    public static <DO, VO extends DO> VO newInstance(Constructor<VO> constructor, DO... initargs) {
        try {
            return constructor.newInstance(initargs);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>显示化获得{@code Class<T>}对象的类型</p>
     * <p>本方法的作用时避免在显示强转时出现<i>未检查警告</i></p>
     * <p>注意{@code Class<\?>}与{@code Class<T>}是同一个类型才能强转</p>
     *
     * @param clazz Class对象实例
     * @param <T>   元素类型
     * @return 如果参数<code>clazz</code>不为<code>null</code>，则返回强转后的对象，否则返回<code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(Class<?> clazz) {
        return (Class<T>) clazz;
    }
}
