package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.Response;

public class VeiculoAcoplamentoValidatorException extends ProLogException {

    public VeiculoAcoplamentoValidatorException(@Nullable final String detailedMessage) {
        super(Response.Status.BAD_REQUEST.getStatusCode(),
              ProLogErrorCodes.CLIENT_SIDE_ERROR.errorCode(),
              "Erro ao realizar processo de acoplamento.",
              detailedMessage,
              GenericException.NO_LOGS_INTO_SENTRY);
    }
}
