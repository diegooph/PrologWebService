package br.com.zalf.prolog.webservice.gente.solicitacaoFolga;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/v2/solicitacoes-folga")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class SolicitacaoFolgaResource {

    private SolicitacaoFolgaService service = new SolicitacaoFolgaService();

    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    @POST
    @Secured(permissions = Pilares.Gente.SolicitacaoFolga.REALIZAR)
    public AbstractResponse insert(SolicitacaoFolga solicitacaoFolga) {
        return service.insert(solicitacaoFolga);
    }

    @PUT
    @Secured(permissions = Pilares.Gente.SolicitacaoFolga.FEEDBACK_SOLICITACAO)
    @Path("/{codigo}")
    public Response update(SolicitacaoFolga solicitacaoFolga, @PathParam("codigo") Long codigo) {
        solicitacaoFolga.setCodigo(codigo);
        if (service.update(solicitacaoFolga)) {
            return Response.ok("Solicitação atualizada com sucesso");
        } else {
            return Response.error("Erro ao atualizar a solicitação");
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
    public List<SolicitacaoFolga> getByColaborador(@PathParam("cpf") long cpf) {
        final Long codColaborador = colaboradorAutenticadoProvider.get().getCodigo();
        return service.getByColaborador(codColaborador);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.SolicitacaoFolga.VISUALIZAR,
            Pilares.Gente.SolicitacaoFolga.REALIZAR,
            Pilares.Gente.SolicitacaoFolga.FEEDBACK_SOLICITACAO
    })
    public List<SolicitacaoFolga> getByColaborador(@QueryParam("codColaborador") Long codColaborador) {
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
            @QueryParam("dataIncial") long dataInicial,
            @QueryParam("dataFinal") long dataFinal,
            @QueryParam("status") String status,
            @PathParam("codUnidade") Long codUnidade,
            @PathParam("codEquipe") String codEquipe,
            @PathParam("cpf") String cpfColaborador) {
        final Long codColaborador = colaboradorAutenticadoProvider.get().getCodigo();
        return service.getAll(DateUtils.toLocalDate(new Date(dataInicial)), DateUtils.toLocalDate(new Date(dataFinal)),
                              codUnidade, codEquipe, status, codColaborador);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.SolicitacaoFolga.VISUALIZAR,
            Pilares.Gente.SolicitacaoFolga.FEEDBACK_SOLICITACAO
    })
    public List<SolicitacaoFolga> getAll(
            @QueryParam("dataInicial") final String dataInicial,
            @QueryParam("dataFinal") final String dataFinal,
            @QueryParam("status") final String status,
            @QueryParam("codUnidade") Long codUnidade,
            @QueryParam("codEquipe") String codEquipe,
            @QueryParam("codColaborador") Long codColaborador) {
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
    public Response delete(@PathParam("codigo") Long codigo) {
        if (service.delete(codigo)) {
            return Response.ok("Solicitação deletada com sucesso");
        } else {
            return Response.error("Erro ao deletar a solicitação");
        }
    }
}