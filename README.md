# Zaply-server
대학생 IT경영학회 큐시즘 31기 밋업 프로젝트 1조 Zaply 백엔드 레포지토리
<br></br>

## 👬 Member
|      정성호     |         염지은         |                                                                                                   
| :------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------------------------------------------------: | 
|   <img src="https://avatars.githubusercontent.com/SeongHo5356?v=4" width=90px alt="정성호"/>       |   <img src="https://avatars.githubusercontent.com/yumzen?v=4" width=90px alt="염지은"/>                       |
|   [@SeongHo5356](https://github.com/SeongHo5356)   |    [@yumzen](https://github.com/yumzen)  | 

<br></br>

## 📝 Technology Stack
| Category             | Technology                                                                 |
|----------------------|---------------------------------------------------------------------------|
| **Language**         | Java 21                                                                 |
| **Framework**        | Spring Boot 3.3.10                                                        |
| **Databases**        | Postgresql, Redis                                                             |
| **Authentication**   | JWT, Spring Security, OAuth2.0                                           |
| **Development Tools**| Lombok                                                   |
| **API Documentation**| Swagger UI (SpringDoc)                                                   |
| **Storage**          | AWS S3                                                                   |
| **Infrastructure**   | Terraform, NCP Server                               |
| **Build Tools**      | Gradle    |

<br></br>

## 📅 ERD
![438857549-03c767c7-2d4b-4e72-b786-d1083101ffe7](https://github.com/user-attachments/assets/ee574bf0-eadd-4604-8a7a-96f7f3e21965)

<br></br>

## Api 명세서
http://zapply.site/swagger-ui/index.html

<br></br>

## 🔨 Project Architecture
![image](https://github.com/user-attachments/assets/e648f450-065a-4160-93da-0ecf269d603b)

<br></br>

## 💬 Question
[BE]
1. 페북, 인스타, 스레드 등 sns에서 제공하는 api를 사용하는 서비스 로직이 많은데, 외부 api를 효율적으로 관리하고 사용할 수 있는 방법이 어떤 게 있나요?
2. 다른 플랫폼의 장기발행 토큰을 안전하게 관리하기 위해 도입할 수 있는 방법이 어떤 게 있나요?
3. 예약발행 기능을 구현하면서, api를 지정된 시각에 빠짐없이 호출되도록 하고 싶은데, 어떻게 하는게 좋을까요? 찾아보니 메세지 큐 기반으로 구현할 수도 있고, scheduler를 활용해 구현할 수도 있는데, 어떤 기준을 가지고 선택을 하면 좋을까요?
  
[FE]
1. 저희 팀은 PWA를 통해 앱 뷰 형식으로 작업을 진행 중입니다! 앱 처럼 보이기 위해 스플래시 이미지나 앱 로고 이미지를 프론트에서 가지고 있는 상황인데, 이를 webp 확장자로 관리를 하려다 보니 화질이 깨지는 것들이 있어 우선 임시로 png 파일로 들고 있는 상황입니다! 프론트에서 들고 있어야 하는 이미지 파일이 많은 경우의 관리 방식이 궁금합니다!
2. constants (상수) 관리 시 한 파일 안에 모두 모아두는 방법 말고 분리해서 관리할 수 있는 좋은 방안이 있는지 궁금합니다!
3. 저희 서비스 성격 상 정적 페이지가 많이 없다고 판단되어 next에서 기본 제공하는 fetch 캐싱이 아닌 tanstack-query를 활용한 클라이언트 캐싱을 하려고 합니다! 이 과정에서 axios 를 활용할 예정인데, axios 를 사용해도 next 의 이점인 서버 캐싱이 가능한지 궁금합니다! 

<br></br>

## 💬 Convention

**commit convention** <br>
`#이슈번호 conventionType: 구현한 내용` <br><br>


**convention Type** <br>
| convention type | description |
| --- | --- |
| `feat` | 새로운 기능 구현 |
| `chore` | 부수적인 코드 수정 및 기타 변경사항 |
| `docs` | 문서 추가 및 수정, 삭제 |
| `fix` | 버그 수정 |
| `test` | 테스트 코드 추가 및 수정, 삭제 |
| `refactor` | 코드 리팩토링 |

<br></br>

## 🪵 Branch
### 
- `컨벤션명/#이슈번호-작업내용`
- pull request를 통해 develop branch에 merge 후, branch delete
- 부득이하게 develop branch에 직접 commit 해야 할 경우, `!hotfix:` 사용

<br></br>

## 📁 Directory

```PlainText
src/
├── main/
│   ├── domain/
│   │   ├── entity/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   └── dto/
            ├── request/
            └── response/
│   ├── global/
│   │   ├── apiPayload/
│   │   ├── config/
│   │   ├── security/
		 
```

<br></br>
