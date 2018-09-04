package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.app.AppDao;
import br.com.zalf.prolog.webservice.app.AppDaoImpl;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoDao;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoDaoImpl;
import br.com.zalf.prolog.webservice.colaborador.ColaboradorDao;
import br.com.zalf.prolog.webservice.colaborador.ColaboradorDaoImpl;
import br.com.zalf.prolog.webservice.colaborador.error.ColaboradorExceptionHandler;
import br.com.zalf.prolog.webservice.colaborador.error.ColaboradorSqlExceptionTranslator;
import br.com.zalf.prolog.webservice.contato.EntreEmContatoDao;
import br.com.zalf.prolog.webservice.contato.EntreEmContatoDaoImpl;
import br.com.zalf.prolog.webservice.dashboard.DashboardDao;
import br.com.zalf.prolog.webservice.dashboard.DashboardDaoImpl;
import br.com.zalf.prolog.webservice.empresa.EmpresaDao;
import br.com.zalf.prolog.webservice.empresa.EmpresaDaoImpl;
import br.com.zalf.prolog.webservice.entrega.escaladiaria.EscalaDiariaDao;
import br.com.zalf.prolog.webservice.entrega.escaladiaria.EscalaDiariaDaoImpl;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDao;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDaoImpl;
import br.com.zalf.prolog.webservice.entrega.mapa.MapaDao;
import br.com.zalf.prolog.webservice.entrega.mapa.MapaDaoImpl;
import br.com.zalf.prolog.webservice.entrega.metas.MetasDao;
import br.com.zalf.prolog.webservice.entrega.metas.MetasDaoImpl;
import br.com.zalf.prolog.webservice.entrega.produtividade.ProdutividadeDao;
import br.com.zalf.prolog.webservice.entrega.produtividade.ProdutividadeDaoImpl;
import br.com.zalf.prolog.webservice.entrega.produtividade.relatorio.ProdutividadeRelatorioDao;
import br.com.zalf.prolog.webservice.entrega.produtividade.relatorio.ProdutividadeRelatorioDaoImpl;
import br.com.zalf.prolog.webservice.entrega.relatorio.RelatorioEntregaDao;
import br.com.zalf.prolog.webservice.entrega.relatorio.RelatorioEntregaDaoImpl;
import br.com.zalf.prolog.webservice.entrega.tracking.TrackingDao;
import br.com.zalf.prolog.webservice.entrega.tracking.TrackingDaoImpl;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.errorhandling.sql.ProLogSqlExceptionTranslator;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistDao;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistDaoImpl;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloDao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloDaoImpl;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OrdemServicoDao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OrdemServicoDaoImpl;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.relatorios.RelatoriosOrdemServicoDao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.relatorios.RelatoriosOrdemServicoDaoImpl;
import br.com.zalf.prolog.webservice.frota.checklist.relatorios.ChecklistRelatorioDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao.ConfiguracaoAfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao.ConfiguracaoAfericaoDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios.AfericaoRelatorioDao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios.AfericaoRelatorioDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.MovimentacaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.MovimentacaoDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.error.PneuExceptionHandler;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.error.PneuSqlExceptionTranslator;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.RecapadoraDao;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.RecapadoraDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico.PneuServicoRealizadoDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico.PneuServicoRealizadoDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico.PneuTipoServicoDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico.PneuTipoServicoDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.RelatorioPneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.RelatorioPneuDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.servico.relatorio.ServicoRelatorioDao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.relatorio.ServicoRelatorioDaoImpl;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDaoImpl;
import br.com.zalf.prolog.webservice.frota.veiculo.error.VeiculoExceptionHandler;
import br.com.zalf.prolog.webservice.frota.veiculo.error.VeiculoSqlExceptionTranslator;
import br.com.zalf.prolog.webservice.frota.veiculo.relatorio.RelatorioVeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.relatorio.RelatorioVeiculoDaoImpl;
import br.com.zalf.prolog.webservice.gente.calendario.CalendarioDao;
import br.com.zalf.prolog.webservice.gente.calendario.CalendarioDaoImpl;
import br.com.zalf.prolog.webservice.gente.contracheque.ContrachequeDao;
import br.com.zalf.prolog.webservice.gente.contracheque.ContrachequeDaoImpl;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ControleIntervaloDao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ControleIntervaloDaoImpl;
import br.com.zalf.prolog.webservice.gente.controleintervalo.DadosIntervaloChangedListener;
import br.com.zalf.prolog.webservice.gente.controleintervalo.VersaoDadosIntervaloAtualizador;
import br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios.ControleIntervaloRelatorioDaoImpl;
import br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios.ControleIntervaloRelatoriosDao;
import br.com.zalf.prolog.webservice.gente.faleConosco.FaleConoscoDao;
import br.com.zalf.prolog.webservice.gente.faleConosco.FaleConoscoDaoImpl;
import br.com.zalf.prolog.webservice.gente.faleConosco.relatorios.FaleConoscoRelatorioDao;
import br.com.zalf.prolog.webservice.gente.faleConosco.relatorios.FaleConoscoRelatorioDaoImpl;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.ProntuarioCondutorDao;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.ProntuarioCondutorDaoImpl;
import br.com.zalf.prolog.webservice.gente.quiz.modelo.QuizModeloDao;
import br.com.zalf.prolog.webservice.gente.quiz.modelo.QuizModeloDaoImpl;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.QuizDao;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.QuizDaoImpl;
import br.com.zalf.prolog.webservice.gente.quiz.relatorios.QuizRelatorioDao;
import br.com.zalf.prolog.webservice.gente.quiz.relatorios.QuizRelatorioDaoImpl;
import br.com.zalf.prolog.webservice.gente.solicitacaoFolga.SolicitacaoFolgaDao;
import br.com.zalf.prolog.webservice.gente.solicitacaoFolga.SolicitacaoFolgaDaoImpl;
import br.com.zalf.prolog.webservice.gente.solicitacaoFolga.relatorios.SolicitacaoFolgaRelatorioDao;
import br.com.zalf.prolog.webservice.gente.solicitacaoFolga.relatorios.SolicitacaoFolgaRelatorioDaoImpl;
import br.com.zalf.prolog.webservice.gente.treinamento.TreinamentoDao;
import br.com.zalf.prolog.webservice.gente.treinamento.TreinamentoDaoImpl;
import br.com.zalf.prolog.webservice.gente.treinamento.relatorios.TreinamentoRelatorioDao;
import br.com.zalf.prolog.webservice.gente.treinamento.relatorios.TreinamentoRelatorioDaoImpl;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDaoImpl;
import br.com.zalf.prolog.webservice.log.LogDao;
import br.com.zalf.prolog.webservice.log.LogDaoImpl;
import br.com.zalf.prolog.webservice.raizen.produtividade.RaizenProdutividadeDao;
import br.com.zalf.prolog.webservice.raizen.produtividade.RaizenProdutividadeDaoImpl;
import br.com.zalf.prolog.webservice.raizen.produtividade.relatorios.RaizenProdutividadeRelatorioDao;
import br.com.zalf.prolog.webservice.raizen.produtividade.relatorios.RaizenProdutividadeRelatorioDaoImpl;
import br.com.zalf.prolog.webservice.seguranca.relato.RelatoDao;
import br.com.zalf.prolog.webservice.seguranca.relato.RelatoDaoImpl;
import br.com.zalf.prolog.webservice.seguranca.relato.relatorio.RelatoRelatorioDao;
import br.com.zalf.prolog.webservice.seguranca.relato.relatorio.RelatoRelatorioDaoImpl;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    public static ChecklistDao provideChecklistDao() {
        return new ChecklistDaoImpl();
    }

    @NotNull
    public static VeiculoDao provideVeiculoDao() {
        return new VeiculoDaoImpl();
    }

    @NotNull
    public static IntegracaoDao provideIntegracaoDao() {
        return new IntegracaoDaoImpl();
    }

    @NotNull
    public static AfericaoDao provideAfericaoDao() {
        return new AfericaoDaoImpl();
    }

    @NotNull
    public static AfericaoRelatorioDao provideAfericaoRelatorioDao(){
        return new AfericaoRelatorioDaoImpl();
    }

    @NotNull
    public static ColaboradorDao provideColaboradorDao() {
        return new ColaboradorDaoImpl();
    }

    @NotNull
    public static PneuDao providePneuDao() {
        return new PneuDaoImpl();
    }

    @NotNull
    public static OrdemServicoDao provideOrdemServicoDao() {
        return new OrdemServicoDaoImpl();
    }

    @NotNull
    public static EmpresaDao provideEmpresaDao() {
        return new EmpresaDaoImpl();
    }

    @NotNull
    public static IndicadorDao provideIndicadorDao() {
        return new IndicadorDaoImpl();
    }

    @NotNull
    public static ServicoDao provideServicoDao() {
        return new ServicoDaoImpl();
    }

    @NotNull
    public static MovimentacaoDao provideMovimentacaoDao() {
        return new MovimentacaoDaoImpl();
    }

    @NotNull
    public static AutenticacaoDao provideAutenticacaoDao() {
        return new AutenticacaoDaoImpl();
    }

    @NotNull
    public static AppDao provideAppDao() {
        return new AppDaoImpl();
    }

    @NotNull
    public static MetasDao provideMetasDao() {
        return new MetasDaoImpl();
    }

    @NotNull
    public static ProdutividadeRelatorioDao provideProdutividadeRelatorioDao() {
        return new ProdutividadeRelatorioDaoImpl();
    }

    @NotNull
    public static ProdutividadeDao provideProdutividadeDao() {
        return new ProdutividadeDaoImpl();
    }

    @NotNull
    public static RelatorioEntregaDao provideRelatorioEntregaDao() {
        return new RelatorioEntregaDaoImpl();
    }

    @NotNull
    public static ChecklistModeloDao provideChecklistModeloDao() {
        return new ChecklistModeloDaoImpl();
    }

    @NotNull
    public static RelatoriosOrdemServicoDao provideRelatoriosOrdemServicoDao() {
        return new RelatoriosOrdemServicoDaoImpl();
    }

    @NotNull
    public static ChecklistRelatorioDaoImpl provideChecklistRelatorioDao() {
        return new ChecklistRelatorioDaoImpl();
    }

    @NotNull
    public static RelatorioPneuDao provideRelatorioPneuDao() {
        return new RelatorioPneuDaoImpl();
    }

    @NotNull
    public static CalendarioDao provideCalendarioDao() {
        return new CalendarioDaoImpl();
    }

    @NotNull
    public static ContrachequeDao provideContrachequeDao() {
        return new ContrachequeDaoImpl();
    }

    @NotNull
    public static ControleIntervaloDao provideControleIntervaloDao() {
        return new ControleIntervaloDaoImpl();
    }

    @NotNull
    public static ControleIntervaloRelatoriosDao provideControleIntervaloRelatoriosDao() {
        return new ControleIntervaloRelatorioDaoImpl();
    }

    @NotNull
    public static FaleConoscoDao provideFaleConoscoDao() {
        return new FaleConoscoDaoImpl();
    }

    @NotNull
    public static FaleConoscoRelatorioDao provideFaleConoscoRelatorioDao() {
        return new FaleConoscoRelatorioDaoImpl();
    }

    @NotNull
    public static ProntuarioCondutorDao provideProntuarioCondutorDao() {
        return new ProntuarioCondutorDaoImpl();
    }

    @NotNull
    public static QuizDao provideQuizDao() {
        return new QuizDaoImpl();
    }

    @NotNull
    public static QuizModeloDao provideQuizModeloDao() {
        return new QuizModeloDaoImpl();
    }

    @NotNull
    public static QuizRelatorioDao provideQuizRelatorioDao() {
        return new QuizRelatorioDaoImpl();
    }

    @NotNull
    public static SolicitacaoFolgaDao provideSolicitacaoFolgaDao() {
        return new SolicitacaoFolgaDaoImpl();
    }

    @NotNull
    public static SolicitacaoFolgaRelatorioDao provideSolicitacaoFolgaRelatorioDao() {
        return new SolicitacaoFolgaRelatorioDaoImpl();
    }

    @NotNull
    public static TreinamentoDao provideTreinamentoDao() {
        return new TreinamentoDaoImpl();
    }

    @NotNull
    public static TreinamentoRelatorioDao provideTreinamentoRelatorioDao() {
        return new TreinamentoRelatorioDaoImpl();
    }

    @NotNull
    public static MapaDao provideMapaDao() {
        return new MapaDaoImpl();
    }

    @NotNull
    public static TrackingDao provideTrackingDao() {
        return new TrackingDaoImpl();
    }

    @NotNull
    public static LogDao provideLogDao() {
        return new LogDaoImpl();
    }

    @NotNull
    public static RelatoDao provideRelatoDao() {
        return new RelatoDaoImpl();
    }

    @NotNull
    public static RelatoRelatorioDao provideRelatoRelatorioDao() {
        return new RelatoRelatorioDaoImpl();
    }

    @NotNull
    public static DashboardDao provideDashboardDao() {
        return new DashboardDaoImpl();
    }

    @NotNull
    public static RelatorioVeiculoDao provideRelatorioVeiculoDao() {
        return new RelatorioVeiculoDaoImpl();
    }

    @NotNull
    public static ServicoRelatorioDao provideServicoRelatorioDao() {
        return new ServicoRelatorioDaoImpl();
    }

    @NotNull
    public static EscalaDiariaDao provideEscalaDiariaDao() {
        return new EscalaDiariaDaoImpl();
    }

    @NotNull
    public static ConfiguracaoAfericaoDao provideConfiguracaoAfericaoDao() {
        return new ConfiguracaoAfericaoDaoImpl();
    }

    @NotNull
    public static RecapadoraDao provideRecapadoraDao() {
        return new RecapadoraDaoImpl();
    }

    @NotNull
    public static PneuTipoServicoDao providePneuTipoServicoDao() {
        return new PneuTipoServicoDaoImpl();
    }

    @NotNull
    public static PneuServicoRealizadoDao providePneuServicoRealizadoDao() {
        return new PneuServicoRealizadoDaoImpl();
    }

    @NotNull
    public static EntreEmContatoDao provideEntreEmContatoDao() {
        return new EntreEmContatoDaoImpl();
    }

    @NotNull
    public static RaizenProdutividadeDao provideRaizenProdutividadeDao() {
        return new RaizenProdutividadeDaoImpl();
    }

    @NotNull
    public static RaizenProdutividadeRelatorioDao provideRaizenProdutividadeRelatorioDao() {
        return new RaizenProdutividadeRelatorioDaoImpl();
    }

    // ================================================
    // OUTROS
    // ================================================
    @NotNull
    public static DadosIntervaloChangedListener provideDadosIntervaloChangedListener() {
        return new VersaoDadosIntervaloAtualizador();
    }

    @NotNull
    public static ProLogExceptionHandler provideProLogExceptionHandler() {
        return new ProLogExceptionHandler(provideProLogSqlExceptionTranslator());
    }

    @NotNull
    private static ProLogSqlExceptionTranslator provideProLogSqlExceptionTranslator() {
        return new ProLogSqlExceptionTranslator();
    }

    @NotNull
    public static ColaboradorExceptionHandler provideColaboradorExceptionHandler() {
        return new ColaboradorExceptionHandler(provideColaboradorSqlExceptionTranslator());
    }

    @NotNull
    private static ColaboradorSqlExceptionTranslator provideColaboradorSqlExceptionTranslator() {
        return new ColaboradorSqlExceptionTranslator();
    }

    @NotNull
    public static VeiculoExceptionHandler provideVeiculoExceptionHandler(){
        return new VeiculoExceptionHandler(provideVeiculoSqlExceptionTranslator());
    }

    @NotNull
    private static VeiculoSqlExceptionTranslator provideVeiculoSqlExceptionTranslator(){
        return new VeiculoSqlExceptionTranslator();
    }

    @NotNull
    public static PneuExceptionHandler providePneuExceptionHandler() {
        return new PneuExceptionHandler(providePneuSqlExceptionTranslator());
    }

    @NotNull
    private static PneuSqlExceptionTranslator providePneuSqlExceptionTranslator() {
        return new PneuSqlExceptionTranslator();
    }
}