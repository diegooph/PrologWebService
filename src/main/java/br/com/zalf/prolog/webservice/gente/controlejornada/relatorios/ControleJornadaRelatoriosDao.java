package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios;

import br.com.zalf.prolog.webservice.Filtros;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Clt;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoRelatorio;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada.FolhaPontoJornadaRelatorio;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
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
public interface ControleJornadaRelatoriosDao {

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
    void getMarcacoesDiariasCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
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
    Report getMarcacoesDiariasReport(Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException;

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
     * @param codUnidade                Código da {@link Unidade} de onde os dados serão filtrados.
     * @param codTipoIntervalo          Código do {@link TipoMarcacao} que os dados serão filtrados
     * @param cpf                       Identificador do {@link Colaborador} para buscar os dados.
     * @param dataInicial               Data inicial do período de filtro.
     * @param dataFinal                 Data final do período de filtro.
     * @param apenasColaboradoresAtivos Indica se a busca deve ser feita considerando apenas os colaboradores ativos.
     * @return - Uma lista {@link FolhaPontoRelatorio} contendo todas as informações filtradas.
     * @throws Throwable - Se algum erro na geração do relatório ocorrer.
     */
    @NotNull
    List<FolhaPontoRelatorio> getFolhaPontoRelatorio(@NotNull final Long codUnidade,
                                                     @NotNull final String codTipoIntervalo,
                                                     @NotNull final String cpf,
                                                     @NotNull final LocalDate dataInicial,
                                                     @NotNull final LocalDate dataFinal,
                                                     final boolean apenasColaboradoresAtivos) throws Throwable;

    /**
     * Relatório que agrupa todas as marcações dos colaboradores dentro de marcações do tipo Jornada definidas na
     * Unidade.
     * O relatório é gerado com base nos filtros recebidos por parâmetro.
     * <p>
     * Para filtrar por todos os tipos de marcações o atributo {@code codTipoIntervalo} deve ser enviado
     * como {@link Filtros#FILTRO_TODOS}.
     * Para filtrar por todos os colaboradores o atributo {@code cpf} deve ser enviado
     * como {@link Filtros#FILTRO_TODOS}.
     *
     * @param codUnidade                Código da {@link Unidade Unidade} de busca do relatório.
     * @param codTipoIntervalo          Código do {@link TipoMarcacao Tipo de Marcação}.
     * @param cpf                       {@link Colaborador#cpf CPF} do colaborador que serão buscados os dados.
     * @param dataInicial               Data Inicial do período do relatório.
     * @param dataFinal                 Data Final do período do relatório.
     * @param apenasColaboradoresAtivos Indica se a busca deve ser feita considerando apenas os colaboradores ativos.
     * @return {@link List<FolhaPontoJornadaRelatorio> Relatórios} de folha de ponto de Jornada. Cada índice desta
     * lista representa um colaborador.
     * @throws Throwable Se qualquer erro ocorrer na geração do relatório.
     */
    @NotNull
    List<FolhaPontoJornadaRelatorio> getFolhaPontoJornadaRelatorio(@NotNull final Long codUnidade,
                                                                   @NotNull final String codTipoIntervalo,
                                                                   @NotNull final String cpf,
                                                                   @NotNull final LocalDate dataInicial,
                                                                   @NotNull final LocalDate dataFinal,
                                                                   final boolean apenasColaboradoresAtivos) throws Throwable;

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
     * da pertencentes ao {@code codUnidade}. Para buscar a soma de todos os {@link TipoMarcacao}s o atributo
     * {@code codTipoIntervalo} deve ser "%". Este método gera um arquivo CSV para fins de exportação.
     *
     * @param out              - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidade       - Código da {@link Unidade} de onde os dados serão filtrados.
     * @param codTipoIntervalo - Código do {@link TipoMarcacao} que os dados serão filtrados
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
     * da pertencentes ao {@code codUnidade}. Para buscar a soma de todos os {@link TipoMarcacao}s o atributo
     * {@code codTipoIntervalo} deve ser "%". Este método gera um objeto {@link Report} para fins de visualização
     * dos dados na aplicação.
     *
     * @param codUnidade       - Código da {@link Unidade} de onde os dados serão filtrados.
     * @param codTipoIntervalo - Código do {@link TipoMarcacao} que os dados serão filtrados
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

    /**
     * Método para gerar um relatório contendo as marcações em um padrão de importação.
     * A function no banco está preparada para não receber os filtros opcionais de colaborador, tipo de intervalo e
     * apenas marcações ativas.
     *
     * @param codUnidade            - Código da {@link Unidade unidade} de onde os dados serão filtrados.
     * @param codTipoIntervalo      - Código do {@link TipoMarcacao tipo de marcação} que os dados serão filtrados.
     * @param codColaborador        - Código do {@link Colaborador colaborador} que os dados serão filtrados.
     * @param apenasMarcacoesAtivas - Filtra apenas por marcações ativas.
     * @param dataInicial           - Data inicial do período de filtro.
     * @param dataFinal             - Data final do período de filtro.
     * @throws Throwable            - Se algum erro ocorrer.
     */
    void getMarcacoesExportacaoGenericaCsv(@NotNull final OutputStream out,
                                           @NotNull final Long codUnidade,
                                           final Long codTipoIntervalo,
                                           final Long codColaborador,
                                           final boolean apenasMarcacoesAtivas,
                                           @NotNull final LocalDate dataInicial,
                                           @NotNull final LocalDate dataFinal) throws Throwable;
}