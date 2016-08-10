package br.com.zalf.prolog.webservice.gente.calendario;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.AbstractResponse;
import br.com.zalf.prolog.models.Evento;
import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.util.L;
import com.google.gson.Gson;

@Path("/calendario")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class CalendarioResource {
	private CalendarioService service = new CalendarioService();

	@GET
	@Secured
	@Path("/{cpf}")
	public List<Evento> getEventosByCpf(
			@PathParam("cpf") Long cpf){
		return service.getEventosByCpf(cpf);
	}

	@GET
	@Secured
	@Path("/{codEmpresa}/{codUnidade}/{equipe}/{funcao}")
	public List<Evento> getAll (@QueryParam("dataInicial") long dataInicial,
								@QueryParam("dataFinal") long dataFinal,
								@PathParam("codEmpresa") Long codEmpresa,
								@PathParam("codUnidade") String codUnidade,
								@PathParam("equipe") String equipe,
								@PathParam("funcao") String funcao) throws SQLException {
		return service.getAll(dataInicial, dataFinal, codEmpresa, codUnidade, equipe, funcao);
	}

	@PUT
	@Secured
	@Path("/{codUnidade}/{codEquipe}/{codFuncao}")
	public AbstractResponse insert (Evento evento,
					@PathParam("codUnidade") String codUnidade,
					@PathParam("codEquipe") String codFuncao,
					@PathParam("codFuncao") String codEquipe){
		return service.insert(evento, codUnidade, codFuncao, codEquipe);
	}

	@POST
	@Secured
	@Path("/{codUnidade}/{codEquipe}/{codFuncao}")
	public Response update (Evento evento,
							@PathParam("codUnidade") String codUnidade,
							@PathParam("codEquipe") String codFuncao,
							@PathParam("codFuncao") String codEquipe){
		if (service.update(evento, codUnidade, codFuncao, codEquipe)){
			return Response.Ok("Evento alterado com sucesso");
		}else{
			return Response.Error("Erro ao editar o evento");
		}
	}

	@DELETE
	@Secured
	@Path("/{codUnidade}/{codEvento}")
	public Response delete (@PathParam("codUnidade") Long codUnidade, @PathParam("codEvento") Long codEvento){
		if (service.delete(codUnidade, codEvento)){
			return Response.Ok("Evento deletado com sucesso");
		}else{
			return Response.Error("Erro ao deletar o evento");
		}
	}
}
