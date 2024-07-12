package productos;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@ComponentScan(basePackages = {
        "productos",
     "productos.api.*",
     "productos.infrastructure.*",
     "productos.api.mapper.*",
     "productos.api.mapper.ProductMapper",
    })
@SpringBootApplication(scanBasePackages= {"productos", "productos.api.mapper"})


public class ProductosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductosApplication.class, args);
	}
        
     
    
    
  @Bean
  public GroupedOpenApi publicApi() {
      return GroupedOpenApi.builder()
              .group("springshop-public")
              .packagesToScan("productos")
              .build();
  }

}
