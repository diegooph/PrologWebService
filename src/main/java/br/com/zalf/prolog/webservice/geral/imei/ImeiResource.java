package br.com.zalf.prolog.webservice.geral.imei;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.geral.imei.model.*;
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
@Path("imei")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ImeiResource {
    @NotNull
    private final ImeiService service = new ImeiService();

    /**
     * Esse método não verifica nenhuma permissão. Isso porque ele poderá ser utilizado como parâmetro ao inserir
     * números IMEI dos aparelhos da empresa.
     * <p>
     * Controlar todos esses usos e permissões é muito complexo, e como esse método não revela nada demais, podemos
     * deixar sem a verificação. Apenas com {@link Secured secured} verificando se tem um token válido.
     */
    @GET
    @Secured
    @Path("/marcas-celular")
    public List<MarcaCelularSelecao> getMarcasCelular() throws ProLogException {
        return service.getMarcasCelular();
    }

    @GET
    @Secured()
    @Path("/imeis-por-empresa")
    public List<Imei> getImeisPorEmpresa(
            @QueryParam("codEmpresa") @Required final Long codEmpresa) throws ProLogException {
        return service.getImeisPorEmpresa(codEmpresa);
    }
}