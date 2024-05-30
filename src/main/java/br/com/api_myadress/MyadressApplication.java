package br.com.api_myadress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"br.com.api_myadress", "rotas", "config"})
public class MyadressApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyadressApplication.class, args);
	}

}
