

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

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
public class ResourceUtils {
    private static final PathMatchingResourcePatternResolver RESOLVER = new PathMatchingResourcePatternResolver();

    private ResourceUtils() {
    }

    /**
     * 解析mapperLocations
     *
     * @param locations
     * @return
     */
    public static Resource[] resolveMapperLocations(String locations) {
        Optional<String> optional = Optional.ofNullable(locations);
        if (optional.isPresent()) {
            return resolveMapperLocations(StringUtil.split(locations, ","));
        }
        return new Resource[0];
    }

    /**
     * 解析mapperLocations
     *
     * @param locations
     * @return
     */
    public static Resource[] resolveMapperLocations(String[] locations) {
        List<Resource> resources = new ArrayList<>();
        if (locations != null) {
            Arrays.stream(locations).forEach(mapperLocation -> {
                try {
                    resources.addAll(Arrays.asList(RESOLVER.getResources(mapperLocation)));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
        }
        return resources.toArray(new Resource[0]);
    }

    /**
     * configLocation
     *
     * @param configLocation
     * @return
     */
    public static Resource resolveConfigLocation(String configLocation) {
        return RESOLVER.getResource(configLocation);
    }


    /**
     * 以相对文件路径的方式读取{@code resource}资源目录下的文件资源
     *
     * @param fileLocation 文件相对路径 比方说{@code resource}下的"file.txt"文件 那么参数便为"file.txt"
     * @return 文件的字符内容
     */
    public static String readFileContent(String fileLocation) {
        try (InputStream is = resolveConfigLocation(fileLocation).getInputStream()) {
            // StringWriter sw = new StringWriter();
            // IOUtils.copy(is, sw, StandardCharsets.UTF_8);

            return new String(FileCopyUtils.copyToByteArray(is),StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(String.format("相对路径{%s}文件读取失败", fileLocation));
        }
    }
}
