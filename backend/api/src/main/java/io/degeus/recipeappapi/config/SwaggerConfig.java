package io.degeus.recipeappapi.config;

import io.degeus.recipeappapi.web.RecipeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.security.Principal;
import java.util.Collections;
import java.util.function.Predicate;

import static java.util.function.Predicate.not;

@Configuration
public class SwaggerConfig {

    public static final String SWAGGER_V2_API_DOCS_ENDPOINT = "/v2/api-docs";
    public static final String SWAGGER_V2_UI_ENDPOINT = "/swagger-ui";
    public static final String SWAGGER_V2_UI_RESOURCES_ENDPOINT = "/swagger-resources";

    @Autowired
    CachingOperationNameGenerator cachingOperationNameGenerator;

    @Bean
    public Docket apiDocket() {

        Predicate<RequestHandler> recipeHandlers = RequestHandlerSelectors.basePackage(RecipeController.class.getPackage().getName());
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(recipeHandlers)
                .apis(not(RequestHandlerSelectors.withMethodAnnotation(SwaggerIgnore.class)))
                .apis(not(RequestHandlerSelectors.withClassAnnotation(SwaggerIgnore.class)))
                .paths(PathSelectors.any())
            .build()
                .ignoredParameterTypes(Principal.class, Authentication.class) //do not generate Authentication related swagger
                .ignoredParameterTypes(User.class, UserDetails.class) //do not generate for spring's User class
//                .securitySchemes(List.<SecurityScheme>of(basicAuthScheme())) //only basicAuth for now
//                .securityContexts(Arrays.asList(securityContext()))
                .useDefaultResponseMessages(false) //removes default 200, 401, 403 and 404 for GET, and 200, 201,  401, 403 and 404 for GET
                .apiInfo(getAppInfo());
        return docket;
    }

//    private SecurityScheme basicAuthScheme() {
//        return new BasicAuth("basicAuth");
//    }

    /**
     * ensures there is a lock icon next to operations that match these path selectors.
     * E.g. registers Basic Auth for /oauth.* endpoint and
     * registers bearer token for /users.*
     * @return
     */
//    private SecurityContext securityContext() {
//        //not including /api/alert.*
//        Predicate protectedPaths = PathSelectors.regex("./recipes.*");
//
//        return SecurityContext.builder()
//                .operationSelector()
//                .forPaths(
//                    protectedPaths
//                )
//                .build();
//                //note: for adding basic auth, see rephub-iam
//    }

    private ApiInfo getAppInfo() {

        return new ApiInfo(
                "Recipe app API",
                " ",
                "1.0",
                null,
                null,
                " ",
                null,
                Collections.emptyList()
        );
    }
}
