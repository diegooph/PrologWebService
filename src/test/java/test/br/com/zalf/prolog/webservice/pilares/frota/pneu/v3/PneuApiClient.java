package test.br.com.zalf.prolog.webservice.pilares.frota.pneu.v3;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.dto.PneuCadastroDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.net.URI;

/**
 * Created on 2021-03-16
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@TestComponent
public class PneuApiClient {
    private static final String RESOURCE = "/v3/pneus";

    @Autowired
    @NotNull
    private TestRestTemplate restTemplate;

    public ResponseEntity<Void> insert(final PneuCadastroDto dto) {
        return insert(dto, Void.class);
    }

    public <T> ResponseEntity<T> insert(final PneuCadastroDto dto, final Class<T> responseType) {
        return restTemplate.postForEntity(URI.create(RESOURCE), dto, responseType);
    }
}
