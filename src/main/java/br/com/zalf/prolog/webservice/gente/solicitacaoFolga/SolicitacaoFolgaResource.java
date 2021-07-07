package br.com.zalf.prolog.webservice.gente.solicitacaoFolga;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.gente.colaborador.ColaboradorBackwardHelper;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@ConsoleDebugLog
@Path("/v2/solicitacoes-folga")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class SolicitacaoFolgaResource {
    @NotNull
    private final SolicitacaoFolgaService service = new SolicitacaoFolgaService();
    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    @POST
    @Secured(permissions = Pilares.Gente.SolicitacaoFolga.REALIZAR)
    public AbstractResponse insert(final SolicitacaoFolga solicitacaoFolga) {
        final Long codColaborador = colaboradorAutenticadoProvider.get().getCodigo();
        solicitacaoFolga.getColaborador().setCodigo(codColaborador);
        return service.insert(solicitacaoFolga);
    }

    @PUT
    @Secured(permissions = Pilares.Gente.SolicitacaoFolga.FEEDBACK_SOLICITACAO)
    @Path("/{codigo}")
    public Response update(final SolicitacaoFolga solicitacaoFolga, @PathParam("codigo") final Long codigo) {
        solicitacaoFolga.setCodigo(codigo);
        final Long codColaboradorSolicitacao =
                ColaboradorBackwardHelper.getCodColaboradorByCpf(colaboradorAutenticadoProvider.get().getCodigo(),
                                                                 solicitacaoFolga.getColaborador().getCpfAsString());
        solicitacaoFolga.getColaborador().setCodigo(codColaboradorSolicitacao);
        solicitacaoFolga.getColaboradorFeedback().setCodigo(colaboradorAutenticadoProvider.get().getCodigo());
        if (service.update(solicitacaoFolga)) {
            return Response.ok("Solicitação atualizada com sucesso");
        } else {
            return Response.error("Erro ao atualizar a solicitação");
        }
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.SolicitacaoFolga.VISUALIZAR,
            Pilares.Gente.SolicitacaoFolga.REALIZAR,
            Pilares.Gente.SolicitacaoFolga.FEEDBACK_SOLICITACAO})
    @Path("/colaboradores/{codColaborador}")
    public List<SolicitacaoFolga> getByColaborador(@PathParam("codColaborador") final Long codColaborador) {
        return service.getByColaborador(codColaborador);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.SolicitacaoFolga.VISUALIZAR,
            Pilares.Gente.SolicitacaoFolga.FEEDBACK_SOLICITACAO})
    @Path("/")
    public List<SolicitacaoFolga> getAll(
            @QueryParam("dataInicial") final String dataInicial,
            @QueryParam("dataFinal") final String dataFinal,
            @QueryParam("status") final String status,
            @QueryParam("codUnidade") final Long codUnidade,
            @QueryParam("codEquipe") final String codEquipe,
            @QueryParam("codColaborador") final Long codColaborador) {
        return service.getAll(DateUtils.parseDate(dataInicial),
                              DateUtils.parseDate(dataFinal),
                              codUnidade,
                              codEquipe,
                              status,
                              codColaborador);
    }

    @DELETE
    @Secured(permissions = Pilares.Gente.SolicitacaoFolga.REALIZAR)
    @Path("{codigo}")
    public Response delete(@PathParam("codigo") final Long codigo) {
        if (service.delete(codigo)) {
            return Response.ok("Solicitação deletada com sucesso");
        } else {
            return Response.error("Erro ao deletar a solicitação");
        }
    }

    /**
     * @deprecated at 11/06/2021. Use
     * {@link SolicitacaoFolgaResource#getByColaborador(Long)} instead.
     */
    @GET
    @Secured(permissions = {Pilares.Gente.SolicitacaoFolga.VISUALIZAR,
            Pilares.Gente.SolicitacaoFolga.REALIZAR,
            Pilares.Gente.SolicitacaoFolga.FEEDBACK_SOLICITACAO})
    @Path("/{cpf}")
    @Deprecated
    public List<SolicitacaoFolga> getByColaborador(@PathParam("cpf") final long cpf) {
        final Long codColaborador = colaboradorAutenticadoProvider.get().getCodigo();
        return service.getByColaborador(codColaborador);
    }

    /**
     * @deprecated at 11/06/2021. Use
     * {@link SolicitacaoFolgaResource#getAll(String, String, String, Long, String, Long)} instead.
     */
    @GET
    @Secured(permissions = {Pilares.Gente.SolicitacaoFolga.VISUALIZAR,
            Pilares.Gente.SolicitacaoFolga.FEEDBACK_SOLICITACAO})
    @Path("/{codUnidade}/{codEquipe}/{cpf}")
    @Deprecated
    public List<SolicitacaoFolga> getAll(
            @QueryParam("dataIncial") final long dataInicial,
            @QueryParam("dataFinal") final long dataFinal,
            @QueryParam("status") final String status,
            @PathParam("codUnidade") final Long codUnidade,
            @PathParam("codEquipe") final String codEquipe,
            @PathParam("cpf") final String cpfColaborador) {
        final Long codColaborador;
        if (cpfColaborador.equals("%")) {
            codColaborador = null;
        } else {
            codColaborador =
                    ColaboradorBackwardHelper.getCodColaboradorByCpf(colaboradorAutenticadoProvider.get().getCodigo(),
                                                                     cpfColaborador);
        }
        return service.getAll(DateUtils.toLocalDate(new Date(dataInicial)), DateUtils.toLocalDate(new Date(dataFinal)),
                              codUnidade, codEquipe, status, codColaborador);
    }
}