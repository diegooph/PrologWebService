package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.commons.imagens.Galeria;
import br.com.zalf.prolog.webservice.commons.imagens.ImagemProlog;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Contém os métodos para manipular os checklists no banco de dados
 */
public interface ChecklistModeloDao {

    @NotNull
    ResultInsertModeloChecklist insertModeloChecklist(
            @NotNull final ModeloChecklistInsercao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean statusAtivo,
            @NotNull final String userToken) throws Throwable;

    void updateModeloChecklist(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final ModeloChecklistEdicao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean podeMudarCodigoContextoPerguntasEAlternativas,
            @NotNull final String userToken) throws Throwable;

    @NotNull
    List<ModeloChecklistListagem> getModelosChecklistListagemByCodUnidade(
            @NotNull final Long codUnidade) throws Throwable;

    @NotNull
    ModeloChecklistVisualizacao getModeloChecklist(@NotNull final Long codUnidade,
                                                   @NotNull final Long codModelo) throws Throwable;

    void updateStatusAtivo(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            final boolean statusAtivo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    @NotNull
    List<ModeloChecklistVisualizacao> getModelosChecklistProLog() throws Throwable;

    @NotNull
    Long insertImagem(@NotNull final Long codEmpresa, @NotNull final ImagemProlog imagemProLog) throws Throwable;

    List<String> getUrlImagensPerguntas(@NotNull final Long codUnidade,
                                        @NotNull final Long codFuncao) throws Throwable;

    @NotNull
    Galeria getGaleriaImagensPublicas() throws Throwable;

    @NotNull
    Galeria getGaleriaImagensEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    @NotNull
    List<ModeloChecklistSelecao> getModelosSelecaoRealizacao(@NotNull final Long codUnidade,
                                                             @NotNull final Long codCargo) throws Throwable;

    @NotNull
    ModeloChecklistRealizacao getModeloChecklistRealizacao(final @NotNull Long codModeloChecklist,
                                                           final @NotNull Long codVeiculo,
                                                           final @NotNull String placaVeiculo,
                                                           final @NotNull TipoChecklist tipoChecklist) throws Throwable;
}