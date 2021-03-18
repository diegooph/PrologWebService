package br.com.zalf.prolog.webservice.frota.pneu.v3.mapper;

import br.com.zalf.prolog.webservice.database._model.DadosDelecao;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.dto.PneuCadastro;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * Created on 2021-03-15
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class PneuCadastroMapper implements PneuMapper<PneuEntity, PneuCadastro> {

    @Override
    @NotNull
    public PneuCadastro toDto(@NotNull final PneuEntity entity) {
        throw new NotImplementedException("Não há necessidade de implementação deste metodo.");
    }

    @Override
    @NotNull
    public PneuEntity toEntity(@NotNull final PneuCadastro dto) {
        return PneuEntity.builder()
                .dadosCadastro(getDadosCadastro(dto))
                .pressao(getPressao(dto))
                .codDimensao(dto.getCodDimensao())
                .codCliente(dto.getCodCliente())
                .codUnidade(dto.getCodUnidade())
                .codEmpresa(dto.getCodEmpresa())
                .codEmpresa(dto.getCodEmpresa())
                .dadosDelecao(getDadosDelecao())
                .status(PneuEntity.Status.ESTOQUE)
                .codModelo(dto.getCodModeloBanda())
                .vidaAtual(BigInteger.valueOf(dto.getVidaAtual()))
                .vidaTotal(BigInteger.valueOf(dto.getVidaTotal()))
                .valor(BigDecimal.valueOf(dto.getCustoAquisicao()))
                .dot(dto.getDot())
                .usado(dto.isPneuUsado())
                .build();

    }

    @NotNull
    private PneuEntity.Pressao getPressao(@NotNull final PneuCadastro dto) {
        return PneuEntity.Pressao.builder()
                .recomendada(BigDecimal.valueOf(dto.getPressaoRecomendada()))
                .build();
    }

    @NotNull
    private PneuEntity.DadosCadastro getDadosCadastro(@NotNull final PneuCadastro dto) {
        return PneuEntity.DadosCadastro.builder()
                .codUnidade(dto.getCodUnidade())
                .dataInclusao(LocalDateTime.now())
                .build();
    }

    private DadosDelecao getDadosDelecao() {
        return DadosDelecao.builder()
                .build();
    }

}
