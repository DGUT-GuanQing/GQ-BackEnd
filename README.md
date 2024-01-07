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
- [x] 消息队列改成kafka
- [ ] 优化抢票模块，引入消费补偿
- [ ] 优化后台界面和相关功能
- [ ] 小程序首页视频功能
- [ ] 小程序整体ui优化
- [ ] 小程序开通外网访问
- [ ] 申请多一台服务器实现多个实例和数据库实现主从


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

e.g.: feat: add new api
or: feat(common): add new api
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


## 技术栈
```text
SpringCloud  
MybatisPlus  
Redis  
RabbitMq  
MySql
```
## 整体架构




## 