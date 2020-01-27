package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.ColaboradorEdicao;
import br.com.zalf.prolog.webservice.colaborador.model.ColaboradorInsercao;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.gente.controlejornada.DadosIntervaloChangedListener;
import br.com.zalf.prolog.webservice.permissao.pilares.FuncaoProLog;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Contém os métodos para manipular os usuários no banco de dados.
 */
public interface ColaboradorDao {

    /**
     * Insere um {@link ColaboradorInsercao colaborador} no bando de dados.
     *
     * @param colaborador              Dados do colaborador a ser inserido.
     * @param intervaloListener        Listener para repassar informações do colaborador inserido no contexto
     *                                 das marcações de jornada.
     * @param checklistOfflineListener Listener para repassar informações do colaborador inserido no contexto
     *                                 da realização do checklist offline.
     * @throws Throwable Caso não seja possível inserir no banco de dados.
     */
    void insert(@NotNull final ColaboradorInsercao colaborador,
                @NotNull final DadosIntervaloChangedListener intervaloListener,
                @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
                @NotNull final String userToken) throws Throwable;

    /**
     * Atualiza os dados de um {@link Colaborador colaborador}.
     *
     * @param colaborador              Novos dados do colaborador a ser atualizado.
     * @param intervaloListener        Listener para repassar informações do colaborador atualizado no contexto
     *                                 das marcações de jornada.
     * @param checklistOfflineListener Listener para repassar informações do colaborador atualizado no contexto
     *                                 da realização do checklist offline.
     * @param userToken                CPF do colaborador a ser atualizado.
     * @throws Throwable Caso não seja possível atualizar as informações.
     */
    void update(@NotNull final ColaboradorEdicao colaborador,
                @NotNull final DadosIntervaloChangedListener intervaloListener,
                @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
                @NotNull final String userToken) throws Throwable;

    /**
     * Método para atualizar o status (ativo ou inativo) de um colaborador.
     *
     * @param cpf                      CPF do colaborador que será atualizado.
     * @param colaborador              Objeto contendo a informação se o colaborador será ATIVADO ou INATIVADO.
     * @param checklistOfflineListener Listener para repassar informações do colaborador atualizado no contexto
     *                                 da realização do checklist offline.
     * @throws Throwable Se ocorrer algum erro na atualização do status do colaborador.
     */
    void updateStatus(@NotNull final Long cpf,
                      @NotNull final Colaborador colaborador,
                      @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    /**
     * Para manter histórico no banco de dados, não é feita exclusão de colaborador,
     * setamos o status para inativo.
     *
     * @param cpf               CPF do colaborador a ser inativado.
     * @param intervaloListener para repassarmos o evento de que um colaborador está sendo inativado.
     * @throws Throwable caso não seja possível inativar o colaborador.
     */
    void delete(@NotNull final Long cpf,
                @NotNull final DadosIntervaloChangedListener intervaloListener,
                @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    /**
     * Busca um colaborador por seu CPF.
     *
     * @param cpf          CPF do {@link Colaborador} que queremos buscar.
     * @param apenasAtivos indica se queremos buscar considerando apenas os colaboradores ativos.
     * @return um {@link Colaborador}.
     * @throws SQLException caso aconteça algum erro na consulta ao banco.
     */
    Colaborador getByCpf(Long cpf, boolean apenasAtivos) throws SQLException;

    /**
     * Busca todos os colaboradores de uma unidade.
     *
     * @param codUnidade   código da unidade.
     * @param apenasAtivos indica se queremos buscar apenas os colaboradores que estão ativos.
     * @return uma lista de colaboradores.
     * @throws SQLException caso não seja possível buscar os dados.
     */
    @NotNull
    List<Colaborador> getAllByUnidade(@NotNull final Long codUnidade, final boolean apenasAtivos) throws Throwable;

    /**
     * Busca todos os colaboradores de uma empresa.
     *
     * @param codEmpresa   código da empresa.
     * @param apenasAtivos indica se queremos buscar apenas os colaboradores que estão ativos.
     * @return uma lista de colaboradores.
     * @throws SQLException caso não seja possível buscar os dados.
     */
    @NotNull
    List<Colaborador> getAllByEmpresa(@NotNull final Long codEmpresa, final boolean apenasAtivos) throws Throwable;

    /**
     * Busca apenas os motoristas e ajudantes de uma unidade
     *
     * @param codUnidade código da unidade
     * @return uma lista de colaboradores
     * @throws SQLException caso não seja possível realizar a busca
     */
    List<Colaborador> getMotoristasAndAjudantes(Long codUnidade) throws SQLException;

    /**
     * Verifica se determinado CPF existe em determinada unidade.
     *
     * @param cpf        cpf a ser verificado
     * @param codUnidade codigo da unidade ao qual o cpf deve pertencer
     * @return verdadeiro caso CPF exista, falso caso contrário
     * @throws SQLException caso não seja possível realizar a operação
     */
    boolean verifyIfCpfExists(Long cpf, Long codUnidade) throws SQLException;

    /**
     * Busca um colaborador por seu token.
     *
     * @param token um token.
     * @return um {@link Colaborador}.
     * @throws SQLException caso ocorrer erro no banco
     */
    @NotNull
    Colaborador getByToken(@NotNull final String token) throws SQLException;

    /**
     * Método que busca uma {@link List<Colaborador>} que possuem acesso à um {@code codFuncaoProLog} específico.
     *
     * @param codUnidade      Código da {@link Unidade} que será buscado os colaboradores.
     * @param codFuncaoProLog Código da {@link FuncaoProLog} que estamos filtrando.
     * @return Uma {@link List<Colaborador>} que possuem a {@link FuncaoProLog} em questão.
     * @throws SQLException Caso algum erro na busca acontecer.
     */
    @NotNull
    List<Colaborador> getColaboradoresComAcessoFuncaoByUnidade(@NotNull final Long codUnidade,
                                                               final int codFuncaoProLog) throws SQLException;

    Long getCodUnidadeByCpf(@NotNull final Long cpf) throws SQLException;


    /**
     * Verifica se um colaborador tem acesso a uma funcionalidade específica do ProLog. A verificação acontece
     * estando o colaborador ativo ou não.
     *
     * @param cpf             CPF do colaborador.
     * @param codPilar        código do pilar do qual a função pertence.
     * @param codFuncaoProLog código único da função no ProLog.
     * @return {@code true} se o colaborador tem acesso; {@code false} caso contrário.
     * @throws SQLException caso aconteça algum erro na consulta.
     */
    boolean colaboradorTemAcessoFuncao(@NotNull final Long cpf, final int codPilar, final int codFuncaoProLog) throws SQLException;

    /**
     * Método utilizado para buscar, unicamente, o código de um colaborador. O método utiliza o {@code cpfColaborador}
     * para identificar qual código irá retornar.
     *
     * @param conn           Conexão com o banco de dados que será utilizada para buscar o código.
     * @param codEmpresa     Código da empresa que o colaborador está vinculado.
     * @param cpfColaborador Cpf que será utilizado para buscar o código do colaborador.
     * @return O código único de identificação do colaborador no sistema.
     * @throws Throwable Se algum erro acontecer.
     */
    @NotNull
    Long getCodColaboradorByCpf(@NotNull final Connection conn,
                                @NotNull final Long codEmpresa,
                                @NotNull final String cpfColaborador) throws Throwable;
}
