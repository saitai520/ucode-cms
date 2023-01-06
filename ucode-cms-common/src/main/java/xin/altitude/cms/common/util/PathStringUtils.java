

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

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 有关路径的工具类
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/01/10 19:20
 **/
public class PathStringUtils {
    private final static String PATTERN = String.format("yyyy%sMM%sdd", File.separatorChar, File.separatorChar);

    /**
     * 获取日期格式文件目录
     *
     * @return 日期路径
     */
    public static String createDateDir() {
        return createDateDir(LocalDate.now());
    }

    /**
     * 获取日期格式文件目录
     *
     * @param localDate LocalDate实例
     * @return 日期路径
     */
    public static String createDateDir(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(PATTERN));
    }
}
