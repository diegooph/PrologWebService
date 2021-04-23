package test.br.com.zalf.prolog.webservice.pilares.frota.checklist;

import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistListagemDto;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2021-04-23
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@TestComponent
public class ChecklistApiClient {
    @NotNull
    private static final String RESOURCE = "/v3/checklists";
    @Autowired
    @NotNull
    private TestRestTemplate restTemplate;

    @NotNull
    public ResponseEntity<List<ChecklistListagemDto>> getChecklistsByFitlro(@NotNull final List<Long> codUnidades,
                                                                            @NotNull final String dataInicial,
                                                                            @NotNull final String dataFinal) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(RESOURCE)
                .queryParam("codUnidades", codUnidades.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .queryParam("dataInicial", dataInicial)
                .queryParam("dataFinal", dataFinal)
                .queryParam("incluirRespostas", false)
                .queryParam("limit", 2)
                .queryParam("offset", 0)
                .build();
        final RequestEntity<Void> requestEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        final ResponseEntity<ClientSideErrorException> exchange =
                restTemplate.exchange(requestEntity,
                                      new ParameterizedTypeReference<ClientSideErrorException>() {});
        return null;
    }
}