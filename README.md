# 莞青小程序后端

<div style="text-align: center;">
    <img src="docs/img/gq.jpg" width="400" height="400">
</div>

## 目标
- [x] 申请域名和服务器
- [x] 中央认证开发
- [x] 抢票模块开发
- [x] 后台模块开发
- [x] 招新模块开发
- [x] 用户模块开发
- [x] 上线并且承担讲座抢票
- [ ] 后台消息通过公众号推送
- [ ] 消息队列改成kafka
- [ ] 优化抢票模块，引入消费补偿
- [ ] 优化后台界面和相关功能
- [ ] 小程序首页视频功能
- [ ] 小程序整体ui优化
- [ ] 小程序开通外网访问
- [ ] 申请多一台服务器实现多个实例和数据库实现主从
- [ ] 开发日志收集模块(再申请一台服务器后)
- [ ] 开发服务监控模块(再申请一台服务器后)

## 开发流程
``` text
git clone url / git pull  
git branch 分支名  
git checkout 分支  
找组长分别要各个模块yml配置  
连接校园网或者vpn启动项目  
开发或者修改bug  
git push 远程名 本地分支名:远程分支名   
提pr
```

## 分支命名规范

1. 分支名携带开发者名称
2. 分支名表述解决了什么问题

分支命名必须标准化, 参照该格式进行分支命名
```bash
<type>-<name>-<description>
```
举例:
- 开发新功能
```bash
feature-<name>-<feature description>
e.g.: feature-honshen-dev_user_login
```

- 如果他是为了修复 bug 而开辟的分支:
```bash
bugfix-<name>-<bug name>
e.g.: bugfix-honshen-login_error
```
其他分支功能类型如下:
`hotfix`、`release`...


### 提交信息规范

```bash
<type>(<scope>): <subject>
e.g.: feat: 添加获取讲座接口
```

### type

```text
# 主要type
feat:     增加新功能
fix:      修复 bug

# 特殊type
docs:     只改动了文档相关的内容
style:    不影响代码含义的改动，例如去掉空格、改变缩进、增删分号
build:    构造工具的或者外部依赖的改动，例如 webpack，npm
refactor: 代码重构时使用
revert:   执行 git revert 打印的 message

```


## 项目详情

### 项目架构
<div style="text-align: left;">
    <img src="docs/img/项目结构图.png" width="800" height="400">
</div>

### 技术栈
```text
SpringCloud  
MybatisPlus  
Redis  
RabbitMq  
Mysql
Minio
SpringSecurity
```

### 功能介绍
```
项目采用微服务，分成网关模块、通用模块、基础核心模块、招新模块、后台模块
网关模块主要做转发和校验
通用模块提供公共的代码，比如全局返回和异常处理
基础核心模块包括了讲座、用户、中央认证、文件上传服务
招新模块主要应对每一年的招新，平常可以关掉这个服务
后台模块对应莞青的后台系统
用户请求会通过网关转发到目标服务，然后会有两层认证，一层是安全框架jwt解析权限和
身份，另一层是检查是否中央认证登陆，然后才会访问controller，如果需要配置白名单，
统一在config里面配置
访问某个接口必须访问网关，由网关转发，不能直接访问目标服务
如果某个服务需要另一个服务的功能，用Feign远程调用，编写好客户端和服务端代码
如果有新的业务，直接新加模块就行了，考虑搞一个其他模块，后续的所有服务全放这里
```

### 服务器和域名
``` text
服务器一台，域名两个，对应两个不同端口(学校的域名只能绑定服务器的某个端口)
后续考虑多申请一台服务器，将服务分开部署
目前只能内网访问，后续考虑申请外网访问
```
