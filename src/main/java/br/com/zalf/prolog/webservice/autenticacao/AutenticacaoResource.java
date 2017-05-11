package br.com.zalf.prolog.webservice.autenticacao;

import br.com.zalf.prolog.webservice.commons.login.Autenticacao;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.commons.util.L;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;



@Path("/autenticacao")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class AutenticacaoResource {
	private static final String TAG = AutenticacaoResource.class.getSimpleName();
	private AutenticacaoService service = new AutenticacaoService();

	@POST
	@Path("/verifyLogin")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Autenticacao verifyLogin(@FormParam("cpf") Long cpf,
									@FormParam("dataNascimento") long dataNascimento) {

		L.d(TAG, String.valueOf(cpf) + "data: " + String.valueOf(dataNascimento));
		if (service.verifyLogin(cpf, new Date(dataNascimento))) {
			Autenticacao autenticacao = service.insertOrUpdate(cpf);
			L.d(TAG, autenticacao.getToken());
			return autenticacao;
		} else {
			return new Autenticacao(Autenticacao.ERROR, cpf, "-1");
		}
	}

	@DELETE
	@Path("{token}")
	@Secured
	public Response delete(@PathParam("token") String token) {
		if (service.delete(token)) {
			return Response.Ok("Token deletado com sucesso");
		} else {
			return Response.Error("Erro ao deletar token");
		}
	}
}