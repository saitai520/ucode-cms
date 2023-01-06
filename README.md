### 一、序言

`UCode Cms`是面向企业级应用软件开发的脚手架。希望构造一个合起来是一个系统，拆分出来是独立的组件，供其它项目复用。

本项目与其它项目的显著区别是拒绝摊大饼式功能集成，核心追求是代码和功能复用。

##### 1、当前版本
Maven仓库访问[地址](https://mvnrepository.com/search?q=xin.altitude.cms)
```xml
<version>1.6.2.2</version>
```
> 学习代码的朋友，记得给[UCode CMS](https://gitee.com/decsa/ucode-cms-vue) 来个star吧
##### 2、使用说明
新时代的码农需要转变编码习惯，摒弃传统的CV开发依赖。实际上Java生态目前极其完善，从底层的JDK到Spring、SpringBoot，再到Apache Commons，再到Guava等几乎提供了95%实际问题的解决方案。

未来编程有几大趋势
- 框架与框架整合

目前SpringBoot做的便是这件事，而且做的非常优秀。

- 编写框架适配器

对于国外用户编写的框架，代码质量很高，美中不足的是对于国人的编码习惯不太友好。为了进一步提高编码效率，需要一些适配器工作。

- 阅读底层框架API接口

上面已经分析了，现实中遇到的大部分问题不需要编码，已经有成熟的解决方案，那么知道解决方案在哪里变得极其重要，熟悉底层接口API变得很迫切。

- 约定大于配置大于编码

一些朋友喜欢将本项目中封装好、并且发布到Maven中央仓库的代码CV到自己的项目中，起手式便是错误的。正确的做法是，先了解并使用已经开源项目的功能，满足不了功能时提Issue，然后才是自己造轮子。

![03bdfbf761575480c59a18d28aa41ae0](https://www.altitude.xin/typora/03bdfbf761575480c59a18d28aa41ae0.png)

### 二、项目基本情况
#### （一）项目特点

##### 1、源码开源

源码开源并且使用MIT开源许可协议，个人或者企业用户免费放心使用，项目源码地址：
```bash
git clone https://gitee.com/decsa/ucode-cms-vue.git
```

##### 2、快速上手

新建空项目，引入相关依赖，初始化数据库，启动项目，一个项目脚手架便搭建完成，更加便捷的操作是克隆[demo项目]，修改数据库连接并启动项目。

```xml
<!--ucode-cms核心依赖-->
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-spring-boot-starter</artifactId>
    <version>1.6.2.2</version>
</dependency>
```

#### （二）项目的内在逻辑
市面上各种管理系统做的非常好，作者无意重复造轮子，简要本项目的初衷。

大部分管理系统属于大杂烩性质的，说的难听点是堆砌功能，其实很多时候仅仅是想用其做个项目初始环境脚手架而已，并不想用过多的功能，齐全的功能此时却是累赘。

第一个特点：`积木式结构`。积木式结构允许使用者在核心依赖的基础上自由选择功能模块，体现的思想是功能复用，减少无意义的功能开发。

第二个特点：`暴露配置`。隐藏源码，暴露配置意味着使用者既能通过配置文件修改系统行为，又能较少的与不相干源码打交道。

第三个特点：`标准化代码`。鼓励使用者按照项目的框架提炼公共代码，按照类似插件性质予以封装，为本项目添砖加瓦。

##### 1、标准化

标准化是UCodeCms项目的核心内涵，即将通用性的功能浓缩成不可修改的互相独立的Maven依赖，用户使用时可`按需配置`。

##### 2、高内聚低耦合

广泛使用高内聚低耦合设计原则，将用户很少修改的源码进行封装，同时对外提供可供修改配置的开关，降低使用成本。

##### 3、积木式项目结构

核心依赖仅需要引入`ucode-cms-spring-boot-starter`便可初始化项目，starter内置其余核心依赖。

### 三、项目细节

#### （一）项目结构

UCode Cms源码项目结构如下

```text
├── sql                                 // 系统数据库SQL脚本
├── ucode-cms-code                      // 代码生成器源码
├── ucode-cms-code-spring-boot-starter  // 代码生成器starter
├── ucode-cms-common                    // 通用代码部分（核心依赖、可跨项目使用）
├── ucode-cms-excel                     // Excel处理模块源码
├── ucode-cms-framework                 // 核心依赖
├── ucode-cms-job                       // 任务调度源码
├── ucode-cms-monitor                   // 系统监控源码
├── ucode-cms-spring-boot-starter       // 核心starter
├── ucode-cms-system                    // 系统部分源码
└── ucode-cms-ui                        // 系统UI部分依赖
```

#### （二）项目依赖

`UCode Cms`使用的是热门主流框架依赖。

| 依赖             | 版本       |
| ---------------- | ---------- |
| JDK              | 1.8        |
| Spring Framework | 5          |
| Spring Boot      | 2.6.3      |
| Mybatis Plus     | 3.5.0      |
| hutool           | 5.7.19     |
| Guava            | 31.0.1-jre |


### 四、对外复用依赖
本系统基于Spring生态，除了满足自身需求外，还能够以Maven依赖的方式对外提供功能和代码的复用。
#### （一）公共代码
公共代码依赖广泛应用于本项目中，同时也能以工具类的方式对外提供服务。

公共代码模块提供丰富的工具类支持，是整个项目的核心支撑。
```xml
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-common</artifactId>
    <version>1.6.2.2</version>
</dependency>
```
##### 1、请求合并
基于MybatisPlus提供一个内置封装的服务类QueueServiceImpl，透明的实现查询详情流量的合并与拆分，使用者可屏蔽内部实现。

对于一定时间区间内的所有请求，合并成一条请求处理。

```java
@Override
public BuOrder getOrderById(Long orderId) {
    return getById(orderId);
}
```
举例说明，如果特定时间区间内汇集了相同的主键请求，那么合并后的请求查询一次数据库便能够响应所有的请求。

子类重写父类方法，可修改合并与拆分的行为。

```java
@Override
protected RequstConfig createRequstConfig() {
    RequstConfig config = new RequstConfig();
    /* 单次最大合并请求数量 */
    config.setMaxRequestSize(100);
    /* 核心线程池大小 */
    config.setCorePoolSize(1);
    /* 请求间隔（毫秒） */
    config.setRequestInterval(200);
    return config;
}
```

#### （二）代码生成器
作者很懒，对于重复性代码连复制粘贴的欲望也没有，于是跨项目版的代码生成器应运而生。支持单表代码生成、多表连接代码生成。
```xml
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-code-spring-boot-starter</artifactId>
    <version>1.6.2.2</version>
</dependency>
```
#### （三）分布式BitMap
支持编码和注解两种方式实现分布式BitMap
```java
@BitMap(key = OrderServiceImpl.ORDER_BITMAP_KEY, value = "#orderId")
public BuOrder getOrder2(Integer orderId) {
    return getById(orderId);
}
```
添加如下依赖，通过注解的方式即可为项目引入分布式BitMap，无需关心实现细节便可大幅提高接口响应速度。
```xml
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-bitmap</artifactId>
    <version>1.6.2.2</version>
</dependency>
```
#### （四）分布式限流
使用注解，基于IP或者用户的方式对接口限流，支持分布式系统。比如短信验证码接口，同一用户（IP）60秒内最多发送一次短信，通过分布式限流模块可大幅提高接口安全性。

为项目引入分布式限流模块非常简单，在控制器接口方法添加如下注解，便可实现分布式限流
```java
@RateLimiter(ttl = 60,threshold = 1)
```
需要引入如下依赖
```xml
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-limiter</artifactId>
    <version>1.6.2.2</version>
</dependency>
```
#### （五）防重复提交
对于一些增加数据的场合，由于非幂等性的原因，网络不稳定容易产生脏数据，这时可使用防重复提交功能。
```xml
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-repeat</artifactId>
    <version>1.6.2.2</version>
</dependency>
```
在一些场合，同一接口、同一参数避免脏数据入库，通常需要防重复提交。比如统计页面浏览量，超过3分钟浏览一次页面增加一次浏览量等。

在控制器方法接口添加如下注解
```java
@RepeatSubmit(interval = 180)
```
#### （六）分布式日志收集
基于Redis pub/sub订阅特性实现的日志收集，用消息队列的方式收集日志，然后从另外系统消费日志（数据落库）。实现了跨系统日志收集复用。
```xml
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-log</artifactId>
    <version>1.6.2.2</version>
</dependency>
```
在一些场合需要记录接口的操作日志信息，比如增加数据、修改数据、删除数据，需要用到日志系统

在控制器方法添加如下注解
```java
@OperLog(title = "参数管理", businessType = BusinessType.INSERT)
```
#### （七）统计API接口响应时间
基于AOP的方式封装统计API接口响应时间，按需引入。
```xml
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-take-time</artifactId>
    <version>1.6.2.2</version>
</dependency>
```
对于想统计接口的响应时间，添加上述依赖，并在控制器方法添加`@TakeTime`注解。

#### （八）多数据源模块
一些项目有多数据源的需求，比如将系统表与业务表分离等，这时有多数据源解决方案，能够显著提高编码效率，使开发者更加专注于业务开发。
```xml
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-db-datasource</artifactId>
    <version>1.6.2.2</version>
</dependency>
```
引入上述依赖，你的项目便有魔法助力，快速拥有多数据源的能力。 使用相当简单：

（1）改造数据源配置
```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/demo-master?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: 123456
    druid:
      slave:
        enabled: true
        url: jdbc:mysql://localhost:3306/demo-slave?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
        username: root
        password: 123456
```
在业务层使用如下注解

```java
// 对应第一个数据源，可作用于类和方法上
@Ds(value = DataSourceType.MASTER)

// 对应第二个数据源，可作用于类和方法上
@Ds(value = DataSourceType.SLAVE)
```

#### （九）定时任务
定时任务模块是对Quartz框架进一步封装，使用更加简洁。
```xml
<dependency>
    <groupId>xin.altitude.cms</groupId>
    <artifactId>ucode-cms-quartz</artifactId>
    <version>1.6.2.2</version>
</dependency>
```

编写定时任务
```java
@Component
@DisallowConcurrentExecution
@CronExp(id = 1, cron = "0/5 * * * * ?")
public class DemoJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        System.out.println("任务1："+LocalDateTime.now());
    }
}
```
使用上述代码便快速在Quartz生态完成一个定时任务。

### 五、使用UCode CMS
上述代码是UCode CMS的内部封装的源代码，不能对外直接使用，这里提供两种对外使用的方式。

#### （一）克隆初始化项目
##### 1、克隆脚手架项目
克隆脚手架项目并导入idea中
```http
https://gitee.com/decsa/demo-cms.git
```
##### 2、使用SQL脚本初始化数据库
使用项目自带的SQL脚本初始化数据库
```text
sql/
├── cms（必选）.sql                                 // 核心脚本（必选）
```
##### 3、配置数据库连接
```yml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    # 主库数据源
    url: jdbc:mysql://localhost:3306/ucode-cms2
    username: root
    password: 123456
```
##### 4、启动项目
启动项目前检查Mysql数据库服务和Redis服务配置信息是否正常。
```http
http://localhost:8080
```

> 使用脚手架或者在脚手架上修改比较方便，原因是将细节内容提供在初始化项目中。

#### （二）从零新建项目

##### 1、添加依赖
```xml
<dependencies>
    <!--Cms核心依赖（必选）-->
    <dependency>
        <groupId>xin.altitude.cms</groupId>
        <artifactId>ucode-cms-spring-boot-starter</artifactId>
        <version>1.6.2.2</version>
    </dependency>
</dependencies>
```
##### 2、添加配置类
此配置类与Spring Security有关，属于权限管理范畴。
```java
@Configuration
public class SecurityConfig extends AbstractSecurityConfig {
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        super.configure(httpSecurity);
    }
}
```

##### 3、初始化数据库
初始化数据库与配置连接信息
```yml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driverClassName: com.mysql.cj.jdbc.Driver
    # 主库数据源
    url: jdbc:mysql://localhost:3306/ucode-cms2
    username: root
    password: 123456
```

##### 4、启动项目
启动项目前检查Mysql数据库服务和Redis服务配置信息是否正常。
```http
http://localhost:8080
```

> 从零新建项目需要注意的细节部分略微多一些，也能够快速体验使用UCode CMS。

### 六、互相交流

如果在使用过程中有任何疑问，欢迎与我联系。

请添加VX号`dream4s`联系入群，谢谢。
