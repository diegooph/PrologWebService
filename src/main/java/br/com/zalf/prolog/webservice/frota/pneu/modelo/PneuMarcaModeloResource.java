package br.com.zalf.prolog.webservice.frota.pneu.modelo;


import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 18/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@DebugLog
@Path("pneus")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class PneuMarcaModeloResource {
    @NotNull
    private final PneuMarcaModeloService service = new PneuMarcaModeloService();

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR, Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/marcas")
    public List<PneuMarcaListagem> getListagemMarcasPneu() {
        return service.getListagemMarcasPneu();
    }

    @POST
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR})
    @Path("/modelos")
    public ResponseWithCod insertModeloPneu(@Valid final PneuModeloInsercao pneuModeloInsercao) {
        return service.insertModeloPneu(pneuModeloInsercao);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR})
    @Path("/modelos")
    public ResponseWithCod updateModeloPneu(
            @HeaderParam("Authorization") final String userToken,
            @Valid final PneuModeloEdicao pneuModeloEdicao) {
        return service.updateModeloPneu(pneuModeloEdicao);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR, Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/modelos")
    public List<PneuModeloListagem> getListagemModelosPneu(@QueryParam("codEmpresa") Long codEmpresa) {
        return service.getListagemModelosPneu(codEmpresa);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR, Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/modelos/{codModelo}")
    public PneuModeloVisualizacao getModeloPneu(@PathParam("codModelo") Long codModelo) {
        return service.getModeloPneu(codModelo);
    }
}