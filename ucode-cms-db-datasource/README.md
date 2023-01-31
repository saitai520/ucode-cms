### 多数据源使用指南
多数据源满足了一些项目有多数据源的需求，比如将系统表与业务表分离等，多数据源解决方案，能够显著提高编码效率，使开发者更加专注于`业务开发`。

##### 1、引入依赖
```xml
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-db-datasource</artifactId>
    <version>1.6.2.5</version>
</dependency>
```
##### 2、单数据源配置
大多数项目使用单数据源，使用单个数据源即能够满足需求。
```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/demo-master?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: 123456
```

##### 3、多数据源配置
多数据源提供一种透明化的方式，允许业务代码动态切换数据源，但不需要修改业务逻辑。
```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/demo-master?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: 123456
    # 增加数据源配置（保持属性一致）
    druid:
      slave:
        enabled: true
        url: jdbc:mysql://localhost:3306/demo-slave?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
        username: root
        password: 123456
```

##### 4、使用示例
在业务层使用如下注解，可作用于类或者方法上

```java
// 对应第一个数据源
@Ds(value = DataSourceType.MASTER)

// 对应第二个数据源
@Ds(value = DataSourceType.SLAVE)
```


---
> 如有疑问，可通过微信`dream4s`与作者联系。源码在[GitHub](https://gitee.com/decsa) ，视频讲解在[B站](https://space.bilibili.com/1936685014) ，本文收藏在[博客天地](http://www.altitude.xin) 。
---

