package br.com.zalf.prolog.webservice.v3.general.branch._model;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2021-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
public final class UnidadeMapper {
    @NotNull
    public List<UnidadeVisualizacaoListagemDto> toDto(@NotNull final List<UnidadeEntity> unidades) {
        return unidades
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @NotNull
    public UnidadeVisualizacaoListagemDto toDto(@NotNull final UnidadeEntity unidade) {
        return new UnidadeVisualizacaoListagemDto(
                unidade.getId(),
                unidade.getName(),
                unidade.getTotalUsers(),
                unidade.getTimezone(),
                unidade.getCreatedAt(),
                unidade.isActive(),
                unidade.getAdditionalId(),
                unidade.getBranchLatitude(),
                unidade.getBranchLongitude(),
                unidade.getGroup().getId(),
                unidade.getGroup().getName());
    }

    @NotNull
    public UnidadeEntity toEntity(@NotNull final UnidadeEdicaoDto dto) {
        return UnidadeEntity.builder()
                .id(dto.getCodUnidade())
                .name(dto.getNomeUnidade())
                .additionalId(dto.getCodAuxiliarUnidade())
                .branchLatitude(dto.getLatitudeUnidade())
                .branchLongitude(dto.getLongitudeUnidade())
                .build();
    }
}
