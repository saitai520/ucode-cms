### 分布式限流使用指南
对HTTP API限流在经常被使用，对于短信验证码接口，同一客户端每60秒仅允许发送一次短信。

分布式限流应确保正常流量通过，非法流量过滤。

##### 1、引入依赖
```xml
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-limiter</artifactId>
    <version>1.6.2.2</version>
</dependency>
```

##### 2、使用注解
为项目引入分布式限流模块非常简单，在控制器接口方法添加如下注解，便可实现分布式限流
```java
@RateLimiter(ttl = 60,threshold = 1)
```

---
> 如有疑问，可通过微信`dream4s`与作者联系。源码在[GitHub](https://gitee.com/decsa) ，视频讲解在[B站](https://space.bilibili.com/1936685014) ，本文收藏在[博客天地](http://www.altitude.xin) 。
---
