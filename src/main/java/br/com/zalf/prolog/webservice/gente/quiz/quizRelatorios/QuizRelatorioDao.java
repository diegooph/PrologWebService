package br.com.zalf.prolog.webservice.gente.quiz.quizRelatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Created on 17/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface QuizRelatorioDao {

    /**
     * Método que busca gera um arquivo CSV contendo informações de realização de
     * quizzes, estratificados por modelo e unidade.
     *
     * @param out           - Local onde o csv será escrito
     * @param codModeloQuiz - Código do modelo de quiz
     * @param codUnidade    - Código da unidade a qual será filtrado os dados
     * @param dataInicial   - Data incial do período de busca
     * @param dataFinal     - Data final do período de busca
     * @throws IOException  - Caso algum erro de escrita aconteça
     * @throws SQLException - Caso algum erro de busca no SQL aconteça
     */
    void getEstratificacaoRealizacaoQuizCsv(OutputStream out,
                                            String codModeloQuiz,
                                            Long codUnidade,
                                            long dataInicial,
                                            long dataFinal) throws IOException, SQLException;

    /**
     * Método que cria um {@link Report} contendo informações de realização de
     * quizzes, estratificados por modelo e unidade.
     *
     * @param codModeloQuiz - Código do modelo de quiz
     * @param codUnidade    - Código da unidade a qual será filtrado os dados
     * @param dataInicial   - Data incial do período de busca
     * @param dataFinal     - Data final do período de busca
     * @return              - Um objeto {@link Report} contendo as informações filtradas
     * @throws SQLException - Caso algum erro de busca no SQL aconteça
     */
    Report getEstratificacaoRealizacaoQuizReport(String codModeloQuiz,
                                                 Long codUnidade,
                                                 long dataInicial,
                                                 long dataFinal) throws SQLException;

    /**
     * Método que cria um arquivo CSV contendo informações de realizações de quiz estratificados
     * por cargos da unidade.
     *
     * @param out           - Local onde o csv será escrito
     * @param codUnidade    - Código da unidade a qual será filtrado os dados
     * @param codModeloQuiz - Código do modelo de quiz
     * @throws SQLException - Caso algum erro de busca no SQL aconteça
     * @throws IOException  - Caso algum erro de escrita aconteça
     */
    void getRealizacaoQuizByCargoCsv(OutputStream out,
                                     Long codUnidade,
                                     String codModeloQuiz) throws SQLException, IOException;

    /**
     * Método que cria um {@link Report} contendo informações de realizações de quiz estratificados
     * por cargos da unidade.
     *
     * @param codUnidade    - Código da unidade a qual será filtrado os dados
     * @param codModeloQuiz - Código do modelo de quiz
     * @return              - Um objeto {@link Report} contendo as informações filtradas
     * @throws SQLException - Caso algum erro de busca no SQL aconteça
     */
    Report getRealizacaoQuizByCargoReport(Long codUnidade, String codModeloQuiz) throws SQLException;

    /**
     * Método que cria um arquivo CSV contendo informações de respostas certas de quizzes de um certo
     * modelo, filtrados por unidade.
     *
     * @param out           - Local onde o csv será escrito
     * @param codUnidade    - Código da unidade a qual será filtrado os dados
     * @param codModeloQuiz - Código do modelo de quiz
     * @throws SQLException - Caso algum erro de busca no SQL aconteça
     * @throws IOException  - Caso algum erro de escrita aconteça
     */
    void getEstratificacaoQuizRespostasCsv(OutputStream out,
                                           Long codUnidade,
                                           String codModeloQuiz) throws SQLException, IOException;

    /**
     * Método que cria um {@link Report} contendo informações de respostas certas de quizzes de um certo
     * modelo, filtrados por unidade.
     *
     * @param codUnidade    - Código da unidade a qual será filtrado os dados
     * @param codModeloQuiz - Código do modelo de quiz
     * @return              - Um objeto {@link Report} contendo as informações filtradas
     * @throws SQLException - Caso algum erro de busca no SQL aconteça
     */
    Report getEstratificacaoQuizRespostasReport(Long codUnidade, String codModeloQuiz) throws SQLException;

    /**
     * Método que cria um arquivo CSV contendo informações gerais dos quizzes, filtrados por unidade de acordo
     * com um período aplicado.
     *
     * @param out           - Local onde o csv será escrito
     * @param codUnidade    - Código da unidade a qual será filtrado os dados
     * @param dataInicial   - Data incial do período de busca
     * @param dataFinal     - Data final do período de busca
     * @throws IOException  - Caso algum erro de escrita aconteça
     * @throws SQLException - Caso algum erro de busca no SQL aconteça
     */
    void getExtratoGeralCsv(OutputStream out,
                            Long codUnidade,
                            long dataInicial,
                            long dataFinal) throws SQLException, IOException;

    /**
     * Método que cria um {@link Report} contendo informações gerais dos quizzes, filtrados por unidade de acordo
     * com um período aplicado.
     *
     * @param codUnidade    - Código da unidade a qual será filtrado os dados
     * @param dataInicial   - Data incial do período de busca
     * @param dataFinal     - Data final do período de busca
     * @return              - Um objeto {@link Report} contendo as informações filtradas
     * @throws SQLException - Caso algum erro de busca no SQL aconteça
     */
    Report getExtratoGeralReport (Long codUnidade, long dataInicial, long dataFinal) throws SQLException;
}
