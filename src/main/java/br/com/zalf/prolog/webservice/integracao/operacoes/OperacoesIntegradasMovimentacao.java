package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.customfields.CampoPersonalizadoDao;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoParaRealizacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created on 14/10/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface OperacoesIntegradasMovimentacao {
    @NotNull
    Long insert(@NotNull final ServicoDao servicoDao,
                @NotNull final CampoPersonalizadoDao campoPersonalizadoDao,
                @NotNull final ProcessoMovimentacao processoMovimentacao,
                @NotNull final OffsetDateTime dataHoraMovimentacao,
                final boolean fecharServicosAutomaticamente) throws Throwable;

    @NotNull
    List<CampoPersonalizadoParaRealizacao> getCamposParaRealizacaoMovimentacao(
            @NotNull final Long codUnidade,
            @NotNull final CampoPersonalizadoDao campoPersonalizadoDao) throws Throwable;
}
