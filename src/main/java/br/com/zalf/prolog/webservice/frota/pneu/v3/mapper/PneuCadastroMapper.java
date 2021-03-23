package br.com.zalf.prolog.webservice.frota.pneu.v3.mapper;

import br.com.zalf.prolog.webservice.database._model.DadosDelecao;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.dto.PneuCadastroDto;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created on 2021-03-15
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class PneuCadastroMapper implements PneuMapper<PneuEntity, PneuCadastroDto> {

    @Override
    @NotNull
    public PneuCadastroDto toDto(@NotNull final PneuEntity entity) {
        throw new NotImplementedException("Não há necessidade de implementação deste metodo.");
    }

    @Override
    @NotNull
    public PneuEntity toEntity(@NotNull final PneuCadastroDto dto) {
        return PneuEntity.builder()
                .dadosCadastro(getDadosCadastro(dto))
                .pressao(getPressao(dto))
                .codDimensao(dto.getCodDimensaoPneu())
                .codCliente(dto.getCodigoCliente())
                .codUnidade(dto.getCodUnidadePneu())
                .codEmpresa(dto.getCodEmpresa())
                .dadosDelecao(DadosDelecao.createDefaultDadosDelecao())
                .status(StatusPneu.ESTOQUE)
                .codModelo(dto.getCodModeloPneu())
                .codModeloBanda(dto.getCodModeloBanda())
                .vidaAtual(dto.getVidaAtualPneu())
                .vidaTotal(dto.getVidaTotalPneu())
                .valor(BigDecimal.valueOf(dto.getValorPneu()))
                .dot(dto.getDotPneu())
                .usado(dto.getPneuNovoNuncaUsado())
                .build();

    }

    @NotNull
    private PneuEntity.Pressao getPressao(@NotNull final PneuCadastroDto dto) {
        return PneuEntity.Pressao.builder()
                .recomendada(BigDecimal.valueOf(dto.getPressaoRecomendada()))
                .build();
    }

    @NotNull
    private PneuEntity.DadosCadastro getDadosCadastro(@NotNull final PneuCadastroDto dto) {
        return PneuEntity.DadosCadastro.builder()
                .codUnidade(dto.getCodUnidadePneu())
                .dataInclusao(LocalDateTime.now())
                .build();
    }
}
