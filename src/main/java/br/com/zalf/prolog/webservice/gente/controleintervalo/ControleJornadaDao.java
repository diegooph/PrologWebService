package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.IntervaloMarcacao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoInicioFim;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoMarcacao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Created on 08/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface ControleJornadaDao {

    /**
     * Método utilizado para sincronizar no Banco de Dados uma Marcação.
     *
     * @param intervaloMarcacao Uma {@link IntervaloMarcacao marcação} contendo os dados a serem inseridos.
     * @return Código único que identifica a marcação no banco de dados.
     * @throws SQLException Se ocorrer algum erro na sincronização.
     */
    @NotNull
    Long insertMarcacaoIntervalo(@NotNull final IntervaloMarcacao intervaloMarcacao) throws Throwable;

    /**
     * Método utilizado para buscar a versão em que os dados dos Intervalos se encontram.
     *
     * @param codUnidade Código da unidade de onde a versão será buscada.
     * @return Valor {@link Long} que representa a versão atual dos dados de Intervalos.
     * @throws SQLException Se algum erro ocorrer na busca dos dados.
     */
    @NotNull
    Optional<Long> getVersaoDadosIntervaloByUnidade(@NotNull final Long codUnidade) throws SQLException;

    /**
     * Método utilizado para buscar uma marcação que esteja em andamento, ou seja, uma marcação que ainda
     * não teve seu fim marcado pelo colaborador.
     * Este método realiza a busca com base no {@code cpfColaborador} e no {@code codTipoIntervalo} para
     * analisar se alguma marcação de um tipo específico do colaborador está em andamento.
     *
     * @param codUnidade       Código da {@link Unidade} de onde será filtrado as marcações.
     * @param cpfColaborador   CPF do {@link Colaborador} que realizou a marcação.
     * @param codTipoIntervalo Código do tipo {@link Intervalo} que será buscado.
     * @return Um {@link IntervaloMarcacao} caso existir ou null.
     * @throws SQLException Se algum erro ocorrer na busca da marcação em andamento.
     */
    @Nullable
    IntervaloMarcacao getUltimaMarcacaoInicioNaoFechada(@NotNull final Long codUnidade,
                                                        @NotNull final Long cpfColaborador,
                                                        @NotNull final Long codTipoIntervalo) throws SQLException;

    /**
     * Método utilizado para listar as marcações do usuário através do {@code cpfColaborador}.
     * Este método irá listar todas as marcações realizadas pelo colaborador, inclusive as marcações editadas
     * ou inseridas pelos seus supervisores.
     *
     * @param codUnidade      Código da {@link Unidade} do colaborador.
     * @param cpfColaborador  CPF do {@link Colaborador}.
     * @param codTipoIntevalo Código do tipo {@link Intervalo} que será buscado.
     * @param limit           Limite de valores retornados na busca.
     * @param offset          Valor de início da busca.
     * @return Uma {@link List<Intervalo>} contendo os intervalos do colaborador e aqueles inseridos em nome dele.
     * @throws SQLException Caso algum erro aconteça na busca.
     */
    @NotNull
    List<Intervalo> getMarcacoesIntervaloColaborador(@NotNull final Long codUnidade,
                                                     @NotNull final Long cpfColaborador,
                                                     @NotNull final String codTipoIntevalo,
                                                     final long limit,
                                                     final long offset) throws SQLException;

    /**
     * Método utilizado para inserir um novo {@link TipoMarcacao}.
     *
     * @param tipoIntervalo O {@link TipoMarcacao} que será inserido.
     * @param listener      Listener da classe {@link DadosIntervaloChangedListener}
     *                      utilizado para notificar alterações nos tipos de intervalos.
     * @return Código do {@link TipoMarcacao} recém inserido.
     * @throws Throwable Caso algum erro na inserção ocorrer.
     */
    @NotNull
    Long insertTipoIntervalo(@NotNull final TipoMarcacao tipoIntervalo,
                             @NotNull final DadosIntervaloChangedListener listener) throws Throwable;

    /**
     * Método utilizado para listar os {@link TipoMarcacao} de uma {@link Unidade}.
     * Este método filtra por {@link TipoMarcacao#ativo} e {@link TipoMarcacao#cargos} através das
     * flags {@code apenasAtivos} e {@code withCargos} respectivamente.
     *
     * @param codUnidade   Código da {@link Unidade} dos tipos sendo filtrados.
     * @param apenasAtivos Se TRUE então apenas os tipos ativos serão retornados, caso FALSE todos.
     * @param withCargos   Se TRUE então será retornado os cargos que podem marcar esse intervalo,
     *                     caso FALSE então cargos serão NULL.
     * @return Uma {@link List<TipoMarcacao>} com os tipos de marcações da unidade.
     * @throws SQLException Caso algum erro ocorrer.
     */
    @NotNull
    List<TipoMarcacao> getTiposIntervalosByUnidade(@NotNull final Long codUnidade,
                                                   final boolean apenasAtivos,
                                                   final boolean withCargos) throws SQLException;

    /**
     * Método utilizado para buscar as informações de um {@link TipoMarcacao} específico
     *
     * @param codUnidade       Código da {@link Unidade} que o tipo pertence.
     * @param codTipoIntervalo Código do {@link TipoMarcacao} que será buscado.
     * @return Um {@link TipoMarcacao} com seus atributos.
     * @throws SQLException Se algum erro ocorrer na busca.
     */
    @NotNull
    TipoMarcacao getTipoIntervalo(@NotNull final Long codUnidade,
                                  @NotNull final Long codTipoIntervalo) throws SQLException;

    /**
     * Método utilizado para atualizar as informações de um {@link TipoMarcacao}.
     *
     * @param tipoIntervalo {@link TipoMarcacao} contendo as novas informações.
     * @param listener      Listener da classe {@link DadosIntervaloChangedListener}
     *                      utilizado para notificar alterações nos tipos de intervalos.
     * @throws Throwable Se algum erro ocorrer durante a atualização.
     */
    void updateTipoIntervalo(@NotNull final TipoMarcacao tipoIntervalo,
                             @NotNull final DadosIntervaloChangedListener listener) throws Throwable;

    /**
     * Método utilizado para inativar ou ativar um {@link TipoMarcacao}.
     *
     * @param codUnidade       Código da {@link Unidade} que o tipo pertence.
     * @param codTipoIntervalo Código do {@link TipoMarcacao} que será ativado ou inativado.
     * @param tipoIntervalo    {@link TipoMarcacao} contendo o status ativo ou inativo.
     * @param listener         Listener da classe {@link DadosIntervaloChangedListener}
     *                         utilizado para notificar alterações nos tipos de intervalos.
     * @throws Throwable Se algum erro ocorrer durante a atualização.
     */
    void updateStatusAtivoTipoIntervalo(@NotNull final Long codUnidade,
                                        @NotNull final Long codTipoIntervalo,
                                        @NotNull final TipoMarcacao tipoIntervalo,
                                        @NotNull final DadosIntervaloChangedListener listener) throws Throwable;

    /**
     * Método utilizado para inserir ó código de uma marcação na tabela de INICIO ou FIM
     * dependendo do {@code tipoInicioFim} recebido por parâmetro.
     *
     * @param conn                {@link Connection} a ser utilizada para realizar esta operação.
     * @param codMarcacaoInserida Código da marcação que deve ser salvo.
     * @param tipoInicioFim       {@link TipoInicioFim#MARCACAO_INICIO} ou {@link TipoInicioFim#MARCACAO_FIM}
     * @throws Throwable Caso algum erro ocorrer na operação.
     */
    void insereMarcacaoInicioOuFim(@NotNull final Connection conn,
                                   @NotNull final Long codMarcacaoInserida,
                                   @NotNull final TipoInicioFim tipoInicioFim) throws Throwable;

    /**
     * Método utilizado para salvar o vínculo entre uma marcação de
     * {@link TipoInicioFim#MARCACAO_INICIO} e {@link TipoInicioFim#MARCACAO_FIM}.
     *
     * @param conn              {@link Connection} a ser utilizada para realizar este vínculo.
     * @param codMarcacaoInicio Código da {@link TipoInicioFim#MARCACAO_INICIO} que será inserida.
     * @param codMarcacaoFim    Código da {@link TipoInicioFim#MARCACAO_FIM} que será inserida.
     * @throws Throwable Caso algum erro ocorrer na criação do vínculo.
     */
    void insereVinculoInicioFim(@NotNull final Connection conn,
                                @NotNull final Long codMarcacaoInicio,
                                @NotNull final Long codMarcacaoFim) throws Throwable;
}