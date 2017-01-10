package br.com.zalf.prolog.webservice.gente.quiz;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.gente.quiz.Quiz;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
@Path("/quiz")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class QuizResource {

    private QuizService service = new QuizService();

    @POST
    @Secured
    public Response insert (Quiz quiz){
        if(service.insert(quiz)){
            return Response.Ok("Quiz inserido com sucesso");
        }else{
            return Response.Error("Erro ao inserir o Quiz");
        }
    }

    @GET
    @Secured
    @Path("/{cpf}")
    public List<Quiz> getRealizadosByColaborador(@PathParam("cpf") Long cpf,
                                                 @QueryParam("limit") int limit,
                                                 @QueryParam("offset") int offset){
        return service.getRealizadosByColaborador(cpf, limit, offset);
    }

    @GET
    @Secured
    @Path("/{codUnidade}/{codModeloQuiz}/{codQuiz}")
    public Quiz getByCod(@PathParam("codUnidade") Long codUnidade,
                         @PathParam("codQuiz") Long codQuiz,
                         @PathParam("codModeloQuiz") Long codModeloQuiz){
        return service.getByCod(codUnidade, codQuiz, codModeloQuiz);
    }

}
