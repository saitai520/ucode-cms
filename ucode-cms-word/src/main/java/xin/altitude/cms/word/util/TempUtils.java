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

package xin.altitude.cms.word.util;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2021/01/13 22:41
 **/
public class TempUtils {


    /**
     * 读取JSON文件
     *
     * @param path
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T readJsonObject(String path, Class<T> clazz) {
        // ClassPathResource classPathResource = new ClassPathResource(path);
        // InputStream inputStream = classPathResource.getStream();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("读取文件失败");
        } else {
            T json = null;
            try {
                json = JSON.parseObject(inputStream, clazz);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    public static <T> List<T> readJsonList(String path, Class<T> clazz) {
        // ClassPathResource classPathResource = new ClassPathResource(path);
        // InputStream inputStream = classPathResource.getStream();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("读取文件失败");
        } else {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Stream<String> streamOfString = new BufferedReader(inputStreamReader).lines();
            String streamToString = streamOfString.collect(Collectors.joining());
            return JSON.parseArray(streamToString, clazz);
        }
    }


}
