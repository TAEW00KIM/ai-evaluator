# AI AutoGrader (ai-evaluator-be)

딥러닝 기초 과제 자동 채점 시스템의 **중앙 API 백엔드 서버**입니다.

Spring Boot를 기반으로 구축되었으며, React 프론트엔드의 요청을 받아 사용자 인증, 과제 제출을 처리하고, Python 채점 서버에 채점을 요청한 뒤 그 결과를 다시 받아 프론트엔드로 전달하는 핵심 허브 역할을 합니다.

---

## 🚀 프로젝트 아키텍처

이 시스템은 세 개의 독립된 서버로 구성되어 유기적으로 동작합니다.

1.  **Frontend (React)**: 사용자가 보는 웹 화면입니다. 과제 파일을 업로드하고, 채점 현황을 폴링(polling)합니다.
2.  **Backend (Spring Boot)**: **(현재 리포지토리)** 모든 API 요청의 게이트웨이입니다.
    * 사용자 인증(OAuth2) 및 세션을 관리합니다.
    * 프론트엔드로부터 과제 파일(zip)을 받아 `uploads/` 디렉터리에 저장합니다.
    * Python 서버의 `/evaluate` API를 호출하여 채점을 "요청"합니다.
    * Python 서버로부터 `/running`, `/complete` 콜백을 받아 제출물의 상태를 DB에 업데이트합니다.
    * 프론트엔드에 채점 결과, 리더보드, 제출 목록 등 모든 데이터를 제공합니다.
3.  **Python Server (FastAPI)**: 실제 채점을 담당하는 격리된 워커(worker)입니다.
    * 백엔드로부터 요청이 오면, `uploads/`의 파일을 `grading_temp/`에 복사 및 압축 해제합니다.
    * 학생의 코드를 실행하고(timeout 적용), `stdout`으로 JSON 결과를 출력받습니다.
    * 채점 완료 후 백엔드의 콜백 API로 점수와 로그를 다시 전송합니다.

---

## 🛠️ 주요 기술 스택

* **Framework**: Spring Boot 3.5.6
* **Language**: Java 17
* **Database**: Spring Data JPA, MySQL
* **Authentication**: Spring Security, OAuth2 (Google Login)
* **Async**: Spring WebFlux (WebClient)
* **API Docs**: Swagger (OpenAPI)

---

## 🔑 핵심 기능

### 1. 사용자 인증 및 권한 관리

* **Google OAuth2 로그인**: Google OIDC를 통한 사용자 인증을 지원합니다.
* **도메인 제한**: `@hufs.ac.kr` 이메일 주소를 가진 사용자만 로그인이 허용됩니다.
* **권한 분리**: `USER`(학생)와 `ADMIN`(관리자) 역할로 분리됩니다.

### 2. 비동기 자동 채점 (핵심 로직)

* **과제 제출 (POST /api/submissions)**:
    1.  학생이 zip 파일을 업로드하면, 서버는 파일을 `uploads/` 디렉터리에 저장합니다.
    2.  `Submission` 엔티티를 `PENDING` 상태로 DB에 저장합니다.
    3.  `@Async`를 통해 Python 서버의 `/evaluate` API를 비동기 호출합니다.
* **상태 업데이트 (내부 API)**:
    1.  `POST /internal/submissions/{id}/running`: Python 서버가 채점을 시작할 때 호출합니다. 상태를 `RUNNING`으로 변경합니다.
    2.  `POST /internal/submissions/{id}/complete`: Python 서버가 채점을 완료했을 때 호출합니다. 점수(score)와 로그(log)를 받아 `COMPLETED` 또는 `ERROR` 상태로 변경합니다.

### 3. 기능 API

* **과제 관리 (Admin)**: `POST`, `GET`, `DELETE /api/admin/assignments/**`
* **결과 조회 (User)**: `GET /api/submissions/me`, `GET /api/submissions/{id}`
* **리더보드 (User)**: `GET /api/leaderboard/{assignmentId}`
* **전체 제출 조회 (Admin)**: `GET /api/admin/submissions`
