package br.com.zalf.prolog.webservice.geral.dispositivo_movel;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.geral.dispositivo_movel.model.DispositivoMovel;
import br.com.zalf.prolog.webservice.geral.dispositivo_movel.model.DispositivoMovelInsercao;
import br.com.zalf.prolog.webservice.geral.dispositivo_movel.model.MarcaDispositivoMovelSelecao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
@Path("dispositivos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DispositivoMovelResource {
    @NotNull
    private final DispositivoMovelService service = new DispositivoMovelService();

    /**
     * Esse método não verifica nenhuma permissão. Isso porque ele poderá ser utilizado como parâmetro ao inserir
     * os dispositivos da empresa.
     * <p>
     * Controlar todos esses usos e permissões é muito complexo, e como esse método não revela nada demais, podemos
     * deixar sem a verificação. Apenas com {@link Secured secured} verificando se tem um token válido.
     */
    @GET
    @Secured
    @Path("/marcas-celular")
    public List<MarcaDispositivoMovelSelecao> getMarcasDispositivos() throws ProLogException {
        return service.getMarcasDispositivos();
    }

    @GET
    @Secured(permissions = {Pilares.Geral.DispositivosMoveis.GESTAO})
    @Path("/dispositivos-por-empresa")
    public List<DispositivoMovel> getDispositivosPorEmpresa(
            @QueryParam("codEmpresa") @Required final Long codEmpresa) throws ProLogException {
        return service.getDispositivosPorEmpresa(codEmpresa);
    }

    @GET
    @Secured(permissions = {Pilares.Geral.DispositivosMoveis.GESTAO})
    @Path("/dispositivo")
    public DispositivoMovel getDispositivo(
            @QueryParam("codEmpresa") @Required final Long codEmpresa,
            @QueryParam("codDispositivo") @Required final Long codDispositivo) throws ProLogException {
        return service.getDispositivoMovel(codEmpresa, codDispositivo);
    }

    @PUT
    @Secured(permissions = {Pilares.Geral.DispositivosMoveis.GESTAO})
    @Path("/dispositivo")
    public Response updateDispositivoMovel(@Required final DispositivoMovel dispositivo) throws ProLogException {
        return service.updateDispositivoMovel(dispositivo);
    }

    @POST
    @Secured(permissions = {Pilares.Geral.DispositivosMoveis.GESTAO})
    @Path("/dispositivo")
    public AbstractResponse insertDispositivoMovel(@Required DispositivoMovelInsercao dispositivo) {
        return service.insertDispositivoMovel(dispositivo);
    }

    @DELETE
    @Secured(permissions = {Pilares.Geral.DispositivosMoveis.GESTAO})
    @Path("/dispositivo/{codEmpresa}/{codDispositivo}")
    public Response deleteDispositivoMovel(
            @PathParam("codEmpresa") @Required final Long codEmpresa,
            @PathParam("codDispositivo") @Required final Long codDispositivo) throws ProLogException {
        return service.deleteDispositivoMovel(codEmpresa, codDispositivo);
    }
}