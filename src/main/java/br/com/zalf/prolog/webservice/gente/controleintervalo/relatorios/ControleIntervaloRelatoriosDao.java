package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Clt;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Created by Zart on 28/08/2017.
 */
public interface ControleIntervaloRelatoriosDao {

    /**
     * Relatório que estratifica todos os intervalos realizadas em um período, uma linha para cada intervalo
     *
     * @param out         OutputStream
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @param cpf         cpf (opcional)
     * @throws SQLException
     * @throws IOException
     */
    void getIntervalosCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica todos os intervalos realizadas em um período, uma linha para cada intervalo
     *
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @param cpf         cpf (opcional)
     * @return um Report
     * @throws SQLException
     * @throws IOException
     */
    @NotNull
    Report getIntervalosReport(Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica todos os mapas, seus colaboradores e intervalos realizados por cada um
     *
     * @param out         OutputStream
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @throws SQLException
     * @throws IOException
     */
    void getIntervalosMapasCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica todos os mapas, seus colaboradores e intervalos realizados por cada um
     *
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @return um Report
     * @throws SQLException
     * @throws IOException
     */
    @NotNull
    Report getIntervalosMapasReport(Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica a aderência por dia, mostrando valores totais, por motorista e por ajudante
     *
     * @param out         OutputStream
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @throws SQLException
     * @throws IOException
     */
    void getAderenciaIntervalosDiariaCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica a aderência por dia, mostrando valores totais, por motorista e por ajudante
     *
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @return um Report
     * @throws SQLException
     * @throws IOException
     */
    @NotNull
    Report getAderenciaIntervalosDiariaReport(Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica a aderência por colaborador
     *
     * @param out         OutputStream
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @throws SQLException
     * @throws IOException
     */
    void getAderenciaIntervalosColaboradorCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal,
                                              String cpf)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica a aderência por colaborador
     *
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @return um Report
     * @throws SQLException
     */
    @NotNull
    Report getAderenciaIntervalosColaboradorReport(Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException;


    void getRelatorioPadraoPortaria1510Csv(@NotNull final OutputStream out,
                                           @NotNull final Long codUnidade,
                                           @NotNull final Long codTipoIntervalo,
                                           @NotNull final String cpf,
                                           @NotNull final LocalDate dataInicial,
                                           @NotNull final LocalDate dataFinal) throws SQLException, IOException;

    /**
     * Método utilizado para gerar um relatório contendo todas as marcações de intervalo do usuário num dado período
     * filtrado. Essas marcações são estratificadas por dia, assim é possível saber o quanto tempo o usuário passou
     * em um certo tipo de intervalo.
     * Este relatório faz contabiliza o tempo que o usuário passou nos intervalos em horas noturnas.
     * Marcações que tiverem seu tempo percorrido durante um range específico de tempo
     * -{@link Clt#RANGE_HORAS_NOTURNAS}- serão somadas para a geração deste relatório.
     * <p>
     * Para que esse relatório seja gerado com informações de todos os {@link Colaborador}es o atributo {@code cpf}
     * deve ser "%".
     * <p>
     * Para que esse relatório seja gerado com informações de todos os {@link Intervalo}s
     * o atributo {@code codTipoIntervalo} deve ser "%".
     *
     * @param codUnidade       - Código da {@link Unidade} de onde os dados serão filtrados.
     * @param codTipoIntervalo - Código do {@link TipoIntervalo} que os dados serão filtrados
     * @param cpf              - Identificador do {@link Colaborador} para buscar os dados.
     * @param dataInicial      - Data inicial do período de filtro.
     * @param dataFinal        - Data final do período de filtro.
     * @return - Uma lista {@link FolhaPontoRelatorio} contendo todas as informações filtradas.
     * @throws Throwable - Se algum erro na geração do relatório ocorrer.
     */
    @NotNull
    List<FolhaPontoRelatorio> getFolhaPontoRelatorio(@NotNull final Long codUnidade,
                                                     @NotNull final String codTipoIntervalo,
                                                     @NotNull final String cpf,
                                                     @NotNull final LocalDate dataInicial,
                                                     @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getMarcacoesComparandoEscalaDiariaReport(@NotNull final Long codUnidade,
                                                    @NotNull final Long codTipoIntervalo,
                                                    @NotNull final LocalDate dataInicial,
                                                    @NotNull final LocalDate dataFinal) throws SQLException;

    void getMarcacoesComparandoEscalaDiariaCsv(@NotNull final OutputStream out,
                                               @NotNull final Long codUnidade,
                                               @NotNull final Long codTipoIntervalo,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal) throws SQLException, IOException;

    /**
     * Método para gerar um relatório contendo a soma do período de todos os intervalos marcados pelos colaboradores
     * da pertencentes ao {@code codUnidade}. Para buscar a soma de todos os {@link TipoIntervalo}s o atributo
     * {@code codTipoIntervalo} deve ser "%". Este método gera um arquivo CSV para fins de exportação.
     *
     * @param out              - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidade       - Código da {@link Unidade} de onde os dados serão filtrados.
     * @param codTipoIntervalo - Código do {@link TipoIntervalo} que os dados serão filtrados
     * @param dataInicial      - Data inicial do período de filtro.
     * @param dataFinal        - Data final do período de filtro.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     * @throws IOException  - Se algum erro na escrita dos dados ocorrer.
     */
    void getTotalTempoByTipoIntervaloCsv(@NotNull final OutputStream out,
                                         @NotNull final Long codUnidade,
                                         @NotNull final String codTipoIntervalo,
                                         @NotNull final LocalDate dataInicial,
                                         @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método para gerar um relatório contendo a soma do período de todos os intervalos marcados pelos colaboradores
     * da pertencentes ao {@code codUnidade}. Para buscar a soma de todos os {@link TipoIntervalo}s o atributo
     * {@code codTipoIntervalo} deve ser "%". Este método gera um objeto {@link Report} para fins de visualização
     * dos dados na aplicação.
     *
     * @param codUnidade       - Código da {@link Unidade} de onde os dados serão filtrados.
     * @param codTipoIntervalo - Código do {@link TipoIntervalo} que os dados serão filtrados
     * @param dataInicial      - Data inicial do período de filtro.
     * @param dataFinal        - Data final do período de filtro.
     * @return - Um objeto {@link Report} com os dados filtrados.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    @NotNull
    Report getTotalTempoByTipoIntervaloReport(@NotNull final Long codUnidade,
                                              @NotNull final String codTipoIntervalo,
                                              @NotNull final LocalDate dataInicial,
                                              @NotNull final LocalDate dataFinal) throws SQLException;
}