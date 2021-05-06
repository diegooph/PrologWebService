package br.com.zalf.prolog.webservice.gente.faleconosco;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/v2/fale-conosco")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class FaleConoscoResource {
    @NotNull
    private final FaleConoscoService service = new FaleConoscoService();
    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    @POST
    @Secured(permissions = Pilares.Gente.FaleConosco.REALIZAR)
    @Path("/{codUnidade}")
    public AbstractResponse insert(final FaleConosco faleConosco, @PathParam("codUnidade") final Long codUnidade) {
        faleConosco.getColaborador().setCodigo(colaboradorAutenticadoProvider.get().getCodigo());
        return service.insert(faleConosco, codUnidade);
    }

    @PUT
    @Secured(permissions = Pilares.Gente.FaleConosco.FEEDBACK)
    @Path("/feedback/{codUnidade}")
    public Response insertFeedback(final FaleConosco faleConosco, @PathParam("codUnidade") final Long codUnidade) {
        faleConosco.getColaboradorFeedback().setCodigo(colaboradorAutenticadoProvider.get().getCodigo());
        if (service.insertFeedback(faleConosco, codUnidade)) {
            return Response.ok("Feedback inserido com sucesso.");
        } else {
            return Response.error("Erro ao inserir o feedback no fale conosco.");
        }
    }

    @GET
    @Secured(permissions = {Pilares.Gente.FaleConosco.REALIZAR})
    @Path("/colaborador/{status}/{cpf}")
    public List<FaleConosco> getByColaborador(@HeaderParam("Authorization") @Required final String userToken,
                                              @PathParam("cpf") final Long cpf,
                                              @PathParam("status") final String status) {
        return service.getByColaborador(userToken, cpf, status);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.FaleConosco.REALIZAR,
            Pilares.Gente.FaleConosco.VISUALIZAR_TODOS,
            Pilares.Gente.FaleConosco.FEEDBACK})
    @Path("/{codUnidade}/{nomeEquipe}/{cpf}")
    public List<FaleConosco> getAll(
            @PathParam("codUnidade") final Long codUnidade,
            @PathParam("nomeEquipe") final String equipe,
            @PathParam("cpf") final String cpf,
            @QueryParam("dataInicial") final long dataInicial,
            @QueryParam("dataFinal") final long dataFinal,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset,
            @QueryParam("status") final String status,
            @QueryParam("categoria") final String categoria) {

        return service.getAll(dataInicial, dataFinal, limit, offset, cpf, equipe, codUnidade, status, categoria);
    }

    /**
     * @deprecated in v0.0.10. Use {@link #getAll(Long, String, String, long, long, int, int, String, String)} )}
     * instead
     */
    @GET
    @Secured(permissions = {Pilares.Gente.FaleConosco.VISUALIZAR_TODOS, Pilares.Gente.FaleConosco.FEEDBACK})
    @Path("/{codUnidade}/{equipe}")
    @Deprecated
    public List<FaleConosco> DEPRECATED(
            @PathParam("codUnidade") final Long codUnidade,
            @PathParam("equipe") final String equipe,
            @QueryParam("dataInicial") final long dataInicial,
            @QueryParam("dataFinal") final long dataFinal,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset,
            @QueryParam("status") final String status,
            @QueryParam("categoria") final String categoria) {

        return service.getAll(dataInicial, dataFinal, limit, offset, "%", equipe, codUnidade, status, categoria);
    }
}