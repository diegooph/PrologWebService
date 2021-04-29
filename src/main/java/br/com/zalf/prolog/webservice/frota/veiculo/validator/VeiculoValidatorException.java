package br.com.zalf.prolog.webservice.frota.veiculo.error;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.Response;

/**
 * Created on 2021-01-12
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoValidatorException extends ProLogException {

    public VeiculoValidatorException(@Nullable final String detailedMessage) {
        super(Response.Status.BAD_REQUEST.getStatusCode(),
              ProLogErrorCodes.CLIENT_SIDE_ERROR.errorCode(),
              "Erro ao realizar processo.",
              detailedMessage,
              GenericException.NO_LOGS_INTO_SENTRY);
    }
}