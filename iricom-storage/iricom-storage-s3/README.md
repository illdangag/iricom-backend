# Iricom Storage S3

---

iricom-storage의 구현체

S3 또는 minio를 사용하여 파일 데이터 저장

## 설정

### VM Option

```
-Dstorage.s3.endpoint={minio를 사용하는 경우 minio api host}
-Dstorage.s3.region={S3 region}
-Dstorage.s3.accessKey={S3 access key}
-Dstorage.s3.secretKey={S3 secret key}
-Dstorage.s3.bucket={S3 bucket}
```