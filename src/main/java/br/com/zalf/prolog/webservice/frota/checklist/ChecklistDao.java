package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.FiltroRegionalUnidadeChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Contém os métodos para manipular os checklists no banco de dados
 */
public interface ChecklistDao {

    /**
     * Insere um checklist no BD salvando na tabela CHECKLIST e chamando métodos
     * especificos que salvam as respostas do map na tabela CHECKLIST_RESPOSTAS.
     *
     * @param conn        Conexão que será utilizada para inserir o checklist.
     * @param checklist   Um checklist respondido pelo usuário.
     * @param foiOffline  Indica se esse checklist foi realizado de forma offline.
     * @param deveAbrirOs Valor que indica se o checklist deve abrir Ordem de Serviço ou não.
     * @return código do checklist recém inserido.
     * @throws SQLException caso não seja possível inserir o checklist no banco de dados
     */
    @NotNull
    Long insert(@NotNull final Connection conn,
                @NotNull final ChecklistInsercao checklist,
                final boolean foiOffline,
                final boolean deveAbrirOs) throws Throwable;

    /**
     * Insere um checklist no BD salvando na tabela CHECKLIST e chamando métodos
     * especificos que salvam as respostas do map na tabela CHECKLIST_RESPOSTAS.
     *
     * @param checklist um checklist
     * @param foiOffline  Indica se esse checklist foi realizado de forma offline.
     * @param deveAbrirOs Valor que indica se o checklist deve abrir Ordem de Serviço ou não.
     * @return código do checklist recém inserido
     * @throws SQLException caso não seja possível inserir o checklist no banco de dados
     */
    @NotNull
    Long insert(@NotNull final ChecklistInsercao checklist,
                final boolean foiOffline,
                final boolean deveAbrirOs) throws Throwable;

    /**
     * Busca um checklist pelo seu código único.
     *
     * @param codChecklist codigo do checklist a ser buscado.
     * @return um checklist
     * @throws SQLException caso não consiga buscar o checklist no banco de dados.
     */
    @NotNull
    Checklist getByCod(@NotNull final Long codChecklist) throws SQLException;

    /**
     * Busca todos os checklists, respeitando os filtros aplicados (recebidos por parâmetro).
     *
     * @return uma {@link List<Checklist> lista de checklists}.
     * @throws SQLException caso não seja possível realizar a busca.
     */
    @NotNull
    List<Checklist> getAll(@NotNull final Long codUnidade,
                           @Nullable final Long codEquipe,
                           @Nullable final Long codTipoVeiculo,
                           @Nullable final String placaVeiculo,
                           final long dataInicial,
                           final long dataFinal,
                           final int limit,
                           final long offset,
                           final boolean resumido) throws SQLException;

    /**
     * Busca os checklists realizados por um colaborador.
     *
     * @return uma {@link List<Checklist> lista de checklists}.
     * @throws SQLException caso não seja possível realizar a busca.
     */
    List<Checklist> getByColaborador(@NotNull final Long cpf,
                                     @NotNull final Long dataInicial,
                                     @NotNull final Long dataFinal,
                                     final int limit,
                                     final long offset,
                                     final boolean resumido) throws SQLException;

    /**
     * Busca as regionais e unidades que o colaborador de código fornecido tem acesso. Isso é verificado com base na
     * permissão do colaborador (nível de acesso à informação).
     *
     * @param codColaborador Código do colaborador para o qual serão buscadas as regionais e unidades que tem acesso.
     * @return Objeto contendo as regionais e unidades que o colaborador tem acesso.
     * @throws Throwable Caso ocorrer algum erro na busca dos dados.
     */
    @NotNull
    FiltroRegionalUnidadeChecklist getRegionaisUnidadesSelecao(@NotNull final Long codColaborador) throws Throwable;

    /**
     * Método utilizado para buscar o {@link DeprecatedFarolChecklist} contendo todas as placas e as informações
     * de liberação. Caso a propriedade {@code itensCriticosRetroativos} seja marcada, o farol irá exibir as
     * informações de itens apontados nos checklists que não foram resolvidos ainda. Caso contrário, o farol exibirá
     * apenas as informações apontadas no checklist realizados no dia.
     *
     * @param codUnidade               - Código da {@link Unidade} que os dados serão buscados.
     * @param dataInicial              - Data inicial do período de filtro.
     * @param dataFinal                - Data final do período de filtro.
     * @param itensCriticosRetroativos - Valor booleano para indicar se deve-se buscar
     *                                 os itens abertos noutros checklists.
     * @return - Um objeto {@link DeprecatedFarolChecklist} contendo as placas e informações de liberação.
     * @throws Throwable - Caso algum erro acontecer na busca dos dados.
     */
    @NotNull
    DeprecatedFarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal,
                                               final boolean itensCriticosRetroativos) throws Throwable;

    /**
     * Método utilizado para identificar se os colaboradores da empresa estão liberadps para realizar o checklist de
     * diferentes unidades.
     *
     * @param codEmpresa Código da empresa que será verificado se está liberado.
     * @return <code>TRUE</code> se os colaboradores da empresa estão aptos a realizar o checklist de diferentes
     * unidades, <code>FALSE</code> caso contrário.
     * @throws Throwable Caso ocorrer algum erro na busca dos dados.
     */
    boolean getChecklistDiferentesUnidadesAtivoEmpresa(@NotNull final Long codEmpresa) throws Throwable;
}