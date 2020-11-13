package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created on 2020-11-03
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("veiculos/acoplamentos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class VeiculoAcoplamentoResource {
    @NotNull
    private final VeiculoAcoplamentoService service = new VeiculoAcoplamentoService();
    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    @POST
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @UsedBy(platforms = {Platform.WEBSITE, Platform.ANDROID})
    @Path("/")
    public AbstractResponse insertProcessoAcoplamento(
            @Valid final VeiculoAcoplamentoProcessoRealizacao processoRealizacao) {
        final Long codProcessoInserido = service.insertProcessoAcoplamento(
                colaboradorAutenticadoProvider.get().getCodigo(),
                processoRealizacao);
        return ResponseWithCod.ok("Processo de engate/desengate realizado com sucesso!", codProcessoInserido);
    }
}
