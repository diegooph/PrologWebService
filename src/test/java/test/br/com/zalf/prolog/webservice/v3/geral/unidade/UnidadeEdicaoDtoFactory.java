package test.br.com.zalf.prolog.webservice.v3.geral.unidade;

import br.com.zalf.prolog.webservice.v3.general.branch._model.UnidadeEdicaoDto;
import br.com.zalf.prolog.webservice.v3.general.branch._model.UnidadeEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-03
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class UnidadeEdicaoDtoFactory {

    @NotNull
    public static UnidadeEdicaoDto createValidUnidadeEdicaoDtoToUpdate(@NotNull final UnidadeEntity entity) {
        return UnidadeEdicaoDto.builder()
                .codUnidade(entity.getCodigo())
                .nomeUnidade(entity.getNome())
                .codAuxiliarUnidade(entity.getCodAuxiliar())
                .longitudeUnidade(entity.getLongitudeUnidade())
                .latitudeUnidade(entity.getLatitudeUnidade())
                .build();
    }

    @NotNull
    public static UnidadeEdicaoDto createUnidadeEdicaoDtoWithInvalidCodUnidade(@NotNull final UnidadeEntity entity) {
        return UnidadeEdicaoDto.builder()
                .codUnidade(-1L)
                .nomeUnidade(entity.getNome())
                .codAuxiliarUnidade(entity.getCodAuxiliar())
                .longitudeUnidade(entity.getLongitudeUnidade())
                .latitudeUnidade(entity.getLatitudeUnidade())
                .build();
    }
}
