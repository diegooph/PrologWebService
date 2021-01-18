package br.com.zalf.prolog.webservice.mappers.geral.unidade;

import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEdicaoDto;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEntity;
import br.com.zalf.prolog.webservice.mappers.Mapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Created on 2020-12-02
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class UnidadeEdicaoMapper implements Mapper<UnidadeEdicaoDto, UnidadeEntity> {

    @Override
    @NotNull
    public UnidadeEdicaoDto toDto(@NotNull final UnidadeEntity entity) {
        return UnidadeEdicaoDto.builder()
                .codUnidade(entity.getCodigo())
                .nomeUnidade(entity.getNome())
                .codAuxiliarUnidade(entity.getCodAuxiliar())
                .latitudeUnidade(entity.getLatitudeUnidade())
                .longitudeUnidade(entity.getLongitudeUnidade())
                .build();
    }

    @Override
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