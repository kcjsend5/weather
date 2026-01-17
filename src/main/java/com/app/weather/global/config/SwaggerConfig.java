package com.app.weather.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components component = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        );
        return new OpenAPI()
                .components(component)
                .info(new Info()
                        .title("Weather Alarm API")
                        .description("실시간 날씨 알림 시스템용 백엔드 프로젝트입니다.")
                        .version("1.0.0"))
                .addSecurityItem(securityRequirement);
    }
    @Bean
    public OpenApiCustomizer customOpenAPI() {
        List<String> tagOrder = List.of(
                "인증", "초단기예보","단기예보","날씨실황","기능","알림설정");

        return openApi -> openApi.setTags(
                openApi.getTags().stream()
                        .sorted(Comparator.comparingInt(tag -> IntStream.range(0, tagOrder.size())
                                .filter(i -> tag.getName().contains(tagOrder.get(i)))
                                .findFirst()
                                .orElse(tagOrder.size())))
                        .toList()
        );
    }
}
