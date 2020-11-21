package vn.com.nimbus.common.config.swagger;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Configuration
@EnableSwagger2WebFlux
public class SwaggerConfig {
    private final TypeResolver resolver;
    @Autowired
    SwaggerConfig(TypeResolver resolver) {
        this.resolver = resolver;
    }
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(true)
                .produces(Set.of("application/json"))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .alternateTypeRules(new RecursiveAlternateTypeRule(resolver,
                        Arrays.asList(
                                AlternateTypeRules.newRule(
                                        resolver.resolve(Mono.class, WildcardType.class),
                                        resolver.resolve(WildcardType.class)),
                                AlternateTypeRules.newRule(
                                        resolver.resolve(ResponseEntity.class, WildcardType.class),
                                        resolver.resolve(WildcardType.class))
                        )))
                .alternateTypeRules(new RecursiveAlternateTypeRule(resolver,
                        Arrays.asList(
                                AlternateTypeRules.newRule(
                                        resolver.resolve(Flux.class, WildcardType.class),
                                        resolver.resolve(List.class, WildcardType.class)),
                                AlternateTypeRules.newRule(
                                        resolver.resolve(ResponseEntity.class, WildcardType.class),
                                        resolver.resolve(WildcardType.class))
                        )));
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Nimbus Blog Internal")
                .description("These services manage all things about blogs")
                .version("1.0")
                .build();
    }
}