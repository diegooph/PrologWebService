package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.pneu.servico._model.Servico;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.VeiculoServico;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.filtro.VeiculoAberturaServicoFiltro;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

/**
 * Created on 14/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface OperacoesIntegradasAfericaoServico {
    @NotNull
    VeiculoServico getVeiculoAberturaServico(@NotNull final VeiculoAberturaServicoFiltro filtro) throws Throwable;

    void fechaServico(@NotNull final Long codUnidade,
                      @NotNull final OffsetDateTime dataHorafechamentoServico,
                      @NotNull final Servico servico) throws Throwable;
}
