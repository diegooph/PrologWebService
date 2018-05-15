package br.com.zalf.prolog.webservice.frota.checklist.modelo;


import br.com.zalf.prolog.webservice.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.commons.imagens.Galeria;
import br.com.zalf.prolog.webservice.commons.imagens.ImagemProLog;
import br.com.zalf.prolog.webservice.frota.checklist.model.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.model.PerguntaRespostaChecklist;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Contém os métodos para manipular os checklists no banco de dados
 */
public interface ChecklistModeloDao {

    /**
     * busca as perguntas do checklist
     *
     * @param codUnidade código da unidade
     * @param codModelo  código do modelo
     * @return lista de perguntas do checklist
     * @throws SQLException se ocorrer erro na execução
     */
    List<PerguntaRespostaChecklist> getPerguntas(Long codUnidade, Long codModelo) throws SQLException;

    /**
     * busca o modelo de checklist usando código da unidade e função
     *
     * @param codUnidade código da unidade
     * @param codFuncao  código da função
     * @return lista de modelo do checklist
     * @throws SQLException se ocorrer erro no banco
     */
    List<ModeloChecklistListagem> getModelosChecklistListagemByCodUnidadeByCodFuncao(Long codUnidade, String codFuncao)
            throws SQLException;

    /**
     * busca um modelo de checklist atraves do modelo e da unidade
     *
     * @param codModelo  código do modelo
     * @param codUnidade código da unidade
     * @return um {@link ModeloChecklist}
     * @throws SQLException se ocorrer erro no bando
     */
    ModeloChecklist getModeloChecklist(Long codModelo, Long codUnidade) throws SQLException;

    /**
     *
     * @param token
     * @param unidade
     * @param codUnidade
     * @param modeloChecklist
     * @throws SQLException
     */
    void updateModeloChecklist(@NotNull final String token,
                               @NotNull final Long unidade,
                               @NotNull final Long codUnidade,
                               @NotNull final ModeloChecklist modeloChecklist) throws SQLException;

    /**
     * insere um checklist
     *
     * @param modeloChecklist o checklist
     * @throws SQLException caso ocorrer erro no banco
     */
    void insertModeloChecklist(ModeloChecklist modeloChecklist) throws SQLException;

    /**
     * marca como inativo o checklist através das informações de unidade e modelo
     *
     * @param codUnidade código da unidade
     * @param codModelo  código do modelo
     * @return valor da operação
     * @throws SQLException caso ocorrer erro no banco
     */
    boolean setModeloChecklistInativo(Long codUnidade, Long codModelo) throws SQLException;

    /**
     * Busca os modelos de checklists padrões disponibilizados pelo ProLog.
     */
    @NotNull
    List<ModeloChecklist> getModelosChecklistProLog() throws SQLException;

    /**
     * Busca a URL das imagens das perguntas.
     *
     * @param codUnidade - Código da unidade.
     * @param codFuncao  - Código da função.
     * @return - Retorna uma lista de Strings contendo as URLs.
     * @throws SQLException - Caso der erro no banco.
     */
    List<String> getUrlImagensPerguntas(Long codUnidade, Long codFuncao) throws SQLException;

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
