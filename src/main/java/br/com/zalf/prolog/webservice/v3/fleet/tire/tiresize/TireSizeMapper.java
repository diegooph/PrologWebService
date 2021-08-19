package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireSizeEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeCreateDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeDto;
import br.com.zalf.prolog.webservice.v3.user.UserEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TireSizeMapper {
    @NotNull
    public TireSizeEntity toEntity(@NotNull final TireSizeCreateDto tireSizeCreateDto,
                                   @NotNull final ColaboradorAutenticado colaboradorAutenticado) {
        return TireSizeEntity.builder()
                .withCompanyId(tireSizeCreateDto.getCompanyId())
                .withHeight(tireSizeCreateDto.getHeight())
                .withWidth(tireSizeCreateDto.getWidth())
                .withRim(tireSizeCreateDto.getRim())
                .withAdditionalId(tireSizeCreateDto.getAdditionalId())
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
    public List<TireSizeDto> toTireSizeDto(@NotNull final List<TireSizeEntity> tireSizeEntities) {
        return tireSizeEntities.stream().map(this::toTireSizeDto).collect(Collectors.toList());
    }

    @NotNull
    public TireSizeDto toTireSizeDto(@NotNull final TireSizeEntity tireSizeEntity) {
        final Optional<UserEntity> createdByUser = tireSizeEntity.getCreateByUser();
        final Optional<UserEntity> lastedUpdateUser = tireSizeEntity.getLastedUpdateUser();
        return TireSizeDto.builder()
                .withId(tireSizeEntity.getId())
                .withHeight(tireSizeEntity.getHeight())
                .withWidth(tireSizeEntity.getWidth())
                .withRim(tireSizeEntity.getRim())
                .withAdditionalId(tireSizeEntity.getAdditionalId())
                .withIsActive(tireSizeEntity.isActive())
                .withCreatedAt(tireSizeEntity.getCreatedAt())
                .withCreatedAtUserId(
                        tireSizeEntity.getCreateByUser()
                                .map(UserEntity::getId)
                                .orElse(null))
                .withCreatedAtUserName(
                        tireSizeEntity.getCreateByUser()
                                .map(UserEntity::getName)
                                .orElse(null))
                .withLastedUpdateAt(tireSizeEntity.getLastedUpdateAt())
                .withLastedUpdateUserId(
                        tireSizeEntity.getLastedUpdateUser()
                                .map(UserEntity::getId)
                                .orElse(null))
                .withLastedUpdateUserName(
                        tireSizeEntity.getLastedUpdateUser()
                                .map(UserEntity::getName)
                                .orElse(null))
                .withRegisterOrigin(tireSizeEntity.getRegisterOrigin())
                .build();
    }
}
