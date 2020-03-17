package br.com.zalf.prolog.webservice.gente.colaborador.relatorios;

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
 * Created on 05/04/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */

@Path("/colaboradores/relatorios")
@Secured(permissions = {
        Pilares.Gente.Colaborador.VISUALIZAR,
        Pilares.Gente.Colaborador.CADASTRAR,
        Pilares.Gente.Colaborador.EDITAR})
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ColaboradorRelatorioResource {

    @GET
    @Path("/listagem-colaboradores-by-unidade/csv")
    @Produces("application/csv")
    public StreamingOutput getListagemColaboradoresByUnidadeCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades) {
        return outputStream -> new ColaboradorRelatorioService()
                .getListagemColaboradoresByUnidadeCsv(outputStream, codUnidades);
    }

    @GET
    @Path("/listagem-colaboradores-by-unidade/report")
    public Report getListagemColaboradoresByUnidadeReport(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades) throws ProLogException {
        return new ColaboradorRelatorioService()
                .getListagemColaboradoresByUnidadeReport(codUnidades);
    }
}
