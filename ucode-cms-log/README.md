### 分布式日志
在一些场合需要记录接口的操作日志信息，比如`增加数据`、`修改数据`、`删除数据`，需要用到日志系统。

基于Redis pub/sub订阅特性，用`消息队列`的方式收集日志。

既可以从当前系统异步消费日志，也可以从其它系统消费日志，伸缩自如。
##### 1、引入依赖
```xml
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-log</artifactId>
    <version>1.6.2.5</version>
</dependency>
```

由于项目中需要操作Redis，因此检查是否有Spring Data Redis依赖，如果没有，则添加。
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```
##### 2、使用注解

在控制器接口方法上添加如下注解
```java
@OperLog(title = "参数管理", businessType = BusinessType.INSERT)
```

添加完注解后，相应的接口便具备了日志收集的能力。

##### 3、日志消费
默认情况下，开启日志空消费模式，避免数据在Redis中累积。空消费对于读取的日志数据不做任何处理，直接抛弃。

重写`DefaultRedisOperateLogListener`类中的`saveData`方法，自定义实现数据消费的行为，比如存储到数据库中。

```java
@Component
public class RedisOperateLogListener extends DefaultRedisOperateLogListener {
    @Override
    protected void saveData(OperateLog operateLog) {
        System.out.println("operateLog = " + operateLog);
    }
}
```
> 需要将实现类注入到Spring容器中。




---
> 如有疑问，可通过微信`dream4s`与作者联系。源码在[GitHub](https://gitee.com/decsa) ，视频讲解在[B站](https://space.bilibili.com/1936685014) ，本文收藏在[博客天地](http://www.altitude.xin) 。
---
