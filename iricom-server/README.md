# Iricom Service

---

## 설정

### Firebase

계정의 인증

- firebase authentication

firebase에서 프로젝트를 생성 후 프로젝트 설정 페이지의 서비스 설정 탭에서 firebase의 비공개 키를 발급 받아 `resources/firebase-adminsdk.json`에 위치

```json
{
  "type": "service_account",
  "project_id": "{{project_id}}",
  "private_key_id": "{{private_key_id}}",
  "private_key": "-----BEGIN PRIVATE KEY-----\n{{private_key}}\n-----END PRIVATE KEY-----\n",
  "client_email": "{{client_email}}",
  "client_id": "{{client_id}}",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "{{client_x509_cert_url}}"
}
```

## 실행

```shell
java \
-Dspring.datasource.url=jdbc:mariadb://{HOST}:{PORT}/{DATABASE} \
-Dspring.datasource.username={USERNAME} \
-Dspring.datasource.password={PASSWORD} \
-jar ./iricom-backend-0.0.0.jar
```

## REST API 문서

`Spring REST Docs`

{Iricom host}/docs/index.html