package br.com.zalf.prolog.webservice.gente.quiz.quizModelo;

import br.com.zalf.prolog.webservice.colaborador.Cargo;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
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

    private QuizModeloService service = new QuizModeloService();

    @GET
    @Secured(permissions = {Pilares.Gente.Quiz.Modelo.VISUALIZAR, Pilares.Gente.Quiz.REALIZAR})
    @Path("/{codUnidade}/{codFuncaoColaborador}")
    public List<ModeloQuiz> getModelosQuizDisponiveisByCodUnidadeByCodFuncao(@PathParam("codUnidade") Long codUnidade,
                                                                             @PathParam("codFuncaoColaborador") Long codFuncaoColaborador) {
        return service.getModelosQuizDisponiveisByCodUnidadeByCodFuncao(codUnidade, codFuncaoColaborador);
    }

    @GET
    @Secured(permissions = Pilares.Gente.Relatorios.QUIZ)
    @Path("/{codUnidade}")
    public List<ModeloQuiz> getModelosQuizByCodUnidade(@PathParam("codUnidade") Long codUnidade) {
        return service.getModelosQuizByCodUnidade(codUnidade);
    }

    @POST
    @Secured
    @Path("/{codUnidade}")
    public AbstractResponse insertModeloQuiz(ModeloQuiz modeloQuiz, @PathParam("codUnidade") Long codUnidade) {
        return service.insertModeloQuiz(modeloQuiz, codUnidade);
    }

    @PUT
    @Secured
    @Path("/{codUnidade}")
    public Response updateModeloQuiz(ModeloQuiz modeloQuiz, @PathParam("codUnidade") Long codUnidade) {
        if (service.updateModeloQuiz(modeloQuiz, codUnidade)){
            return Response.Ok("Modelo de quiz atualizado com sucesso");
        } else {
            return Response.Error("Erro ao atualizar o quiz");
        }
    }

    @PUT
    @Secured
    @Path("/funcoes/{codUnidade}/{codModeloQuiz}")
    public Response updateCargosModeloQuiz(List<Cargo> funcoes, @PathParam("codModeloQuiz") Long codModeloQuiz,
                                           @PathParam("codUnidade") Long codUnidade) {
        if(service.updateCargosModeloQuiz(funcoes, codModeloQuiz, codUnidade)){
            return Response.Ok("Funções alteradas com sucesso");
        }else {
            return Response.Error("Erro ao alterar as funções vinculadas ao quiz");
        }
    }

}