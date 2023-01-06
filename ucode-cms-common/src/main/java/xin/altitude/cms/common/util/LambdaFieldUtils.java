package xin.altitude.cms.common.util;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
public class LambdaFieldUtils {


    public static <T> String getFieldName(SFunction<T, ?> column) {
        try {
            // 通过获取对象方法，判断是否存在该方法
            Method method = column.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            // 利用jdk的SerializedLambda 解析方法引用
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(column);
            String methodName = serializedLambda.getImplMethodName();
            return resolveFieldName(methodName);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static String resolveFieldName(String methodName) {
        if (methodName.startsWith("get")) {
            return firstToLowerCase(methodName.substring(3));
        } else if (methodName.startsWith("is")) {
            return firstToLowerCase(methodName.substring(2));
        }
        return methodName;
    }

    private static String firstToLowerCase(String s) {
        return Optional.ofNullable(s)
            .map(e -> e.substring(0, 1).toLowerCase() + e.substring(1))
            .orElse(null);
    }
}

