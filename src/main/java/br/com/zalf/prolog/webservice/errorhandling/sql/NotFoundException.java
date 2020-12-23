package br.com.zalf.prolog.webservice.errorhandling.sql;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;

import javax.ws.rs.core.Response;

/**
 * Created on 2020-11-09
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class NotFoundException extends DataAccessException {
    private static final Response.Status DEFAULT_STATUS = Response.Status.BAD_REQUEST;
    private static final ProLogErrorCodes DEFAULT_ERROR_CODE = ProLogErrorCodes.CHAVE_NAO_EXISTENTE;

    public NotFoundException() {
        super(DEFAULT_STATUS.getStatusCode(),
              DEFAULT_ERROR_CODE.errorCode(),
              "Registro não encontrado.",
              "Chave de acesso ao registro não consta na base de dados.",
              false);
    }
}
