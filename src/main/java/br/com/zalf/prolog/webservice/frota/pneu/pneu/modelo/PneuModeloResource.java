package br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo;


import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model.PneuMarcaModelo;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model.PneuModeloInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model.PneuModeloEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model.PneuModeloVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 18/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("pneus")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class PneuModeloResource {
    @NotNull
    private final PneuModeloService service = new PneuModeloService();

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR, Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/marcaModelos/{codEmpresa}")
    public List<PneuMarcaModelo> listagemMarcasModelosPneu(@PathParam("codEmpresa") Long codEmpresa)
            throws ProLogException {
        return service.listagemMarcasModelosPneu(codEmpresa);
    }

    @POST
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR})
    @Path("/modelo-insert")
    public AbstractResponse insertModeloPneu(@Required final PneuModeloInsercao pneuModeloInsercao)
            throws ProLogException {
        return service.insertModeloPneu(pneuModeloInsercao);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR})
    @Path("/pneu-update")
    public ResponseWithCod updateModeloPneu(
            @HeaderParam("Authorization") @Required final String userToken,
            @Required final PneuModeloEdicao pneuModeloEdicao) throws ProLogException {
        return service.updateModeloPneu(pneuModeloEdicao);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR, Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/modelos/{codModelo}")
    public PneuModeloVisualizacao getModeloPneu(@PathParam("codModelo") Long codModelo) throws ProLogException {
        return service.getModeloPneu(codModelo);
    }
}