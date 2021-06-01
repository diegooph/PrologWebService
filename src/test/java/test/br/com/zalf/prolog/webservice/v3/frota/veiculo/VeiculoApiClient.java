package test.br.com.zalf.prolog.webservice.v3.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoCadastroDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.net.URI;

@TestComponent
public class VeiculoApiClient {
    private static final String RESOURCE = "/api/v3/veiculos";
    @Autowired
    private TestRestTemplate restTemplate;

    @NotNull
    public ResponseEntity<SuccessResponse> insert(@NotNull final VeiculoCadastroDto dto) {
        return insert(dto, SuccessResponse.class);
    }

    @NotNull
    public <T> ResponseEntity<T> insert(@NotNull final VeiculoCadastroDto dto,
                                        @NotNull final Class<T> responseType) {
        return restTemplate.postForEntity(URI.create(RESOURCE), dto, responseType);
    }
}
