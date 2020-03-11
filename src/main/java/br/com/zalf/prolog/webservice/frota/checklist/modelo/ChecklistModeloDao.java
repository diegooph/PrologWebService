package br.com.zalf.prolog.webservice.frota.checklist.modelo;


import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.gente.unidade._model.Unidade;
import br.com.zalf.prolog.webservice.commons.imagens.Galeria;
import br.com.zalf.prolog.webservice.commons.imagens.ImagemProLog;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.permissao.pilares.FuncaoProLog;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Contém os métodos para manipular os checklists no banco de dados
 */
public interface ChecklistModeloDao {

    /**
     * Insere um novo {@link ModeloChecklistInsercao modelo de checklist} na base de dados.
     *
     * @param modeloChecklist          O {@link ModeloChecklistInsercao modelo} contendo as informações para inserir.
     * @param checklistOfflineListener Listener utilizado para notificar sobre a atualização de modelos de checklist.
     * @param statusAtivo              Propriedade que diz se o modelo de checklist será adicionado como ativo ou
     *                                 inativo. É utilizado a inserção com <code>statusAtivo = false</code> para as
     *                                 integrações onde é necessário uma parametrização com tabelas DE-PARA.
     * @param userToken                O token do usuário que fez a requisição.
     * @return Um objeto contendo o código do modelo de checklist inserido e o código da versão do modelo.
     * @throws Throwable Caso ocorrer algum erro ao salvar os dados.
     */
    @NotNull
    ResultInsertModeloChecklist insertModeloChecklist(
            @NotNull final ModeloChecklistInsercao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean statusAtivo,
            @NotNull final String userToken) throws Throwable;

    /**
     * Atualiza um {@link ModeloChecklistEdicao} específico. Essa atualização pode ser:
     * * {@link ModeloChecklistEdicao#nome}.
     * * {@link ModeloChecklistEdicao#cargosLiberados}.
     * * {@link ModeloChecklistEdicao#tiposVeiculoLiberados}.
     * * {@link ModeloChecklistEdicao#perguntas}.
     *
     * @param codUnidade                                    Código da Unidade.
     * @param codModelo                                     Código do modelo.
     * @param modeloChecklist                               O novo {@link ModeloChecklistEdicao} que será inserido.
     * @param checklistOfflineListener                      Listener utilizado para notificar sobre a atualização de modelos de
     *                                                      checklist.
     * @param podeMudarCodigoContextoPerguntasEAlternativas Esta propriedade é utilizada para dizer se as perguntas e
     *                                                      alternativas presentes na edição do modelo serão
     *                                                      sobrescritas e manterão os mesmos códigos de contexto ou se
     *                                                      podem ser recriadas alterando o código de contexto. Essa
     *                                                      propriedade é utilizada por integrações onde  há o vínculo
     *                                                      com códigos em tabelas DE-PARA, assim não pode-se alterar
     *                                                      esses códigos.
     * @param userToken                                     O token do usuário que fez a requisição.
     * @throws Throwable Se algum erro acontecer na atualização dos dados.
     */
    void updateModeloChecklist(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final ModeloChecklistEdicao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean podeMudarCodigoContextoPerguntasEAlternativas,
            @NotNull final String userToken) throws Throwable;

    /**
     * Busca a listagem de {@link ModeloChecklistListagem modelos de checklist}
     * da {@link Unidade unidade}.
     *
     * @param codUnidade Código da {@link Unidade}.
     * @return Lista de {@link ModeloChecklistListagem} da Unidade.
     * @throws Throwable Se ocorrer algum erro na busca dos dados.
     */
    @NotNull
    List<ModeloChecklistListagem> getModelosChecklistListagemByCodUnidade(
            @NotNull final Long codUnidade) throws Throwable;

    /**
     * Busca um {@link ModeloChecklistVisualizacao modelo de checklist} através do
     * {@link ModeloChecklistVisualizacao#getCodModelo()} e {@link Unidade#getCodigo()}.
     *
     * @param codUnidade Código da unidade.
     * @param codModelo  Código do modelo do checklist.
     * @return Um {@link ModeloChecklistVisualizacao} com todas as informações.
     * @throws Throwable Se ocorrer qualquer erro na busca dos dados do modelo de checklist.
     */
    @NotNull
    ModeloChecklistVisualizacao getModeloChecklist(@NotNull final Long codUnidade,
                                                   @NotNull final Long codModelo) throws Throwable;

    /**
     * Marca um {@link ModeloChecklistVisualizacao} como ativo ou inativo.
     *
     * @param codUnidade               Código da {@link Unidade}.
     * @param codModelo                Código do modelo.
     * @param statusAtivo              O novo status indicando se o modelo será ativado ou inativado.
     * @param checklistOfflineListener Listener utilizado para notificar atualizações nos modelos de checklist.
     * @throws Throwable Caso ocorrer erro no banco.
     */
    void updateStatusAtivo(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            final boolean statusAtivo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    /**
     * Busca os modelos de checklists padrões disponibilizados pelo ProLog.
     */
    @NotNull
    List<ModeloChecklistVisualizacao> getModelosChecklistProLog() throws Throwable;

    /**
     * Método que insere uma imagem na {@link Galeria} da {@link Empresa}.
     *
     * @param codEmpresa   Código da empresa a qual devemos inserir a imagem.
     * @param imagemProLog Imagem que deve ser inserida.
     * @return Código da imagem que foi inserida.
     * @throws SQLException Caso algum erro na query ocorrer.
     */
    @NotNull
    Long insertImagem(@NotNull final Long codEmpresa, @NotNull final ImagemProLog imagemProLog) throws Throwable;

    /**
     * Busca a URLs das imagens das perguntas.
     *
     * @param codUnidade Código da {@link Unidade}.
     * @param codFuncao  Código da {@link FuncaoProLog}.
     * @return Retorna uma lista de Strings contendo as URLs.
     * @throws Throwable Caso der erro no banco.
     */
    List<String> getUrlImagensPerguntas(@NotNull final Long codUnidade,
                                        @NotNull final Long codFuncao) throws Throwable;

    /**
     * Este método busca uma lista de URLs em forma de {@link String}.
     * Com base nessas strings uma {@link Galeria} é criada.
     *
     * @return Um {@link List<String>} contendo as URLs das imagens.
     * @throws Throwable Caso algum erro na query ocorrer.
     */
    @NotNull
    Galeria getGaleriaImagensPublicas() throws Throwable;

    /**
     * Este método busca com base no código da {@link Empresa} uma lista de URLs em forma de {@link String}.
     * Com base nessas strings uma {@link Galeria} é criada.
     *
     * @param codEmpresa Código da empresa a qual devemos buscar as imagens.
     * @return Um {@link List<String>} contendo as URLs das imagens.
     * @throws Throwable Caso algum erro na query ocorrer.
     */
    @NotNull
    Galeria getGaleriaImagensEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    /**
     * Busca os modelos de checklist disponíveis para seleção (e posterior realização) em uma unidade. É buscado
     * apenas os modelos que o cargo do colaborador que faz a requisição tem acesso (através do <code>codCargo</code>).
     * <p>
     * Além disso, serão buscados apenas modelos que tenham tipos de veículos vinculados e que esses tipos tenham, pelo
     * menos, uma placa vinculada e não deletada.
     *
     * @param codUnidade O código da unidade da qual buscar os modelos de checklist.
     * @param codCargo   O código do cargo do usuário que irá realizar o checklist.
     * @return Os modelos de checklist disponíveis para realização.
     * @throws Throwable Caso algum erro ocorrer.
     */
    @NotNull
    List<ModeloChecklistSelecao> getModelosSelecaoRealizacao(@NotNull final Long codUnidade,
                                                             @NotNull final Long codCargo) throws Throwable;

    @NotNull
    ModeloChecklistRealizacao getModeloChecklistRealizacao(final @NotNull Long codModeloChecklist,
                                                           final @NotNull Long codVeiculo,
                                                           final @NotNull String placaVeiculo,
                                                           final @NotNull TipoChecklist tipoChecklist) throws Throwable;
}