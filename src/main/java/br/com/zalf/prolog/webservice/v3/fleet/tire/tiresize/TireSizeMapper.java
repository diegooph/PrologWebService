package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireSizeEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeCreation;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeListing;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeUpdated;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeVisualization;
import br.com.zalf.prolog.webservice.v3.user.UserEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TireSizeMapper {
    @NotNull
    public TireSizeEntity toEntity(@NotNull final TireSizeCreation tireSizeCreation,
                                   @NotNull final ColaboradorAutenticado colaboradorAutenticado) {
        return TireSizeEntity.builder()
                .withCompanyId(tireSizeCreation.getCompanyId())
                .withHeight(tireSizeCreation.getHeight())
                .withWidth(tireSizeCreation.getWidth())
                .withRim(tireSizeCreation.getRim())
                .withAdditionalId(tireSizeCreation.getAdditionalId())
                .withActive(true)
                .withCreatedAt(LocalDateTime.now())
                .withCreateByUser(
                        UserEntity.builder()
                                .withId(colaboradorAutenticado.getCodigo())
                                .build())
                .withLastedUpdateAt(LocalDateTime.now())
                .withLastedUpdateUser(
                        UserEntity.builder()
                                .withId(colaboradorAutenticado.getCodigo())
                                .build())
                .withRegisterOrigin("WS")
                .build();
    }

    @NotNull
    public List<TireSizeListing> toTireSizeListing(@NotNull final List<TireSizeEntity> tireSizesEntities) {
        return tireSizesEntities.stream().map(this::toTireSizeListing).collect(Collectors.toList());
    }

    @NotNull
    public TireSizeListing toTireSizeListing(@NotNull final TireSizeEntity tireSizeEntity) {
        return TireSizeListing.builder()
                .withId(tireSizeEntity.getId())
                .withCompanyId(tireSizeEntity.getCompanyId())
                .withHeight(tireSizeEntity.getHeight())
                .withWidth(tireSizeEntity.getWidth())
                .withAdditionalId(tireSizeEntity.getAdditionalId())
                .withRim(tireSizeEntity.getRim())
                .withIsActive(tireSizeEntity.isActive())
                .build();
    }

    @NotNull
    public TireSizeUpdated toTireSizeUpdated(@NotNull final TireSizeEntity tireSizeEntity) {
        return TireSizeUpdated.builder()
                .withCompanyId(tireSizeEntity.getCompanyId())
                .withTireSizeId(tireSizeEntity.getId())
                .withTireSizeHeight(tireSizeEntity.getHeight())
                .withTireSizeWidth(tireSizeEntity.getWidth())
                .withTireSizeRim(tireSizeEntity.getRim())
                .withAdditionalId(tireSizeEntity.getAdditionalId())
                .withActive(tireSizeEntity.isActive())
                .build();
    }

    @NotNull
    public TireSizeVisualization toTireSizeVisualization(@NotNull final TireSizeEntity tireSizeEntity) {
        return TireSizeVisualization.builder()
                .withId(tireSizeEntity.getId())
                .withHeight(tireSizeEntity.getHeight())
                .withWidth(tireSizeEntity.getWidth())
                .withRim(tireSizeEntity.getRim())
                .withAdditionalId(tireSizeEntity.getAdditionalId())
                .withActive(tireSizeEntity.isActive())
                .withCreatedAt(tireSizeEntity.getCreatedAt())
                .withCreatedAtUserId(tireSizeEntity.getCreateByUser().getId())
                .withCreatedAtUserName(tireSizeEntity.getCreateByUser().getName())
                .withLastedUpdateAt(tireSizeEntity.getLastedUpdateAt())
                .withLastedUpdateUserId(tireSizeEntity.getLastedUpdateUser().getId())
                .withLastedUpdateUserName(tireSizeEntity.getLastedUpdateUser().getName())
                .withRegisterOrigin(tireSizeEntity.getRegisterOrigin())
                .build();
    }
}
