package br.com.zalf.prolog.webservice.v3.geral.unidade._model;

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
    public List<UnidadeVisualizacaoListagemDto> toDto(@NotNull final List<UnidadeProjection> unidades) {
        return unidades
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @NotNull
    public UnidadeVisualizacaoListagemDto toDto(@NotNull final UnidadeProjection unidade) {
        return new UnidadeVisualizacaoListagemDto(
                unidade.getCodigoUnidade(),
                unidade.getNomeUnidade(),
                unidade.getTotalColaboradores(),
                unidade.getTimezoneUnidade(),
                unidade.getDataHoraCadastroUnidade(),
                unidade.isUnidadeAtiva(),
                unidade.getCodAuxiliar(),
                unidade.getLatitudeUnidade(),
                unidade.getLongitudeUnidade(),
                unidade.getCodRegional(),
                unidade.getNomeRegional());
    }

    @NotNull
    public UnidadeEntity toEntity(@NotNull final UnidadeEdicaoDto dto) {
        return UnidadeEntity.builder()
                .codigo(dto.getCodUnidade())
                .nome(dto.getNomeUnidade())
                .codAuxiliar(dto.getCodAuxiliarUnidade())
                .latitudeUnidade(dto.getLatitudeUnidade())
                .longitudeUnidade(dto.getLongitudeUnidade())
                .build();
    }
}
