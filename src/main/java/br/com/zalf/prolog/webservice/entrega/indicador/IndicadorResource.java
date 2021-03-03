package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.webservice.entrega.produtividade.PeriodoProdutividade;
import br.com.zalf.prolog.webservice.entrega.produtividade.ProdutividadeService;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

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
public class IndicadorResource {

    private IndicadorService service = new IndicadorService();

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/acumulados/{cpf}")
    public List<IndicadorAcumulado> getAcumuladoIndicadoresIndividual(@QueryParam("dataInicial") Long dataInicial,
                                                                      @QueryParam("dataFinal") Long dataFinal,
                                                                      @PathParam("cpf") Long cpf){
        return service.getAcumuladoIndicadoresIndividual(dataInicial, dataFinal, cpf);
    }

    /**
     * Retorna os indicadores respeitando o período da produtividade
     *
     * @param ano ano da competência a ser consultada
     * @param mes mês final da competência
     * @param cpf cpf do colaborador
     * @return uma lista de {@link IndicadorAcumulado}
     */
    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/acumulados/produtividades/{cpf}/{ano}/{mes}")
    public List<IndicadorAcumulado> getAcumuladoIndicadoresIndividual(@PathParam("ano") int ano,
                                                                      @PathParam("mes") int mes,
                                                                      @PathParam("cpf") Long cpf){
        ProdutividadeService produtividadeService = new ProdutividadeService();
        PeriodoProdutividade periodoProdutividade = produtividadeService.getPeriodoProdutividade(ano, mes, null, cpf);
        return service.getAcumuladoIndicadoresIndividual(periodoProdutividade.getDataInicio().getTime(),
                periodoProdutividade.getDataTermino().getTime(), cpf);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/extratos/{indicador}/{cpf}")
    public List<Indicador> getExtratoIndicador(@QueryParam("dataInicial") Long dataInicial,
                                               @QueryParam("dataFinal") Long dataFinal,
                                               @PathParam("cpf") String cpf,
                                               @PathParam("indicador") String indicador) {
        return service.getExtratoIndicador(dataInicial, dataFinal, "%", "%", "%", "%", cpf, indicador);
    }

}
