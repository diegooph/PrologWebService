package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.commons.colaborador.Funcao;
import br.com.zalf.prolog.commons.login.Autenticacao;
import br.com.zalf.prolog.commons.login.LoginHolder;
import br.com.zalf.prolog.commons.network.Request;
import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.util.L;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/colaboradores")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ColaboradorResource {

	private static final String TAG = ColaboradorResource.class.getSimpleName();
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
		L.d(TAG, cpf.toString());
		return service.getByCod(cpf);
	}
	
	@GET
	@Secured
	@Path("/loginHolder/{cpf}")
	public LoginHolder getLoginHolder(@PathParam("cpf") Long cpf) {
		return service.getLoginHolder(cpf);
	}
	
	@GET
	@Path("/{codUnidade}/")
	@Secured
	public List<Colaborador> getAll(@PathParam("codUnidade") Long codUnidade) {
		return service.getAll(codUnidade);
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
		
		L.d(TAG, String.valueOf(cpf) + "data: " + String.valueOf(dataNascimento));
		if (service.verifyLogin(cpf, new Date(dataNascimento))) {
			AutenticacaoService autenticacaoService = new AutenticacaoService();
			Autenticacao autenticacao = autenticacaoService.insertOrUpdate(cpf);
			L.d(TAG, autenticacao.getToken());
			return autenticacao;
		} else {
			return new Autenticacao(Autenticacao.ERROR, cpf, "-1");
		}
	}
}
