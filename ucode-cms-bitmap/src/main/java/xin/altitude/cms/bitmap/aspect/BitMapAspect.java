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

package xin.altitude.cms.bitmap.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.altitude.cms.bitmap.annotation.BitMap;
import xin.altitude.cms.bitmap.util.ParserUtils;
import xin.altitude.cms.common.entity.AjaxResult;
import xin.altitude.cms.common.util.RedisBitMapUtils;

import java.lang.reflect.Method;
import java.util.TreeMap;

/**
 * Redis BitMap AOP
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2021/03/28 10:49
 **/
@Aspect
public class BitMapAspect {
    private static final Logger logger = LoggerFactory.getLogger(BitMapAspect.class);

    @Pointcut("@annotation(xin.altitude.cms.bitmap.annotation.BitMap)")
    public void aspect() {

    }

    @Around("aspect()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        BitMap annotation = method.getAnnotation(BitMap.class);
        TreeMap<String, Object> map = ParserUtils.createTreeMap(point, signature);
        String idString = ParserUtils.parse(annotation.id(), map);
        if (idString != null) {
            long id = Long.parseLong(idString);
            if (RedisBitMapUtils.isPresent(annotation.key(), id)) {
                return point.proceed();
            } else {
                logger.info(String.format("当前主键ID{%d}不存在", id));
                return method.getReturnType().equals(AjaxResult.class) ? AjaxResult.success() : null;
            }
        }
        throw new RuntimeException("主键ID解析不正确，请按照参考格式书写");
    }

}
