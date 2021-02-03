package br.com.zalf.prolog.webservice.frota.pneu;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.VersaoAppBloqueadaException;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Esse Resource deve conter cada método que tenha como path base 'pneus', tenha sido depreciado, porém ainda seja
 * necessário manter rodando por conta de retrocompatibilidade. Os métodos tanto podem fazer adaptações para apontarem
 * para novos métodos como podem conter algum bloqueio que retorne uma mensagem específica para o cliente.
 *
 * Created on 2019-11-05
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/pneus")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class BackwardCompatiblePneuResource {

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR, Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/marcaModelos/{codEmpresa}")
    public List<Marca> getMarcaModeloPneuByCodEmpresa(@PathParam("codEmpresa") Long codEmpresa) {
        throw new VersaoAppBloqueadaException("Atualize o aplicativo para utilizar esta funcionalidade");
    }

    /**
     * @deprecated at 2018-08-22. Utilize {@link PneuResource#marcarFotoComoSincronizada(Long, String)}.
     * Este método ainda é mantido para permitir que apps antigos sincronizem suas fotos.
     * Como este resource foi liberado apenas para versões do app > 57, nós adicionamos o
     * {@link AppVersionCodeHandler} neste método para permitir que apenas ele tenha um tratamento diferente, permitindo
     * que a sincronia das fotos aconteça para aplicativos antigos (version code > 55). Isso funciona pois o
     * {@link AppVersionCodeHandler} prioriza anotações a nível de método.
     */
    @PUT
    @Secured
    @Path("/unidades/{codUnidade}/{codPneu}/fotos-cadastro/sincronizada")
    @AppVersionCodeHandler(
            implementation = DefaultAppVersionCodeHandler.class,
            targetVersionCode = 55,
            versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
            actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
    public Response DEPRECATED_MARCAR_FOTO_COMO_SINCRONIZADA(@PathParam("codUnidade") @Required final Long codUnidade,
                                                             @PathParam("codPneu") @Required final Long codPneu,
                                                             @QueryParam("urlFotoPneu") @Required final String urlFotoPneu) {
        new PneuService().marcarFotoComoSincronizada(codPneu, urlFotoPneu);
        return Response.ok("Foto marcada como sincronizada com sucesso");
    }
}
