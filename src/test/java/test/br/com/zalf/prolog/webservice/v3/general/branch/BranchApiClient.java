package test.br.com.zalf.prolog.webservice.v3.general.branch;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.v3.general.branch.BranchResource;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchDto;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchUpdateDto;
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

/**
 * Created on 2021-03-03
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@TestComponent
public class BranchApiClient {
    @Autowired
    @NotNull
    private TestRestTemplate restTemplate;

    @NotNull
    public ResponseEntity<BranchDto> getBranchById(@NotNull final Long branchId) {
        final RequestEntity<Void> requestEntity = RequestEntity
                .get(URI.create(BranchResource.RESOURCE_PATH + "/" + branchId))
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(requestEntity, ParameterizedTypeReference.forType(BranchDto.class));
    }

    @NotNull
    public ResponseEntity<List<BranchDto>> getBranches(@NotNull final Long companyId,
                                                       @NotNull final List<Long> groupsId) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(BranchResource.RESOURCE_PATH)
                .queryParam("codEmpresa", companyId)
                .queryParam("codsRegionais", groupsId)
                .build();
        final RequestEntity<Void> requestEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<BranchDto>>() {});
    }

    @NotNull
    public ResponseEntity<SuccessResponse> updateBranch(@NotNull final BranchUpdateDto dto) {
        return updateBranch(dto, SuccessResponse.class);
    }

    @NotNull
    public <T> ResponseEntity<T> updateBranch(@NotNull final BranchUpdateDto dto,
                                              @NotNull final Class<T> responseType) {
        final RequestEntity<BranchUpdateDto> requestEntity = RequestEntity
                .put(URI.create(BranchResource.RESOURCE_PATH))
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
        return restTemplate.exchange(requestEntity, responseType);
    }
}