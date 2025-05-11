package com.illdangag.iricom.core.test.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illdangag.iricom.core.test.data.FirebaseClientConfig;
import com.illdangag.iricom.core.test.data.FirebaseTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FirebaseUtils {
    private static FirebaseClientConfig firebaseClientConfig;

    static {
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("firebase-client.json");
            ObjectMapper objectMapper = new ObjectMapper();
            firebaseClientConfig = objectMapper.readValue(inputStream, FirebaseClientConfig.class);
        } catch (Exception exception) {
            log.error("Fail to read firebase client json.", exception);
        }
    }

    private FirebaseUtils() {
    }

    // TODO 유효하지 않은 토큰인 경우 예외 처리
    public static FirebaseTokenResponse getToken(String email, String password) throws Exception {
        URI uri = new URIBuilder(firebaseClientConfig.getHost() + "/v1/accounts:signInWithPassword")
                .addParameter("key", firebaseClientConfig.getApiKey())
                .build();

        HttpPost httpPost = new HttpPost(uri);

        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        List<NameValuePair> list = new ArrayList<>(5);
        list.add(new BasicNameValuePair("email", email));
        list.add(new BasicNameValuePair("password", password));
        list.add(new BasicNameValuePair("returnSecureToken", "true"));
        httpPost.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));

        String responseBody = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode >= 500 && statusCode < 600) {
                throw new IOException("Status code : " + statusCode);
            }

            HttpEntity entity = httpResponse.getEntity();
            responseBody = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException exception) {
            throw exception;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responseBody, FirebaseTokenResponse.class);
    }
}
