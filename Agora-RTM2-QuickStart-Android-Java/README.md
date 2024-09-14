# Agora RTM2 Quick Start Android Demo

*其他语言版本： [简体中文](README.zh.md)*

The Agora RTM2 Quick Start  Demo is an open-source demo that will help you get message chat integrated directly into your Android applications using the Agora RTM SDK.

With this sample app, you can:

- Login RTM server
- Join channel
- Send and receive channel message
- Logout RTM server

## Developer Environment Requirements

- Android Studio
- Java 8 and above
- Real devices
- Some simulators are function missing or have performance issue, so real device is the best choice

## Running the App

### 1. clone this repo
```
git clone git@github.com:AgoraIO/RTM2.git
```

### 2. replace appid 

create a developer account at [Agora.io](https://dashboard.agora.io/signin/), and obtain an App ID.
Update "app/src/main/res/values/strings_config.xml" with your App ID .

```
<string name="agora_app_id"><#YOUR APP ID#></string>
```

### 3. integration SDK

Integrate RTM SDK via CDN or use Gradle

Use CDN :
* Download the RTM SDK in Agora.io SDK. After decompressing, copy the *.jar under the LIBS folder to the app/libs of this project. The arm64-v8a/x86/armeabi-v7a under the LIBS folder is copied to the app/src/main/jniLibs of this project.

* Add the following dependency in the "app/build.gradle" file dependency property of this project (the example is added in this code):

  ```
  compile fileTree(dir: 'libs', include: ['*.jar'])
  ```


Or use `Gradle` to build and run.

* To ensure that the build script and all projects within the Gradle build have access to dependencies hosted on Maven Central, add the following code to `/Gradle Scripts/build.gradle(Project: <projectname>)`:

  ```
  buildscript {
       repositories {
           // ...
           mavenCentral()
       }
       // ...
   }
   allprojects {
       repositories {
           // ...
           mavenCentral()
       }
   }
  ```

* Add the following code to the `/Gradle Scripts/build.gradle(Module: <projectName>.app)` file to integrate the SDK into your Android project:

  ```
  dependencies {
       // ...
       implementation 'io.agora:agora-rtm:x.y.z'
   }
  ```

### 4. build and run
Finally, open project with Android Studio, connect your Android device, build and run.


## Connect Us

- You can find full API document at [Document Center](https://docs.agora.io/en/)
- You can file bugs about this demo at [issue](https://github.com/AgoraIO/RTM2/issues)

## License

The MIT License (MIT).