package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.entrega.indicador.indicadores.Indicador;
import br.com.zalf.prolog.entrega.indicador.indicadores.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.util.Android;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zalf on 14/09/16.
 */
@Path("/indicadores")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Entrega.Indicadores.VISUALIZAR)
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
    @Android
    @Path("/extratos/{indicador}/{cpf}")
    public List<Indicador> getExtratoIndicador(@QueryParam("dataInicial") Long dataInicial,
                                               @QueryParam("dataFinal") Long dataFinal,
                                               @PathParam("cpf") String cpf,
                                               @PathParam("indicador") String indicador) {
        return service.getExtratoIndicador(dataInicial, dataFinal, "%", "%", "%", "%", cpf, indicador);
    }

}
