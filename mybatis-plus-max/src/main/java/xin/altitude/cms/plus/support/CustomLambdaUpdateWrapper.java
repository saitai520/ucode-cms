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

package xin.altitude.cms.plus.support;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

/**
 * <p>自定义{@link com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper}更新包装器</p>
 * <p>用以实现Lambda风格的<i>自增</i>、<i>自减</i>更新操作</p>
 * <p>避免在Java代码层面出现任何形式的表字段<i>魔法值</i></p>
 *
 * <pre>
 *    public boolean updateUserAge(Long userId) {
 *         CustomLambdaUpdateWrapper&lt;User&gt; wrapper = new CustomLambdaUpdateWrapper&lt;&gt;();
 *         wrapper.incr(User::getAge, 1).eq(User::getUserId, userId);
 *         return update(wrapper);
 *     }
 * </pre>
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
public class CustomLambdaUpdateWrapper<T> extends LambdaUpdateWrapper<T> {

    /**
     * 通过构造器创建{@code CustomLambdaUpdateWrapper}更新包装器实例
     *
     * @param entityClass 实体类{@code Class}对象
     */
    public CustomLambdaUpdateWrapper(Class<T> entityClass) {
        super(entityClass);
    }

    /**
     * 通过构造器创建{@code CustomLambdaUpdateWrapper}更新包装器实例
     *
     * @param entity 实体类对象
     */
    public CustomLambdaUpdateWrapper(T entity) {
        super(entity);
    }

    /**
     * 通过构造器创建{@code CustomLambdaUpdateWrapper}更新包装器实例
     */
    public CustomLambdaUpdateWrapper() {
        super();
    }

    /**
     * <p>指定列自增 默认自增长值为<i>1</i></p>
     * <p>需选用具有具备四则运算的数字类型类 不可选择字符串列</p>
     *
     * @param column 列引用
     */
    public CustomLambdaUpdateWrapper<T> incr(SFunction<T, ? extends Number> column) {
        return incr(column, 1);
    }

    /**
     * <p>指定列自增 自定义增长值</p>
     * <p>需选用具有具备四则运算的数字类型类 不可选择字符串列</p>
     * <p>自增长值不宜过大 合适选择增长值</p>
     *
     * @param column 列引用
     * @param value  增长值 增长值小于或者等于0 将忽略本次更新
     */
    public CustomLambdaUpdateWrapper<T> incr(SFunction<T, ? extends Number> column, int value) {
        String s = super.columnToString(column);
        int max = Math.max(value, 0);
        setSql(max > 0, String.format("%s =  %s + %d", s, s, max));
        return this;
    }

    /**
     * <p>指定列自减 默认自减少值为<i>1</i></p>
     * <p>需选用具有具备四则运算的数字类型类 不可选择字符串列</p>
     *
     * @param column 列引用
     */
    public CustomLambdaUpdateWrapper<T> desc(SFunction<T, ? extends Number> column) {
        return desc(column, 1);
    }

    /**
     * <p>指定列自减 自定义自减值</p>
     * <p>需选用具有具备四则运算的数字类型类 不可选择字符串列</p>
     * <p>自减少值不宜过大 合适选择增长值</p>
     *
     * @param column 列引用
     * @param value  减少值 减少值小于或者等于0 将忽略本次更新
     */
    public CustomLambdaUpdateWrapper<T> desc(SFunction<T, ? extends Number> column, int value) {
        String s = super.columnToString(column);
        int max = Math.max(value, 0);
        setSql(max > 0, String.format("%s =  %s - %d", s, s, max));
        return this;
    }
}
