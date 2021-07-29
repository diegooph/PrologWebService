package br.com.zalf.prolog.webservice.v3.fleet.processeskm._model;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
public final class UpdateProcessKmMapper {

    @NotNull
    public UpdateProcessKm toUpdateProcessKm(@NotNull final UpdateProcessKmDto dto,
                                             @Nullable final Long userIdUpdate) {
        return UpdateProcessKm
                .builder()
                .withCompanyId(dto.getCompanyId())
                .withVehicleId(dto.getVehicleId())
                .withProcessId(dto.getProcessId())
                .withProcessType(dto.getProcessType())
                .withUserIdUpdate(userIdUpdate)
                .withNewKm(dto.getNewKm())
                .build();
    }

    @NotNull
    public UpdateProcessKmEntity toEntity(@NotNull final UpdateProcessKm process,
                                          final long oldKm) {
        return UpdateProcessKmEntity
                .builder()
                .withKmUpdatedAt(Now.getOffsetDateTimeUtc())
                .withUserIdUpdate(process.getUserIdUpdate())
                .withUpdateSource(OrigemAcaoEnum.PROLOG_WEB)
                .withProcessIdUpdated(process.getProcessId())
                .withProcessTypeUpdated(process.getProcessType())
                .withOldKm(oldKm)
                .withNewKm(process.getNewKm())
                .build();
    }
}
