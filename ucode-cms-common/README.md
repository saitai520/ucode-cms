### 公共代码模块使用指南
公共代码模块大量使用Lambda表达式和流，封装常用工具类，能够显著减少业务代码复杂度。

##### 1、模块依赖
```xml
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-common</artifactId>
    <version>1.6.2.2</version>
</dependency>
```
引入上述依赖便可以直接使用内置的大量工具类

##### 2、SpringUtils
`SpringUtils`工具类提供以静态方法的方式获取Spring容器对象的能力
```java
SchedulerFactoryBean factoryBean = SpringUtils.getBean(SchedulerFactoryBean.class);
```
在一些静态方法、无法使用依赖注入的场景，`SpringUtils`工具类相当实用。

##### 3、EntityUtils
`EntityUtils`实体转化工具类能够简化实体类与VO等转化的代码，支持将单个对象、集合对象、分页对象优雅的转换成其VO对象；能够将集合转换成Set集合、Map集合。

```java
Industry industry = new Industry();
IndustryNode industryNode = EntityUtils.toObj(industry, IndustryNode::new);
```
示例代码演示将实体类对象其它对象，尽管通过反射属性复制也能够达到同样的需求，基于构造器的方式转换运行效率更高。

##### 4、RedisUtils
`RedisUtils`工具类封装了访问Redis的常用方法，提供静态方法的方式调用，对于字符串、数组、Hash、Set、ZSet、BitMap等数据结构访问方式都做了封装，使用更加方便。

演示示例略

更多工具类可直接阅读源码。


---
> 如有疑问，可通过微信`dream4s`与作者联系。源码在[GitHub](https://gitee.com/decsa) ，视频讲解在[B站](https://space.bilibili.com/1936685014) ，本文收藏在[博客天地](http://www.altitude.xin) 。
---
