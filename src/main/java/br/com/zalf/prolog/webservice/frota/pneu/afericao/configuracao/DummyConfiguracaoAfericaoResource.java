package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoCronogramaServico;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoCronogramaServicoHistorico;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 11/25/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
@Path("/dummies")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DummyConfiguracaoAfericaoResource {

    @GET
    @Secured
    @Path("/abertura-servico")
    public List<ConfiguracaoCronogramaServico> getConfiguracaoAberturaServico() {
        final List<ConfiguracaoCronogramaServico> configuracao = new ArrayList<>();
        configuracao.add(ConfiguracaoCronogramaServico.getDummy());
        configuracao.add(ConfiguracaoCronogramaServico.getDummy());
        ConfiguracaoCronogramaServico configVazia = new ConfiguracaoCronogramaServico(
                null,
                3L,
                1L,
                "Sul",
                3L,
                "Unidade Teste Zalf",
                2272L,
                null,
                null,
                null,
                null,
                null,
                null);
        configuracao.add(configVazia);
        return configuracao;
    }

    @GET
    @Secured
    @Path("/abertura-servico-historico")
    public List<ConfiguracaoCronogramaServicoHistorico> getConfiguracaoAberturaServicoHistorico() {
        final List<ConfiguracaoCronogramaServicoHistorico> historico = new ArrayList<>();
        historico.add(ConfiguracaoCronogramaServicoHistorico.getDummy());
        historico.add(ConfiguracaoCronogramaServicoHistorico.getDummy());
        return historico;
    }
}
