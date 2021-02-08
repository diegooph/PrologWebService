package br.com.zalf.prolog.webservice.gente.calendario;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("/v2/calendario")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class CalendarioResource {
	
	private CalendarioService service = new CalendarioService();

	@POST
	@Secured(permissions = Pilares.Gente.Calendario.CRIAR_EVENTO)
	@Path("/{codUnidade}/{nomeEquipe}/{codFuncao}")
	public AbstractResponse insert(Evento evento,
									@PathParam("codUnidade") String codUnidade,
									@PathParam("nomeEquipe") String nomeEquipe,
									@PathParam("codFuncao") String codFuncao) {
		return service.insert(evento, codUnidade, codFuncao, nomeEquipe);
	}

	@PUT
	@Secured(permissions = {Pilares.Gente.Calendario.ALTERAR_EVENTO, Pilares.Gente.Calendario.CRIAR_EVENTO})
	@Path("/{codUnidade}/{nomeEquipe}/{codFuncao}")
	public Response update(Evento evento,
							@PathParam("codUnidade") String codUnidade,
							@PathParam("nomeEquipe") String nomeEquipe,
							@PathParam("codFuncao") String codFuncao) {
		if (service.update(evento, codUnidade, codFuncao, nomeEquipe)) {
			return Response.ok("Evento alterado com sucesso");
		} else {
			return Response.error("Erro ao editar o evento");
		}
	}

	@GET
	@Secured(permissions = {Pilares.Gente.Calendario.VISUALIZAR_PROPRIOS, Pilares.Gente.Calendario.ALTERAR_EVENTO,
			Pilares.Gente.Calendario.CRIAR_EVENTO})
	@Path("/{cpf}")
	public List<Evento> getEventosByCpf(
			@PathParam("cpf") Long cpf) {
		return service.getEventosByCpf(cpf);
	}

	@GET
	@Secured(permissions = {Pilares.Gente.Calendario.VISUALIZAR_PROPRIOS, Pilares.Gente.Calendario.ALTERAR_EVENTO,
			Pilares.Gente.Calendario.CRIAR_EVENTO })
	@Path("/{codEmpresa}/{codUnidade}/{nomeEquipe}/{codFuncao}")
	public List<Evento> getAll(@QueryParam("dataInicial") long dataInicial,
								@QueryParam("dataFinal") long dataFinal,
								@PathParam("codEmpresa") Long codEmpresa,
								@PathParam("codUnidade") String codUnidade,
								@PathParam("nomeEquipe") String nomeEquipe,
								@PathParam("codFuncao") String codFuncao) throws SQLException {
		return service.getAll(dataInicial, dataFinal, codEmpresa, codUnidade, nomeEquipe, codFuncao);
	}

	@DELETE
	@Secured(permissions = {Pilares.Gente.Calendario.ALTERAR_EVENTO, Pilares.Gente.Calendario.CRIAR_EVENTO})
	@Path("/{codUnidade}/{codEvento}")
	public Response delete(@PathParam("codUnidade") Long codUnidade, @PathParam("codEvento") Long codEvento) {
		if (service.delete(codUnidade, codEvento)) {
			return Response.ok("Evento deletado com sucesso");
		} else {
			return Response.error("Erro ao deletar o evento");
		}
	}
}