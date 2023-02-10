# MSA example

### 멀티모듈을 구성하는 어플리케이션들
> 편의상 멀티모듈로 구성하였으며 각 모듈을 서로 의존하지는 않는다. 각 실행구성에서 실행시키면 된다

- **config**(port:8888)
- **auth**(port:9999)
- **gateway**(port:80)
- **business-app**(port:8080)

### 선행 작업
consul 설치: service registry 역할
* https://developer.hashicorp.com/consul/downloads

> 모든 어플리케이션은 consul에 등록되어야 한다.
> 각 클라이언트(모듈)에는 service discovery client의 의존성이 포함되어있고,  
> consul서버에 클라이언트를 등록하는 세팅값이 app설정파일(application.yml)에 적용되어 있다.

### 실행 순서
- consul agent 실행 (기본포트: 8500)
  - 개발모드로 실행: `consul agent -data-dir=tmp/consul -dev` 
  - 종료: `consul leave`
- config server 실행
- business-app, auth, gateway 실행


### 테스트 방법
1. auth서버를 실행시키면 dummy jwt를 콘솔에 출력해준다.

`dummy JWT is = eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VySWQiLCJpYXQiOjE2NzYwMDY0ODEsImV4cCI6MTY3NjAxMDA4MX0.BXpR8AiarZD9LdUTO9FXbt1fNH-TlE1MbpVrDTQfdNA
`

2. gateway에 등록된 엔드포인트를 호출한다

``` curl -H 'Authorization: Bearer {TOKEN}' 'localhost:80/need-auth' ```

<img width="1647" alt="image" src="https://user-images.githubusercontent.com/119831160/218009582-7ec19369-a06f-4134-ad00-004aec393025.png">

