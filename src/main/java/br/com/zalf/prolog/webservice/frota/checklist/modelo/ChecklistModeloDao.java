package br.com.zalf.prolog.webservice.frota.checklist.modelo;


import br.com.zalf.prolog.webservice.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.imagens.Galeria;
import br.com.zalf.prolog.webservice.commons.imagens.ImagemProLog;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.insercao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.visualizacao.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.visualizacao.ModeloChecklistVisualizacao;
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
     * @param modeloChecklist O {@link ModeloChecklistInsercao modelo} contendo as informações para inserir.
     * @throws SQLException Caso ocorrer algum erro ao salvar os dados.
     */
    void insertModeloChecklist(@NotNull final ModeloChecklistInsercao modeloChecklist) throws Throwable;

    /**
     * Busca a listagem de {@link ModeloChecklistListagem} da {@link Unidade} filtrado pela {@link FuncaoProLog}.
     *
     * @param codUnidade - Código da unidade.
     * @param codFuncao  - Código da função ou "%" para buscar de todas as funções.
     * @return - Lista de {@link ModeloChecklistListagem} da Unidade.
     * @throws SQLException - Se ocorrer erro no banco.
     */
    List<ModeloChecklistListagem> getModelosChecklistListagemByCodUnidadeByCodFuncao(
            @NotNull final Long codUnidade,
            @NotNull final String codFuncao) throws SQLException;

    /**
     * Busca um modelo de checklist através do {@link ModeloChecklistVisualizacao#getCodigo()} e {@link Unidade#getCodigo()}.
     *
     * @param codUnidade - Código da unidade.
     * @param codModelo  - Código do modelo.
     * @return - Um {@link ModeloChecklistVisualizacao}.
     * @throws SQLException - Se ocorrer erro no bando.
     */
    ModeloChecklistVisualizacao getModeloChecklist(@NotNull final Long codUnidade,
                                                   @NotNull final Long codModelo) throws SQLException;

    /**
     * Atualiza um {@link ModeloChecklistEdicao} específico. Essa atualização pode ser:
     * * {@link ModeloChecklistEdicao#nome}.
     * * {@link ModeloChecklistEdicao#cargosLiberados}.
     * * {@link ModeloChecklistEdicao#tiposVeiculoLiberados}.
     * * {@link ModeloChecklistEdicao#perguntas}.
     *
     * @param token           - Token do usuário que está solicitando a alteração do {@link ModeloChecklistEdicao}.
     * @param unidade         - Código da Unidade.
     * @param codUnidade      - Código do modelo.
     * @param modeloChecklist - O novo {@link ModeloChecklistEdicao} que será inserido.
     * @throws Exception      - Se algum erro ocorrer.
     */
    void updateModeloChecklist(@NotNull final String token,
                               @NotNull final Long unidade,
                               @NotNull final Long codUnidade,
                               @NotNull final ModeloChecklistEdicao modeloChecklist) throws Exception;

    /**
     * Busca as perguntas que compoẽm o checklist.
     *
     * @param codUnidade - Código da unidade.
     * @param codModelo  - Código do modelo.
     * @return - Lista de {@link PerguntaRespostaChecklist}.
     * @throws SQLException - Se ocorrer erro na execução.
     */
    List<PerguntaRespostaChecklist> getPerguntas(@NotNull final Long codUnidade,
                                                 @NotNull final  Long codModelo) throws SQLException;

    /**
     * Marca um {@link ModeloChecklist} como ativo ou inativo.
     *
     * @param codUnidade - Código da unidade.
     * @param codModelo  - Código do modelo.
     * @param statusAtivo - o novo status indicando se o modelo será ativado ou inativado.
     * @throws SQLException - Caso ocorrer erro no banco.
     */
    void updateStatusAtivo(@NotNull final Long codUnidade,
                           @NotNull final Long codModelo,
                           final boolean statusAtivo) throws Throwable;

    /**
     * Busca os modelos de checklists padrões disponibilizados pelo ProLog.
     */
    @NotNull
    List<ModeloChecklistVisualizacao> getModelosChecklistProLog() throws SQLException;

    /**
     * Busca a URL das imagens das perguntas.
     *
     * @param codUnidade - Código da unidade.
     * @param codFuncao  - Código da função.
     * @return - Retorna uma lista de Strings contendo as URLs.
     * @throws SQLException - Caso der erro no banco.
     */
    List<String> getUrlImagensPerguntas(@NotNull final Long codUnidade, @NotNull final Long codFuncao) throws SQLException;

    /**
     * Este método busca uma lista de URLs em forma de {@link String}.
     * Com base nessas strings uma {@link Galeria} é criada.
     *
     * @return - Um {@link List<String>} contendo as URLs das imagens.
     * @throws SQLException - Caso algum erro na query ocorrer.
     */
    Galeria getGaleriaImagensPublicas() throws SQLException;

    /**
     * Este método busca com base no código da {@link Empresa} uma lista de URLs em forma de {@link String}.
     * Com base nessas strings uma {@link Galeria} é criada.
     *
     * @param codEmpresa - Código da empresa a qual devemos buscar as imagens.
     * @return - Um {@link List<String>} contendo as URLs das imagens.
     * @throws SQLException - Caso algum erro na query ocorrer.
     */
    Galeria getGaleriaImagensEmpresa(@NotNull final Long codEmpresa) throws SQLException;

    /**
     * Método que insere uma imagem na {@link Galeria} da {@link Empresa}.
     *
     * @param codEmpresa   - Código da empresa a qual devemos inserir a imagem.
     * @param imagemProLog - Imagem que deve ser inserida.
     * @return - Código da imagem que foi inserida.
     * @throws SQLException - Caso algum erro na query ocorrer.
     */
    @NotNull
    Long insertImagem(@NotNull final Long codEmpresa, @NotNull final ImagemProLog imagemProLog) throws SQLException;
}
