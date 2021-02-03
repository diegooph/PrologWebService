package br.com.zalf.prolog.webservice.gente.quiz.modelo;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
@Path("/quizzes/modelos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class QuizModeloResource {
    @NotNull
    private final QuizModeloService service = new QuizModeloService();

    @POST
    @Secured(permissions = {Pilares.Gente.Quiz.Modelo.CRIAR})
    @Path("/{codUnidade}")
    public AbstractResponse insertModeloQuiz(@PathParam("codUnidade") @Required final Long codUnidade,
                                             @Required final ModeloQuiz modeloQuiz) throws ProLogException {
        return service.insertModeloQuiz(codUnidade, modeloQuiz);
    }

    @PUT
    @Secured(permissions = {
            Pilares.Gente.Quiz.Modelo.CRIAR,
            Pilares.Gente.Quiz.Modelo.ALTERAR})
    @Path("/{codUnidade}")
    public Response updateModeloQuiz(@PathParam("codUnidade") @Required final Long codUnidade,
                                     @Required final ModeloQuiz modeloQuiz) throws ProLogException {
        return service.updateModeloQuiz(codUnidade, modeloQuiz);
    }

    @PUT
    @Secured(permissions = {
            Pilares.Gente.Quiz.Modelo.CRIAR,
            Pilares.Gente.Quiz.Modelo.ALTERAR})
    @Path("/funcoes/{codUnidade}/{codModeloQuiz}")
    public Response updateCargosModeloQuiz(@PathParam("codUnidade") @Required final Long codUnidade,
                                           @PathParam("codModeloQuiz") @Required final Long codModeloQuiz,
                                           @Required final List<Cargo> funcoes) throws ProLogException {
        return service.updateCargosModeloQuiz(codUnidade, codModeloQuiz, funcoes);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Quiz.Modelo.VISUALIZAR,
            Pilares.Gente.Quiz.REALIZAR})
    @Path("/{codUnidade}/{codFuncaoColaborador}")
    public List<ModeloQuiz> getModelosQuizDisponiveisByCodUnidadeByCodFuncao(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("codFuncaoColaborador") @Required final Long codFuncaoColaborador) throws ProLogException {
        return service.getModelosQuizDisponiveisByCodUnidadeByCodFuncao(codUnidade, codFuncaoColaborador);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Quiz.Modelo.CRIAR,
            Pilares.Gente.Quiz.Modelo.VISUALIZAR,
            Pilares.Gente.Quiz.Modelo.ALTERAR,
            Pilares.Gente.Relatorios.QUIZ})
    @Path("/listagem")
    public List<ModeloQuizListagem> getModelosQuizzesByCodUnidade(
            @QueryParam("codUnidade") @Required final Long codUnidade) throws ProLogException {
        return service.getModelosQuizzesByCodUnidade(codUnidade);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Quiz.REALIZAR,
            Pilares.Gente.Quiz.VISUALIZAR,
            Pilares.Gente.Quiz.Modelo.CRIAR,
            Pilares.Gente.Quiz.Modelo.VISUALIZAR,
            Pilares.Gente.Quiz.Modelo.ALTERAR})
    @Path("/{codUnidade}/{codModeloQuiz}/completos")
    public ModeloQuiz getModeloQuiz(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("codModeloQuiz") @Required final Long codModeloQuiz) throws ProLogException {
        return service.getModeloQuiz(codUnidade, codModeloQuiz);
    }
}