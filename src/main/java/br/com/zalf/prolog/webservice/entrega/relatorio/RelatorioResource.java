package br.com.zalf.prolog.webservice.entrega.relatorio;

import br.com.zalf.prolog.entrega.indicador.indicadores.ConsolidadoDia;
import br.com.zalf.prolog.entrega.indicador.indicadores.Indicador;
import br.com.zalf.prolog.entrega.indicador.indicadores.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.util.Android;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zalf on 14/09/16.
 */
@Path("/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RelatorioResource {

    RelatorioService service = new RelatorioService();

    @GET
    @Secured
    @Android
    @Path("/acumulados/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}")
    public List<IndicadorAcumulado> getAcumuladoIndicadores(@QueryParam("dataInicial") Long dataInicial,
                                                            @QueryParam("dataFinal") Long dataFinal,
                                                            @PathParam("codEmpresa") Long codEmpresa,
                                                            @PathParam("codRegional") String codRegional,
                                                            @PathParam("codUnidade") String codUnidade,
                                                            @PathParam("equipe") String equipe){
        return service.getAcumuladoIndicadores(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe);
    }

    @GET
    @Secured
    @Android
    @Path("/extratos/{indicador}/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}/{cpf}")
    public List<Indicador> getExtratoIndicador(@QueryParam("dataInicial") Long dataInicial,
                                               @QueryParam("dataFinal") Long dataFinal,
                                               @PathParam("codRegional") String codRegional,
                                               @PathParam("codEmpresa") Long codEmpresa,
                                               @PathParam("codUnidade") String codUnidade,
                                               @PathParam("equipe") String equipe,
                                               @PathParam("cpf") String cpf,
                                               @PathParam("indicador") String indicador){
        return service.getExtratoIndicador(dataInicial, dataFinal, codRegional, codEmpresa,
                                                       codUnidade, equipe, cpf, indicador);
    }

    @GET
    @Secured
    @Android
    @Path("/acumulados/diarios/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}")
    public List<ConsolidadoDia> getConsolidadoDia(@QueryParam("dataInicial") Long dataInicial,
                                                  @QueryParam("dataFinal") Long dataFinal,
                                                  @PathParam("codRegional") String codRegional,
                                                  @PathParam("codEmpresa") Long codEmpresa,
                                                  @PathParam("codUnidade") String codUnidade,
                                                  @PathParam("equipe") String equipe){
        return service.getConsolidadoDia(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe);
    }
}
