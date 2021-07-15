package test.br.com.zalf.prolog.webservice.v3.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoCadastroDto;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoListagemDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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

    public <T> ResponseEntity<List<VeiculoListagemDto>> getVeiculoListagem(
            @NotNull final List<Long> codUnidades,
            final boolean statusAtivo,
            final int limit,
            final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(RESOURCE)
                .queryParam("codUnidades", codUnidades.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .queryParam("statusAtivo", statusAtivo)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build();
        final RequestEntity<Void> requestEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(requestEntity,
                                     new ParameterizedTypeReference<List<VeiculoListagemDto>>() {});
    }

    @NotNull
    public ResponseEntity<ClientSideErrorException> getVeiculoListagemBadRequest() {
        final UriComponents components = UriComponentsBuilder
                .fromPath(RESOURCE)
                .queryParam("codUnidades", "a")
                .queryParam("statusAtivo", false)
                .queryParam("limit", 1000)
                .queryParam("offset", 0)
                .build();
        final RequestEntity<Void> requestEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(requestEntity, new ParameterizedTypeReference<ClientSideErrorException>() {});
    }
}
