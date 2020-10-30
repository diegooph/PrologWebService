package br.com.zalf.prolog.webservice.log;

import br.com.zalf.prolog.webservice.log._model.RequestLog;
import br.com.zalf.prolog.webservice.log._model.ResponseLog;
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
     * Método utilizado para salvar as informações referentes às comunicações entre os sistemas integrados e também
     * requisições feitas de nosso front.
     * Esse método salva todas as informações enviadas através de requests, como também todas as informações recebidas
     * através de responses.
     *
     * @param requestLog  Objeto que contém as informações capturadas da requisição feita pelo Prolog.
     * @param responseLog Objeto que contém as informações recebidas pelo Prolog como resposta da integração.
     * @throws Throwable Se algum erro ocorrer no processo de salvar os dados.
     */
    void insertRequestResponseLog(@NotNull final RequestLog requestLog,
                                  @Nullable final ResponseLog responseLog) throws Throwable;
}
