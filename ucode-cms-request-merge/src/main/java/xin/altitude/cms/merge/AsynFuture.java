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

package xin.altitude.cms.merge;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

/**
 * 包装实体Future类
 *
 * @param <T> 实体
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
public class AsynFuture<T> {
    private Serializable id;
    private CompletableFuture<T> future;

    public AsynFuture(Serializable id, CompletableFuture<T> future) {
        this.id = id;
        this.future = future;
    }

    public Serializable getId() {
        return this.id;
    }

    public void setId(Serializable id) {
        this.id = id;
    }

    public CompletableFuture<T> getFuture() {
        return this.future;
    }

    public void setFuture(CompletableFuture<T> future) {
        this.future = future;
    }
}
