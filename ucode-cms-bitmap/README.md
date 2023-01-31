### BitMap使用指南
想要你的项目中快速拥有Redis版BitMap的能力，请按照如下步骤进行：

##### 1、引入依赖
```xml
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-bitmap</artifactId>
    <version>1.6.2.5</version>
</dependency>
```
由于用到了Redis，因此容器中应当配置好了Redis数据源，考虑到Redis数据源几乎是项目的标配，这里省略。

##### 2、编码式使用
从BitMap检查当前ID是否存在，如果不存在，则直接返回，不查询数据库；如果存在，则访问数据库查询详情。
```java
public BuOrder getOrder(Integer orderId) {
    /* 如果不存在，则快速返回，流量不走DB */
    if (!RedisUtils.getBit(ORDER_BITMAP_KEY, orderId)) {
        return null;
    }
    return getById(orderId);
}
```
编码式对业务代码有入侵性，入侵代码相对较少。

##### 3、注解版使用
注解版提供实现的功能与编码式相同，具体的代码实现不同。
```java
@BitMap(key = OrderServiceImpl.ORDER_BITMAP_KEY, id = "#orderId")
public BuOrder getOrder(Integer orderId) {
    return getById(orderId);
}
```
注解版与业务完全解藕，透明化的使用BitMap，可拆可装，灵活自如。


---
> 如有疑问，可通过微信`dream4s`与作者联系。源码在[GitHub](https://gitee.com/decsa)，视频讲解在[B站](https://space.bilibili.com/1936685014)，本文收藏在[博客天地](http://www.altitude.xin)。
---
