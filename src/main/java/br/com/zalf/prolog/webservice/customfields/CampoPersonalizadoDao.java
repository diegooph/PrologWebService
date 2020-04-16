package br.com.zalf.prolog.webservice.customfields;

import br.com.zalf.prolog.webservice.customfields._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.List;

/**
 * Created on 2020-03-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface CampoPersonalizadoDao {

    /**
     * Busca os campos personalizados que estão disponíveis para preenchimento durante um processo de movimentação na
     * unidade. Note que para um campo estar disponível para uso no processo de movimentação na unidade, além de estar
     * cadastrado na empresa para a funcionalidade de movimentação, também precisa estar vinculado à unidade na tabela:
     * <b>movimentacao_campo_personalizado_unidade</b>.
     *
     * @param codUnidade Código da unidade onde o processo de movimentação será realizado.
     * @return Uma lista de campos personalizados que podem ser respondidos no processo de movimentação. Ou uma lista
     * vazia caso não exista nenhum. Nunca {@code null}.
     * @throws Throwable Caso qualquer erro ocorrer.
     */
    @NotNull
    List<CampoPersonalizadoParaRealizacao> getCamposParaRealizacaoMovimentacao(
            @NotNull final Long codUnidade) throws Throwable;

    /**
     * Salva no banco de dados as respostas fornecidas para cada campo personalizado presente na lista de
     * {@code respostas}. A tabela onde as respostas serão salvas depende da {@code funcaoProlog} informada, pois é
     * o atributo {@link CampoPersonalizadoFuncaoProlog#getTableNameRespostas() tableNameRespostas} desse enum que
     * fornece o nome da tabela.
     * A conexão com banco é feita utilizando a connection informada.
     * O Prolog assume que toda tabela de respostas tenha, pelo menos, as seguintes colunas:
     * <pre>
     *      cod_tipo_campo;
     *      cod_campo;
     *      resposta;
     *      resposta_lista_selecao.
     * </pre>
     * Caso a tabela onde as respostas serão salvas tenha mais colunas, é necessário informar o nome e o valor que deve
     * ser setado para cada uma delas e passar isso como uma lista, que nesse método é chamada de
     * {@code colunasEspecificas}. É possível utilizar o builder {@link ColunaTabelaRespostaBuilder} para construir essa
     * lista de maneira encadeada.
     *
     * @param conn               A connection que será utilizada para salvar as respostas.
     * @param funcaoProlog       A funcionalidade do Prolog para da qual os campos respondidos pertencem.
     * @param respostas          As respostas dos campos personalizados.
     * @param colunasEspecificas As colunas específicas que a tabela onde as respostas serão salvas possui.
     *                           Ou {@code null} caso não tenha nenhuma coluna específica.
     * @throws Throwable Caso qualquer erro ocorrer.
     */
    void salvaRespostasCamposPersonalizados(
            @NotNull final Connection conn,
            @NotNull final CampoPersonalizadoFuncaoProlog funcaoProlog,
            @NotNull final List<CampoPersonalizadoResposta> respostas,
            @Nullable final List<ColunaTabelaResposta> colunasEspecificas) throws Throwable;
}
