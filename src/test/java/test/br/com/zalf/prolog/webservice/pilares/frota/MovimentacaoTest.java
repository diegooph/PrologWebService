package test.br.com.zalf.prolog.webservice.pilares.frota;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.PneuService;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.PneuAnalise;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.MovimentacaoService;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.DestinoAnalise;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.DestinoEstoque;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.OrigemAnalise;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.OrigemEstoque;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizadoIncrementaVida;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.Recapadora;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

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
    private static final String TOKEN_COLABORADOR = "TOKEN";
    private static final long COD_PNEU_TESTE = 2399L;

    private MovimentacaoService movimentacaoService;
    private PneuService pneuService;
    private Pneu pneuComum;

    @Override
    public void initialize() throws Throwable {
        movimentacaoService = new MovimentacaoService();
        pneuService = new PneuService();
        pneuComum = pneuService.getPneuByCod(COD_PNEU_TESTE, 5L);
    }

    @Test
    public void testInsertMovimentacaoEstoqueToAnalise() throws ProLogException {
        // Move pneu ESTOQUE --> ANALISE
        final ProcessoMovimentacao processoMovimentacaoAnalise = createProcessoMovimentacaoAnalise();
        final AbstractResponse response = movimentacaoService.insert(TOKEN_COLABORADOR, processoMovimentacaoAnalise);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatus());
        Assert.assertEquals("OK", response.getStatus());

        // Valida informações do pneu
        final List<Pneu> pneusAnalise =
                pneuService.getPneusByCodUnidadeByStatus(5L, StatusPneu.ANALISE.asString());

        PneuAnalise pneuAnalise = null;
        for (final Pneu pneu : pneusAnalise) {
            if (pneu.getCodigo() == COD_PNEU_TESTE) {
                pneuAnalise = (PneuAnalise) pneu;
                break;
            }
        }
        Assert.assertNotNull(pneuAnalise);
        Assert.assertEquals(pneuAnalise.getStatus(), StatusPneu.ANALISE);

        Assert.assertNotNull(pneuAnalise.getCodigoColeta());
        Assert.assertNotNull(pneuAnalise.getRecapadora());
        Assert.assertNotNull(pneuAnalise.getRecapadora().getNome());
        Assert.assertNotNull(pneuAnalise.getRecapadora().getCodigo());
        Assert.assertNotNull(pneuAnalise.getRecapadora().getCodEmpresa());

        Assert.assertEquals("Luizsson", pneuAnalise.getCodigoColeta());
        Assert.assertEquals("TESTE", pneuAnalise.getRecapadora().getNome());
        Assert.assertEquals(new Long(2), pneuAnalise.getRecapadora().getCodigo());
        Assert.assertEquals(new Long(3), pneuAnalise.getRecapadora().getCodEmpresa());
    }

    @Test
    public void testInsertMovimentacaoAnaliseToEstoque() throws ProLogException {
        // Move o mesmo pneu ANALISE --> ESTOQUE
        final ProcessoMovimentacao processoMovimentacaoEstoque = createProcessoMovimentacaoEstoque();
        final AbstractResponse response = movimentacaoService.insert(TOKEN_COLABORADOR, processoMovimentacaoEstoque);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatus());
        Assert.assertEquals("OK", response.getStatus());

        final Pneu pneuRetorno = pneuService.getPneuByCod(COD_PNEU_TESTE, 5L);

        Assert.assertNotNull(pneuRetorno);
        Assert.assertEquals(pneuRetorno.getStatus(), StatusPneu.ESTOQUE);
    }

    @Test
    public void testInsertPneuCreateSegundaVida() throws Throwable {
        final Pneu pneu = pneuService.getPneuByCod(COD_PNEU_TESTE, 5L);
        pneu.setCodigoCliente(pneu.getCodigoCliente().concat("1"));
        pneu.setDot("1310");
        pneu.setValor(new BigDecimal(2250));
        pneu.getBanda().setValor(new BigDecimal(399));
        final AbstractResponse response = pneuService.insert(TOKEN_COLABORADOR, 5L, pneu, false);
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

        final OrigemAnalise analise = new OrigemAnalise(createServicosRealizados());
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

    private List<PneuServicoRealizado> createServicosRealizados() {
        final List<PneuServicoRealizado> servicos = new ArrayList<>();
        final PneuServicoRealizadoIncrementaVida servicoRecapagem = createServicoRecapagem();
        servicos.add(servicoRecapagem);
        trocarBandaPneu(servicoRecapagem.getCodModeloBanda());
        servicos.add(createServicoVulcanizacao());
        return servicos;
    }

    private void trocarBandaPneu(@NotNull final Long codModeloBanda) {
        pneuComum.getBanda().getModelo().setCodigo(codModeloBanda);
    }

    private PneuServicoRealizado createServicoVulcanizacao() {
        final PneuServicoRealizado vulcanizacao = new PneuServicoRealizado();
        vulcanizacao.setCodPneuTipoServico(2L);
        vulcanizacao.setCodUnidade(5L);
        vulcanizacao.setCodPneu(pneuComum.getCodigo());
        vulcanizacao.setCusto(new BigDecimal(150));
        vulcanizacao.setVidaMomentoRealizacaoServico(pneuComum.getVidaAtual());
        return vulcanizacao;
    }

    private PneuServicoRealizadoIncrementaVida createServicoRecapagem() {
        final PneuServicoRealizadoIncrementaVida recapagem = new PneuServicoRealizadoIncrementaVida();
        recapagem.setCodPneuTipoServico(1L);
        recapagem.setCodUnidade(5L);
        recapagem.setCodPneu(pneuComum.getCodigo());
        recapagem.setCusto(new BigDecimal(800));
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
        final DestinoAnalise analise = new DestinoAnalise(createRecapadora(), "Luizsson");

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
        recapadora.setCodigo(2L);
        recapadora.setCodEmpresa(3L);
        recapadora.setNome("TESTE");
        recapadora.setAtiva(true);
        return recapadora;
    }
}
