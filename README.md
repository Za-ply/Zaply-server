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

## 🔨 Project Architecture
![DWyLfwAAAABJRU5ErkJggg](https://github.com/user-attachments/assets/3f6ad088-d6be-404d-8c43-4a6195c74b8b)

<br></br>

## 📅 ERD
![438857549-03c767c7-2d4b-4e72-b786-d1083101ffe7](https://github.com/user-attachments/assets/ee574bf0-eadd-4604-8a7a-96f7f3e21965)

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
