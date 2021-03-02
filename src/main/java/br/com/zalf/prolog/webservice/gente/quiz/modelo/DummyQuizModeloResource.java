package br.com.zalf.prolog.webservice.gente.quiz.modelo;

import br.com.zalf.prolog.webservice.DummyData;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.util.RandomUtils;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.interceptors.debug.ResourceDebugOnly;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 01/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/v2/dummies")
@ConsoleDebugLog
@ResourceDebugOnly
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DummyQuizModeloResource extends DummyData {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/modelos-quizzes-listagem")
    public List<ModeloQuizListagem> getModelosQuizzesListagem() {
        final List<ModeloQuizListagem> modelos = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            modelos.add(ModeloQuizListagem.createDummy(
                    RandomUtils.randomBoolean(),
                    RandomUtils.randomBoolean()));
        }
        return modelos;
    }
}