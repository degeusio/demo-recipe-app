package io.degeus.recipeappapi.config;

import io.degeus.recipeappapi.RecipeAppApiApplication;
import io.degeus.recipeappapi.repository.UserRepository;
import io.degeus.recipeappapi.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackageClasses = RecipeAppApiApplication.class,
        entityManagerFactoryRef = "appEntityManager", //explicitly named, preventing to accidentally using Spring Framework's default
        transactionManagerRef = PersistenceConfig.APP_TRANSACTION_MANAGER //explicitly named, preventing to accidentally using Spring Framework's default
)
@EnableJpaAuditing //enables a.o. capturing of Create and LastModifiedTimestamp
@RequiredArgsConstructor
@Slf4j
public class PersistenceConfig {

    public static final String APP_TRANSACTION_MANAGER = "appTransactionManager";

    private final AppProperties appProperties;

    @Bean
    public DataSource dataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        log.info("Using datastore url: [{}]", appProperties.getDb().getUrl());
        log.info("Using datastore user: [{}]", appProperties.getDb().getUsername());

        dataSource.setUrl(appProperties.getDb().getUrl());
        dataSource.setDriverClassName(appProperties.getDb().getDriverClassName());
        dataSource.setUsername(appProperties.getDb().getUsername());
        dataSource.setPassword(appProperties.getDb().getPassword());
        return dataSource;
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository) {
        return new UserDetailsServiceImpl(userRepository);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean appEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());

        em.setPackagesToScan("io.degeus.recipeappapi");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.lock.timeout", "0"); //lock wait time to acquire a lock in millis.
        properties.put("hibernate.dialect", appProperties.getDb().getDatabasePlatform());
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", false); //  # hides harmless stacktrace during startup, see https://stackoverflow.com/questions/4588755/disabling-contextual-lob-creation-as-createclob-method-threw-error
        //Spring Boot, provides defaults for both: https://www.baeldung.com/hibernate-field-naming-spring-boot
        properties.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        properties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        em.setJpaPropertyMap(properties);
        return em;
    }

    /**
     * note: Default spring's transaction timeout may be indefinite, see e.g. <a href="https://stackoverflow.com/questions/46827279/transactional-timeout-default-value#:~:text=In%20Spring%20documentation%2C%20it%20is,default%20timeout%20value(%2D1)">link</a>.
     * For production, specifying a timeout out
     */
    @Bean(name = APP_TRANSACTION_MANAGER)
    public PlatformTransactionManager appTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(appEntityManager().getObject());
        transactionManager.setDefaultTimeout(appProperties.getDb().getDefaultTransactionTimeoutInSeconds());
        return transactionManager;
    }
}
