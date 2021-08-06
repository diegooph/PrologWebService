package br.com.zalf.prolog.webservice.autenticacao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/v2/autenticacao")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class AutenticacaoResource {
    private static final String TAG = AutenticacaoResource.class.getSimpleName();
    @NotNull
    private final AutenticacaoService service = new AutenticacaoService();

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Autenticacao authenticate(@FormParam("cpf") final Long cpf,
                                     @FormParam("dataNascimento") final String dataNascimento) {
        if (service.verifyIfUserExists(cpf, dataNascimento, true).isPresent()) {
            final Autenticacao autenticacao = service.createTokenByCpf(cpf);
            Log.d(TAG, autenticacao.getToken());
            return autenticacao;
        } else {
            return new Autenticacao(Autenticacao.ERROR, cpf, "-1");
        }
    }

    @DELETE
    @Path("/{token}")
    @Secured
    public Response delete(@PathParam("token") final String token) {
        if (service.delete(token)) {
            return Response.ok("Token deletado com sucesso");
        } else {
            return Response.error("Erro ao deletar token");
        }
    }

    @GET
    @Secured
    public boolean verifyTokenValidity() {
        // Verifica se um token é valido, retornando true, caso contrario retorna 401.
        return true;
    }
}