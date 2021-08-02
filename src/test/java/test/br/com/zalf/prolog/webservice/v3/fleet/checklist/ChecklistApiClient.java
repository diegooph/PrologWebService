package test.br.com.zalf.prolog.webservice.v3.fleet.checklist;

import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.v3.fleet.checklist.ChecklistResource;
import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    @Autowired
    @NotNull
    private TestRestTemplate restTemplate;

    @NotNull
    public ResponseEntity<List<ChecklistDto>> getChecklistsByFilter(@NotNull final List<Long> codUnidades,
                                                                    @NotNull final String startDate,
                                                                    @NotNull final String endDate,
                                                                    @Nullable final Long userId,
                                                                    @Nullable final Long vehicleId,
                                                                    @Nullable final Long vehicleTypeId,
                                                                    final boolean includeAnswers,
                                                                    final int limit,
                                                                    final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(ChecklistResource.RESOURCE_PATH)
                .queryParam("codUnidades", codUnidades.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .queryParam("dataInicial", startDate)
                .queryParam("dataFinal", endDate)
                .queryParam("codColaborador", userId)
                .queryParam("codVeiculo", vehicleId)
                .queryParam("codTipoVeiculo", vehicleTypeId)
                .queryParam("incluirRespostas", includeAnswers)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build();
        final RequestEntity<Void> requestEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<ChecklistDto>>() {});
    }

    public ResponseEntity<ClientSideErrorException> getChecklistsWithWrongBranchesId(
            @NotNull final List<String> wrongBranchesId,
            final String startDate,
            final String endDate,
            final int limit,
            final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(ChecklistResource.RESOURCE_PATH)
                .queryParam("codUnidades", String.join(",", wrongBranchesId))
                .queryParam("dataInicial", startDate)
                .queryParam("dataFinal", endDate)
                .queryParam("incluirRespostas", false)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build();
        final RequestEntity<Void> requestEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(requestEntity, new ParameterizedTypeReference<ClientSideErrorException>() {});
    }
}