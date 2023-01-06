
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

package xin.altitude.cms.code.constant;

/**
 * 代码生成器部分常量
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/12/02 18:51
 **/
public class CodeConstant {
    /**
     * 第三方会话工厂
     */
    public final static String CODE_SQL_SESSION_FACTORY = "CODE_SQL_SESSION_FACTORY";
    /**
     * 当前运行的项目根目录
     */
    public static final String PROJECT_ROOT_ABSOLUTE_PATH = System.getProperty("user.dir");
    /**
     * 项目空间路径
     */
    public static final String PROJECT_RELATIVE_JAVA_PATH = "src/main/java";
    /**
     * mybatis空间路径
     */
    public static final String PROJECT_RELATIVE_MAPPER_PATH = "src/main/resources/mapper";
    /**
     * Mysql数据库元数据主键名称
     */
    public static final String PRIMARY = "PRIMARY";
    /**
     * 系统表前缀
     */
    public static final String SYS_TABLE_PREFIX = "sys_";
    /**
     * 任务表前缀
     */
    public static final String QRTZ_TABLE_PREFIX = "qrtz_";
}
