package xin.altitude.cms.common.util;

import xin.altitude.cms.common.lang.AbstractObject;

/**
 * Clone工具类
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2021/03/24 19:49
 **/
public class CloneUtils {
    private CloneUtils() {
    }

    /**
     * 使用原生clone方法复制对象
     *
     * @param obj 原始对象实例
     * @param <T> 原始对象泛型
     * @return 原始对象的深拷贝
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractObject> T clone(T obj) {
        if (obj != null) {
            return (T) obj.clone();
        }
        return null;
    }
}
