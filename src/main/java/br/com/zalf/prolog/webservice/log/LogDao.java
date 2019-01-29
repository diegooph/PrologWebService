package br.com.zalf.prolog.webservice.log;

import br.com.zalf.prolog.webservice.integracao.logger.LogRequisicao;
import org.jetbrains.annotations.NotNull;

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
     * Método utilizado para registrar no banco de dados, ações pertinentes à acessos aos
     * métodos disponibilizados pelo ProLog para parceiros, via integração.
     * <p>
     * As informações para serem salvas podem visualizadas no objeto {@link LogRequisicao log}.
     *
     * @param tokenRequisicao Token utilizado na requisição.
     * @param logRequisicao   Objeto que contém as informações a serem armazenadas.
     * @throws Throwable Se ocorrer algum problema ao salvar os dados.
     */
    void insertRequestLog(@NotNull final String tokenRequisicao,
                          @NotNull final LogRequisicao logRequisicao) throws Throwable;
}
