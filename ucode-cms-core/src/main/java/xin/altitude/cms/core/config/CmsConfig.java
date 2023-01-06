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

package xin.altitude.cms.core.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Paths;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/12/22 17:09
 **/
@Data
@ConfigurationProperties(prefix = "ucode")
@ConditionalOnMissingBean(value = xin.altitude.cms.core.config.CmsConfig.class)
public class CmsConfig {
    // public final static String UNIFORM_PREFIX = "";
    private Cms cms = new CmsConfig.Cms();
    private Token token = new CmsConfig.Token();
    private Xss xss = new CmsConfig.Xss();
    private Swagger swagger = new CmsConfig.Swagger();
    private Job job = new CmsConfig.Job();
    private Thread thread = new CmsConfig.Thread();

    @Data
    public static class Cms {
        /* 上传路径 */
        private String profile;
        /* 获取地址开关 */
        private boolean addressEnabled;
        /* 项目名称 */
        private String name;
        /* 版本 */
        private String version;
        /* 版权年份 */
        private String copyrightYear;
        /* 实例演示开关 */
        private boolean demoEnabled;
        /* 启用认证开关 */
        private boolean authEnabled;
        /* 是否开启验证码 */
        private boolean captchaEnabled;
        /* 验证码类型 */
        private String captchaType;

        /**
         * 获取导入上传路径
         */
        public String getImportPath() {
            // return FilenameUtils.concat(getProfile(), "import");
            return Paths.get(getProfile(), "import").toString();
        }

        /**
         * 获取头像上传路径
         */
        public String getAvatarPath() {
            // return FilenameUtils.concat(getProfile(), "avatar");
            return Paths.get(getProfile(), "avatar").toString();
        }

        /**
         * 获取下载路径
         */
        public String getDownloadPath() {
            // return FilenameUtils.concat(getProfile(), "download");
            return Paths.get(getProfile(), "download").toString();
        }

        /**
         * 获取上传路径
         */
        public String getUploadPath() {
            // return FilenameUtils.concat(getProfile(), "upload");
            return Paths.get(getProfile(), "upload").toString();
        }

        public String getProfile() {
            return profile == null ? System.getProperty("java.io.tmpdir") : profile;
        }
    }

    /**
     * Token全局配置文件
     */
    @Data
    public static class Token {
        /* 令牌自定义标识 */
        private String header;
        /* 令牌密钥 */
        private String secret;
        /* 令牌有效期 */
        private Integer expireTime = 30;
    }

    /**
     * xss全局配置文件
     */
    @Data
    public static class Xss {
        /* 过滤开关 */
        private Boolean enabled = true;
        /* 排除链接（多个用逗号分隔） */
        private String excludes;
        /* 匹配链接 */
        private String urlPatterns;
    }

    /**
     * Swagger全局配置文件
     */
    @Data
    public static class Swagger {
        /* 是否开启swagger */
        private Boolean enabled = true;
        /* 请求前缀 */
        private String pathMapping;
    }

    /**
     * 定时任务全局配置文件
     */
    @Data
    public static class Job {
        /* 是否开启定时任务 */
        private Boolean enabled = false;
        /* 定时任务数据是否持久化到数据库 */
        private Boolean persist = false;
    }

    /**
     * 线程池全局配置文件
     */
    @Data
    public static class Thread {
        /* 是否开启内置线程池 */
        // private Boolean enabled = true;
        /* 核心线程池大小 */
        private int corePoolSize = 10;
        /* 最大可创建的线程数 */
        private int maxPoolSize = 200;
        /* 线程池维护线程所允许的空闲时间 */
        private int keepAliveSeconds = 300;
        /* 定时任务类线程池开关 */
        // private boolean scheduleEnabled = true;
        /* 固定大小类线程池开关 */
        private boolean fixEnabled = false;
        /* 可伸缩大小类线程池开关 */
        private boolean cacheEnabled = false;

    }
}
