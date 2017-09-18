package br.com.zalf.prolog.webservice.gente.quiz.quiz;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.Quiz;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
@Path("/quizzes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class QuizResource {

    private QuizService service = new QuizService();

    @POST
    @Secured(permissions = Pilares.Gente.Quiz.REALIZAR)
    public Response insert(Quiz quiz) {
        if (service.insert(quiz)) {
            return Response.ok("Quiz inserido com sucesso");
        } else {
            return Response.error("Erro ao inserir o Quiz");
        }
    }

    @GET
    @Secured(permissions = {Pilares.Gente.Quiz.VISUALIZAR, Pilares.Gente.Quiz.REALIZAR})
    @Path("/{cpf}")
    public List<Quiz> getRealizadosByColaborador(@PathParam("cpf") Long cpf,
                                                 @QueryParam("limit") int limit,
                                                 @QueryParam("offset") int offset) {
        return service.getRealizadosByColaborador(cpf, limit, offset);
    }

    @GET
    @Secured(permissions = {Pilares.Gente.Quiz.VISUALIZAR, Pilares.Gente.Quiz.REALIZAR})
    @Path("/{codUnidade}/{codModeloQuiz}/{codQuiz}")
    public Quiz getByCod(@PathParam("codUnidade") Long codUnidade,
                         @PathParam("codModeloQuiz") Long codModeloQuiz,
                         @PathParam("codQuiz") Long codQuiz) {
        return service.getByCod(codUnidade, codQuiz, codModeloQuiz);
    }
}