package test.br.com.zalf.prolog.webservice.v3.geral.unidade;

import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchUpdateDto;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-03
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class UnidadeEdicaoDtoFactory {

    @NotNull
    public static BranchUpdateDto createValidUnidadeEdicaoDtoToUpdate(@NotNull final BranchEntity entity) {
        return BranchUpdateDto.builder()
                .codUnidade(entity.getId())
                .nomeUnidade(entity.getName())
                .codAuxiliarUnidade(entity.getAdditionalId())
                .longitudeUnidade(entity.getBranchLongitude())
                .latitudeUnidade(entity.getBranchLatitude())
                .build();
    }

    @NotNull
    public static BranchUpdateDto createUnidadeEdicaoDtoWithInvalidCodUnidade(@NotNull final BranchEntity entity) {
        return BranchUpdateDto.builder()
                .codUnidade(-1L)
                .nomeUnidade(entity.getName())
                .codAuxiliarUnidade(entity.getAdditionalId())
                .longitudeUnidade(entity.getBranchLongitude())
                .latitudeUnidade(entity.getBranchLatitude())
                .build();
    }
}
