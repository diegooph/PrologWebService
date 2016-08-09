package br.com.zalf.prolog.webservice.gente.faleConosco;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.FaleConosco;
import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import java.util.List;

@Path("/faleConosco")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class FaleConoscoResource {
	private FaleConoscoService service = new FaleConoscoService();

	@POST
	@Secured
	@Path("/{codUnidade}")
	public Response insert(FaleConosco faleConosco, @PathParam("codUnidade") Long codUnidade) {
		if (service.insert(faleConosco, codUnidade)) {
			return Response.Ok("Fale conosco inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir fale conosco");
		}
	}

//	@PUT
//	public Response update(FaleConosco faleConosco) {
//		if (service.update(faleConosco)) {
//			return Response.Ok("Fale conosco atualizado com sucesso");
//		} else {
//			return Response.Error("Erro ao atualizar fale conosco");
//		}
//	}
//	
//	@GET
//	@Path("{codigo}")
//	public FaleConosco getByCod(@PathParam("codigo") Long codigo) {
//		return service.getByCod(codigo);
//	}

	@GET
	@Secured
	@Path("/{codUnidade}/{equipe}")
	public List<FaleConosco> getAll(
			@QueryParam("dataInicial") long dataInicial,
			@QueryParam("dataFinal") long dataFinal,
			@QueryParam("limit") int limit,
			@QueryParam("offset") int offset,
			@PathParam("equipe") String equipe,
			@PathParam("codUnidade") Long codUnidade,
			@QueryParam("status") String status,
			@QueryParam("categoria") String categoria){

		return service.getAll(dataInicial, dataFinal, limit, offset, equipe, codUnidade, status, categoria);
	}

	@PUT
	@Secured
	@Path("/feedback/{codUnidade}")
	public Response insertFeedback(FaleConosco faleConosco, @PathParam("codUnidade") Long codUnidade){
		if(service.insertFeedback(faleConosco, codUnidade)){
			return Response.Ok("Feedback inserido com sucesso.");
		}else{
			return Response.Error("Erro ao inserir o feedback no fale conosco.");
		}
	}

	@GET
	@Secured
	@Path("/colaborador/{status}/{cpf}")
	public List<FaleConosco> getByColaborador(@PathParam("cpf") Long cpf,
											  @PathParam("status") String status) {
		return service.getByColaborador(cpf, status);
	}

//	@DELETE
//	@Path("{codigo}")
//	public Response delete(@PathParam("codigo") Long codigo) {
//		if (service.delete(codigo)) {
//			return Response.Ok("Fale conosco deletado com sucesso");
//		} else {
//			return Response.Error("Erro ao deletar fale conosco");
//		}
//	}
}
