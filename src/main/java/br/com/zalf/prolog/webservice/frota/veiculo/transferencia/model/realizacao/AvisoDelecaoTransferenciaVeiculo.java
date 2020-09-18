package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-09-16
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AvisoDelecaoTransferenciaVeiculo {
    @NotNull
    private final StatusDelecaoTransferenciaVeiculo statusDelecaoTransferenciaVeiculo;
    @NotNull
    private final String mensagemExibicao;

    public AvisoDelecaoTransferenciaVeiculo(
            @NotNull final StatusDelecaoTransferenciaVeiculo statusDelecaoTransferenciaVeiculo,
            @NotNull final String mensagemExibicao) {
        this.statusDelecaoTransferenciaVeiculo = statusDelecaoTransferenciaVeiculo;
        this.mensagemExibicao = mensagemExibicao;
    }

    @NotNull
    public StatusDelecaoTransferenciaVeiculo getStatusDelecaoTransferenciaVeiculo() {
        return statusDelecaoTransferenciaVeiculo;
    }

    @NotNull
    public String getMensagemExibicao() {
        return mensagemExibicao;
    }

    public boolean deveDeletarItensOrdemServicoChecklist() {
        return ((!statusDelecaoTransferenciaVeiculo
                .equals(StatusDelecaoTransferenciaVeiculo.DELECAO_OS_CHECK_BLOQUEADA)) &&
                (!statusDelecaoTransferenciaVeiculo
                        .equals(StatusDelecaoTransferenciaVeiculo.DELECAO_OS_CHECK_E_SERVICOS_PNEUS_BLOQUEADA)));
    }

    public boolean deveDeletarServicosPneus() {
        return ((!statusDelecaoTransferenciaVeiculo
                .equals(StatusDelecaoTransferenciaVeiculo.DELECAO_SERVICOS_PNEUS_BLOQUEADA)) &&
                (!statusDelecaoTransferenciaVeiculo
                        .equals(StatusDelecaoTransferenciaVeiculo.DELECAO_OS_CHECK_E_SERVICOS_PNEUS_BLOQUEADA)));
    }
}