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
