package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogErrorCodes;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import com.sun.istack.internal.NotNull;

import javax.ws.rs.core.Response;

public class VersaoDadosIntervaloDesatualizadaException extends ProLogException {

    @NotNull
    private final Long codUnidade;
    private final long versaoDados;

    public VersaoDadosIntervaloDesatualizadaException(@NotNull final Long codUnidade, final long versaoDados) {
        this.codUnidade = codUnidade;
        this.versaoDados = versaoDados;
    }

    @Override
    public int getHttpStatusCode() {
        return Response.Status.PRECONDITION_FAILED.getStatusCode();
    }

    @Override
    public int getApplicationErrorCode() {
        return ProLogErrorCodes.VERSAO_DADOS_INTERVALO_DESATUALIZADA.errorCode();
    }

    @Override
    public String getMessage() {
        return "A versão dos dados: " + versaoDados + " está desatualzada para a unidade: " + codUnidade;
    }

    @Override
    public String getDeveloperMessage() {
        return "Consulte a tabela INTERVALO_UNIDADE para saber qual a versão atual";
    }

    @Override
    public String getMoreInfoLink() {
        return null;
    }
}
