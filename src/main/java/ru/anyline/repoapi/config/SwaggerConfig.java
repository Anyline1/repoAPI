package ru.anyline.repoapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

public class SwaggerConfig {

    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Get public repos API")
                        .version("13.08")
                        .description("API для получения публичных репозиториев с GitHub"));
    }
}
