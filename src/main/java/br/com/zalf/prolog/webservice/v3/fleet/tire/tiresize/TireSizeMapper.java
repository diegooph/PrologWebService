package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeCreateDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeStatusChangeDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeUpdateDto;
import br.com.zalf.prolog.webservice.v3.user.UserEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TireSizeMapper {
    @NotNull
    public TireSizeEntity toEntity(@NotNull final TireSizeCreateDto tireSizeCreateDto,
                                   @NotNull final Optional<ColaboradorAutenticado> colaboradorAutenticado,
                                   @NotNull final OrigemAcaoEnum registerOrigin) {
        return TireSizeEntity.builder()
                .withCompanyId(tireSizeCreateDto.getCompanyId())
                .withHeight(tireSizeCreateDto.getHeight())
                .withWidth(tireSizeCreateDto.getWidth())
                .withRim(tireSizeCreateDto.getRim())
                .withAdditionalId(tireSizeCreateDto.getAdditionalId())
                .withIsActive(true)
                .withCreatedAt(Now.getLocalDateTimeUtc())
                .withCreateByUser(createUserFrom(colaboradorAutenticado))
                .withLastUpdateAt(Now.getLocalDateTimeUtc())
                .withLastUpdateUser(createUserFrom(colaboradorAutenticado))
                .withRegisterOrigin(registerOrigin)
                .build();
    }

    @NotNull
    public List<TireSizeDto> toTireSizeDto(@NotNull final List<TireSizeEntity> tireSizeEntities) {
        return tireSizeEntities.stream().map(this::toTireSizeDto).collect(Collectors.toList());
    }

    @NotNull
    public TireSizeDto toTireSizeDto(@NotNull final TireSizeEntity tireSizeEntity) {
        final Optional<UserEntity> createdByUser = tireSizeEntity.getCreateByUser();
        final Optional<UserEntity> lastedUpdateUser = tireSizeEntity.getLastUpdateUser();
        return TireSizeDto.builder()
                .withId(tireSizeEntity.getId())
                .withHeight(tireSizeEntity.getHeight())
                .withWidth(tireSizeEntity.getWidth())
                .withRim(tireSizeEntity.getRim())
                .withAdditionalId(tireSizeEntity.getAdditionalId())
                .withIsActive(tireSizeEntity.isActive())
                .withCreatedAt(tireSizeEntity.getCreatedAtWithTimezone())
                .withCreatedByUserId(createdByUser.map(UserEntity::getId).orElse(null))
                .withCreatedByUserName(createdByUser.map(UserEntity::getName).orElse(null))
                .withLastUpdateAt(tireSizeEntity.getLastUpdatedAtWithTimezone().orElse(null))
                .withLastUpdateByUserId(lastedUpdateUser.map(UserEntity::getId).orElse(null))
                .withLastUpdateByUserName(lastedUpdateUser.map(UserEntity::getName).orElse(null))
                .withRegisterOrigin(tireSizeEntity.getRegisterOrigin())
                .build();
    }

    @NotNull
    public TireSizeEntity toTireSizeEntityUpdate(
            @NotNull final TireSizeUpdateDto tireSizeUpdateDto,
            @NotNull final TireSizeEntity tireSizeEntity,
            @NotNull final Optional<ColaboradorAutenticado> colaboradorAutenticado) {
        return tireSizeEntity.toBuilder()
                .withHeight(tireSizeUpdateDto.getHeight())
                .withWidth(tireSizeUpdateDto.getWidth())
                .withRim(tireSizeUpdateDto.getRim())
                .withAdditionalId(tireSizeUpdateDto.getAdditionalId())
                .withIsActive(tireSizeUpdateDto.isActive())
                .withLastUpdateUser(createUserFrom(colaboradorAutenticado))
                .withLastUpdateAt(LocalDateTime.now())
                .build();
    }

    @NotNull
    public TireSizeEntity toTireSizeEntityUpdate(
            @NotNull final TireSizeStatusChangeDto tireSizeStatusChangeDto,
            @NotNull final TireSizeEntity tireSizeEntity,
            @NotNull final Optional<ColaboradorAutenticado> colaboradorAutenticado) {
        return tireSizeEntity.toBuilder()
                .withIsActive(tireSizeStatusChangeDto.isActive())
                .withLastUpdateUser(createUserFrom(colaboradorAutenticado))
                .withLastUpdateAt(LocalDateTime.now())
                .build();
    }

    @Nullable
    private UserEntity createUserFrom(@NotNull final Optional<ColaboradorAutenticado> colaboradorAutenticado) {
        if (colaboradorAutenticado.isEmpty()) {
            return null;
        }
        return UserEntity.builder()
                .withId(colaboradorAutenticado.map(ColaboradorAutenticado::getCodigo).get())
                .build();
    }
}
