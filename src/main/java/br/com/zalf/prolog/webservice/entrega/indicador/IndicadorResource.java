package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.webservice.entrega.produtividade.PeriodoProdutividade;
import br.com.zalf.prolog.webservice.entrega.produtividade.ProdutividadeService;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zalf on 14/09/16.
 */
@Path("/v2/indicadores")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Entrega.Indicadores.INDICADORES)
@ConsoleDebugLog
public class IndicadorResource {

    @NotNull
    private final IndicadorService service = new IndicadorService();

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/acumulados/{cpf}")
    public List<IndicadorAcumulado> getAcumuladoIndicadoresIndividual(@QueryParam("dataInicial") final Long dataInicial,
                                                                      @QueryParam("dataFinal") final Long dataFinal,
                                                                      @PathParam("cpf") final Long cpf) {
        return service.getAcumuladoIndicadoresIndividual(dataInicial, dataFinal, cpf);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/acumulados/produtividades/{cpf}/{ano}/{mes}")
    public List<IndicadorAcumulado> getAcumuladoIndicadoresIndividual(@PathParam("ano") final int ano,
                                                                      @PathParam("mes") final int mes,
                                                                      @PathParam("cpf") final Long cpf) {
        final ProdutividadeService produtividadeService = new ProdutividadeService();
        final PeriodoProdutividade periodoProdutividade =
                produtividadeService.getPeriodoProdutividade(ano, mes, null, cpf);
        return service.getAcumuladoIndicadoresIndividual(periodoProdutividade.getDataInicio().getTime(),
                                                         periodoProdutividade.getDataTermino().getTime(),
                                                         cpf);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/extratos/{indicador}/{cpf}")
    public List<Indicador> getExtratoIndicador(@QueryParam("dataInicial") final Long dataInicial,
                                               @QueryParam("dataFinal") final Long dataFinal,
                                               @PathParam("cpf") final String cpf,
                                               @PathParam("indicador") final String indicador) {
        return service.getExtratoIndicador(dataInicial, dataFinal, "%", "%", "%", "%", cpf, indicador);
    }
}
