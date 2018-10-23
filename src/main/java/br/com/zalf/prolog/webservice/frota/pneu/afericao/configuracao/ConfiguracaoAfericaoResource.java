package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao.model.ConfiguracaoAlertaColetaSulco;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao.model.ConfiguracaoTipoVeiculoAferivel;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/afericoes/configuracoes/")
@DebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ConfiguracaoAfericaoResource {
    @NotNull
    private final ConfiguracaoAfericaoService service = new ConfiguracaoAfericaoService();

    @PUT
    @Secured(permissions = Pilares.Frota.Afericao.ConfiguracaoAfericao.CONFIGURAR)
    @Path("/tipos-veiculo/{codUnidade}")
    public Response updateTiposVeiculos(@PathParam("codUnidade") Long codUnidade,
                                        List<ConfiguracaoTipoVeiculoAferivel> configuracoes) throws ProLogException {
        return service.updateConfiguracaoTiposVeiculosAferiveis(codUnidade, configuracoes);
    }

    @GET
    @Secured(permissions = Pilares.Frota.Afericao.ConfiguracaoAfericao.CONFIGURAR)
    @Path("/tipos-veiculo/{codUnidade}")
    public List<ConfiguracaoTipoVeiculoAferivel> getConfiguracoesTipoAfericaoVeiculo(
            @PathParam("codUnidade") Long codUnidade) throws ProLogException {
        return service.getConfiguracoesTipoAfericaoVeiculo(codUnidade);
    }

    @PUT
    @Secured(permissions = Pilares.Frota.Afericao.ConfiguracaoAfericao.CONFIGURAR)
    @Path("/alertas-sulcos")
    public Response updateTiposVeiculos(List<ConfiguracaoAlertaColetaSulco> configuracoes) throws ProLogException {
        return service.updateConfiguracaoAlertaColetaSulco(configuracoes);
    }

    @GET
    @Secured(permissions = Pilares.Frota.Afericao.ConfiguracaoAfericao.CONFIGURAR)
    @Path("/alertas-sulcos")
    public List<ConfiguracaoAlertaColetaSulco> getConfiguracoesAlertaColetaSulco(
            @QueryParam("codColaborador") Long codColaborador) throws ProLogException {
        return service.getConfiguracoesAlertaColetaSulco(codColaborador);
    }
}