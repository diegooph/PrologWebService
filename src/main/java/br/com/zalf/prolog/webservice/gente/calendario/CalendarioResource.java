package br.com.zalf.prolog.webservice.gente.calendario;

import br.com.zalf.prolog.commons.network.AbstractResponse;
import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.gente.calendario.Evento;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("/calendario")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class CalendarioResource {
	
	private CalendarioService service = new CalendarioService();

	@POST
	@Secured(permissions = Pilares.Gente.Calendario.CRIAR_EVENTO)
	@Path("/{codUnidade}/{codEquipe}/{codFuncao}")
	public AbstractResponse insert(Evento evento,
									@PathParam("codUnidade") String codUnidade,
									@PathParam("codEquipe") String codFuncao,
									@PathParam("codFuncao") String codEquipe) {
		return service.insert(evento, codUnidade, codFuncao, codEquipe);
	}

	@PUT
	@Secured(permissions = {Pilares.Gente.Calendario.ALTERAR_EVENTO, Pilares.Gente.Calendario.CRIAR_EVENTO})
	@Path("/{codUnidade}/{codEquipe}/{codFuncao}")
	public Response update(Evento evento,
							@PathParam("codUnidade") String codUnidade,
							@PathParam("codEquipe") String codFuncao,
							@PathParam("codFuncao") String codEquipe) {
		if (service.update(evento, codUnidade, codFuncao, codEquipe)) {
			return Response.Ok("Evento alterado com sucesso");
		} else {
			return Response.Error("Erro ao editar o evento");
		}
	}

	@GET
	@Secured(permissions = {Pilares.Gente.Calendario.VISUALIZAR, Pilares.Gente.Calendario.ALTERAR_EVENTO,
			Pilares.Gente.Calendario.CRIAR_EVENTO})
	@Path("/{cpf}")
	public List<Evento> getEventosByCpf(
			@PathParam("cpf") Long cpf) {
		return service.getEventosByCpf(cpf);
	}

	@GET
	@Secured(permissions = {Pilares.Gente.Calendario.VISUALIZAR, Pilares.Gente.Calendario.ALTERAR_EVENTO,
			Pilares.Gente.Calendario.CRIAR_EVENTO })
	@Path("/{codEmpresa}/{codUnidade}/{equipe}/{funcao}")
	public List<Evento> getAll(@QueryParam("dataInicial") long dataInicial,
								@QueryParam("dataFinal") long dataFinal,
								@PathParam("codEmpresa") Long codEmpresa,
								@PathParam("codUnidade") String codUnidade,
								@PathParam("equipe") String equipe,
								@PathParam("funcao") String funcao) throws SQLException {
		return service.getAll(dataInicial, dataFinal, codEmpresa, codUnidade, equipe, funcao);
	}

	@DELETE
	@Secured(permissions = {Pilares.Gente.Calendario.ALTERAR_EVENTO, Pilares.Gente.Calendario.CRIAR_EVENTO})
	@Path("/{codUnidade}/{codEvento}")
	public Response delete(@PathParam("codUnidade") Long codUnidade, @PathParam("codEvento") Long codEvento) {
		if (service.delete(codUnidade, codEvento)) {
			return Response.Ok("Evento deletado com sucesso");
		} else {
			return Response.Error("Erro ao deletar o evento");
		}
	}
}