package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Optional;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicaoInicioFim;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAtivacaoInativacao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteEdicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.ConsolidadoMarcacoesDia;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoAjusteHistoricoExibicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoColaboradorAjuste;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@DebugLog
@Path("/controle-jornada/ajustes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ControleJornadaAjusteResource {
    @NotNull
    private final ControleJornadaAjusteService service = new ControleJornadaAjusteService();

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured()
    @Path("/marcacoes-consolidadas")
    public List<ConsolidadoMarcacoesDia> getMarcacoesConsolidadasParaAjuste(
            @QueryParam("codUnidade") @Required Long codUnidade,
            @QueryParam("codTipoMarcacao") @Optional Long codTipoMarcacao,
            @QueryParam("codColaborador") @Optional Long codColaborador,
            @QueryParam("dataInicial") @Required String dataInicial,
            @QueryParam("dataFinal") @Required String dataFinal) throws ProLogException {
        return service.getMarcacoesConsolidadasParaAjuste(
                codUnidade,
                codTipoMarcacao,
                codColaborador,
                dataInicial,
                dataFinal);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured()
    @Path("/marcacoes-colaboradores")
    public List<MarcacaoColaboradorAjuste> getMarcacoesColaboradorParaAjuste(
            @QueryParam("codColaborador") @Required Long codColaborador,
            @QueryParam("codTipoMarcacao") @Required Long codTipoMarcacao,
            @QueryParam("dia") @Required String dia) throws ProLogException {
        return service.getMarcacoesColaboradorParaAjuste(codColaborador, codTipoMarcacao, dia);
    }

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured()
    @Path("/adicionar-marcacao-avulsa")
    public Response adicionarMarcacaoAjuste(@HeaderParam("Authorization") String userToken,
                                            @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws ProLogException {
        return service.adicionarMarcacaoAjuste(userToken, marcacaoAjuste);
    }

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured()
    @Path("/adicionar-marcacao-inicio-fim")
    public Response adicionarMarcacaoAjuste(
            @HeaderParam("Authorization") String userToken,
            @NotNull final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste) throws ProLogException {
        return service.adicionarMarcacaoAjusteInicioFim(userToken, marcacaoAjuste);
    }

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured()
    @Path("/editar-marcacao")
    public Response editarMarcacaoAjuste(@HeaderParam("Authorization") String userToken,
                                         @NotNull final MarcacaoAjusteEdicao marcacaoAjuste) throws ProLogException {
        return service.editarMarcacaoAjuste(userToken, marcacaoAjuste);
    }

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured()
    @Path("/ativar-inativar-marcacao")
    public Response ativarInativarMarcacaoAjuste(
            @HeaderParam("Authorization") String userToken,
            @NotNull final MarcacaoAjusteAtivacaoInativacao marcacaoAjuste) throws ProLogException {
        return service.ativarInativarMarcacaoAjuste(userToken, marcacaoAjuste);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured()
    @Path("/historicos")
    public List<MarcacaoAjusteHistoricoExibicao> getHistoricoAjusteMarcacoes(
            @QueryParam("codMarcacoes") @Required List<Long> codMarcacoes) throws ProLogException {
        return service.getHistoricoAjusteMarcacoes(codMarcacoes);
    }
}