package br.com.zalf.prolog.webservice.gente.controlejornada;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

public final class VersaoDadosIntervaloDesatualizadaException extends ProLogException {

    @NotNull
    private final Long codUnidade;
    private final long versaoDados;

    public VersaoDadosIntervaloDesatualizadaException(int statusCode,
                                                      int proLogErrorCode,
                                                      String message,
                                                      String developerMessage,
                                                      @NotNull final Long codUnidade,
                                                      final long versaoDados) {
        super(statusCode, proLogErrorCode, message, developerMessage);
        this.codUnidade = codUnidade;
        this.versaoDados = versaoDados;
    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public long getVersaoDados() {
        return versaoDados;
    }
}
