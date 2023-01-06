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

package xin.altitude.cms.filter.field.aspect;

/**
 * @author 赛先生和泰先生
 * @author 笔者专题技术博客 —— http://www.altitude.xin
 * @author B站视频 —— https://space.bilibili.com/1936685014
 **/

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import xin.altitude.cms.common.entity.AjaxResult;
import xin.altitude.cms.common.util.ColUtils;
import xin.altitude.cms.filter.field.annotation.FieldFilter;
import xin.altitude.cms.filter.field.util.HelpUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Aspect
public class FieldFilterAspect {

    @Pointcut("@annotation(xin.altitude.cms.filter.field.annotation.FieldFilter)")
    public void fieldFilterAspect() {
    }

    @Around("fieldFilterAspect()")
    public AjaxResult around(ProceedingJoinPoint pjp) throws Throwable {
        AjaxResult r = (AjaxResult) pjp.proceed();
        Object data = r.get(AjaxResult.DATA_TAG);
        if (data == null) {
            return r;
        }
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        FieldFilter annotation = method.getAnnotation(FieldFilter.class);
        String fields = annotation.fieldNames();
        List<String> fieldNames = Arrays.asList(fields.split(","));
        if (ColUtils.isNotEmpty(fieldNames)) {
            boolean isInclude = annotation.isInclude();
            if (data instanceof List) {
                r.put("data", HelpUtils.filterFields((List<?>) data, isInclude, fieldNames));
            } else {
                r.put("data", HelpUtils.filterFields(data, isInclude, fieldNames));
            }
        }

        return r;
    }
}

