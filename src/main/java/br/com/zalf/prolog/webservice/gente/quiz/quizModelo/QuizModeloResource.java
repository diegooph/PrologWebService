package br.com.zalf.prolog.webservice.gente.quiz.quizModelo;

import br.com.zalf.prolog.gente.quiz.ModeloQuiz;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
@Path("/quizzes/modelos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class QuizModeloResource {

    QuizModeloService service = new QuizModeloService();

    @GET
    @Secured(permissions = {Pilares.Gente.Quiz.Modelo.VISUALIZAR, Pilares.Gente.Quiz.REALIZAR})
    @Path("/{codUnidade}/{codFuncaoColaborador}")
    public List<ModeloQuiz> getModelosQuizDisponiveisByCodUnidadeByCodFuncao(@PathParam("codUnidade") Long codUnidade,
                                                                             @PathParam("codFuncaoColaborador") Long codFuncaoColaborador) {
        return service.getModelosQuizDisponiveisByCodUnidadeByCodFuncao(codUnidade, codFuncaoColaborador);
    }

    @GET
    @Secured(permissions = Pilares.Gente.Relatorios.VISUALIZAR)
    @Path("/{codUnidade}")
    public List<ModeloQuiz> getModelosQuizByCodUnidade(@PathParam("codUnidade") Long codUnidade) {
        return service.getModelosQuizByCodUnidade(codUnidade);
    }
}