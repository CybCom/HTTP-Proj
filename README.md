# COURSE COMPLETED. ARCHIVED.

---

# 提醒

## 务必clone到本机的主用户目录下，否则可能产生路径问题

---

# 任务要求

## 基于Java Socket API搭建简单的HTTP客户端和服务器端程序

> Good luck and have fun!

## 说明：

### 1. 不允许基于netty等框架，完全基于Java Socket API进行编写

### 2. 不分区使用的IO模型，BIO、NIO和AIO都可以

### 3. 实现基础的HTTP请求、响应功能，具体要求如下：

    1. HTTP客户端可以发送请求报文、呈现响应报文（命令行和GUI都可以）

    2. HTTP客户端对301、302、304的状态码做相应的处理

    3. HTTP服务器端支持GET和POST请求

    4. HTTP服务器端支持200、301、302、304、404、405、500的状态码

    5. HTTP服务器端实现长连接

    6. MIME至少支持三种类型，包含一种非文本类型

### 4．基于以上的要求，实现注册，登录功能(数据无需持久化，存在内存中即可，只需要实现注册和登录的接口，可以使用postman等方法模拟请求发送，无需客户端)。

---

# 具体实现

## 1 请求报文、响应报文的实现

### 报文结构

![imag](message%20structure.png)

**报文类对报文数据进行封装，对成员变量提供get(),set()方法,并提供方法去创建或发送报文**  


### 创建报文

```java
public Request(InputStream reqStream) throws IOException;  //解析从client发送的请求流并生成请求报文
public Response(Request request);   //根据生成的请求报文生成响应报文
```

### 发送报文

**实现send()方法，接受一个OutputStream的参数，用于将报文序列化并发送**

```java
public void send(OutputStream out)throws IOException;
```

## 2 服务器

**HttpServer类通过静态方法start()实例化一个ServerSocket监听指定端口(default:8080)**
**，每当收到一个连接请求，将该次请求封装成一个HttpRequestHandler对象，实现Runnable接口，以支持同时有多个http请求**

## 3 客户端

### 1 GET方法用于获取服务器目录下的资源，url为相对路径

```java
public void get(String url);
```

### 2 POST方法用于向服务器提交数据，在本项目中仅实现提交账号密码，与注册登陆有关

```java
public void post(String url, String user_name, String password);
```

## 4 状态码

**在JsonServer类中，我们持有服务器资源的状态**  

```java
private static final ServerResourceBean resourceBean =
        JSON.parseObject(FileUtil.readUtf8String(new File(FILE_SYS_JSON_PATH.toUri())), ServerResourceBean.class);
```

**并据以下流程图设置response状态码（关于http缓存，虽然我们在状态头里保留了Cache-Control和Expires等key值，但我们在实现时都忽略他们，默认为client都缓存，并根据条件请求来更新缓存，具体看If-Modified-Since，Etag保留但未实现）**


![image](status_code.png)

**详见JsonServer类**

## 5 资源管理

**server的资源在resources/webRoot目录下，各资源状态用json文件保存，用javaBean类与json文件互转来管理资源**  

**json文件关于每一个资源的记录如下:**

```java
    {
        "Url": "/tomb_raider.png",
        "Allow": "GET",
        "ModifiedJudge": {
        "Last_modified": "Thu, 08 Dec 2022 06:08:00 GMT",
        "Etag": "1"
        },
        "ReLocationJudge": {
        "IsMove": false,
        "New_url": "null",
        "IsPermanent": false
        }
        },
```

> 运用了alibaba的fastjson，hutool等第三方工具包,maven依赖关系如下

```java
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>2.0.20</version>
</dependency>
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.8.10</version>
</dependency>
```

## 6 长连接

### 1 HttpTask类里设置一个最大时间跨度，超过时间未请求，则服务器关闭socket

```java
 private static final int MAX_GAP=30000;//不发消息的最大时间跨度
```

### 2 实例化HttpClient对象时在构造函数中建立socket连接，每次get()或post()时进行异常检测，若server端的socket已关闭，则重新建立socket连接

```java
public void get(String url) {
        try {
            //进行get()操作
        } catch (IOException ex) {
            try {
                client = new Socket(hostname, port);
                System.out.println("超时，重新建立连接！");
                System.out.println("连接到主机：" + hostname + " ,端口号：" + port);
                System.out.println("远程主机地址：" + client.getRemoteSocketAddress());
                get(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
```

## 7 MIME类型

**MIME中存储一张表，通过 getMimeType() 提供文件扩展名到报文头部 "Content-Type" 的映射**

```java
public String getMimeType(String Uri);
```

## 8 登陆的实现

**通过postman来模拟或client提供了登陆注册的接口**  
**数据保存在内存中，不持久化**

---

# 参考资料

**https://developer.mozilla.org/zh-CN/docs/Web/HTTP**  
**https://www.cnblogs.com/chris-cp/p/5082514.html**  
**https://blog.csdn.net/YaoLang1995/article/details/89316912**  
**https://blog.csdn.net/weixin_42316952/article/details/114081588**  
**https://zhuanlan.zhihu.com/p/388270712**  
**https://blog.csdn.net/qq_34375473/article/details/122162682**


















