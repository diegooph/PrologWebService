package br.com.zalf.prolog.webservice.log._model;

/**
 * Created on 2020-10-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public enum LogLevel {
    /**
     * Loga todas as informações das classes {@link RequestLog} e {@link ResponseLog} exceto o BODY e os HEADERS.
     */
    BASIC,
    /**
     * Loga as informações do modo BASIC e os HEADERS do request e response.
     */
    HEADERS,
    /**
     * Loga as informações do modo BASIC e o BODY do request e response.
     */
    BODY,
    /**
     * Loga as informações do modo BASIC, os HEADERS e o BODY do request e response.
     */
    ALL
}
