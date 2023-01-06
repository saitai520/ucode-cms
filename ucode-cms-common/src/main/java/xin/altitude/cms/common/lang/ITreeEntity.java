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

package xin.altitude.cms.common.lang;

/**
 * 列表实体类实现{@code ITreeEntity}接口抽象方法
 *
 * @param <T> id或者pid的泛型
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/

public interface ITreeEntity<T> {
    /**
     * 获取ID的值
     *
     * @return ID
     */
    T getId();

    /**
     * 获取名称值
     *
     * @return 名称
     */
    String getName();

    /**
     * 获取父ID的值
     *
     * @return 父ID
     */
    T getParentId();

}
