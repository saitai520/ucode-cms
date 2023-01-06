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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import xin.altitude.cms.common.util.MD5Utils;
import xin.altitude.cms.common.util.ServletUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2022/06/13 15:24
 **/
public class DocUtils {
    private static final String KEY = "data";
    private static final String SUFFIX = ".doc";

    public static <T> void fillWord(String path, Map<String, T> dataMap, String... fileName) {
        /* 从ClassPath路径中获取模版内容字符串 */
        String templateContent = CommonDocUtils.readTemplate(path);
        /* 将数据填充进模版，生成新的字符串 */
        String content = doProcess(templateContent, dataMap);
        /* 将填充后的字符串以word附件的形式返回 */
        returnWord(content, fileName);
    }

    public static <T> void fillWord(String path, T data, String... fileName) {
        Map<String, T> dataMap = createMap(data);
        /* 从ClassPath路径中获取模版内容字符串 */
        String templateContent = CommonDocUtils.readTemplate(path);
        /* 将数据填充进模版，生成新的字符串 */
        String content = doProcess(templateContent, dataMap);
        /* 将填充后的字符串以word附件的形式返回 */
        returnWord(content, fileName);
    }

    private static <T> Map<String, T> createMap(T data) {
        Map<String, T> dataMap = new HashMap<>();
        dataMap.put(KEY, data);
        return dataMap;
    }

    public static <T> void fillWord(String path, List<T> data, String... fileName) {
        Map<String, List<T>> dataMap = createMap(data);
        /* 从ClassPath路径中获取模版内容字符串 */
        String templateContent = CommonDocUtils.readTemplate(path);
        /* 将数据填充进模版，生成新的字符串 */
        String content = doProcess(templateContent, dataMap);
        /* 将填充后的字符串以word附件的形式返回 */
        returnWord(content, fileName);
    }

    private static <T> Map<String, List<T>> createMap(List<T> data) {
        Map<String, List<T>> dataMap = new HashMap<>();
        dataMap.put(KEY, data);
        return dataMap;
    }

    public static <T> void fillXml(String path, T data) {
        Map<String, T> dataMap = createMap(data);
        /* 从ClassPath路径中获取模版内容字符串 */
        String templateContent = CommonDocUtils.readTemplate(path);
        /* 将数据填充进模版，生成新的字符串 */
        String content = doProcess(templateContent, dataMap);
        /* 将填充后的字符串以XML字符串的形式返回 */
        returnXml(content);
    }

    public static <T> void fillXml(String path, List<T> data) {
        Map<String, List<T>> dataMap = createMap(data);
        /* 从ClassPath路径中获取模版内容字符串 */
        String templateContent = CommonDocUtils.readTemplate(path);
        /* 将数据填充进模版，生成新的字符串 */
        String content = doProcess(templateContent, dataMap);
        /* 将填充后的字符串以XML字符串的形式返回 */
        returnXml(content);
    }

    /**
     * 将模版和数据混合填充，生成解析后的字符串
     *
     * @param path 模版路径（相对于resource目录地址）
     * @param data 动态数据（集合类型）
     * @param <T>  动态数据的类型
     * @return 将动态数据填充到模版后的新字符串
     */
    public static <T> String resolveString(String path, List<T> data) {
        Map<String, List<T>> dataMap = createMap(data);
        String templateContent = CommonDocUtils.readTemplate(path);
        return doProcess(templateContent, dataMap);
    }

    /**
     * 将模版和数据混合填充，生成解析后的字符串
     *
     * @param path 模版路径（相对于resource目录地址）
     * @param data 动态数据（对象类型）
     * @param <T>  动态数据的类型
     * @return 将动态数据填充到模版后的新字符串
     */
    public static <T> String resolveString(String path, T data) {
        Map<String, T> dataMap = createMap(data);
        String templateContent = CommonDocUtils.readTemplate(path);
        return doProcess(templateContent, dataMap);
    }


    /**
     * 将XML文件内容字符串以文本的形式返回
     *
     * @param text XML文本字符串
     */
    public static void returnXml(String text) {
        HttpServletResponse response = ServletUtils.getResponse();
        try {
            response.setContentType("text/xml");
            response.getOutputStream().write(text.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将XML文件内容字符串以Word（doc）附件的形式返回
     *
     * @param text     XML文本字符串
     * @param fileName 下载文件文件名
     */
    public static void returnWord(String text, String... fileName) {
        HttpServletResponse response = ServletUtils.getResponse();
        String content = null;
        response.setContentType("application/msword");

        if (fileName.length == 0) {
            content = "attachment; filename=" + System.currentTimeMillis() + SUFFIX;
        } else {
            try {
                content = "attachment; filename=" + URLEncoder.encode(fileName[0] + SUFFIX, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        response.setHeader("Content-Disposition", content);
        try {
            response.getOutputStream().write(text.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 解析字符串模板,通用方法
     *
     * @param templateContent 字符串模板
     * @param dataMap;        数据
     * @param configuration   配置
     * @return 解析后内容
     */
    private static <T> String doProcess(String templateContent, Map<String, T> dataMap, Configuration... configuration) {
        if (templateContent == null) {
            throw new RuntimeException("模版解析异常!");
        }
        if (configuration.length == 0) {
            configuration = new Configuration[]{new Configuration()};
        }
        StringWriter out = new StringWriter();
        try {
            String name = MD5Utils.md5(templateContent);
            new Template(name, new StringReader(templateContent), configuration[0]).process(dataMap, out);
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }


}
