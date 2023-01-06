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
package xin.altitude.cms.common.constant;

/**
 * 定义标准CacheName名称列表
 * 不同名有不同的过期时间
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/05/12 16:34
 **/
public interface CNTC {
    String CACHE_01SECS = "CACHE_01SECS";
    String CACHE_03SECS = "CACHE_03SECS";
    String CACHE_05SECS = "CACHE_05SECS";
    String CACHE_10SECS = "CACHE_10SECS";
    String CACHE_15SECS = "CACHE_15SECS";
    String CACHE_30SECS = "CACHE_30SECS";
    String CACHE_01MINS = "CACHE_01MINS";
    String CACHE_03MINS = "CACHE_03MINS";
    String CACHE_05MINS = "CACHE_05MINS";
    String CACHE_1HOURS = "CACHE_1HOURS";
    String CACHE_1DAYS = "CACHE_1DAYS";
}
