package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.VeiculoLiberacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import com.sun.istack.internal.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Operações integradas do checklist.
 */
interface OperacoesIntegradasChecklist {

    Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(
            @NotNull final Long codUnidade,
            @NotNull final Long codFuncao) throws Exception;

    NovoChecklistHolder getNovoChecklistHolder(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final String placaVeiculo) throws Exception;

    boolean insertChecklist(@NotNull final Checklist checklist) throws Exception;

    @NotNull
    List<VeiculoLiberacao> getStatusLiberacaoVeiculos(@NotNull final Long codUnidade) throws Exception;
}