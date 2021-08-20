package io.degeus.recipeappapi.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    private DatasourceProperties db;

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
}
