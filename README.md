# MSA example

### 멀티모듈을 구성하는 어플리케이션들
> 편의상 멀티모듈로 구성하였으며 각 모듈을 서로 의존하지는 않는다. 실행시에는 각각 실행시키면 된다

- config
- gateway
- auth
- business-app


### 선행 작업
consul(service registry) 설치
* https://developer.hashicorp.com/consul/downloads


각 클라이언트(모듈)에는 service discovery client의 의존성이 포함되어있고,  
consul서버에 클라이언트를 등록하는 세팅값이 app설정파일(application.yml)에 적용되어 있다.

### 실행 순서
- consul agent 실행 
  - 개발모드로 실행: `consul agent -data-dir=tmp/consul -dev` 
  - 종료: `consul leave`
- config server 실행
- business-app, auth, gateway 실행
