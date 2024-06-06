

## API - MyAddress
</br>

### CONTEXTO
---
Está é uma API que eu criei exclusivamente para minha Aplicação Front-End (**MyAddress**), onde um usuário informa seu CEP, e, é retornado além do endereço, várias outras informações sobre o CEP. A minha API se comunica com a **API ViaCEP**

**OBS**: eu poderia fazer com que minha Aplicação Front-End se comunicasse diretamente com a **API ViaCEP**, porém, eu queria realizar uma Aplicação Fullstack, logo, optei por criar uma API que se comunicasse com ela.

</br>

### INÍCIO
---
Primeiro, fui no **Spring Initializr** e criei o projeto `br.com.myaddress`. Além de adicionar as dependências: `Spring Web` e `Dev Tools`

Ao abrir a IDE, o projeto deve estar assim:

```markdown
# diretório > src > main > java > br > com > api_myaddress > MyaddressAplication.java

```

Além disso, dentro da pasta `java`, vamos criar mais duas pastas:

-   `config`: será responsável por permitir todos os tipos de `requisições` e `Métodos HTTP`
-   `rotas`: vai ser responsável por receber a `requição`, se comunicar com o `ViaCEP`, e, retornar a `resposta`

Dentro de `config`, crie o arquivo `CorsConfig.java`, e, dentro de `rotas` crie os arquivos `Rotas.java`, `Endereco.java`, `ComunicacaoApi.java`

Por fim, faço com que o `Método Main` (`MyaddressAplication.java`) fique de olho nestes arquivos que criamos:

```java
package br.com.api_myadress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"br.com.api_myadress", "rotas", "config"})
public class MyaddressApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyaddressApplication.class, args);
	}
}


```

</br>

### RECEBER REQUISIÇÕES
---
Primeiro vamos fazer o seguinte, suba a sua pasta `front-end` (**Veja meu Repositório:  MyAddress**) num `Server` da `Vercel`

Após isso, vamos no arquivo `CorsConfig.java`, e, permitimos todos os tipos de `Métodos HTTP`, porém, somente de `websites` com a `URL` que a `Vercel` criou para nós:

`CorsConfig.java`
```java
package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://my-address-rho.vercel.app/") 
                .allowedMethods("GET", "POST", "PUT", "DELETE") 
                .allowedHeaders("*"); // permite todos os cabeçalhos
    }
}

```

</br>

### COMUNICARSE COM O ViaCEP
---
Vamos agora dentro de `ComunicacaoApi.java` realizar uma comunicação com `ViaCEP`, com o `CEP` que receberemos do `Front-End`

Porém, para fazermos isso, vamos antes no `Record Endereco.java` que guardará os Dados pegos de `ViaCEP` de acordo com os nomes de sua `Key`:

`Endereco.java`

```java
package rotas;

public record Endereco(String cep, String logradouro, String complemento, String localidade, String uf) {}

```

`ComunicacaoApi.java`

```java
package rotas;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;

public class ComunicacaoApi {
    public Endereco procurar_endereco(String cep) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("<https://viacep.com.br/ws/>" + cep + "/json/"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            return gson.fromJson(response.body(), Endereco.class);

        } catch(Exception e) {
            throw new RuntimeException("O CEP informado deve ter EXATAMENTE 8 dígitos.");
        }
    }
}


```

Vamos entender:

1.  Criamos um `Método` que recebe um `CEP` como `parâmetro`, que irá fazer a comunicação, e este `Método` deve retornar um Dado do tipo `Endereco`:

```java
public Endereco procurar_endereco(String cep) {...}

```

1.  Dentro do `Método` faremos um `try/catch` porque é recomendado em momentos de `requisições` e porque o comando `client.send()` nos OBRIGA A USAR UM `try/catch`

Aqui, o `try` realiza a comunicação com `ViaCEP`:

```java
try {
	HttpClient client = HttpClient.newHttpClient();

	HttpRequest request = HttpRequest.newBuilder()
		.uri(URI.create("<https://viacep.com.br/ws/>" + cep + "/json/"))
		.build();

	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

```

Ainda no `try`, nós devemos colocar as respostas dentro de um `Objeto` que venha da `Classe Endereco`. Logo, precisamos usar o `Gson()`, para isso, fui no site `Maven Repository`, copiei o que deveria colocar no arquivo `pom.xml` e coloquei:

```xml
<dependencies>
	......
	<!-- <https://mvnrepository.com/artifact/com.google.code.gson/gson> -->
	<dependency>
		<groupId>com.google.code.gson</groupId>
		<artifactId>gson</artifactId>
		<version>2.11.0</version>
	</dependency>
</dependencies>

```

Após isso, criei um `Objeto` do tipo `Endereco`, e, guardei os Dados que `ViaCEP` me enviou:

```java
try {
	HttpClient client = HttpClient.newHttpClient();

	HttpRequest request = HttpRequest.newBuilder()
		.uri(URI.create("<https://viacep.com.br/ws/>" + cep + "/json/"))
		.build();

	HttpResponse<String> response = client
		.send(request, HttpResponse.BodyHandlers.ofString());
								
	Gson gson = new Gson();
	return gson.fromJson(response.body(), Endereco.class);
}

```

1.  Por fim, como não sabemos exatamente os tipos de `exceções` que podem ocorrer, colocamos `Exception`, e, falamos que se algum ocorrer deve rodar um `RuntimeException` com uma mensagem:

```java
try {
	.....
} catch(Exception e) {
	throw new RuntimeException("O CEP informado deve ter EXATAMENTE 8 dígitos.");
}

```

`ComunicacaoApi.java`
```java
package rotas; 

import java.net.URI; 
import java.net.http.HttpClient; 
import java.net.http.HttpRequest; 
import java.net.http.HttpResponse; 
import com.google.gson.Gson; 

public class ComunicacaoApi { 
	public Endereco procurar_endereco(String cep) { 
		try { 
			HttpClient client = HttpClient.newHttpClient(); 

			HttpRequest request = HttpRequest.newBuilder() 
				.uri(URI.create("https://viacep.com.br/ws/" + cep + "/json/"))
				.build(); 

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); 

			Gson gson = new Gson(); 

			return gson.fromJson(response.body(), Endereco.class); 

		} catch(Exception e) { 
			throw new RuntimeException("O CEP informado deve ter EXATAMENTE 8 dígitos."); 
		} 
	}
}
```

</br>

### RECEBER E RESPONDER REQUISIÇÃO
---
Agora para finalizar, vamos em `Rotas.java` para recebermos a `requisição` do `Front`, mandar para o `ComunicaoApi.java` e retornar o resultado para o `Front`

Porém, antes de continuarmos devemos lembrar que o `CEP` está sendo enviado num `body` e como `JSON`. Logo, devemos fazer o seguinte, qualquer `Método POST` deve pegar os Dados de `body`:

```java
package rotas;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Rotas {

    @PostMapping()
    public Endereco index(@RequestBody String requisicao) { <-------
    }
}


```

Mas, não pode ser do tipo `String`, ao invés disso, devemos criar uma outra `Classe` que irá armazenar esse `JSON`, e, criarmos um `Método` para pegarmos seu valor (que no caso é o `CEP`):

`RequisicaoFront.java`

```java
package rotas;

public class RequisicaoFront {
    private String cep;

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
}


```

Agora falamos que a `requisição` deve armazenar seu `body` em `RequisicaoFront`, e, com o `Método` `getCep()` pegamos o `CEP` enviado:

`Rotas.java`

```java
package rotas;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Rotas {

    @PostMapping()
    public Endereco index(@RequestBody RequisicaoFront requisicao) {   <------
        String cep = requisicao.getCep();       <------
    }
}


```

Por fim, criamos um `Objeto api` de `ComunicacaoApi` e usamos seu `Método procurar_api()`

No final, retornarmos seu valor que é do tipo `Endereco`, e, caso dê um `catch` do tipo `RuntimeException` vai rodar a mensagem que colocamos em `ComunicacaoApi.java`:
```java
package rotas;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Rotas {

    @PostMapping()
    public Endereco index(@RequestBody RequisicaoFront requisicao) {   <------
        String cep = requisicao.getCep();                              <------

        try {
            ComunicacaoApi api = new ComunicacaoApi();
            System.out.println("Deu certo a comunicação");
            return api.procurar_endereco(cep);    

        } catch(RuntimeException e) {
            System.out.println("Houve um erro: " + e.getMessage());
        }
        return null;
    }
}

```

</br>

### DOCKERFILE E DEPLOY
---
Para nosso `Back-End` subir num `Server`, vamos usar o site `Render`, porém, para rodar tudo direitinho devemos ter um arquivo `Dockfile` com as seguintes informações:

OBS: Copiei o `pom.xml`, taquei no `ChatGPT` e pedi para me fazer um `Dockerfile`

```docker
# Use a imagem oficial do Maven para a fase de build
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copie o arquivo pom.xml e as dependências do projeto
COPY pom.xml .
COPY src ./src

# Execute o build do projeto
RUN mvn clean package -DskipTests

# Use a imagem oficial do OpenJDK para a fase de runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copie o jar da fase de build para a fase de runtime
COPY --from=build /app/target/api-myadress-0.0.1-SNAPSHOT.jar app.jar

# Exponha a porta que a aplicação irá rodar
EXPOSE 4545

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]

```

Pronto, agora criamos um repositório no `GitHub` para nossa pasta `Back-End`, vamos no site `Render`:

-   `Webservice`
-   Copia o repositório da pasta `Back-End`
-   `Runtime: Docker`
-   Finalize e crie.

Uma coisa que devemos fazer antes, é, voltar para a pasta `Front-End`, no arquivo `main.ts` troque a `URL` que o `fetch()` fará, para a `URL` que o `Render` criou:

```tsx
const url: string = '<https://api-myaddress.onrender.com/>';

```

Adicione essas alterações ao `GitHub`, espera a `Vercel` atualizar, espera o `Render` fazer com que no `Terminal` deles apareça na tela `"Server live"`, e pronto, só testar sua `Aplicação`

</br>

### OBS SOBRE O RENDER
---
Eu estou usando a forma gratuita, isso significa que após 15 minutos fora do site, o **Server** irá se desligar.
