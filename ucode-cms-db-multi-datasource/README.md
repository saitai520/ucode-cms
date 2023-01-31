### 基于SpringBoot轻量版多数据源实现
`多数据源`满足了一些项目有多数据源的需求，比如将系统表与业务表分离等，多数据源解决方案，能够显著提高编码效率，使开发者更加专注于`业务开发`。

对于`2B项目`，由于业务比较复杂，通常来讲一个数据源往往不能满足需求，有`两个`甚至`多个`数据源的需求，甚至需要不同类型的数据源来支持业务的开发。

> 本方案以轻量化的实现完成了上述需求

##### 1、引入依赖
使用多数据源的第一步是引入依赖。
```xml
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-db-multi-datasource</artifactId>
    <version>1.6.2.5</version>
</dependency>
```
##### 2、单数据源配置
大多数项目使用`单数据源`即能够满足需求（形式如下），多数据源是在单数据源的基础上进一步拓展，不影响单数据源的使用，划重点！
```yaml
spring:
  datasource:
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
        driverClassName: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/test_067?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
        username: root
        password: 123456
    ucode:
        more:
            ds1:
                driverClassName: com.mysql.cj.jdbc.Driver
                url: jdbc:mysql://localhost:3306/test_068?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
                username: root
                password: 123456
            ds3:
                driverClassName: com.mysql.cj.jdbc.Driver
                url: jdbc:mysql://localhost:3306/test_069?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
                username: root
                password: 123456
```

##### 4、使用示例
在业务层使用如下注解，可作用于类或者方法上

```java
// 对应第一个数据源
@Ds(name = "ds1")

// 对应第二个数据源
@Ds(name = "ds2")
```

```java
/**
 * 默认数据源
 */
@GetMapping("/list0")
public AjaxResult list0() {
    return AjaxResult.success(userService.getById(1));
}

/**
 * ds1数据源
 */
@Ds(name = "ds1")
@GetMapping("/list1")
public AjaxResult list1() {
    return AjaxResult.success(userService.getById(1));
}

/**
 * ds2数据源
 */
@Ds(name = "ds3")
@GetMapping("/list2")
public AjaxResult list2() {
    return AjaxResult.success(userService.getById(1));
}
```

