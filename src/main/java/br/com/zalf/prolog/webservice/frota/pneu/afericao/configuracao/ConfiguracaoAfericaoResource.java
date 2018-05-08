package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.ConfiguracaoTipoVeiculoAfericao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/afericoes/configuracoes/")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ConfiguracaoAfericaoResource {

    private final ConfiguracaoAfericaoService service = new ConfiguracaoAfericaoService();

    @PUT
    @Secured
    @Path("/{codUnidade}")
    public Response update(@PathParam("codUnidade") Long codUnidade,
                           ConfiguracaoTipoVeiculoAfericao configuracao) throws Exception {
        return service.updateConfiguracao(codUnidade, configuracao);
    }

    @GET
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR)
    @Path("/cronogramas/{codUnidade}")
    public List<ConfiguracaoTipoVeiculoAfericao> getConfiguracoesTipoAfericaoVeiculo(
            @PathParam("codUnidade") Long codUnidade) throws Exception {
        return service.getConfiguracoesTipoAfericaoVeiculo(codUnidade);
    }
}
