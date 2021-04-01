package br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model;

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
public final class AlteracaoKmProcessoMapper {

    @NotNull
    public AlteracaoKmProcesso toAlteracaoKmProcesso(@NotNull final AlteracaoKmProcessoDto dto,
                                                     @Nullable final Long codColaboradorAlteracao) {
        return AlteracaoKmProcesso
                .builder()
                .withCodEmpresa(dto.getCodEmpresa())
                .withCodVeiculo(dto.getCodVeiculo())
                .withCodProcesso(dto.getCodProcesso())
                .withTipoProcesso(dto.getTipoProcesso())
                .withCodColaboradorAlteracao(codColaboradorAlteracao)
                .withNovoKm(dto.getNovoKm())
                .build();
    }

    @NotNull
    public AlteracaoKmProcessoEntity toEntity(@NotNull final AlteracaoKmProcesso processo,
                                              final long kmAntigo) {
        return AlteracaoKmProcessoEntity
                .builder()
                .withDataHoraAlteracaoKm(Now.getOffsetDateTimeUtc())
                .withCodColaboradorAlteracaoKm(processo.getCodColaboradorAlteracao())
                .withOrigemAlteracao(OrigemAcaoEnum.PROLOG_WEB)
                .withCodProcessoAlterado(processo.getCodProcesso())
                .withTipoProcessoAlterado(processo.getTipoProcesso())
                .withKmAntigo(kmAntigo)
                .withKmNovo(processo.getNovoKm())
                .build();
    }
}
