package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created on 21/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class VeiculoSemDiagramaException extends ProLogException {
    @NotNull
    private final List<TipoVeiculoDiagrama> tiposVeiculoSemDiagrama;

    public VeiculoSemDiagramaException(@NotNull final String message,
                                       @NotNull final List<TipoVeiculoDiagrama> tiposVeiculoSemDiagrama) {
        super(
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                ProLogErrorCodes.VEICULO_SEM_DIAGRAMA.errorCode(),
                message);
        this.tiposVeiculoSemDiagrama = tiposVeiculoSemDiagrama;
    }

    @NotNull
    public List<TipoVeiculoDiagrama> getTiposVeiculoSemDiagrama() {
        return tiposVeiculoSemDiagrama;
    }
}
