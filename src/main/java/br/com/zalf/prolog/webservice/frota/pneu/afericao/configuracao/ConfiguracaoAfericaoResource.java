package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoAberturaServico;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoAlertaColetaSulco;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoTipoVeiculoAferivel;
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
@Secured(permissions = Pilares.Frota.Afericao.ConfiguracaoAfericao.CONFIGURAR)
public class ConfiguracaoAfericaoResource {
    @NotNull
    private final ConfiguracaoAfericaoService service = new ConfiguracaoAfericaoService();

    @PUT
    @Path("/tipos-veiculo/{codUnidade}")
    public Response updateConfiguracaoTiposVeiculosAferiveis(
            @PathParam("codUnidade") @NotNull final Long codUnidade,
            @NotNull final List<ConfiguracaoTipoVeiculoAferivel> configuracoes) throws ProLogException {
        return service.updateConfiguracaoTiposVeiculosAferiveis(codUnidade, configuracoes);
    }

    @GET
    @Path("/tipos-veiculo/{codUnidade}")
    public List<ConfiguracaoTipoVeiculoAferivel> getConfiguracoesTipoAfericaoVeiculo(
            @PathParam("codUnidade") @NotNull final Long codUnidade) throws ProLogException {
        return service.getConfiguracoesTipoAfericaoVeiculo(codUnidade);
    }

    @PUT
    @Path("/alertas-sulcos")
    public Response updateConfiguracaoAlertaColetaSulco(
            @NotNull final List<ConfiguracaoAlertaColetaSulco> configuracoes) throws ProLogException {
        return service.updateConfiguracaoAlertaColetaSulco(configuracoes);
    }

    @GET
    @Path("/alertas-sulcos")
    public List<ConfiguracaoAlertaColetaSulco> getConfiguracoesAlertaColetaSulco(
            @QueryParam("codColaborador") @NotNull final Long codColaborador) throws ProLogException {
        return service.getConfiguracoesAlertaColetaSulco(codColaborador);
    }

    @PUT
    @Path("/abertura-servico")
    public Response updateConfiguracaoAberturaServico(
            @NotNull final List<ConfiguracaoAberturaServico> configuracoes) throws ProLogException {
        return service.updateConfiguracaoAberturaServico(configuracoes);
    }

    @GET
    @Path("/abertura-servico")
    public List<ConfiguracaoAberturaServico> getConfiguracaoAberturaServico(
            @QueryParam("codColaborador") @NotNull final Long codColaborador) throws ProLogException {
        return service.getConfiguracaoAberturaServico(codColaborador);
    }
}