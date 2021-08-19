package test.br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.TireSizeResource;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeCreateDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeDto;
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

@TestComponent
public class TireSizeApiClient {
    @Autowired
    @NotNull
    private TestRestTemplate restTemplate;

    @NotNull
    public ResponseEntity<SuccessResponse> insert(@NotNull final TireSizeCreateDto dto) {
        return restTemplate.postForEntity(URI.create(TireSizeResource.RESOURCE_PATH), dto, SuccessResponse.class);
    }

    @NotNull
    public ResponseEntity<List<TireSizeDto>> getAllTireSizes(final long companyId,
                                                             final boolean tireSizeStatus) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(TireSizeResource.RESOURCE_PATH)
                .queryParam("companyId", companyId)
                .queryParam("status", tireSizeStatus)
                .build();
        final RequestEntity<Void> reqEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(reqEntity, new ParameterizedTypeReference<List<TireSizeDto>>() {
        });
    }

    @NotNull
    public ResponseEntity<ClientSideErrorException> getAllTireSizesWithWrongCompanyId(final long companyId,
                                                                                      final boolean tireSizeStatus) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(TireSizeResource.RESOURCE_PATH)
                .queryParam("companyId", companyId)
                .queryParam("status", tireSizeStatus)
                .build();
        final RequestEntity<Void> reqEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(reqEntity, new ParameterizedTypeReference<ClientSideErrorException>() {
        });
    }
}
