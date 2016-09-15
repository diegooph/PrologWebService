package br.com.zalf.prolog.webservice.empresa;

import br.com.zalf.prolog.commons.colaborador.Empresa;
import br.com.zalf.prolog.commons.colaborador.Equipe;
import br.com.zalf.prolog.commons.colaborador.Funcao;
import br.com.zalf.prolog.commons.colaborador.Setor;
import br.com.zalf.prolog.commons.imports.HolderMapaTracking;
import br.com.zalf.prolog.commons.network.AbstractResponse;
import br.com.zalf.prolog.commons.network.Request;
import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("/empresa")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EmpresaResource {

	private EmpresaService service = new EmpresaService();

	@POST
	@Secured
	@Path("/getEquipesByCodUnidade/{codUnidade}")
	public List<Equipe> getEquipesByCodUnidade(@PathParam("codUnidade") Long codUnidade) throws SQLException {
		return service.getEquipesByCodUnidade(codUnidade);
	}

	@PUT
	@Path("/updateEquipe")
	public boolean updateEquipe (Request<Equipe> request) throws SQLException {
		return service.updateEquipe(request);
	}

	@POST
	@Path("/insertEquipe")
	public Response createEquipe (Request<Equipe> request) {
		if (service.createEquipe(request)) {
			return Response.Ok("Equipe inserida com sucesso");
		} else {
			return Response.Error("Erro ao inserir equipe");
		}
	}

	@GET
	@Secured
	@Path("/funcoes/{codUnidade}")
	public List<Funcao> getFuncoesByCodUnidade(
			@PathParam("codUnidade") Long codUnidade) {
		return service.getFuncoesByCodUnidade(codUnidade);
	}

	@GET
	@Secured
	@Path("/setores/{codUnidade}")
	public List<Setor> getSetorByCodUnidade(@PathParam("codUnidade") Long codUnidade) {
		return service.getSetorByCodUnidade(codUnidade);
	}

	@POST
	@Secured
	@Path("/setores/{codUnidade}")
	public AbstractResponse insertSetor(String nome, @PathParam("codUnidade") Long codUnidade) {
		return service.insertSetor(nome,codUnidade);
	}

	@GET
	@Secured
	@Path("/resumoDados/{codUnidade}/{ano}/{mes}")
	public List<HolderMapaTracking> getResumoAtualizacaoDados(@PathParam("ano")int ano,
															  @PathParam("mes") int mes,
															  @PathParam("codUnidade") Long codUnidade) {
			return service.getResumoAtualizacaoDados(ano, mes, codUnidade);
	}

	@GET
	@Secured
	@Path("/filtros/{cpf}")
	public List<Empresa> getFiltros(
			@PathParam("cpf") Long cpf) {
		return service.getFiltros(cpf);
	}


}
