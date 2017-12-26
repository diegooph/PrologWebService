package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoDao;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoDaoImpl;
import br.com.zalf.prolog.webservice.colaborador.ColaboradorDao;
import br.com.zalf.prolog.webservice.colaborador.ColaboradorDaoImpl;
import br.com.zalf.prolog.webservice.empresa.EmpresaDao;
import br.com.zalf.prolog.webservice.empresa.EmpresaDaoImpl;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDao;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDaoImpl;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistDao;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistDaoImpl;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OrdemServicoDao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OrdemServicoDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.MovimentacaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.MovimentacaoDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDaoImpl;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDaoImpl;
import br.com.zalf.prolog.webservice.gente.controleintervalo.DadosIntervaloChangedListener;
import br.com.zalf.prolog.webservice.gente.controleintervalo.VersaoDadosIntervaloAtualizador;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDaoImpl;

/**
 * Provide a simple way to use DI by hand.
 */
public final class Injection {

    private Injection() {
        throw new IllegalStateException(Injection.class.getSimpleName() + " cannot be instantiated!");
    }

    // ================================================
    // DAOS
    // ================================================
    public static ChecklistDao provideChecklistDao() {
        return new ChecklistDaoImpl();
    }

    public static VeiculoDao provideVeiculoDao() {
        return new VeiculoDaoImpl();
    }

    public static IntegracaoDao provideIntegracaoDao() {
        return new IntegracaoDaoImpl();
    }

    public static AfericaoDao provideAfericaoDao() {
        return new AfericaoDaoImpl();
    }

    public static ColaboradorDao provideColaboradorDao() {
        return new ColaboradorDaoImpl();
    }

    public static PneuDao providePneuDao() {
        return new PneuDaoImpl();
    }

    public static OrdemServicoDao provideOrdemServicoDao() {
        return new OrdemServicoDaoImpl();
    }

    public static EmpresaDao provideEmpresaDao() {
        return new EmpresaDaoImpl();
    }

    public static IndicadorDao provideIndicadorDao() {
        return new IndicadorDaoImpl();
    }

    public static ServicoDao provideServicoDao() {
        return new ServicoDaoImpl();
    }

    public static MovimentacaoDao provideMovimentacaoDao() {
        return new MovimentacaoDaoImpl();
    }

    public static AutenticacaoDao provideAutenticacaoDao() {
        return new AutenticacaoDaoImpl();
    }

    // ================================================
    // OUTROS
    // ================================================
    public static DadosIntervaloChangedListener provideDadosIntervaloChangedListener() {
        return new VersaoDadosIntervaloAtualizador();
    }
}