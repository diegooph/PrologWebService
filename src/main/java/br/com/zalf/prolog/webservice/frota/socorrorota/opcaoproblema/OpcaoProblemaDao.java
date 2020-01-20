package br.com.zalf.prolog.webservice.frota.socorrorota.opcaoproblema;

import br.com.zalf.prolog.webservice.frota.socorrorota._model.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 09/12/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface OpcaoProblemaDao {
    /**
     * Busca as opções de problema disponíveis para a abertura de socorro em rota por empresa
     *
     * @param codEmpresa Código da empresa
     * @return Uma lista de {@link OpcaoProblemaAberturaSocorro opções de problema} ativos por unidade
     * @throws Throwable Se algo der errado na busca.
     */
    @NotNull
    List<OpcaoProblemaAberturaSocorro> getOpcoesProblemasDisponiveisAberturaSocorroByEmpresa(
            @NotNull final Long codEmpresa) throws Throwable;

    /**
     * Busca as opções de problema por empresa
     *
     * @param codEmpresa Código da empresa
     * @return Uma lista de {@link OpcaoProblemaSocorroRotaListagem opções de problema}
     * @throws Throwable Se algo der errado na busca.
     */
    @NotNull
    List<OpcaoProblemaSocorroRotaListagem> getOpcoesProblemasSocorroRotaByEmpresa(@NotNull final Long codEmpresa)
            throws Throwable;

    /**
     * Busca uma opção de problema específica
     *
     * @param codOpcaoProblema Código da Opção de Problema
     * @return Uma {@link OpcaoProblemaSocorroRotaVisualizacao opção de problema específica}
     * @throws Throwable Se algo der errado na busca.
     */
    @NotNull
    OpcaoProblemaSocorroRotaVisualizacao getOpcaoProblemaSocorroRotaVisualizacao(@NotNull final Long codOpcaoProblema)
            throws Throwable;

    /**
     * Cria uma nova opção de problema no banco de dados.
     *
     * @param opcaoProblemaSocorroRotaCadastro Objeto contendo as informações da opção de problema.
     * @return Código gerado pelo BD para a nova opção de problema.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Long insertOpcoesProblemas(
            @NotNull final OpcaoProblemaSocorroRotaCadastro opcaoProblemaSocorroRotaCadastro) throws Throwable;


    /**
     * Edita uma opção de problema no banco de dados.
     *
     * @param opcaoProblemaSocorroRotaEdicao Objeto contendo as informações da opção de problema.
     * @return Código gerado pelo BD para a nova opção de problema.
     * @throws Throwable Se algum erro ocorrer.
     */
    void updateOpcoesProblemas(
            @NotNull final OpcaoProblemaSocorroRotaEdicao opcaoProblemaSocorroRotaEdicao) throws Throwable;

    /**
     * Marca um {@link OpcaoProblemaSocorroRotaVisualizacao} como ativo ou inativo.
     * @param opcaoProblemaSocorroRotaStatus  Objeto com o código da opção de problema e seu novo status indicando se a
     *                                        opção será ativada ou inativada.
     * @throws Throwable Caso ocorrer erro no banco.
     */
    void updateStatusAtivo(@NotNull final OpcaoProblemaSocorroRotaStatus opcaoProblemaSocorroRotaStatus)
            throws Throwable;
}