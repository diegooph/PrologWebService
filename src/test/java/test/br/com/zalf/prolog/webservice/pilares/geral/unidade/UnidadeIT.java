package test.br.com.zalf.prolog.webservice.pilares.geral.unidade;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.errorhandling.sql.NotFoundException;
import br.com.zalf.prolog.webservice.geral.unidade.UnidadeDao;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEdicaoDto;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEntity;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacaoListagemDto;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UnidadeIT extends IntegrationTest {

    private static final String RESOURCE = "unidades/";
    private static final Long TEST_UNIDADE_ID = 5L;

    @Autowired
    private UnidadeDao dao;

    private <T> void assertBaseValidations(final ResponseEntity<T> responseEntity) {
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Dado código da unidade, retorne a UnidadeVisualizacaoListagem correspondente.")
    void givenCodUnidadeToRequest_ThenReturnUnidadeVisualizacaoListagemAndStatusOK() {

        final RequestEntity<Void> requestEntity = RequestEntity
                .get(URI.create(createPathWithPort(RESOURCE + TEST_UNIDADE_ID)))
                .accept(MediaType.APPLICATION_JSON)
                .build();

        final ResponseEntity<UnidadeVisualizacaoListagemDto> response = getTestRestTemplate()
                .exchange(requestEntity, ParameterizedTypeReference.forType(UnidadeVisualizacaoListagemDto.class));

        assertBaseValidations(response);
        assertThat(response.getBody().getCodUnidade())
                .isEqualTo(TEST_UNIDADE_ID);
    }

    @Test
    @DisplayName("Dado código da empresa e códigos de regionais, retorne uma lista de UnidadeVisualizacaoListagem.")
    void givenCodUnidadeAndCodRegionais_ThenReturnListUnidadeVisualizacaoListagemAndStatusOk() {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("codEmpresa", "3");
        params.add("codsRegionais", "1");

        final UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(createPathWithPort(RESOURCE))
                .queryParams(params);

        final RequestEntity<Void> requestEntity = RequestEntity
                .get(URI.create(builder.toUriString()))
                .accept(MediaType.APPLICATION_JSON)
                .build();

        final ResponseEntity<List<UnidadeVisualizacaoListagemDto>> response = getTestRestTemplate()
                .exchange(requestEntity, new ParameterizedTypeReference<List<UnidadeVisualizacaoListagemDto>>() {});

        assertBaseValidations(response);
        response.getBody().stream()
                .map(Assertions::assertThat)
                .forEach(AbstractAssert::isNotNull);
    }

    @Nested
    class PersistenceTests {

        private UnidadeEntity baseEntity;

        @BeforeEach
        void setUp() {
            baseEntity = dao.findById(TEST_UNIDADE_ID)
                    .orElseThrow(NotFoundException::new);
        }

        @Test
        @DisplayName("Dado UnidadeEdicaoDto, retorne SuccessResponse")
        void givenUnidadeEdicaoDto_ThenReturnSuccessResponseAndStatusOk() {
            final UnidadeEdicaoDto dtoToUpdate = UnidadeEdicaoDto.builder()
                    .codUnidade(baseEntity.getCodigo())
                    .nomeUnidade(baseEntity.getNome() + "Test")
                    .codAuxiliarUnidade(baseEntity.getCodAuxiliar())
                    .latitudeUnidade(baseEntity.getLatitudeUnidade())
                    .longitudeUnidade(baseEntity.getLongitudeUnidade())
                    .build();

            final RequestEntity<UnidadeEdicaoDto> requestEntity = RequestEntity
                    .put(URI.create(createPathWithPort(RESOURCE)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dtoToUpdate);

            final ResponseEntity<SuccessResponse> response = getTestRestTemplate()
                    .exchange(requestEntity, SuccessResponse.class);

            assertBaseValidations(response);
            assertThat(response.getBody().getUniqueItemId()).isEqualTo(dtoToUpdate.getCodUnidade());
        }
    }
}
