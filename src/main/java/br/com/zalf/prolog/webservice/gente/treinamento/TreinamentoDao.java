package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.webservice.gente.treinamento.model.Treinamento;
import br.com.zalf.prolog.webservice.gente.treinamento.model.TreinamentoColaborador;

import java.sql.SQLException;
import java.util.List;

/**
 * Contém os métodos para manipular os treinamentos
 */
public interface TreinamentoDao {

    /**
     * busca todos os trienamentos dentre as datas inicial e final
     *
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @param codFuncao   código da função
     * @param codUnidade  código da unidade
     * @param comCargosLiberados  com cargos liberados
     * @param apenasTreinamentosLiberados  apenas os treinamentos liberados
     * @param limit       limit de busca no banco
     * @param offset      offset de busca no banco
     * @return uma lista de treinamentos
     * @throws SQLException caso operação falhar
     */
    List<Treinamento> getAll(Long dataInicial,
                             Long dataFinal,
                             String codFuncao,
                             Long codUnidade,
                             Boolean comCargosLiberados,
                             boolean apenasTreinamentosLiberados,
                             long limit,
                             long offset) throws SQLException;

    /**
     * Busca os treinamentos ainda não visualizados por um colaborador específico
     *
     * @param cpf um cpf, serão buscados os treinamentos ainda não visualizados por ele
     * @return lista de Treinamento
     * @throws SQLException caso não seja possível realizar a busca
     */
    List<Treinamento> getNaoVistosColaborador(Long cpf) throws SQLException;

    /**
     * Busca os treinamentos já visualizados por um colaborador específico
     *
     * @param cpf um cpf, serão buscados os treinamentos já visualizados por ele
     * @return lista de Treinamento
     * @throws SQLException caso não seja possível realizar a busca
     */
    List<Treinamento> getVistosColaborador(Long cpf) throws SQLException;

    /**
     * Insere uma linha na tabela treinamento_colaborador, na qual armazena a data
     * que um treinamento foi visualizado, associando o código do treinamento com um cpf
     *
     * @param codTreinamento
     * @param cpf
     * @return resultado da requisição
     * @throws SQLException caso não seja possível realizar o insert
     */
    boolean marcarTreinamentoComoVisto(Long codTreinamento, Long cpf) throws SQLException;

    /**
     * inserir um treinamento
     *
     * @param treinamento treinamento a ser inserido
     * @return código do gerado pelo BD para o treinamento inserido
     * @throws SQLException caso operação falhar
     */
    Long insert(Treinamento treinamento) throws SQLException;

    /**
     * busca os colaboradores que visualizaram o treinamento
     *
     * @param codTreinamento código do treinamento
     * @param codUnidade     código da unidade
     * @return uma lista de colaboradores
     * @throws SQLException caso operação falhar
     */
    List<TreinamentoColaborador> getVisualizacoesByTreinamento(Long codTreinamento, Long codUnidade) throws SQLException;

    /**
     * Busca um treinamento a partir do seu código
     *
     * @param codTreinamento código do treinamento
     * @param codUnidade     código da unidade
     * @return um Treinamento
     * @throws SQLException caso não seja possível realizar a busca
     */
    Treinamento getTreinamentoByCod(Long codTreinamento, Long codUnidade, boolean comCargosLiberados) throws SQLException;

    /**
     * Atualiza os dados gerais de um treinamento
     *
     * @param treinamento
     * @return
     * @throws SQLException
     */
    boolean updateTreinamento(Treinamento treinamento) throws SQLException;

    /**
     * Atualiza as URLs de um treinamento
     *
     * @param urls
     * @param codTreinamento
     * @return
     * @throws SQLException
     */
    boolean updateUrlImagensTreinamento(List<String> urls, Long codTreinamento) throws SQLException;

    /**
     * Deleta todos os dados de um treinamento (visualizaçoes, urls, funcoes..)
     *
     * @param codTreinamento cód do treinamento a ser deletado
     * @return
     * @throws SQLException
     */
    boolean deleteTreinamento(Long codTreinamento) throws SQLException;
}