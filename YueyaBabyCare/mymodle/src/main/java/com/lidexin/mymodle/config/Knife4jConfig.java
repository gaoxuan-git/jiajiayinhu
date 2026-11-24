package com.lidexin.mymodle.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Knife4j(Swagger) API文档配置
 * 访问地址：http://localhost:8080/doc.html
 * 
 * @author 张三
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
public class Knife4jConfig {

    @Value("${mymodle.swagger.enabled:true}")
    private Boolean enabled;

    @Value("${mymodle.swagger.title:MyModle API接口文档}")
    private String title;

    @Value("${mymodle.swagger.description:MyModle项目接口文档}")
    private String description;

    @Value("${mymodle.swagger.version:1.0.0}")
    private String version;

    @Value("${mymodle.swagger.base-package:com.lidexin}")
    private String basePackage;

    @Value("${mymodle.swagger.contact.name:张三}")
    private String contactName;

    @Value("${mymodle.swagger.contact.email:your-email@example.com}")
    private String contactEmail;

    @Value("${mymodle.swagger.contact.url:https://github.com/yourusername}")
    private String contactUrl;

    /**
     * 创建API文档
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                // 是否启用Swagger
                .enable(enabled)
                // 用来创建该API的基本信息，展示在文档的页面中（自定义展示的信息）
                .apiInfo(apiInfo())
                // 分组名称
                .groupName(version)
                .select()
                // 扫描的包路径
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                // 指定路径处理PathSelectors.any()代表所有的路径
                .paths(PathSelectors.any())
                .build()
                // 配置安全认证（JWT）
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * API文档基本信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .contact(new Contact(contactName, contactUrl, contactEmail))
                .version(version)
                .build();
    }

    /**
     * 安全模式，这里配置通过请求头Authorization传递token
     */
    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> apiKeyList = new ArrayList<>();
        apiKeyList.add(new ApiKey("Authorization", "Authorization", "header"));
        return apiKeyList;
    }

    /**
     * 安全上下文
     */
    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("^(?!auth).*$"))
                        .build());
        return securityContexts;
    }

    /**
     * 默认的安全引用
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("Authorization", authorizationScopes));
        return securityReferences;
    }
}

