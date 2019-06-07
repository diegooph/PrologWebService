package br.com.zalf.prolog.webservice.frota.veiculo.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

/**
 * Created on 02/05/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */


@Path("/veiculos/relatorios")
@Secured(permissions = {
        Pilares.Frota.Veiculo.VISUALIZAR_RELATORIOS})
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class VeiculoRelatorioResource {

    @GET
    @Path("/listagem-veiculos-by-unidade/csv")
    @Produces("application/csv")
    public StreamingOutput getListagemVeiculosByUnidadeCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades) {
        return outputStream -> new VeiculoRelatorioService()
                .getListagemVeiculosByUnidadeCsv(outputStream, codUnidades);
    }

    @GET
    @Path("/listagem-veiculos-by-unidade/report")
    public Report getListagemVeiculosByUnidadeReport(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades) throws ProLogException {
        return new VeiculoRelatorioService()
                .getListagemVeiculosByUnidadeReport(codUnidades);
    }
}
