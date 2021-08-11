package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireSizeEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TireSizeMapper {
    public TireSizeEntity toEntity(@NotNull final TireSizeCreation tireSizeCreation,
                                   @NotNull final ColaboradorAutenticado colaboradorAutenticado) {

        return TireSizeEntity.builder()
                .withCompanyId(tireSizeCreation.getCompanyId())
                .withHeight(tireSizeCreation.getHeight())
                .withWidth(tireSizeCreation.getWidth())
                .withRim(tireSizeCreation.getRim())
                .withAdditionalId(tireSizeCreation.getAdditionalId())
                .withIsActive(true)
                .withCreatedAt(LocalDateTime.now())
                .withCreatedByUserId(colaboradorAutenticado.getCodigo())
                .withLastedUpdateAt(LocalDateTime.now())
                .withLastedUpdateUserId(colaboradorAutenticado.getCodigo())
                .withRegisterOrigin("WS")
                .build();
    }
}
