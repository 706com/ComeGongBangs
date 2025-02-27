<h1 align="center">Backend</h1>

---

[//]: # (## ⛓️ Stacks)

## 📣  담당 기능

**🙂 회원 기능**

| 담당 업무            | 세부 사항                                                                                                        | 
|:-----------------|:-------------------------------------------------------------------------------------------------------------|
| 로그인 기능           | [JWT를 활용한 토큰 방식 채택, BlackList Token 도입, Redis TTL 활용](https://develop-706.tistory.com/36)                    |
| 이메일 인증 기능        | Java Mail Sender 를 통한 UUID 인증 요청 , Redis TTL 활용                                                              |
| 소셜 기능            | OAuth2 (Kakao) 로그인 / 회원가입 적용 , [Kubernetes 분산 서버에 의한 OAuth2 소셜 인증 실패 극복](https://develop-706.tistory.com/37) | 
| 회원 정보 수정 / 삭제 기능 | X                                                                                                            | 
| 로그아웃 기능          | /logout 요청이 들어오면 JWT 내포한 쿠키 삭제                                                                               | 


<br>

**❤️좋아요(찜) 및 팔로우 기능**

| 담당 업무 | 세부 사항 |
|:------|:------|
| 작성예정  | 작성예정  |


<br>

**🎟️ 이벤트 쿠폰발급 기능**

| 담당 업무 | 세부 사항 |
|:------|:------|
| 작성예정 | 작성예정  |

[//]: # (🎈 상품 기능)

[//]: # (🙆‍♀️마이 페이지 기능)

[//]: # (🔨 공방 페이지 기능&#40;Atelier&#41;)

[//]: # (✏️ 리뷰 및 문의 기능)

[//]: # (🔎 검색 기능)

[//]: # (💳 결제 및 선물하기 기능)


### 🚀 Backend



### 프로젝트 진행 [ 24.08.29 ~ 24.10.28 ]
| 기간                  |             세부 사항              | 
|:--------------------|:------------------------------:|
| 24.08.29 - 24.10.28 | 프로젝트 종료 및 Kubernetes shut down | 

<br>

### 리팩토링 [ 24.11.02 ~ ing ]
| 기간                  | 담당 업무                                  |                      세부 사항                      |
|:--------------------|:---------------------------------------|:-----------------------------------------------:|
| 24.11.02 - 24.11.10 | Github Action 을 통한 AWS 무중단 배포 및 이전     | EC2, S3, RDS, Github Action 을 활용한 Blue/Green 배포 |
| 25.01.24 - 25.01.24 | 도메인 변경 (comegongbang -> comegongbangs) |                        X                        |
| 25.02.24 - 25.02.24 | .yml 파일 분리                             |                 로컬/테스트/운영 환경 분리                 |
| 25.02.25 - 25.02.25 | 정적 팩토리 메서드 패턴 적용 / DTO record class 적용 |                        X                        |


[//]: # (## 📌 성능개선)

[//]: # ()
[//]: # (#### &nbsp;&nbsp; &nbsp;[로그인성능개선 바로가기]&#40;https://github.com/beyond-sw-camp/be06-fin-SYNergy-ComeGongBang/wiki/%F0%9F%92%AB-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%84%B1%EB%8A%A5%EA%B0%9C%EC%84%A0-%E2%80%90-Redis&#41;)

[//]: # (#### &nbsp;&nbsp; &nbsp;[쿠폰성능개선 바로가기]&#40;https://github.com/beyond-sw-camp/be06-fin-SYNergy-ComeGongBang/wiki/%E2%8F%B3-%EC%BF%A0%ED%8F%B0-%EC%84%A0%EC%B0%A9%EC%88%9C-%EA%B8%B0%EB%8A%A5-%EC%A0%81%EC%9A%A9%EA%B3%BC%EC%A0%95-%E2%80%90-Redis&#41;)

[//]: # (#### &nbsp;&nbsp; &nbsp;[검색기능개선 바로가기]&#40;https://github.com/beyond-sw-camp/be06-fin-SYNergy-ComeGongBang/wiki/%F0%9F%94%8D-%EA%B2%80%EC%83%89-%EC%84%B1%EB%8A%A5-%EA%B8%B0%EB%8A%A5%EA%B0%9C%EC%84%A0-%EA%B3%BC%EC%A0%95-%E2%80%90-ElasticSearch,-Logstash&#41;)

[//]: # ()
[//]: # ([//]: # &#40;    <img src="img/ERD_latest.png"/>&#41;)
[//]: # ([//]: # &#40;<img src="backend/img/system_architecture.png"/>&#41;)

<br>


<!--
## 💽&nbsp;&nbsp;CI/CD 배포 방식 및 시나리오

<br>



### 🧐 Blue/Green 방식을 사용한 이유 
&nbsp; `개발 초기 단계`인 점을 감안하여, `Blue/Green` 무중단 배포 기법을 선택하였다.  
&nbsp;  서비스 사용자 유입에 대한 예측과 사용자의 니즈를 파악하지 못한다는 점에서, 비교 대상군인 카나리와 비교해 봤을 때 레플리카셋 설정의 기준의 모호함을 감안하였다.  
&nbsp;  초기 새로운 기능의 개발 및 출시가 되었을 시, 개발자가 차마 예측하지 못한 다양한 에러에 대한 대응책으로, `이전 버전으로 롤백`이 가능하다는 점에서 현 초기 서비스와 적합하다고 판단하였다.  

### 🧐 추후 개선방향
#### Blue-Green 기법의 특성상 안고 가야할 과제는 다음과 같다.
&nbsp; 평상시에 어느 한쪽만을 이용한다는 것은, 인프라의 절반은 "놀고 있는" 상태가 된다는 것을 의미한다. 이는 비용적 측면에서 두 배의 차이를 보이기 때문에 적절한 레플리카셋으로 비용적 부담을 조절해야한다는 점이 있다.  

#### 만일, 서버의 크기와 사용자의 유입률이 증대가 된다면, 프론트엔드 측에 "카나리" 배포 기법 적용을 염두하고 있다.  
&nbsp; 직관적인 요소들로 사용자의 니즈를 파악할 수 있는 프론트엔드에서 새로운 버전 출시에 대한 A/B 테스트를 통해 어느쪽이 만족도가 높은지 추적하고 그에 따른 Release 방식을 채택함으로서, 사용자와 개발자 모두에게 좀 더 만족도 높은 기법으로 예상된다.  




<br>

<!--
#### ( 주의 ❗)
#### Blue/Green 방식으로 무중단 배포를 할 때, 만약 서버가 구동중인 상황이 클라우드나 가상환경이 아니라면? 정말 그냥 컴퓨터를 통해 물리적인 서버로 존재한다면? 
#### 기존에 있던 서버의 환경과 같은 수준의 서버를 두배로 늘렸다가 필요 없어지면 다시 줄이는 비 효율적인 방식을 선택할 수 없다. 한마디로, 물리적으로 존재하는 서버에서는 사용하기 어려우며 현재위치 배포 방식이 더 어울린다. Blue/Green방식은 쉽게 인스턴스를 생성하고 없앨 수 있는 클라우드 환경이나, 컨테이너를 올렸다가 내리는 것이 자유로운 Docker등의 가상환경에서 사용하는 것이 바람직하다.
-->


---

<!--
## 💻 CI/CD 시연 영상

<br>
<details>
<summary><b>🤵🏻‍♂️ Backend CI/CD </b></summary><br>
    <div>
    <details>
         <summary><b>Jenkins Pipeline</b></summary>
                  <br>
         <p><b>
          ➡ 백엔드 응답 메시지를 바꾸고 깃에 푸시
          ➡ 파이프라인이 작동
          ➡ 파드가 새로 생성
          ➡ 바뀐 응답메시지 확인
          </b></p><br>
         <p><img src="./img/backendPipeline.gif"/></p>
         </details>
    </div>
</details>
<br>
-->

<br>




<!--

## ✨ 프로젝트 기본 소개

<br>

### 프로젝트 배경
- 배경적기

### 프로젝트 목표
- 목표적기


<br>

---

## 📌 시연사이트 바로가기

### 📊 시연사이트링크넣기

<br>
---
-->


<!--
## &nbsp;📌 프로젝트 설명


### 👉&nbsp;&nbsp; Front
- LoadBalacer type의 서비스에 의해 외부에 연결되어 있다.
- nginx의 Reverse Proxy를 통해 front주소 /api가 붙어 있으면 k8s안의 Backend Service에 연결한다.
- 채팅 및 알람 기능은 연결을 지속적으로 유지하기 위해 http1.1이상 규격을 사용해야하며 nginx가 Reverse proxy 적용시 http1.1을 유지 하게 한다.
- 채팅의 경우 header가 http에서 ws로 upgrade 할 수 있도록 설정한다.
- Deployment로 k8s에서 작동하며 부하분산을 위해 2개의 pod로 운영된다.
- RollingUpdate 방식으로 무중단 배포 된다.

#### 🤔 [ Frontend 설명 더보기 ](https://github.com/beyond-sw-camp/be06-4th-SYNerge/wiki/Frontend)
<br>

### 👉&nbsp;&nbsp;Back
- SCDF에 의해 batch서버가 1분에 한번씩 pod로 작동하며, 이때 회원의 일정을 조회를 해서 메세지를 produce 하여 Cluster Ip를 통해 kafka broker로 전달한다. kafka broker는 Backend 서버에게 메세지를 전달하며, Backend는 메세지를 consume 하여 Frontend에게 SseEmitter를 통해 데이터를 전송한다.
- Deployment로 k8s에서 작동하며 부하분산을 위해 2개의 pod로 운영된다.
- 2개의 서버의 websocket session이 서로 달라 채팅 데이터가 누락이 될 수 있어, 채팅 메세지가 생성되면 kafka broker에게 전달하고 그 메세지를 2개의 서버가 consume한다.
- RollingUpdate 방식으로 무중단 배포 된다.
- Front, DB, kafka와 cluster ip로 통신하여 외부에 노출되지 않는다.

#### 🤔 [ Backend 설명 더보기 ](https://github.com/beyond-sw-camp/be06-4th-SYNerge/wiki/Backend)
<br>

### 👉&nbsp;&nbsp;CI/CD
- 개발자 Github에 push하게 되면, webhook에 의해 Jenkins가 작동한다.
- Jenkins는 pipeLine script에 따라 git clone, build, docker image build, docker image push의 과정을 거쳐 manifest 파일을 k8s master 서버 전송 후 deployment를 실행한다.

#### 🤔 [ CICD 설명 더보기 ](https://github.com/beyond-sw-camp/be06-4th-SYNerge/wiki/CI---CD)
-->
