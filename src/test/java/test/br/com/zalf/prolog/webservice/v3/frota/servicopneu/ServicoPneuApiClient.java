package test.br.com.zalf.prolog.webservice.v3.frota.servicopneu;

import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceDto;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.ServicoPneuStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Created on 2021-05-28
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@TestComponent
public class ServicoPneuApiClient {
    private static final String RESOURCE = "/api/v3/servicos-pneu";
    @Autowired
    private TestRestTemplate restTemplate;

    public ResponseEntity<List<TireMaintenanceDto>> getServicosByFiltros(final List<Long> codUnidades,
                                                                         final ServicoPneuStatus status,
                                                                         final int limit,
                                                                         final int offset) {
        return getServicosByFiltros(codUnidades, status, null, null, limit, offset);
    }

    public ResponseEntity<List<TireMaintenanceDto>> getServicosByFiltros(final List<Long> codUnidades,
                                                                         final int limit,
                                                                         final int offset) {
        return getServicosByFiltros(codUnidades, null, limit, offset);
    }

    public ResponseEntity<List<TireMaintenanceDto>> getServicosByFiltros(final List<Long> codUnidades,
                                                                         final ServicoPneuStatus status,
                                                                         final Long codVeiculo,
                                                                         final Long codPneu,
                                                                         final int limit,
                                                                         final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(RESOURCE)
                .queryParam("codUnidades", codUnidades)
                .queryParam("statusServicoPneu", status)
                .queryParam("codVeiculo", codVeiculo)
                .queryParam("codPneu", codPneu)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build();
        final RequestEntity<Void> requestEntity = RequestEntity
                .get(components.toUri())
                .build();
        return restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<TireMaintenanceDto>>() {});
    }
}
