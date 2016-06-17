package br.com.zalf.prolog.webservice.colaborador;

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

import br.com.zalf.prolog.models.Autenticacao;
import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.Funcao;
import br.com.zalf.prolog.models.LoginHolder;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.auth.Secured;

@Path("/colaboradores")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ColaboradorResource {
	private ColaboradorService service = new ColaboradorService();
	
	@POST
	@Secured
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response insert(Colaborador colaborador) {
		if (service.insert(colaborador)) {
			return Response.Ok("Colaborador inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir colaborador");
		}
	}
	
	@PUT
	@Secured
	@Path("/{cpf}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response update(@PathParam("cpf") Long cpfAntigo, Colaborador colaborador) {
		if (service.update(cpfAntigo, colaborador)) {
			return Response.Ok("Colaborador atualizado com sucesso");
		} else {
			return Response.Error("Erro ao atualizar o colaborador");
		}
	}
	
	@GET
	@Secured
	@Path("/getByCod/{cpf}")
	public Colaborador getByCod(@PathParam("cpf") Long cpf) {
		System.out.println(cpf);
		return service.getByCod(cpf);
	}
	
	@GET
	@Secured
	@Path("/loginHolder/{cpf}")
	public LoginHolder getLoginHolder(@PathParam("cpf") Long cpf) {
		return service.getLoginHolder(cpf);
	}
	
	@POST
	@Path("/getAll")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public List<Colaborador> getAll(Request request) {
		return service.getAll(request);
	}
	
	@POST
	@Path("/getAtivosByUnidade")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Colaborador> getAtivosByUnidade(@FormParam("codUnidade") Long codUnidade, @FormParam("token") String token, @FormParam("cpf") Long cpf) {
		return service.getAtivosByUnidade(codUnidade, token, cpf);
	}
	
	@DELETE
	@Path("/{cpf}")
	@Secured
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response delete(@PathParam("cpf") Long cpf) {
		if (service.delete(cpf)) {
			return Response.Ok("Colaborador deletado com sucesso");
		} else {
			return Response.Error("Falha ao deletar colaborador");
		}
	}
	
	@GET
	@Path("/funcao/{codigo}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Funcao getFuncaoByCod(@PathParam("codigo") Long codigo) {
		return service.getFuncaoByCod(codigo);
	}
	
	@POST
	@Path("/verifyLogin")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Autenticacao verifyLogin(@FormParam("cpf") Long cpf, 
			@FormParam("dataNascimento") long dataNascimento) {
		
		System.out.println(cpf + "data: " + dataNascimento);
		
		if (service.verifyLogin(cpf, new Date(dataNascimento))) {
			AutenticacaoService autenticacaoService = new AutenticacaoService();
			Autenticacao autenticacao = autenticacaoService.insertOrUpdate(cpf);
			System.out.println(autenticacao.getToken());
			return autenticacao;
		} else {
			return new Autenticacao(Autenticacao.ERROR, cpf, "-1");
		}
	}
}
