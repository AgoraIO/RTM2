# Agora RTM2 Windows CPP Quick Start Demo

*Read this in other languages: [English](README.md)*

这个开源示例项目演示了如何快速集成 Agora RTM (Real Time Messaging) SDK 实现收发消息功能。

在这个示例项目中包含了以下功能：

- 登录 RTM 服务器
- 加入频道
- 发送和接收频道消息
- 登出 RTM 服务器

## 运行环境

- Windows 10/11
- x86-64
- C++ 11 and above
- Visual Studio

## 运行示例程序

### 第1步: 克隆项目工程

```
git clone git@github.com:AgoraIO/RTM2.git
```

### 第2步: 创建项目 AppID

在 [Agora.io 注册](https://dashboard.agora.io/cn/signup/) 注册账号，并创建自己的测试项目，获取到 AppID。


### 第3步: 集成 SDK

在 Agora.io SDK 下载 RTM SDK，解压后将其中的 sdk 文件夹复制到本项目的 ./rtmdemo目录下。

### 第4步: 编译运行程序

使用Visual Studio打开./rtmdemo目录下的rtmdemo.sln工程文件, 将第二步创建的AppID填写到main.cpp中，然后编译运行demo(命令行demo，非UI demo)
```
const std::string APP_ID = "Your APP ID";
```


## 联系我们

- 完整的 API 文档见 [文档中心](https://doc.shengwang.cn/doc/rtm2/cpp/landing-page)
- 如果在集成中遇到问题, 你可以到 [开发者社区](https://dev.agora.io/cn/) 提问
- 如果有售前咨询问题, 可以拨打 400 632 6626，或加入官方Q群 12742516 提问
- 如果需要售后技术支持, 你可以在 [Agora Dashboard](https://dashboard.agora.io) 提交工单
- 如果发现了示例代码的 bug, 欢迎提交 [issue](https://github.com/AgoraIO/Rtm2/issues)

## 代码许可

The MIT License (MIT).