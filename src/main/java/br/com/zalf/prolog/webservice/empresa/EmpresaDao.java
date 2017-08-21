package br.com.zalf.prolog.webservice.empresa;

import br.com.zalf.prolog.webservice.colaborador.*;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Request;
import br.com.zalf.prolog.webservice.permissao.Visao;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.NoContentException;
import java.sql.SQLException;
import java.util.List;

public interface EmpresaDao {

    /**
     * Busca uma equipe no banco de dados.
     *
     * @param codUnidade código da unidade onde a equipe será buscada
     * @param codEquipe  código da equipe
     * @return um objeto {@link Equipe} referente ao código
     * @throws SQLException caso não for possível processar a requisição
     */
    Equipe getEquipe(Long codUnidade, Long codEquipe) throws SQLException;

    /**
     * Insere uma equipe.
     *
     * @param codUnidade código da unidade onde a equipe será inserida
     * @param equipe     equipe que deve ser inserida
     * @return objeto da classe {@link AbstractResponse} que encapsula um resposta OK ou ERROR
     * @throws SQLException caso ocorrer algum erro no banco
     */
    AbstractResponse insertEquipe(@NotNull Long codUnidade, @NotNull Equipe equipe) throws SQLException;

    /**
     * Atualiza uma equipe.
     *
     * @param codUnidade código da unidade que a equipe pertence
     * @param codEquipe  código da equipe que desejamos atualizar
     * @param equipe     equipe para ser atualizada
     * @return true se a atualização deu certo; caso contrário false
     * @throws SQLException caso ocorrer algum erro no banco
     */
    boolean updateEquipe(@NotNull Long codUnidade, @NotNull Long codEquipe, @NotNull Equipe equipe) throws SQLException;

    /**
     * Cria uma equipe.
     *
     * @param request objeto que encapsula uma equipe
     * @return valor da operação
     * @throws SQLException caso ocorrer erro no banco
     */
    @Deprecated
    boolean createEquipe(Request<Equipe> request) throws SQLException;

    /**
     * Atualiza uma equipe.
     *
     * @param request objeto que encapsula uma equipe
     * @return valor da operação
     * @throws SQLException caso ocorrer erro no banco
     */
    @Deprecated
    boolean updateEquipe(Request<Equipe> request) throws SQLException;

    /**
     * Cadastra um setor no banco de dados.
     *
     * @param setor      o setor para ser inserido
     * @param codUnidade código da unidade na qual esse setor será inserido
     * @return objeto que encapsula um resposta OK ou ERROR
     * @throws SQLException caso ocorrer erro no banco
     */
    AbstractResponse insertSetor(@NotNull Long codUnidade, @NotNull Setor setor) throws SQLException;


    /**
     * Busca um setor de uma unidade.
     *
     * @param codUnidade código da unidade onde o setor será buscado
     * @param codSetor   código do setor a ser buscado
     * @return um objeto {@link Setor} referente ao código do setor
     * @throws SQLException caso não for possível processar a requisição
     */
    Setor getSetor(Long codUnidade, Long codSetor) throws SQLException;

    /**
     * Atualiza um {@link Setor}.
     *
     * @param codUnidade código da {@link Unidade} que o {@link Setor} pertence
     * @param codSetor   código do {@link Setor} que desejamos atualizar
     * @param setor      {@link Setor} para ser atualizado
     * @return true se a atualização deu certo; caso contrário false
     * @throws SQLException caso ocorrer algum erro no banco
     */
    boolean updateSetor(@NotNull Long codUnidade, @NotNull Long codSetor, @NotNull Setor setor) throws SQLException;

    /**
     * Lista as equipes de uma unidade.
     *
     * @param codUnidade código de uma unidade
     * @return lista de equipes da unidade
     * @throws SQLException caso ocorrer erro no banco
     */
    List<Equipe> getEquipesByCodUnidade(Long codUnidade) throws SQLException;

    /**
     * @param ano        ano da busca
     * @param mes        mês da busca
     * @param codUnidade unidade que deseja-se buscar
     * @return           uma lista de {@link HolderMapaTracking}
     * @throws SQLException       caso ocorrer erro no banco
     * @throws NoContentException se não tiver conteúdo
     */
    List<HolderMapaTracking> getResumoAtualizacaoDados(int ano, int mes, Long codUnidade) throws SQLException, NoContentException;

    /**
     * Lista os setores referentes ao código da unidade.
     *
     * @param codUnidade código de uma unidade
     * @return lista de setores da unidade
     * @throws SQLException caso ocorrer erro no banco
     */
    List<Setor> getSetorByCodUnidade(Long codUnidade) throws SQLException;

    /**
     * Lista as funções de uma unidade.
     *
     * @param codUnidade código de uma unidade
     * @return lista de funções da unidade
     * @throws SQLException caso ocorrer erro no banco
     */
    List<Cargo> getFuncoesByCodUnidade(Long codUnidade) throws SQLException;

    /**
     * Busca os itens do Filtro (empresa, unidade, equipe).
     *
     * @param cpf do solicitante, busca a partir das permissões
     * @return list de Empresa, contendo os itens do filtro
     * @throws SQLException caso não seja possível realizar a busca
     */
    List<Empresa> getFiltros(Long cpf) throws SQLException;

    /**
     * Busca as funções do prolog de um determinado cargo.
     *
     * @param codUnidade código da unidade
     * @param codCargo   código do cargo
     * @return {@link Visao} de uma {@link Cargo}
     * @throws SQLException caso não seja possível realizar a busca
     */
    Visao getVisaoCargo(Long codUnidade, Long codCargo) throws SQLException;

    /**
     * Busca as funções do prolog a partir do código da unidade.
     *
     * @param codUnidade código da unidade
     * @return {@link Visao} da {@link Unidade}
     * @throws SQLException caso não seja possível realizar a busca
     */
    Visao getVisaoUnidade(Long codUnidade) throws SQLException;

    /**
     * Insere ou atualiza as funções do prolog cadastradas para determinado cargo.
     *
     * @param visao      {@link Visao} de uma {@link Cargo}
     * @param codUnidade código da unidade
     * @param codCargo   código do cargo
     * @return boolean com o resultado da operação
     * @throws SQLException caso não seja possível realizar a operação
     */
    boolean alterarVisaoCargo(Visao visao, Long codUnidade, Long codCargo) throws SQLException;

    Long getCodEquipeByCodUnidadeByNome(Long codUnidade, String nomeEquipe) throws SQLException;

    /**
     * Insere um cargo (função).
     *
     * @param cargo função que será inserida
     * @param codUnidade unidade que está solicitando o cadastro
     * @return código da função
     * @throws SQLException caso não seja possível realizar a operação
     */
    Long insertFuncao(Cargo cargo, Long codUnidade) throws SQLException;
}