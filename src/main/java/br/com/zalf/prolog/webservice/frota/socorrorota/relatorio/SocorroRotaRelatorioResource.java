package br.com.zalf.prolog.webservice.frota.socorrorota.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

/**
 * Created on 12/02/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("/v2/socorro-rota/relatorios")
@Secured(permissions = {Pilares.Frota.SocorroRota.TRATAR_SOCORRO,
        Pilares.Frota.SocorroRota.VISUALIZAR_SOCORROS_E_RELATORIOS})
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")

public class SocorroRotaRelatorioResource {

    @GET
    @Path("/dados-gerais-socorro-rota/csv")
    @Produces("application/csv")
    public StreamingOutput getDadosGeraisSocorrosRotasCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal,
            @QueryParam("statusSocorrosRotas") @Required final List<String> statusSocorrosRotas) {
        return outputStream -> new SocorroRotaRelatorioService().getDadosGeraisSocorrosRotasCsv(
                outputStream,
                codUnidades,
                dataInicial,
                dataFinal,
                statusSocorrosRotas);
    }

    @GET
    @Path("/dados-gerais-socorro-rota/report")
    public Report getDadosGeraisSocorrosRotasReport(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal,
            @QueryParam("statusSocorrosRotas") @Required final List<String> statusSocorrosRotas) throws ProLogException {
        return new SocorroRotaRelatorioService().getDadosGeraisSocorrosRotasReport(
                codUnidades,
                dataInicial,
                dataFinal,
                statusSocorrosRotas);
    }
}