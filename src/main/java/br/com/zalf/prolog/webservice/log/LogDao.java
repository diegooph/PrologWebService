package br.com.zalf.prolog.webservice.log;

import br.com.zalf.prolog.webservice.integracao.logger._model.RequestLogApi;
import br.com.zalf.prolog.webservice.integracao.logger._model.RequestLogProlog;
import br.com.zalf.prolog.webservice.integracao.logger._model.ResponseLogApi;
import br.com.zalf.prolog.webservice.integracao.logger._model.ResponseLogProlog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

/**
 * Created by didi on 9/15/16.
 */
public interface LogDao {

    /**
     * Método utilizado para loggar informações pertinentes, recebidas das Aplicações do ProLog.
     * Pode ser utilizada tanto pelo Sistema Web quanto pelo Aplicativo.
     *
     * @param log           Descrição do log que será salvo no banco de dados.
     * @param identificador Identificador do log para tornar fácil a identificação.
     * @return <code>TRUE</code> se a operação for sucesso, <code>FALSE</code> caso contrário.
     * @throws SQLException Saso ocorrer qualquer erro salvamento do log.
     */
    boolean insert(@NotNull final String log, @NotNull final String identificador) throws SQLException;

    /**
     * Método utilizado para salvar as informações referentes às comunicações entre os sistemas integrados.
     * Esse método salva todas as informações recebidas através de requests, como também todas as informações devolvidas
     * através de responses.
     * Método exclusivo para logar requisições que são recebidas nos endpoints de integração.
     *
     * @param tokenRequisicao Token da empresa que realizou a requisição.
     * @param requestLog      Objeto que contém as informações capturadas da requisição.
     * @param responseLog     Objeto que contém as informações enviadas como resposta da integração.
     * @throws Throwable Se algum erro ocorrer no processo de salvar os dados.
     */
    void insertRequestResponseLogApi(@NotNull final String tokenRequisicao,
                                     @NotNull final RequestLogApi requestLog,
                                     @Nullable final ResponseLogApi responseLog) throws Throwable;

    /**
     * Método utilizado para salvar as informações referentes às comunicações entre os sistemas integrados.
     * Esse método salva todas as informações enviadas através de requests, como também todas as informações recebidas
     * através de responses.
     * Método exclusivo para logar requisições disparadas pelo Prolog para endpoints de integração.
     *
     * @param requestLog  Objeto que contém as informações capturadas da requisição feita pelo Prolog.
     * @param responseLog Objeto que contém as informações recebidas pelo Prolog como resposta da integração.
     * @throws Throwable Se algum erro ocorrer no processo de salvar os dados.
     */
    void insertRequestResponseLogProlog(@NotNull final RequestLogProlog requestLog,
                                        @Nullable final ResponseLogProlog responseLog) throws Throwable;
}
