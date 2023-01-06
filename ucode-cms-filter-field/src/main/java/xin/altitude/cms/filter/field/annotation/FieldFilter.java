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

package xin.altitude.cms.filter.field.annotation;

/**
 * 控制器字段过滤注解
 *
 * @author 赛先生和泰先生
 * @author 笔者专题技术博客 —— http://www.altitude.xin
 * @author B站视频 —— https://space.bilibili.com/1936685014
 **/


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldFilter {
    /**
     * <p>指示当前字段的过滤的具体行为：</p>
     * <p>如果为<code>true</code>则代表{@link #fieldNames()}是包含，所有非包含有效字段均会被过滤</p>
     * <p>如果为<code>false</code>则代表{@link #fieldNames()}是排除，所有包含有效字段均会被过滤</p>
     */
    boolean isInclude() default false;

    String fieldNames() default "createTime,updateTime";

}

