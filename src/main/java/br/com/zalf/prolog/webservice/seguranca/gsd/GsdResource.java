package br.com.zalf.prolog.webservice.seguranca.gsd;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.commons.questoes.Pergunta;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.seguranca.gsd.Gsd;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/gsd")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class GsdResource {

	private GsdService service = new GsdService();

	@POST
	public Response insert(Gsd gsd) {
		gsd.setDataHora(new Date(System.currentTimeMillis()));
		if (service.insert(gsd)) {
			return Response.Ok("Gsd inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir gsd");
		}
	}

	@GET
	@Path("/perguntas")
	public List<Pergunta> getPerguntas() {
		return service.getPerguntas();
	}

	@GET
	@Secured
	public List<Gsd> getAll(
			@QueryParam("dataInicial") long dataInicial,
			@QueryParam("dataFinal") long dataFinal,
			@QueryParam("equipe") String equipe,
			@QueryParam("codUnidade") Long codUnidade,
			@QueryParam("limit") long limit, 
			@QueryParam("offset") long offset){
		return service.getAll(DateUtils.toLocalDate(new Date(dataInicial)),DateUtils.toLocalDate(new Date(dataFinal)),
				equipe, codUnidade, limit, offset);
	}

	@POST
	@Path("/avaliador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Gsd> getByAvaliador(@FormParam("cpf") Long cpf,
			@FormParam("token") String token) {
		return service.getByAvaliador(cpf, token);
	}

	@POST
	@Path("/exceto/avaliador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Gsd> getAllExcetoAvaliador(@FormParam("cpf") Long cpf, 
			@FormParam("token") String token) {
		return service.getAllExcetoAvaliador(cpf, token);
	}
}