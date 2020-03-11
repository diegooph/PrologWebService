package br.com.zalf.prolog.webservice.gente.controlejornada;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.unidade._model.Unidade;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Created on 08/11/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ControleJornadaDao {
    /**
     * Método utilizado para salvar no Banco de Dados uma Marcação.
     *
     * @param intervaloMarcacao Uma {@link IntervaloMarcacao marcação} contendo os dados a serem inseridos.
     * @return Código único que identifica a marcação no banco de dados.
     * @throws Throwable Se ocorrer algum erro na sincronização.
     */
    @NotNull
    Long insertMarcacaoIntervalo(@NotNull final IntervaloMarcacao intervaloMarcacao) throws Throwable;

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
     * @throws Throwable Se algum erro ocorrer na busca da marcação em andamento.
     */
    @Nullable
    IntervaloMarcacao getUltimaMarcacaoInicioNaoFechada(@NotNull final Long codUnidade,
                                                        @NotNull final Long cpfColaborador,
                                                        @NotNull final Long codTipoIntervalo) throws Throwable;

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

    @NotNull
    List<Intervalo> getMarcacoesIntervaloColaborador(@NotNull final Long codUnidade,
                                                     @NotNull final Long cpf,
                                                     @NotNull final String codTipo,
                                                     final long limit,
                                                     final long offset) throws Throwable;

    /**
     * Busca uma lista de {@link MarcacaoListagem marcações} para exibição na listagem respeitando os parâmetros de
     * filtro informados. Marcações incompletas (sem fim ou início) também serão buscadas.
     *
     * @param codUnidade  Código da {@link Unidade unidade} de onde será filtrado as marcações.
     * @param cpf         CPF do {@link Colaborador colaborador} do qual se quer buscar as marcações ou
     *                    <code>NULL</code> para ignorar esse filtro.
     * @param codTipo     Código do tipo de marcação do qual se quer buscar as marcações ou <code>NULL</code> para
     *                    ignorar esse filtro.
     * @param dataInicial Data inicial a partir da qual se quer buscar as marcações. É inclusiva.
     * @param dataFinal   Data final até a qual se quer buscar as marcações. É inclusiva.
     * @return Uma lista de {@link MarcacaoListagem marcações} respeitando os parâmetros de filtro.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    List<MarcacaoListagem> getMarcacoesColaboradorPorData(@NotNull final Long codUnidade,
                                                          @Nullable final Long cpf,
                                                          @Nullable final Long codTipo,
                                                          @NotNull final LocalDate dataInicial,
                                                          @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método específico para validar o Token de autenticação para sincronização de marcacões de jornada.
     *
     * @param tokenMarcacaoJornada Token de sincronização de Marcações de Jornada.
     * @return Valor booleano que representa se o usuário está apto a sincronizar os dados.
     * @throws Throwable Caso não seja possível verificar a existência do Token no banco de dados.
     */
    boolean verifyIfTokenMarcacaoExists(@NotNull final String tokenMarcacaoJornada) throws Throwable;

    @NotNull
    Optional<DadosMarcacaoUnidade> getDadosMarcacaoUnidade(@NotNull final Long codUnidade) throws Throwable;
}