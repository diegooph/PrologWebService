package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 14/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface OperacoesIntegradasPneu {
    @NotNull
    Long insert(@NotNull final Pneu pneu,
                @NotNull final Long codUnidade,
                @NotNull final OrigemAcaoEnum origemCadastro) throws Throwable;

    @NotNull
    List<Long> insert(@NotNull final List<Pneu> pneus) throws Throwable;

    void update(@NotNull final Pneu pneu,
                @NotNull final Long codUnidade,
                @NotNull final Long codOriginalPneu) throws Throwable;

    @NotNull
    List<Pneu> getPneusByCodUnidadesByStatus(@NotNull final List<Long> codUnidades,
                                             @NotNull final StatusPneu status) throws Throwable;
}
