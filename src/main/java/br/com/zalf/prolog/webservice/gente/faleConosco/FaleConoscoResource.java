package br.com.zalf.prolog.webservice.gente.faleConosco;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/v2/fale-conosco")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class FaleConoscoResource {

    private FaleConoscoService service = new FaleConoscoService();

    @POST
    @Secured(permissions = Pilares.Gente.FaleConosco.REALIZAR)
    @Path("/{codUnidade}")
    public AbstractResponse insert(FaleConosco faleConosco, @PathParam("codUnidade") Long codUnidade) {
        return service.insert(faleConosco, codUnidade);
    }

    @PUT
    @Secured(permissions = Pilares.Gente.FaleConosco.FEEDBACK)
    @Path("/feedback/{codUnidade}")
    public Response insertFeedback(FaleConosco faleConosco, @PathParam("codUnidade") Long codUnidade) {
        if (service.insertFeedback(faleConosco, codUnidade)) {
            return Response.ok("Feedback inserido com sucesso.");
        } else {
            return Response.error("Erro ao inserir o feedback no fale conosco.");
        }
    }

    @GET
    @Secured(permissions = {Pilares.Gente.FaleConosco.REALIZAR})
    @Path("/colaborador/{status}/{cpf}")
    public List<FaleConosco> getByColaborador(@PathParam("cpf") Long cpf,
                                              @PathParam("status") String status) {
        return service.getByColaborador(cpf, status);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.FaleConosco.REALIZAR,
            Pilares.Gente.FaleConosco.VISUALIZAR_TODOS,
            Pilares.Gente.FaleConosco.FEEDBACK})
    @Path("/{codUnidade}/{nomeEquipe}/{cpf}")
    public List<FaleConosco> getAll(
            @PathParam("codUnidade") Long codUnidade,
            @PathParam("nomeEquipe") String equipe,
            @PathParam("cpf") String cpf,
            @QueryParam("dataInicial") long dataInicial,
            @QueryParam("dataFinal") long dataFinal,
            @QueryParam("limit") int limit,
            @QueryParam("offset") int offset,
            @QueryParam("status") String status,
            @QueryParam("categoria") String categoria) {

        return service.getAll(dataInicial, dataFinal, limit, offset, cpf, equipe, codUnidade, status, categoria);
    }

    /**
     * @deprecated in v0.0.10. Use {@link #getAll(Long, String, String, long, long, int, int, String, String)} )} instead
     */
    @GET
    @Secured(permissions = {Pilares.Gente.FaleConosco.VISUALIZAR_TODOS, Pilares.Gente.FaleConosco.FEEDBACK})
    @Path("/{codUnidade}/{equipe}")
    @Deprecated
    public List<FaleConosco> DEPRECATED(
            @PathParam("codUnidade") Long codUnidade,
            @PathParam("equipe") String equipe,
            @QueryParam("dataInicial") long dataInicial,
            @QueryParam("dataFinal") long dataFinal,
            @QueryParam("limit") int limit,
            @QueryParam("offset") int offset,
            @QueryParam("status") String status,
            @QueryParam("categoria") String categoria) {

        return service.getAll(dataInicial, dataFinal, limit, offset, "%", equipe, codUnidade, status, categoria);
    }
}