package com.jlink;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@SpringBootApplication
@EnableSwagger2WebMvc
public class JLinkApplication {


    public static void main(String[] args) {
        SpringApplication.run(JLinkApplication.class, args);
    }

    @Bean(value = "defaultApi2")
    public Docket defaultApi2(OpenApiExtensionResolver openApiExtensionResolver) {
        String groupName = "2.X版本";
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("jlink RESTful APIs")
                        .description("# jlink RESTful APIs")
                        .termsOfServiceUrl("http://www.jlink.com/")
                        .contact("jlink@163.com")
                        .version("1.0")
                        .build())
                //分组名称
                .groupName("2.X版本")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.jlink.controller"))
                .paths(PathSelectors.any())
                .build()
                .extensions(openApiExtensionResolver.buildExtensions("1.0.0"));
        return docket;
    }
}
