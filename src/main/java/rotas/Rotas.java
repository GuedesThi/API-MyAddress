package rotas;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Rotas {

    @PostMapping()
    public Endereco index(@RequestParam String cep) {

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
