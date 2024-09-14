# Agora RTM2 Linux Java Quick Start Demo

*其他语言版本： [简体中文](README.zh.md)*

The Agora RTM2 Quick Start  Demo is an open-source demo that will help you get message chat integrated directly into your Android applications using the Agora RTM SDK.

With this sample app, you can:

- Login RTM server
- Join channel
- Send and receive channel message
- Logout RTM server

## Developer Environment Requirements

- ubuntu 18.04 or Debian 9.9
- x86-64
- Java 8 and above

## Running the App

### 1. clone this repo
```
git clone git@github.com:AgoraIO/RTM2.git
```

### 2. replace appid 

create a developer account at [Agora.io](https://dashboard.agora.io/signin/), and obtain an App ID.
Update "src/main/java/io/agora/RtmJavaDemo.java" with your App ID .

```
class APPID {
    public static final String APP_ID = "Your App ID";
}
```

### 3. integration SDK

update the SDK version in the pom.xml.

```
      <dependency>                                                         
        <groupId>io.agora</groupId> 
        <artifactId>rtm-java</artifactId>                      
        <version>2.1.12-beta</version>                                                  
      </dependency>
```

### 4. build and run

execute the following command to compile and run the program.

```
./clear.sh 
mvn clean
mvn package
export LD_LIBRARY_PATH=/tmp/rtm
java -cp target/RTM-Java-Demo-1.0-SNAPSHOT.jar io.agora.RtmJavaDemo
```


## Connect Us

- You can find full API document at [Document Center](https://docs.agora.io/en/)
- You can file bugs about this demo at [issue](https://github.com/AgoraIO/RTM2/issues)

## License

The MIT License (MIT).