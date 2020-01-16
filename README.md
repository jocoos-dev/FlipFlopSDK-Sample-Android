# FlipFlop SDK for Android

FlipFlop SDK

## 소개

라이브 커머스 SDK

## 사용방법

##### AndroidManifest.xml에 아래의 내용을 추가하여 권한이 허용되도록 한다.

```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.RECORD_AUDIO"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

##### Amazon 관련 서비스가 동작할 수 있도록 아래의 내용을 AndroidManifest.xml에 추가한다.

```
<service
    android:name="com.amazonaws.mobileconnectors.s3.transferutility.TransferService"
    android:enabled="true"/>
```

##### SDK 초기화

 - 웹에서 회원가입 후 생성된 client id와 client secret을 사용하여 SDK 초기화를 한다.
 - Application instance의 onCreate()에서 호출하는 것을 추천한다.

```
FlipFlop.initialize(clientId, clientSecret)
```

##### 사용자 인증

 - FlipFlop SDK를 사용하려면 위의 초기화 후 사용자 정보를 SDK에 넣어서 인증을 해주어야 한다.
 - 여기서 얻은 FlipFlop instance를 사용해서 SDK가 제공하는 기능을 사용하게 된다.
 - userId와 username은 필수이고 비어 있어서도 안된다.
 - profileUrl은 선택이다.

```
suspend FlipFlop.authorize(userId, username, profileUrl): FFResult<FlipFlop, FlipFlopException>
```

## 라이센스

 - This project is licensed under the MIT license.

