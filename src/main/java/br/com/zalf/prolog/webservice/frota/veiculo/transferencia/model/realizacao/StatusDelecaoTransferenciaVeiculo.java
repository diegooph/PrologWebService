package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao;

/**
 * Created on 2019-09-16
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum StatusDelecaoTransferenciaVeiculo {
    NENHUMA_DELECAO_BLOQUEADA,
    DELECAO_OS_CHECK_BLOQUEADA,
    DELECAO_SERVICOS_PNEUS_BLOQUEADA,
    DELECAO_OS_CHECK_E_SERVICOS_PNEUS_BLOQUEADA;

    public static StatusDelecaoTransferenciaVeiculo create(final boolean delecaoOsCheckBloqueada,
                                                           final boolean delecaoServicosPneusBloqueada) {
        if ((delecaoOsCheckBloqueada) && (delecaoServicosPneusBloqueada)) {
            return DELECAO_OS_CHECK_E_SERVICOS_PNEUS_BLOQUEADA;
        } else if (delecaoOsCheckBloqueada) {
            return DELECAO_OS_CHECK_BLOQUEADA;
        } else if (delecaoServicosPneusBloqueada) {
            return DELECAO_SERVICOS_PNEUS_BLOQUEADA;
        } else {
            return NENHUMA_DELECAO_BLOQUEADA;
        }
    }
}