package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.webservice.commons.util.Android;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.Site;
import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Zalf on 14/09/16.
 */
@Path("/indicadores")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Entrega.Indicadores.INDICADORES)
public class IndicadorResource {

    private IndicadorService service = new IndicadorService();

    @GET
    @Android
    @Path("/acumulados/{cpf}")
    public List<IndicadorAcumulado> getAcumuladoIndicadoresIndividual(@QueryParam("dataInicial") Long dataInicial,
                                                                      @QueryParam("dataFinal") Long dataFinal,
                                                                      @PathParam("cpf") Long cpf){
        return service.getAcumuladoIndicadoresIndividual(dataInicial, dataFinal, cpf);
    }

    @GET
    @Site
    @Path("/acumulados/produtividades/{cpf}/{ano}/{mes}")
    public List<IndicadorAcumulado> getAcumuladoIndicadoresIndividual(@PathParam("ano") int ano,
                                                                      @PathParam("mes") int mes,
                                                                      @PathParam("cpf") Long cpf){
        return service.getAcumuladoIndicadoresIndividual(DateUtils.getDataInicialPeriodoProdutividade(ano, mes).getTime(),
                DateUtils.toSqlDate(LocalDate.of(ano, mes, 20)).getTime(), cpf);
    }

    @GET
    @Android
    @Path("/extratos/{indicador}/{cpf}")
    public List<Indicador> getExtratoIndicador(@QueryParam("dataInicial") Long dataInicial,
                                               @QueryParam("dataFinal") Long dataFinal,
                                               @PathParam("cpf") String cpf,
                                               @PathParam("indicador") String indicador) {
        return service.getExtratoIndicador(dataInicial, dataFinal, "%", "%", "%", "%", cpf, indicador);
    }

}
