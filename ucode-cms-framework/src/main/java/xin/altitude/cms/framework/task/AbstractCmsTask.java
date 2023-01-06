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

package xin.altitude.cms.framework.task;

/**
 * 这里提供定时任务编写的演示示例，分别包含无参数方法、单一参数方法、多参数方法
 * 其中参数类型包含：字符串、整型、布尔、长整型、浮点型
 * 添加到此类或者子类中，并且加载到容器中的方法方能被正确调用
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2021/01/12 16:01
 **/
public abstract class AbstractCmsTask {
    public void mutiParams(String s, Boolean b, Long l, Double d, Integer i) {
        System.out.printf("执行多参方法： 字符串类型%s，布尔类型%d，长整型%d，浮点型%d，整形%d%n", s, b, l, d, i);
    }

    public void oneParams(String params) {
        System.out.println("执行有参方法：" + params);
    }

    public void noParams() {
        System.out.println("执行无参方法");
    }
}
