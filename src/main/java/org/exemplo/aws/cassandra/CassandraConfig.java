package org.exemplo.aws.cassandra;

import com.datastax.oss.driver.internal.core.ssl.DefaultSslEngineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import software.aws.mcs.auth.SigV4AuthProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;

@Configuration
public class CassandraConfig {
    @Bean
    public CqlSessionFactoryBean session() {
        SigV4AuthProvider provider = new SigV4AuthProvider("us-east-1");

        CqlSessionFactoryBean session = new CqlSessionFactoryBean();

        List<InetSocketAddress> contactPoints = Collections.singletonList(new InetSocketAddress("cassandra.us-east-1.amazonaws.com", 9142));

        session.setSessionBuilderConfigurer(config -> {
            try {
                return config
                        .addContactPoints(contactPoints)
                        .withKeyspace("teste")
                        .withAuthProvider(provider)
                        .withSslContext(this.sslContext("cassandra_truststore.jks", "amazon"))
                        .withLocalDatacenter("us-east-1");
            } catch (Exception e) {

                return  null;
            }
        });
        session.setKeyspaceName("teste");

        return session;
    }

    private SSLContext sslContext(String keystoreFile, String password)
            throws GeneralSecurityException, IOException {
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream in = new FileInputStream(keystoreFile)) {
            keystore.load(in, password.toCharArray());
        }
        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, password.toCharArray());

        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(
                keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(),
                new SecureRandom());

        return sslContext;
    }
}
