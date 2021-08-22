package io.degeus.recipeappapi.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    private DatasourceProperties db;
    private CorsProperties cors;

    @Getter
    @Setter
    public static class DatasourceProperties {

        private String url;
        private String username;
        private String password;
        private String driverClassName;
        private String databasePlatform;
        private int defaultTransactionTimeoutInSeconds;
    }

    @Getter
    @Setter
    public static class CorsProperties {
        private List<String> allowedOrigins;
    }
}
