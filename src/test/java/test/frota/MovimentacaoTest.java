package test.frota;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.MovimentacaoService;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.destino.DestinoAnalise;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.destino.DestinoEstoque;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.OrigemAnalise;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.OrigemEstoque;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuService;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.PneuComum;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.Recapadora;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico.model.ServicoRealizadoRecapadora;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico.model.ServicoRealizadoRecapagem;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import test.BaseTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created on 07/06/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MovimentacaoTest extends BaseTest {

    private MovimentacaoService movimentacaoService;
    private PneuService pneuService;
    private PneuComum pneuComum;

    @Override
    public void initialize() {
        movimentacaoService = new MovimentacaoService();
        pneuService = new PneuService();
        pneuComum = pneuService.getPneuByCod(1795L, 5L);
    }

    @Test
    public void testInsertMovimentacaoEstoqueToAnalise() {
        // Move pneu ESTOQUE --> ANALISE
        final ProcessoMovimentacao processoMovimentacaoAnalise = createProcessoMovimentacaoAnalise();
        final AbstractResponse response = movimentacaoService.insert(processoMovimentacaoAnalise);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatus());
    }

    @Test
    public void testInsertMovimentacaoAnaliseToEstoque() {
        // Move o mesmo pneu ANALISE --> ESTOQUE
        final ProcessoMovimentacao processoMovimentacaoEstoque = createProcessoMovimentacaoEstoque();
        final AbstractResponse response = movimentacaoService.insert(processoMovimentacaoEstoque);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatus());
    }

    @Test
    public void testInsertPneuCreateSegundaVida() throws Throwable {
        final PneuComum pneu = pneuService.getPneuByCod(1795L, 5L);
        pneu.setCodigoCliente(pneu.getCodigoCliente().concat("1"));
        pneu.setDot("1310");
        pneu.getBanda().setValor(new BigDecimal(399));
        final AbstractResponse response = pneuService.insert(pneu, 5L);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatus());
    }

    private ProcessoMovimentacao createProcessoMovimentacaoEstoque() {
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(3383283194L);
        final Unidade unidade = new Unidade();
        unidade.setCodigo(5L);
        return new ProcessoMovimentacao(
                null,
                unidade,
                createMovimentacoesEstoque(),
                colaborador,
                new Date(System.currentTimeMillis()),
                "processo realizado com teste automatizado");
    }

    private List<Movimentacao> createMovimentacoesEstoque() {
        final List<Movimentacao> movimentacoes = new ArrayList<>();

        final OrigemAnalise analise = new OrigemAnalise();
        analise.setServicosRealizados(createServicosRealizados());
        final DestinoEstoque estoque = new DestinoEstoque();

        final Movimentacao movimentacao = new Movimentacao(
                null,
                pneuComum,
                analise,
                estoque,
                "movimentação realizada com teste automatizado");

        movimentacoes.add(movimentacao);
        return movimentacoes;
    }

    private List<ServicoRealizadoRecapadora> createServicosRealizados() {
        final List<ServicoRealizadoRecapadora> servicos = new ArrayList<>();
        final ServicoRealizadoRecapagem servicoRecapagem = createServicoRecapagem();
        servicos.add(servicoRecapagem);
        trocarBandaPneu(servicoRecapagem.getCodModeloBanda());
        servicos.add(createServicoVulcanizacao());
        return servicos;
    }

    private void trocarBandaPneu(@NotNull final Long codModeloBanda) {
        pneuComum.getBanda().getModelo().setCodigo(codModeloBanda);
    }

    private ServicoRealizadoRecapadora createServicoVulcanizacao() {
        ServicoRealizadoRecapadora vulcanizacao = new ServicoRealizadoRecapadora();
        vulcanizacao.setCodTipoServicoRecapadora(4L);
        vulcanizacao.setCodUnidade(5L);
        vulcanizacao.setCodPneu(pneuComum.getCodigo());
        vulcanizacao.setValor(new BigDecimal(150));
        vulcanizacao.setVidaMomentoRealizacaoServico(pneuComum.getVidaAtual());
        return vulcanizacao;
    }

    private ServicoRealizadoRecapagem createServicoRecapagem() {
        final ServicoRealizadoRecapagem recapagem = new ServicoRealizadoRecapagem();
        recapagem.setCodTipoServicoRecapadora(1L);
        recapagem.setCodUnidade(5L);
        recapagem.setCodPneu(pneuComum.getCodigo());
        recapagem.setValor(new BigDecimal(800));
        recapagem.setVidaMomentoRealizacaoServico(pneuComum.getVidaAtual());
        recapagem.setCodModeloBanda(12L);
        recapagem.setVidaNovaPneu(pneuComum.getVidaAtual() + 1);
        return recapagem;
    }

    private ProcessoMovimentacao createProcessoMovimentacaoAnalise() {
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(3383283194L);
        final Unidade unidade = new Unidade();
        unidade.setCodigo(5L);
        return new ProcessoMovimentacao(
                null,
                unidade,
                createMovimentacoesAnalise(),
                colaborador,
                new Date(System.currentTimeMillis()),
                "processo realizado com teste automatizado");
    }

    private List<Movimentacao> createMovimentacoesAnalise() {
        final List<Movimentacao> movimentacoes = new ArrayList<>();

        final OrigemEstoque estoque = new OrigemEstoque();
        final DestinoAnalise analise = new DestinoAnalise();
        analise.setCodigoColeta("Luizsson");
        analise.setRecapadoraDestino(createRecapadora());

        final Movimentacao movimentacao = new Movimentacao(
                null,
                pneuComum,
                estoque,
                analise,
                "movimentação realizada com teste automatizado");

        movimentacoes.add(movimentacao);
        return movimentacoes;
    }

    private Recapadora createRecapadora() {
        final Recapadora recapadora = new Recapadora();
        recapadora.setCodigo(3L);
        recapadora.setCodEmpresa(3L);
        recapadora.setNome("TESTE");
        recapadora.setAtiva(true);
        return recapadora;
    }
}
