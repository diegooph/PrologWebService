package br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Optional;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoMarcacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 20/12/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@DebugLog
@Path("/controle-jornada/tipos-marcacoes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class TipoMarcacaoResource {
    @NotNull
    private final TipoMarcacaoService service = new TipoMarcacaoService();

    @POST
    @Secured(permissions = Pilares.Gente.Intervalo.CRIAR_TIPO_INTERVALO)
    public AbstractResponse insertTipoIntervalo(TipoMarcacao tipoIntervalo) throws ProLogException {
        return service.insertTipoMarcacao(tipoIntervalo);
    }

    @PUT
    @Secured(permissions = Pilares.Gente.Intervalo.ALTERAR_TIPO_INTERVALO)
    public Response updateTipoInvervalo(TipoMarcacao tipoIntervalo) throws ProLogException {
        service.updateTipoMarcacao(tipoIntervalo);
        return Response.ok("Tipo de marcação atualizada com sucesso");
    }

    @PUT
    @Path("/{codTipoMarcacao}/status-ativo")
    @Secured(permissions = Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO)
    public Response inativarTipoIntervalo(
            @Required @PathParam("codTipoMarcacao") Long codTipoIntervalo,
            @Required final TipoMarcacao tipoIntervalo) throws ProLogException {
        service.updateStatusAtivo(codTipoIntervalo, tipoIntervalo);
        if (tipoIntervalo.isAtivo()) {
            return Response.ok("Tipo de marcação ativada com sucesso");
        } else {
            return Response.ok("Tipo de marcação inativada com sucesso");
        }
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Intervalo.MARCAR_INTERVALO,
            Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
            Pilares.Gente.Intervalo.CRIAR_TIPO_INTERVALO,
            Pilares.Gente.Relatorios.INTERVALOS})
    @Path("/{codTipoMarcacao}")
    public TipoMarcacao getTipoMarcacao(
            @PathParam("codTipoMarcacao") @Required final Long codTipoMarcacao) throws ProLogException {
        return service.getTipoMarcacao(codTipoMarcacao);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Intervalo.MARCAR_INTERVALO,
            Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
            Pilares.Gente.Intervalo.CRIAR_TIPO_INTERVALO,
            Pilares.Gente.Relatorios.INTERVALOS})
    @Path("/resumidos")
    public List<TipoMarcacao> getTiposMarcacoesResumidos(
            @Required @QueryParam("codUnidade") Long codUnidade,
            @Optional @QueryParam("apenasAtivos")
            @DefaultValue("true") boolean apenasAtivos) throws ProLogException {
        return service.getTiposMarcacoes(codUnidade, apenasAtivos, false);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Intervalo.MARCAR_INTERVALO,
            Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
            Pilares.Gente.Intervalo.CRIAR_TIPO_INTERVALO,
            Pilares.Gente.Relatorios.INTERVALOS})
    @Path("/completos")
    public List<TipoMarcacao> getTiposMarcacoesCompletos(
            @Required @QueryParam("codUnidade") Long codUnidade,
            @Optional @QueryParam("apenasAtivos")
            @DefaultValue("true") boolean apenasAtivos) throws ProLogException {
        return service.getTiposMarcacoes(codUnidade, apenasAtivos, true);
    }
}