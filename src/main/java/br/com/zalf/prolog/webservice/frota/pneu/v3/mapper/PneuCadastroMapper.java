package br.com.zalf.prolog.webservice.frota.pneu.v3.mapper;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.dto.PneuCadastroDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Created on 2021-03-15
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class PneuCadastroMapper {
    @NotNull
    public PneuEntity toEntity(@NotNull final PneuCadastroDto dto) {
        return PneuEntity.builder()
                .codEmpresa(dto.getCodEmpresaAlocado())
                .codUnidade(dto.getCodUnidadeAlocado())
                .codigoCliente(dto.getCodigoCliente())
                .codModelo(dto.getCodModeloPneu())
                .codDimensao(dto.getCodDimensaoPneu())
                .pressaoRecomendada(dto.getPressaoRecomendada())
                .status(StatusPneu.ESTOQUE)
                .vidaAtual(dto.getVidaAtualPneu())
                .vidaTotal(dto.getVidaTotalPneu())
                .codModeloBanda(dto.getCodModeloBanda())
                .dot(dto.getDotPneu())
                .valor(dto.getValorPneu())
                .dataHoraCadastro(Now.getOffsetDateTimeUtc())
                .pneuNovoNuncaRodado(dto.getPneuNovoNuncaRodado())
                .codUnidadeCadastro(dto.getCodUnidadeAlocado())
                .build();
    }
}
