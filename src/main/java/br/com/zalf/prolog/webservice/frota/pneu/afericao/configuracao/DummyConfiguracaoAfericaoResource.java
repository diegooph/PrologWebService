package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoAberturaServico;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoAberturaServicoHistoricoExibicao;
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
public class DummyConfiguracaoAfericaoResource {

    @GET
    @Secured
    @Path("/abertura-servico")
    public List<ConfiguracaoAberturaServico> getConfiguracaoAberturaServico() {
        final List<ConfiguracaoAberturaServico> configuracao = new ArrayList<>();
        configuracao.add(ConfiguracaoAberturaServico.getDummy());
        configuracao.add(ConfiguracaoAberturaServico.getDummy());
        ConfiguracaoAberturaServico configVazia = new ConfiguracaoAberturaServico(
                null,
                3L,
                1L,
                "Sul",
                3L,
                "Unidade Teste Zalf",
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
    public List<ConfiguracaoAberturaServicoHistoricoExibicao> getConfiguracaoAberturaServicoHistoricoExibicao() {
        final List<ConfiguracaoAberturaServicoHistoricoExibicao> historico = new ArrayList<>();
        historico.add(ConfiguracaoAberturaServicoHistoricoExibicao.getDummy());
        historico.add(ConfiguracaoAberturaServicoHistoricoExibicao.getDummy());
        return historico;
    }
}
