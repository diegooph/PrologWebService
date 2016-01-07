package br.com.zalf.oprojeto.webservice.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.empresa.oprojeto.models.Colaborador;
import br.com.empresa.oprojeto.models.Funcao;
import br.com.empresa.oprojeto.models.Response;
import br.com.zalf.oprojeto.webservice.services.ColaboradorService;

@Path("/colaboradores")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ColaboradorResource {
	private ColaboradorService service = new ColaboradorService();
	
	@POST
	public Response insert(Colaborador colaborador) {
		if (service.insert(colaborador)) {
			return Response.Ok("Colaborador inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir colaborador");
		}
	}
	
	
	@PUT
	public Response update(Colaborador colaborador) {
		if (service.update(colaborador)) {
			return Response.Ok("Colaborador atualizado com sucesso");
		} else {
			return Response.Error("Erro ao atualizar o colaborador");
		}
	}
	
	@GET
	public List<Colaborador> getAll() {
		return service.getAll();
	}
	
	@GET
	@Path("{cpf}")
	public Colaborador getByCod(@PathParam("cpf") Long cpf) {
		return service.getByCod(cpf);
	}
	
	@DELETE
	@Path("{cpf}")
	public Response delete(@PathParam("cpf") Long cpf) {
		if (service.delete(cpf)) {
			return Response.Ok("Colaborador deletado com sucesso");
		} else {
			return Response.Error("Falha ao deletar colaborador");
		}
	}
	
	@GET
	@Path("/funcao/{codigo}")
	public Funcao getFuncaoByCod(@PathParam("codigo") Long codigo) {
		return service.getFuncaoByCod(codigo);
	}
	
	// TODO: deveria ser feito com get, porém os dados da pessoa (usando get) 
	// ficariam explícitos no link da requisição e não em seu corpo como é com 
	// post e put
	@POST
	@Path("/verifyLogin")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response verifyLogin(@FormParam("cpf") Long cpf, 
			@FormParam("dataNascimento") long dataNascimento) {
		if (service.verifyLogin(cpf, new Date(dataNascimento))) {
			return Response.Ok("Login liberado");
		} else {
			return Response.Error("Erro de autenticação");
		}
	}
}
