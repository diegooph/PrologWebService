package test.br.com.zalf.prolog.webservice.pilares.geral.unidade;

import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEdicaoDto;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEntity;

/**
 * Created on 2021-03-03
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class UnidadeEdicaoDtoFactory {

    public static UnidadeEdicaoDto createValidUnidadeEdicaoDtoToUpdate(final UnidadeEntity entity) {
        return UnidadeEdicaoDto.builder()
                .codUnidade(entity.getCodigo())
                .nomeUnidade(entity.getNome())
                .codAuxiliarUnidade(entity.getCodAuxiliar())
                .longitudeUnidade(entity.getLongitudeUnidade())
                .latitudeUnidade(entity.getLatitudeUnidade())
                .build();
    }

}
