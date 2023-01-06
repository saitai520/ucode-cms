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

package xin.altitude.cms.code.util.format;

import cn.hutool.core.util.ReUtil;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/07/07 15:24
 **/
public class JavaFormat4Domain {
    public static Map<String, String> mapZY = new HashMap<String, String>();

    /**
     * 格式化java代码(入口代码)
     **/
    public static String formJava(String data) {
        String dataTmp = replaceStrToUUid(data, "\"");
        dataTmp = replaceStrToUUid(dataTmp, "'");
        dataTmp = repalceHHF(dataTmp, "\n", "");
        dataTmp = repalceHHF(dataTmp, "{", "{\n");
        dataTmp = repalceHHF(dataTmp, "}", "}\n");
        // dataTmp = repalceHHF(dataTmp, "/*", "\n/*\n");
        // dataTmp = repalceHHF(dataTmp, "* @", "\n* @");
        dataTmp = repalceHHF(dataTmp, " */", " */\n");
        dataTmp = repalceHHF(dataTmp, ";", ";\n");
        // dataTmp = repalceHHF(dataTmp, "@", "\n@");
        dataTmp = repalceHHF(dataTmp, "public", "\npublic");
        dataTmp = repalceHHF(dataTmp, ")    ", ")    \n");
        dataTmp = repalceHHF(dataTmp, "//", "\n//");
        dataTmp = repalceHHFX(dataTmp, "\n");
        for (Map.Entry<String, String> r : mapZY.entrySet()) {
            dataTmp = dataTmp.replace(r.getKey(), r.getValue());
        }
        if (dataTmp == null) {
            return data;
        }
        dataTmp = recursiveMatchReplace(dataTmp, 1);
        return dataTmp;
    }

    /**
     * 循环替换指定字符为随机uuid  并将uui存入全局map:mapZY
     **/
    public static String replaceStrToUUid(String string, String type) {
        Matcher slashMatcher = Pattern.compile(type).matcher(string);
        boolean bool = false;
        StringBuilder sb = new StringBuilder();
        int indexHome = -1; //开始截取下标
        while (slashMatcher.find()) {
            int indexEnd = slashMatcher.start();
            String tmp = string.substring(indexHome + 1, indexEnd); //获取"号前面的数据
            if (indexHome == -1 || bool == false) {
                sb.append(tmp);
                bool = true;
                indexHome = indexEnd;
            } else {
                if (bool) {
                    String tem2 = "";
                    for (int i = indexEnd - 1; i > -1; i--) {
                        char c = string.charAt(i);
                        if (c == '\\') {
                            tem2 += c;
                        } else {
                            break;
                        }
                    }
                    int tem2Len = tem2.length();
                    if (tem2Len > -1) {
                        //结束符前有斜杠转义符 需要判断转义个数奇偶   奇数是转义了  偶数才算是结束符号
                        if (tem2Len % 2 == 1) {
                            //奇数 非结束符
                        } else {
                            //偶数才算是结束符号
                            String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                            uuid = type + uuid + type;
                            mapZY.put(uuid, type + tmp + type);
                            sb.append(uuid);
                            bool = false;
                            indexHome = indexEnd;
                        }
                    }
                }
            }
        }
        sb.append(string.substring(indexHome + 1));
        return sb.toString();
    }


    //处理换行
    public static String repalceHHF(String data, String a, String b) {
        try {
            data = data.replace(a, "$<<yunwangA>>$<<yunwangB>>");
            String[] arr = data.split("$<<yunwangA>>");
            StringBuilder result = new StringBuilder();
            if (arr != null) {
                for (int i = 0; i < arr.length; i++) {
                    String t = arr[i];
                    result.append(t.trim());
                    if (t.indexOf("//") != -1 && "\n".equals(a)) {
                        result.append("\n");
                    }
                }
            }
            String res = result.toString();
            res = res.replace("$<<yunwangB>>", b);
            res = res.replace("$<<yunwangA>>", "");
            return res;
        } catch (Exception e) {
        }
        return null;
    }

    //处理缩进
    public static String repalceHHFX(String data, String a) {
        try {
            String[] arr = data.split(a);
            StringBuilder result = new StringBuilder();
            if (arr != null) {
                String zbf = "    ";
                Stack<String> stack = new Stack<String>();
                for (int i = 0; i < arr.length; i++) {
                    String tem = arr[i].trim();
                    if (tem.indexOf("{") != -1) {
                        String kg = getStack(stack, false);
                        if (kg == null) {
                            result.append((tem + "\n"));
                            kg = "";
                        } else {
                            kg = kg + zbf;
                            result.append(kg + tem + "\n");
                        }
                        stack.push(kg);
                    } else if (tem.indexOf("}") != -1) {
                        String kg = getStack(stack, true);
                        if (kg == null) {
                            result.append(tem + "\n");
                        } else {
                            result.append(kg + tem + "\n");
                        }
                    } else {
                        String kg = getStack(stack, false);
                        if (kg == null) {
                            result.append(tem + "\n");
                        } else {
                            result.append(kg + zbf + tem + "\n");
                        }
                    }
                }
            }
            return result.toString();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获得栈数据
     **/
    public static String getStack(Stack<String> stack, boolean bool) {
        String result = null;
        try {
            if (bool) {
                return stack.pop();
            }
            return stack.peek();
        } catch (EmptyStackException e) {
        }
        return result;
    }

    /**
     * 连续注解添加回车
     */
    public static String recursiveMatchReplace(String data, int signal) {

        if (signal > 0) {
            final String regex = "(@[a-zA-Z]+@)|(@[a-zA-Z]+\\(.*\\)@)";
            List<String> list = ReUtil.findAllGroup0(regex, data);
            for (String s : list) {
                data = data.replace(s, s.substring(0, s.lastIndexOf('@')) + "\n@");
            }
            list = ReUtil.findAllGroup0(regex, data);
            return recursiveMatchReplace(data, list.size());
        } else {
            return data;
        }
    }

}
