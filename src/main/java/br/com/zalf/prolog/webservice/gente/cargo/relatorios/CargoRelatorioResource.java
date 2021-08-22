package br.com.zalf.prolog.webservice.gente.cargo.relatorios;

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
 * Created on 25/03/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("/v2/cargos/relatorios")
@Secured(permissions = {
        Pilares.Gente.Permissao.VINCULAR_CARGO,
        Pilares.Gente.Permissao.VISUALIZAR})
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class CargoRelatorioResource {

    @GET
    @Path("/permissoes-detalhadas/csv")
    @Produces("application/csv")
    public StreamingOutput getPermissoesDetalhadasCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades) throws ProLogException {
        return outputStream -> new CargoRelatorioService().getPermissoesDetalhadasCsv(
                outputStream,
                codUnidades);
    }

    @GET
    @Path("/permissoes-detalhadas/report")
    public Report getPermissoesDetalhadasReport(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades) throws ProLogException {
        return new CargoRelatorioService().getPermissoesDetalhadasReport(
                codUnidades);
    }
}