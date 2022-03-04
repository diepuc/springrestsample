package com.userbot.test;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SwaggerGenerator {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @PostConstruct
    public Boolean disableSSLValidation() throws Exception {
        final SSLContext sslContext = SSLContext.getInstance("TLS");

        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        } }, null);

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        hostNameVerifier();

        return true;
    }

    public void hostNameVerifier() {
        final HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier ();
        final HostnameVerifier localhostAcceptedHostnameVerifier = new HostnameVerifier () {
            public boolean verify ( String hostname, SSLSession sslSession ) {
                if ( hostname.equals ( "localhost" ) ) {
                    return true;
                }
                return defaultHostnameVerifier.verify ( hostname, sslSession );
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier ( localhostAcceptedHostnameVerifier );
    }

    @Test
    public void generateSwagger() throws Exception {
        JsonNode response = testRestTemplate
                .withBasicAuth("admin", "alab123")
                .getForObject(testRestTemplate.getRootUri() + "/v3/api-docs", JsonNode.class);
        FileUtils.writeStringToFile(new File("swagger.json"), response.toPrettyString(), StandardCharsets.UTF_8);
    }
}
