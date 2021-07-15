package test.br.com.zalf.prolog.webservice.v3.geral.unidade;

import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchUpdateDto;
import br.com.zalf.prolog.webservice.v3.general.branch._model.UnidadeEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-03
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class UnidadeEdicaoDtoFactory {

    @NotNull
    public static BranchUpdateDto createValidUnidadeEdicaoDtoToUpdate(@NotNull final UnidadeEntity entity) {
        return BranchUpdateDto.builder()
                .branchId(entity.getId())
                .branchName(entity.getName())
                .branchAdditionalId(entity.getAdditionalId())
                .branchLongitude(entity.getBranchLongitude())
                .branchLatitude(entity.getBranchLatitude())
                .build();
    }

    @NotNull
    public static BranchUpdateDto createUnidadeEdicaoDtoWithInvalidCodUnidade(@NotNull final UnidadeEntity entity) {
        return BranchUpdateDto.builder()
                .branchId(-1L)
                .branchName(entity.getName())
                .branchAdditionalId(entity.getAdditionalId())
                .branchLongitude(entity.getBranchLongitude())
                .branchLatitude(entity.getBranchLatitude())
                .build();
    }
}
