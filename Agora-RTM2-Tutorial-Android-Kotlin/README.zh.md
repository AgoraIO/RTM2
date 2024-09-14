# Agora RTM2 Android Kotlin Demo

*Read this in other languages: [English](README.md)*

这个开源示例项目演示了如何快速集成 Agora RTM (Real Time Messaging) SDK 实现收发消息功能。

在这个示例项目中包含了以下功能：

- 登录 RTM 服务器
- 发送和接收点对点消息
- 加入频道
- 发送和接收频道消息
- 设置和更新属性
- 获取和使用锁
- 设置和更新临时状态
- 等等

## 运行环境

- Android Studio
- Java 8
- 真实 Android 设备 
- 部分模拟器会存在功能缺失或者性能问题，所以推荐使用真机

## 运行示例程序

### 第1步: 克隆项目工程

```
git clone git@github.com:AgoraIO/RTM2.git
```

### 第2步: 创建项目 AppID

在 [Agora.io 注册](https://dashboard.agora.io/cn/signup/) 注册账号，并创建自己的测试项目，获取到 AppID。

将 AppID 填写进 "app/src/main/res/values/strings_config.xml"

```
<string name="agora_app_id"><#YOUR APP ID#></string>
```

### 第3步: 集成 SDK

通过 CDN 方式集成 SDK 或者 使用 Gradle 集成 SDK

使用 CDN 方式集成：
* 在 Agora.io SDK 下载 RTM SDK，解压后将其中的 libs 文件夹下的 *.jar 复制到本项目的 app/libs 下，其中的 libs 文件夹下的 arm64-v8a/x86/armeabi-v7a 复制到本项目的 app/src/main/jniLibs 下。

* 在本项目的 "app/build.gradle" 文件依赖属性中添加如下依赖关系（此处代码中已添加示例）：

  ```
  compile fileTree(dir: 'libs', include: ['*.jar'])
  ```


使用 Gradle 集成 SDK：

* 在 `/Gradle Scripts/build.gradle(Project: <projectname>)` 文件中添加如下代码，添加 Maven Central 依赖：

  ```
  buildscript {
      repositories {
          // ...
          mavenCentral()
      }
      ...
  }
  allprojects {
      repositories {
          // ...
          mavenCentral()
      }
  }
  ```

* 在 `/Gradle Scripts/build.gradle(Module: <projectname>.app)` 文件中添加如下代码，将 SDK 集成到你的 Android 项目中：

  ```
  ...
  dependencies {
      // ...
      // 将 x.y.z 替换为具体的 SDK 版本号，如 2.2.1
      // 可通过发版说明获取最新版本号
      implementation 'io.agora:agora-rtm:x.y.z'
  }
  ```


### 第4步: 编译运行程序

最后用 Android Studio 打开该项目，连上设备，编译并运行


## 联系我们

- 完整的 API 文档见 [文档中心](https://doc.shengwang.cn/doc/rtm2/android/landing-page/)
- 如果在集成中遇到问题, 你可以到 [开发者社区](https://dev.agora.io/cn/) 提问
- 如果有售前咨询问题, 可以拨打 400 632 6626，或加入官方Q群 12742516 提问
- 如果需要售后技术支持, 你可以在 [Agora Dashboard](https://dashboard.agora.io) 提交工单
- 如果发现了示例代码的 bug, 欢迎提交 [issue](https://github.com/AgoraIO/Rtm2/issues)

## 代码许可

The MIT License (MIT).