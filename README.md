# MSA example

### 멀티모듈 구성서버
- config
- gateway
- auth
- business-app

### 선행 작업
consul(service registry) 설치
* https://developer.hashicorp.com/consul/downloads


각 서버에는 service discovery client의 의존성이 포함되어있고, 클라이언트 설정이 yml에 되어있다

### 실행 순서
- consul agent 실행 
 - 개발모드로 실행 > `consul agent -data-dir=tmp/consul -dev` 
 - 종료 
 > consul leave
- config 실행
- business-app, auth, gateway 실행
