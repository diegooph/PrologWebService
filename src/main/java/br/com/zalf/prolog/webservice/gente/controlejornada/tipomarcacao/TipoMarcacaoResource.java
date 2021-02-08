package br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
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
@ConsoleDebugLog
@Path("/v2/controle-jornada/tipos-marcacoes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class TipoMarcacaoResource {
    @NotNull
    private final TipoMarcacaoService service = new TipoMarcacaoService();

    @POST
    @Secured(permissions = Pilares.Gente.Intervalo.CRIAR_TIPO_INTERVALO)
    public AbstractResponse insertTipoIntervalo(@Required final TipoMarcacao tipoIntervalo) throws ProLogException {
        return service.insertTipoMarcacao(tipoIntervalo);
    }

    @PUT
    @Secured(permissions = Pilares.Gente.Intervalo.ALTERAR_TIPO_INTERVALO)
    public Response updateTipoInvervalo(@Required final TipoMarcacao tipoIntervalo) throws ProLogException {
        return service.updateTipoMarcacao(tipoIntervalo);
    }

    @PUT
    @Path("/{codTipoMarcacao}/status-ativo")
    @Secured(permissions = Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO)
    public Response inativarTipoIntervalo(
            @PathParam("codTipoMarcacao") @Required final Long codTipoMarcacao,
            @Required final TipoMarcacao tipoMarcacao) throws ProLogException {
        return service.updateStatusAtivo(codTipoMarcacao, tipoMarcacao);
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
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("apenasAtivos") @DefaultValue("true") final boolean apenasAtivos) throws ProLogException {
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
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("apenasAtivos") @DefaultValue("true") final boolean apenasAtivos) throws ProLogException {
        return service.getTiposMarcacoes(codUnidade, apenasAtivos, true);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Intervalo.MARCAR_INTERVALO,
            Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
            Pilares.Gente.Intervalo.CRIAR_TIPO_INTERVALO,
            Pilares.Gente.Relatorios.INTERVALOS})
    @Path("/formula-calculo-jornada")
    public FormulaCalculoJornada getForumaCalculoJornada(
            @QueryParam("codUnidade") @Required final Long codUnidade) throws ProLogException {
        return service.getForumaCalculoJornada(codUnidade);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Intervalo.MARCAR_INTERVALO,
            Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
            Pilares.Gente.Intervalo.CRIAR_TIPO_INTERVALO,
            Pilares.Gente.Relatorios.INTERVALOS})
    @Path("/tem-tipo-jornada")
    public boolean unidadeTemTipoDefinidoComoJornada(
            @QueryParam("codUnidade") @Required final Long codUnidade) throws ProLogException {
        return service.unidadeTemTipoDefinidoComoJornada(codUnidade);
    }
}