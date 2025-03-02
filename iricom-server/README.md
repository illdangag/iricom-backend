# Iricom Service

---

## 설정

### Firebase

계정의 인증

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

### Database

JPA 설정을 위한 `resources/application-database.yml`

```yaml
spring:
  datasource:
    driver-class-name: org.mariadb.jdbc.Driver
    url: jdbc:{{database_url}}
    username: {{username}}
    password: {{password}}
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MariaDB106Dialect
        show_sql: false
        format_sql: true
        use_sql_comments: true
      org.hibernate.envers.audit_table_suffix: _history
      org.hibernate.envers.revision_field_name: rev_id
      org.hibernate.envers.store_data_at_delete: true
```

### storage

storage 설정을 위한 `resources/application-storage.yml`

**S3 object storage**

```yaml
storage:
  s3:
    endpoint: {{endpoint_url}}
    region: {{region}}
    accessKey: {{access_key}}
    secretKey: {{secret_key}}
    bucket: {{bucket}}
```

**File storage**

```yaml
storage:
  path: {{file_path:/home/iricom/file}}
```

## REST API 문서

`Spring REST Docs`

{Iricom host}/docs/index.html
