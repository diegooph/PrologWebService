package br.com.zalf.prolog.webservice.gente.faleConosco;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.gente.fale_conosco.FaleConosco;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/faleConosco")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class FaleConoscoResource {

	private FaleConoscoService service = new FaleConoscoService();

	@POST
	@Secured(permissions = Pilares.Gente.FaleConosco.REALIZAR)
	@Path("/{codUnidade}")
	public Response insert(FaleConosco faleConosco, @PathParam("codUnidade") Long codUnidade) {
		if (service.insert(faleConosco, codUnidade)) {
			return Response.Ok("Fale conosco inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir fale conosco");
		}
	}

	@PUT
	@Secured(permissions = Pilares.Gente.FaleConosco.FEEDBACK)
	@Path("/feedback/{codUnidade}")
	public Response insertFeedback(FaleConosco faleConosco, @PathParam("codUnidade") Long codUnidade) {
		if (service.insertFeedback(faleConosco, codUnidade)) {
			return Response.Ok("Feedback inserido com sucesso.");
		} else {
			return Response.Error("Erro ao inserir o feedback no fale conosco.");
		}
	}

	@GET
	@Secured(permissions = {Pilares.Gente.FaleConosco.REALIZAR, Pilares.Gente.FaleConosco.VISUALIZAR})
	@Path("/colaborador/{status}/{cpf}")
	public List<FaleConosco> getByColaborador(@PathParam("cpf") Long cpf,
											  @PathParam("status") String status) {
		return service.getByColaborador(cpf, status);
	}

	@GET
	@Secured(permissions = {Pilares.Gente.FaleConosco.VISUALIZAR, Pilares.Gente.FaleConosco.FEEDBACK})
	@Path("/{codUnidade}/{equipe}")
	public List<FaleConosco> getAll(
			@PathParam("equipe") String equipe,
			@PathParam("codUnidade") Long codUnidade,
			@QueryParam("dataInicial") long dataInicial,
			@QueryParam("dataFinal") long dataFinal,
			@QueryParam("limit") int limit,
			@QueryParam("offset") int offset,
			@QueryParam("status") String status,
			@QueryParam("categoria") String categoria) {

		return service.getAll(dataInicial, dataFinal, limit, offset, equipe, codUnidade, status, categoria);
	}
}