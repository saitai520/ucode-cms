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

package xin.altitude.cms.common.entity;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
public class TreeNode<T> implements Comparable<TreeNode<T>> {
    private T id;
    private String name;
    private List<TreeNode<T>> childList;

    public TreeNode() {
    }

    public TreeNode(T id, String name) {
        this.id = id;
        this.name = name;
    }

    public TreeNode(TreeNode<T> node) {
        if (Objects.nonNull(node)) {
            this.id = node.id;
            this.name = node.name;
            this.childList = node.childList;
        }
    }


    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TreeNode<T>> getChildList() {
        return childList;
    }

    public void setChildList(List<TreeNode<T>> childList) {
        this.childList = childList;
    }

    @Override
    public int compareTo(TreeNode<T> o) {
        if (o.id instanceof Integer) {
            return Integer.compare((Integer) this.id, (Integer) o.id);
        } else if (o.id instanceof Long) {
            return Long.compare((Long) this.id, (Long) o.id);
        } else if (o.id instanceof String) {
            return ((String) this.id).compareTo((String) o.id);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", childList=" + childList +
            '}';
    }
}
