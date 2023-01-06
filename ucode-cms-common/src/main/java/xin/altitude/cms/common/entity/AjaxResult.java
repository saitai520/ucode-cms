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

package xin.altitude.cms.common.entity;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.springframework.http.HttpStatus;
import xin.altitude.cms.common.util.FieldFilterUtils;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * 操作消息提醒
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
public class AjaxResult extends LinkedHashMap<String, Object> {
    /**
     * 状态码
     */
    public static final String CODE_TAG = "code";
    /**
     * 返回内容
     */
    public static final String MSG_TAG = "msg";
    /**
     * 数据对象
     */
    public static final String DATA_TAG = "data";
    /**
     * 操作成功信息
     */
    protected static final String SUCCESS_MSG = "操作成功";

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    public AjaxResult() {
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public AjaxResult(int code, String msg) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    public AjaxResult(int code, String msg, Object data) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        Optional.ofNullable(data).ifPresent(e -> super.put(DATA_TAG, e));
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static AjaxResult success() {
        return new AjaxResult(HttpStatus.OK.value(), SUCCESS_MSG);
    }

    /**
     * 返回成功数据
     *
     * @param data 响应数据
     * @return 成功消息
     */
    public static AjaxResult success(Object data) {
        return new AjaxResult(HttpStatus.OK.value(), SUCCESS_MSG, data);
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxResult success(String msg, Object data) {
        return new AjaxResult(HttpStatus.OK.value(), msg, data);
    }

    /**
     * 完成对象实体类的属性过滤
     *
     * @param data   原始对象实例
     * @param action 方法引用选中需要过滤排除的列
     * @param <T>    原始数据类型
     * @return AjaxResult
     * @since 1.5.7
     */
    @SafeVarargs
    public static <T> AjaxResult success(T data, final SFunction<T, ? extends Serializable>... action) {
        return success(data, false, action);
    }

    /**
     * 完成对象实体类的属性过滤
     *
     * @param data      原始对象实例
     * @param isInclude 如果是true代表保留字段、false代表排除字段
     * @param action    方法引用选中需要过滤排除的列
     * @param <T>       原始数据类型
     * @return AjaxResult
     * @since 1.5.7.2
     */
    @SafeVarargs
    public static <T> AjaxResult success(T data, boolean isInclude, final SFunction<T, ? extends Serializable>... action) {
        return AjaxResult.success(SUCCESS_MSG, FieldFilterUtils.filterFields(data, isInclude, action));
    }

    /**
     * 完成列表对象实体类的属性过滤
     *
     * @param data   原始列表对象实例
     * @param action 方法引用选中需要过滤排除的列
     * @param <T>    原始数据类型
     * @return AjaxResult
     * @since 1.5.7
     */
    @SafeVarargs
    public static <T> AjaxResult success(List<T> data, final SFunction<T, ? extends Serializable>... action) {
        return success(data, false, action);
    }

    /**
     * 完成列表对象实体类的属性过滤
     *
     * @param data      原始列表对象实例
     * @param isInclude 如果是true代表保留字段、false代表排除字段
     * @param action    方法引用选中需要过滤排除的列
     * @param <T>       原始数据类型
     * @return AjaxResult
     * @since 1.5.7.2
     */
    @SafeVarargs
    public static <T> AjaxResult success(List<T> data, boolean isInclude, final SFunction<T, ? extends Serializable>... action) {
        return AjaxResult.success(SUCCESS_MSG, FieldFilterUtils.filterFields(data, isInclude, action));
    }

    /**
     * 完成分页对象实体类的属性过滤
     *
     * @param data   原始分页对象实例
     * @param action 方法引用选中需要过滤排除的列
     * @param <T>    原始数据类型
     * @return AjaxResult
     * @since 1.5.7
     */
    @SafeVarargs
    public static <T> AjaxResult success(IPage<T> data, final SFunction<T, ? extends Serializable>... action) {
        return success(data, false, action);
    }

    /**
     * 完成分页对象实体类的属性过滤
     *
     * @param data      原始分页对象实例
     * @param isInclude 如果是true代表保留字段、false代表排除字段
     * @param action    方法引用选中需要过滤排除的列
     * @param <T>       原始数据类型
     * @return AjaxResult
     * @since 1.5.7.2
     */
    @SafeVarargs
    public static <T> AjaxResult success(IPage<T> data, boolean isInclude, final SFunction<T, ? extends Serializable>... action) {
        return AjaxResult.success(SUCCESS_MSG, FieldFilterUtils.filterFields(data, isInclude, action));
    }


    /**
     * 返回错误消息
     *
     * @return 警告消息
     */
    public static AjaxResult error() {
        return AjaxResult.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxResult error(String msg) {
        return new AjaxResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static AjaxResult error(String msg, Object data) {
        return new AjaxResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static AjaxResult error(int code, String msg) {
        return new AjaxResult(code, msg);
    }
}
