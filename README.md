# wfg-news
新闻网站开发
## 自媒体实战

**重点功能技术分析**

- Redis： 分布式会话，session共享，单点登录，防刷，计数

- Fastdfs+Nginx : 搭建分布式文件系统，文件上传，人脸隐私保护

- 阿里AI ： 人脸对别
- RabbitMQ： 安装配置，异步解耦，延迟消息
- SpringCloud： 业务分而治之，可伸缩，扩展

- Freemarker：构建模板页，实现页面静态化

**微服务架构图**

![](https://pic.imgdb.cn/item/6188c5e62ab3f51d91c7c37d.jpg)

**技术架构图**

![](https://pic.imgdb.cn/item/6188ce362ab3f51d91eba0e4.jpg)

**构建maven聚合工程**

![](https://pic.imgdb.cn/item/6188e5832ab3f51d913893ee.jpg)

**spring boot 常用注解使用**

- spring boot 默认是扫描启动类所在的包下面的bean，因此在多模块项目需要**使用 ComponentScan，额外指定别的pacakge**，而mapperScan 注解是为了扫描mapper包的



- **`@Configuration`用于定义配置类**，可替换**bean 的**`xml`配置文件 ，其内部可以含有一个或多个被**@Bean注解的方法**



- **propertySource 注解主要用于加载指定的配置文件**，常与**ConfigurationProperties注解**一起使用，此可以自动将配置文件中的**同名配置映射为实体类中对应的属性**

  ps: 当主配置文件 (application.yml) 与自定义配置文件 (config.properties) 中存在相同名称的配置项时，**主配置文件会覆盖自定义配置文件**

 



### 1.1 短信登录注册流程

- 短信登录注册
- 短信验证码发送与限制
- 分布式会话
- 用户信息完善，oss/FastDfs文件上传
- AOP日志监控



**短信发送流程**

![](https://pic.imgdb.cn/item/618d23062ab3f51d9185ed37.jpg)

分布式系统下，**session**只会在单机服务器中保存状态，因此会导致session失效，因此**保存验证码采用redis**





**短信登录注册流程**

![](https://pic.imgdb.cn/item/618d26ec2ab3f51d9187c10e.png)





### 2  基础知识

#### 2.1 CAP理论

c ： 一致性（所有的数据都是一致的,在不同节点获得的数据都是一样的）

A:    可用性（无论在任何时候：如部分节点宕机，但功能仍然可以使用）

p： 分区容错性 （即出现问题，机器故障、网络故障、机器停电等异常情况下仍然能够满足一致性和可用性。）

 一个分布式系统里面，节点组成的网络本来应该是连通的。然而可能因为一些故障，使得有些节点之间不连通了，整个网络就分成了几块区域。数据就散布在了这些不连通的区域中。这就叫分区。

**为什么无法同时满足cap?**

![](https://pic.imgdb.cn/item/619500372ab3f51d917a9fb6.jpg)

由于地域问题，数据在同步时会出现延迟，如果此时需要满足一致性（c）需求则需要阻塞系统，会导致功能不可用（即不满足A）；如果此时需要满足可用性（A）,则会导致脏数据（一致性c不满足）。

**一般情况下P是必须满足的**，只能从一致性（C）或可用性（A）来挑选

- CP

  此时满足强一致性，但会导致系统效率不高。（如redis集群）

- AP

​      此时满足可用性，**平时开发一般采用AP,使用弱一致性**

- CA

  满足一致性和可用性，一般都是单体应用，关系型数据库

### 3 其他问题

#### 3.1 springBoot解决 跨域问题配置

如 **前端访问后端时调用不同的域名**

```java
@Configuration
public class CorsConfig {
    public CorsConfig(){}


    @Bean
    public CorsFilter corsFilter(){
        // 1 添加cors配置信息
        CorsConfiguration config = new CorsConfiguration();
        //开放所有的站点向后端发送请求
        config.addAllowedOrigin("*");
        // 设置是否发送cookie信息
        config.setAllowCredentials(true);
        //设置允许请求的方式
        config.addAllowedMethod("*");
        //设置允许的header
        config.addAllowedHeader("*");
        // 2. 为url添加映射路径
        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", config);
        // 3. 返回重新定义好的corsSource
        return new CorsFilter(corsSource);



    }


}

```



#### 3.2 自定义异常拦截处理返回json信息到前端

使用**@RestControllerAdvice+@ExceptionHandler注解**实现全局异常处理

```java
/**
 * @program: imooc-news-dev
 * @description: 统一异常拦截处理
 *
 * 针对异常的类型进行捕获，然后返回json信息到前端
 *
 * @author: wfg
 * @create: 2021-11-13 17:58
 */

@RestControllerAdvice
public class GraceExceptionHandler {

    @ExceptionHandler(MyCustomException.class)
    public GraceJSONResult  returnMyException(MyCustomException e){
      e.printStackTrace();
      return  GraceJSONResult.exception(e.getResponseStatusEnum());
    }
}
```

#### 3.3 自定义拦截器实现对特定接口的拦截

具体的实现为实现**HandlerInterceptor**接口，此接口提供了3个方法便于我们扩展在controller不同的时期进行扩展。

```java
public interface HandlerInterceptor {
       
    
  //拦截请求，访问controller之前
    
	default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		return true;
	}

   // 在请求访问到contro之后，渲染视图之前
    
	default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {
	}

   // 在请求访问到controller之后 渲染视图之前
    
	default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception ex) throws Exception {
	}

}
```



第二步 **扩展WebMvcConfigurer**     将拦截器加入到WebMvcConfigurer

```java
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public PassportInterceptor passportInterceptor(){
        return  new PassportInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加拦截器
       registry.addInterceptor(passportInterceptor())
        //添加拦截路径
               .addPathPatterns("/passport/getSMSCode");
    }
}
```

#### 3.4 验证参数处理

在处理前端发送过参数信息时（BO/DTO）需要对参数进行校验，如：在处理登录时需要对**属性进行校验**，以及**怎样获取属性错误信息**



- 校验 参数体

```java
@Data
public class RegisterLoginBO {

    @NotNull(message = "手机号不能为空")
    private String  mobile;

    @NotNull(message = "短信验证码不能为空")
    private String  smsCode;


}

```

- 使用对其进行校验

```java
/**
 参数处理 需要在校验的 BO 处加 @valid 注解，BindingResult 则是校验错误后的信息
*/
@Override
    public GraceJSONResult doLogin(@RequestBody @Valid  RegisterLoginBO registerLoginBO,
                                   BindingResult result) {
        
        
       // 判断BindingResult 中是否保存了错误的验证信息,如果有则返回
        if(result.hasErrors()){
          Map<String,String> map = getErrors(result);
          return  GraceJSONResult.errorMap(map);
        }
     // 验证逻辑
        .....
        
    }
```

- 处理错误信息

```java
    /**
     * 获取BO中的错误信息
     * @param result
     */
    public void getErrors(BindingResult result){

        Map<String,String> map = new HashMap<>();

        List<FieldError> errorList = result.getFieldErrors();

         for(FieldError error : errorList){
             // 验证错误时 所对应的某个属性
             String field = error.getField();
             // 验证的错误消息
             String msg = error.getDefaultMessage();
             
             map.put(field,msg);

         }


    }
```

#### 3.5  分布式存储用户信息

问题出现：在不同的服务器下无法知道所保存的用户会话信息。

使用 redis+token 可以解决，后端中将token存储在redis中，

在前端将信息存储在cookie 或localstorge中



**代码逻辑**

```java
           // 保存token到redis中
             String uToKen = UUID.randomUUID().toString();
             redis.set(REDIS_USER_TOKEN+":"+user.getId(),uToKen);

             // 保存用户id和token 到cookie中
             setCookie(request,response,"utoken",uToKen,COOKIE_MONTH);
             setCookie(request,response,"uid",user.getId(),COOKIE_MONTH);
```



**将信息存储到token中**

```java
   /**
     * 解决分布式存储session 问题 ：redis+cookie
     */
     public void setCookie(HttpServletRequest request,
                           HttpServletResponse response,
                           String  cookieName,
                           String  cookieValue,
                           Integer maxAge
                           ){
         try {
             //设置编码
             cookieValue = URLEncoder.encode(cookieValue,"utf-8");
             // 存储cookie值
             setCookieValue(request, response, cookieName, cookieValue, maxAge);
         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         }

     }


    public void setCookieValue(HttpServletRequest request,
                          HttpServletResponse response,
                          String  cookieName,
                          String  cookieValue,
                          Integer maxAge
    ){
        //填充cookie
        Cookie cookie = new Cookie(cookieName,cookieValue);
        cookie.setMaxAge(maxAge);
        // 跨域共享设置 设置为相同域名
        cookie.setDomain("imoocnews.com");
        cookie.setPath("/");
        //设置到响应体中
        response.addCookie(cookie);
           }
```

#### 3.6 解决请求过多问题

对于**经常请求且不常改变数据**的接口，可以将请求出的数据放到浏览器端进行保存，高级版为 **对其进行使用redis缓存**

- sessionStorage  保存数据有效期：打开浏览器到关闭浏览器
- localStorage 是永久存在，对于用户信息不适合存放
- cookie ：也不适合存放用户信息，而且cookie的大小限制为4K



cookie 不设置过期时间，随浏览器关闭而失效，设置了过期时间后，会保存在硬盘里。只能存储字符串，大小限制于4k，用户禁用cookie则无法使用。



**写入Redis的逻辑**

- 判断redis中是否包含数据，没有则将数据存储到redis中

- 有则直接取出数据

- 对于更新数据时，**查询出最新的数据，放入到Redis中**,需要解决双写不一致问题

  

**双写数据不一致问题**

**在做数据修改后，正常情况会从数据库中读取最新数据再放入redis中。但有时因为网络原因，redis数据未写入成功，此时会导致redis是旧数据，mysql是新数据**



**解决双写一致问题**

使用**缓存双删策略**,第一次删除redis中数据，再更新数据库，让请求来临时重新写到数据库中，是防止redis数据未写入成功，导致redis中数据是旧数据，第二步删除解决的是在第一次删除之后，数据库更新之前，查询请求并发来查询数据库内容并在写到redis      导致脏数据

```java
   String userId = updateUserInfoBO.getId();
        // 保证双写一致，先删除redis中的数据，后更新数据库
        redis.del(REDIS_USER_INFO+":"+userId);
        AppUser userInfo = new AppUser();
        BeanUtils.copyProperties(updateUserInfoBO,userInfo);
        userInfo.setUpdatedTime(new Date());
        userInfo.setActiveStatus(UserStatus.ACTIVE.type);
        int result=appUserMapper.updateById(userInfo);

        if(result!=1){
            GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
        }
        // 再次查询用户的最新信息，放入到redis中
        AppUser user  = getUser(userId);
        redis.set(REDIS_USER_INFO+":"+userId, JsonUtils.objectToJson(user));

        // 缓存双删策略  
        try{
            Thread.sleep(100);
             redis.del(REDIS_USER_INFO+":"+userId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
```

流程：

当A线程先删除了缓存后，正要进行数据库个更新操作之前。此时B线程来进行查询操作，但此时redis中未找到数据，就会上数据库中查询数。之后A线程进行数据库更新操作，完成数据更新。那么此时A线程redis的第二次删和B线程的redis存谁先进行就涉及到了A线程延时删的情况。若A先第二次删（A不进行延时删），B再存，此种情况redis就一直是旧数据，其他线程访问得到的也一直是旧数据。若B先存，A再删，则没问题（理想情况）


问题一 ： 为什么需要set

加set是因为，若线程没有重新写缓存，下一步del，发现redis没数据，就会报错

问题二： 加sleep线程休眠是因为

保证B的存再A的删之后，充其量也就是在休眠的短时间内其他线程得到旧数据，之后得到的就是新数据。
