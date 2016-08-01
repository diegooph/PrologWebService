package br.com.zalf.prolog.webservice.empresa;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.*;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

@Path("/empresa")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EmpresaResource {
	
	EmpresaService service = new EmpresaService();
	
	@POST
	@Path("/getEquipesByCodUnidade")
	public List<Equipe> getEquipesByCodUnidade(Request<Equipe> request) throws SQLException{
		return service.getEquipesByCodUnidade(request);
	}
	
	@PUT
	@Path("/updateEquipe")
	public boolean updateEquipe (Request<Equipe> request) throws SQLException{
		return service.updateEquipe(request);
	}
	
	@POST
	@Path("/insertEquipe")
	public Response createEquipe (Request<Equipe> request){
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
			@PathParam("codUnidade") Long codUnidade){
				return service.getFuncoesByCodUnidade(codUnidade);
	}

	@GET
	@Secured
	@Path("/setores/{codUnidade}")
	public List<Setor> getSetorByCodUnidade(@PathParam("codUnidade") Long codUnidade){
		return service.getSetorByCodUnidade(codUnidade);
	}

	@POST
	@Secured
	@Path("/setores/{codUnidade}")
	public AbstractResponse insertSetor(String nome, @PathParam("codUnidade") Long codUnidade){
		return service.insertSetor(nome,codUnidade);
	}
	

}
