package br.com.zalf.prolog.webservice.gente.colaborador;

import br.com.zalf.prolog.webservice.AmazonCredentialsProvider;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.autenticacao._model.token.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistService;
import br.com.zalf.prolog.webservice.frota.checklist.offline.ChecklistOfflineService;
import br.com.zalf.prolog.webservice.gente.colaborador.model.*;
import br.com.zalf.prolog.webservice.gente.controlejornada.ControleJornadaService;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.IntervaloOfflineSupport;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacaoDao;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.seguranca.relato.RelatoDao;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Classe ColaboradorService responsavel por comunicar-se com a interface DAO
 */
public class ColaboradorService {
    private static final String TAG = ColaboradorService.class.getSimpleName();
    @NotNull
    private final ColaboradorDao dao = Injection.provideColaboradorDao();

    public void insert(@NotNull final ColaboradorInsercao colaborador, @NotNull final String userToken) {
        try {
            dao.insert(
                    colaborador,
                    Injection.provideDadosIntervaloChangedListener(),
                    Injection.provideDadosChecklistOfflineChangedListener(),
                    TokenCleaner.getOnlyToken(userToken));
        } catch (final Throwable e) {
            final String errorMessage = "Erro ao inserir o colaborador, tente novamente.";
            Log.e(TAG, errorMessage, e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, errorMessage);
        }
    }

    public void update(@NotNull final ColaboradorEdicao colaborador, @NotNull final String userToken) {
        try {
            dao.update(
                    colaborador,
                    Injection.provideDadosIntervaloChangedListener(),
                    Injection.provideDadosChecklistOfflineChangedListener(),
                    TokenCleaner.getOnlyToken(userToken));
        } catch (final Throwable e) {
            final String errorMessage = "Erro ao atualizar colaborador, tente novamente.";
            Log.e(TAG, String.format("Erro ao atualizar o colaborador de código: %d", colaborador.getCodigo()), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, errorMessage);
        }
    }

    public boolean updateStatus(final Long cpf, final Colaborador colaborador) {
        try {
            dao.updateStatus(cpf, colaborador, Injection.provideDadosChecklistOfflineChangedListener());
            return true;
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao atualizar o status do colaborador %d", cpf), e);
            return false;
        }
    }

    public boolean delete(final Long cpf) {
        try {
            dao.delete(
                    cpf,
                    Injection.provideDadosIntervaloChangedListener(),
                    Injection.provideDadosChecklistOfflineChangedListener());
            return true;
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao deletar o colaborador %d", cpf), e);
            return false;
        }
    }

    @NotNull
    public Colaborador getByCpf(final Long cpf) throws ProLogException {
        try {
            return dao.getByCpf(cpf, false);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar o colaborador com CPF %d", cpf), throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar colaborador, tente novamente");
        }
    }

    @NotNull
    public Colaborador getByToken(final String token) throws ProLogException {
        try {
            return dao.getByToken(token);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar o colaborador com token %s", token), throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar colaborador, tente novamente");
        }
    }

    public Long getCodUnidadeByCpf(@NotNull final Long cpf) {
        try {
            return dao.getCodUnidadeByCpf(cpf);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o código da unidade para o CPF: %d", cpf), e);
            return null;
        }
    }

    @NotNull
    public List<Colaborador> getAllByUnidade(final Long codUnidade, final boolean apenasAtivos) throws ProLogException {
        try {
            return dao.getAllByUnidade(codUnidade, apenasAtivos);
        } catch (final Throwable e) {
            final String errorMessage = "Erro ao buscar os colaboradores";
            Log.e(TAG, String.format("Erro ao buscar todos os colaboradores da unidade %d", codUnidade), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, errorMessage);
        }
    }

    @NotNull
    public List<ColaboradorListagem> getAllByUnidades(@NotNull final List<Long> codUnidades,
                                                      final boolean apenasAtivos) {
        try {
            return dao.getAllByUnidades(codUnidades, apenasAtivos);
        } catch (final Throwable e) {
            final String errorMessage = "Erro ao buscar os colaboradores.";
            Log.e(TAG, String.format(errorMessage), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, errorMessage);
        }
    }

    public List<Colaborador> getAllByEmpresa(final Long codEmrpesa, final boolean apenasAtivos) throws ProLogException {
        try {
            return dao.getAllByEmpresa(codEmrpesa, apenasAtivos);
        } catch (final Throwable e) {
            final String errorMessage = "Erro ao buscar os colaboradores";
            Log.e(TAG, String.format("Erro ao buscar todos os colaboradores da empresa %d", codEmrpesa), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, errorMessage);
        }
    }

    public List<Colaborador> getMotoristasAndAjudantes(final Long codUnidade) {
        try {
            return dao.getMotoristasAndAjudantes(codUnidade);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar todos os ajudantes e motoristas da unidade %d", codUnidade), e);
            return null;
        }
    }

    @NotNull
    public LoginHolder getLoginHolder(@NotNull final LoginRequest loginRequest) {
        final LoginHolder loginHolder = new LoginHolder();
        try {
            loginHolder.setColaborador(dao.getByCpf(loginRequest.getCpf(), true));
            final Colaborador colaborador = loginHolder.getColaborador();

            // Em agosto de 2019 foi desenvolvido a funcionalidade de gestão de dispositivos móveis no ProLog e o
            // Pilar GERAL foi criado contendo essa funcionalidade. O aplicativo Android não estava preparado para um
            // novo pilar e impedia o login caso qualquer pilar diferente do 1, 2, 3 e 4 fosse enviado.
            // Esse comportamento do app também foi alterado em agosto/19, porém, para manter compatibilidade com apps
            // antigos, nós removemos o Pilar GERAL para não quebrar o login.
            // ATENÇÃO: devemos deixar essa remoção acontecendo por, pelo menos, 1 ano.
            colaborador.getVisao().removePilar(Pilares.GERAL);

            // Se usuário tem acesso aos relatos, precisamos também setar essas informações no LoginHolder.
            if (colaborador.getVisao().hasAccessToFunction(Pilares.Seguranca.Relato.REALIZAR)) {
                loginHolder.setAmazonCredentials(new AmazonCredentialsProvider().getAmazonCredentials());
                final RelatoDao relatoDao = Injection.provideRelatoDao();
                loginHolder.setAlternativasRelato(relatoDao.getAlternativas(
                        colaborador.getUnidade().getCodigo(),
                        colaborador.getSetor().getCodigo()));
            } else if (colaborador.getVisao()
                    .hasAccessToFunction(Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE)
                    || colaborador.getVisao().hasAccessToFunction(Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE)
                    || colaborador.getVisao()
                    .hasAccessToFunction(Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE)) {
                loginHolder.setAmazonCredentials(new AmazonCredentialsProvider().getAmazonCredentials());
            } else if (colaborador.getVisao().hasAccessToFunction(Pilares.Frota.Pneu.CADASTRAR)) {
                loginHolder.setAmazonCredentials(new AmazonCredentialsProvider().getAmazonCredentials());
            }

            final ControleJornadaService controleJornadaService = new ControleJornadaService();
            final IntervaloOfflineSupport intervaloOfflineSupport = controleJornadaService.getIntervaloOfflineSupport(
                    loginRequest.getVersaoDadosIntervalo(),
                    colaborador.getUnidade().getCodigo(),
                    this);
            loginHolder.setIntervaloOfflineSupport(intervaloOfflineSupport);

            final ChecklistOfflineService checklistOfflineService = new ChecklistOfflineService();
            final boolean checklistOfflineAtivoEmpresa =
                    checklistOfflineService.getChecklistOfflineAtivoEmpresa(colaborador.getCodEmpresa());
            loginHolder.setChecklistOfflineAtivoEmpresa(checklistOfflineAtivoEmpresa);

            final ChecklistService checklistService = new ChecklistService();
            final boolean checklistDiferentesUnidadesAtivoEmpresa =
                    checklistService.getChecklistDiferentesUnidadesAtivoEmpresa(colaborador.getCodEmpresa());
            loginHolder.setChecklistDiferentesUnidadesAtivoEmpresa(checklistDiferentesUnidadesAtivoEmpresa);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar o loginHolder", e);
            throw new RuntimeException("Erro ao criar LoginHolder");
        }

        return loginHolder;
    }

    @NotNull
    public List<Colaborador> getColaboradoresComAcessoFuncaoByUnidade(@NotNull final Long codUnidade,
                                                                      final int codFuncaoProLog) {
        try {
            return dao.getColaboradoresComAcessoFuncaoByUnidade(codUnidade, codFuncaoProLog);
        } catch (final SQLException e) {
            Log.e(TAG,
                  String.format("Erro ao buscar colaboradores com acesso a uma determinada " +
                                        "função(%d) de uma determinada unidade(%d)", codFuncaoProLog, codUnidade),
                  e);
            throw new RuntimeException("Erro ao buscar colaboradores com acesso a unidade");
        }
    }

    @Deprecated
    public LoginHolder getLoginHolder(final Long cpf) {
        final LoginHolder loginHolder = new LoginHolder();
        try {
            loginHolder.setColaborador(dao.getByCpf(cpf, true));
            final Colaborador colaborador = loginHolder.getColaborador();

            // Se usuário tem acesso aos relatos, precisamos também setar essas informações no LoginHolder.
            if (colaborador.getVisao().hasAccessToFunction(Pilares.SEGURANCA, Pilares.Seguranca.Relato.REALIZAR)) {
                loginHolder.setAmazonCredentials(new AmazonCredentialsProvider().getAmazonCredentials());
                final RelatoDao relatoDao = Injection.provideRelatoDao();
                loginHolder.setAlternativasRelato(relatoDao.getAlternativas(
                        colaborador.getCodUnidade(),
                        colaborador.getSetor().getCodigo()));
            }

            // Se usuário tem acesso a marcação de intervalo, precisamos setar os tipos de intervalo também.
            if (colaborador.getVisao().hasAccessToFunction(Pilares.GENTE, Pilares.Gente.Intervalo.MARCAR_INTERVALO)) {
                final TipoMarcacaoDao dao = Injection.provideTipoMarcacaoDao();
                final List<TipoMarcacao> tiposIntervalo = dao.getTiposMarcacoes(
                        colaborador.getUnidade().getCodigo(),
                        true,
                        true);
                loginHolder.setTiposIntervalos(tiposIntervalo);
            }
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao criar LoginHolder para o cpf %d", cpf), t);
            throw new RuntimeException("Erro ao criar LoginHolder");
        }
        return loginHolder;
    }

    @NotNull
    public Long getCodColaboradorByCpf(@NotNull final Long codEmpresa, @NotNull final String cpfColaborador)
            throws Throwable {
        try {
            return dao.getCodColaboradorByCpfAndCodEmpresa(codEmpresa, cpfColaborador);
        } catch (final SQLException e) {
            Log.e(TAG,
                  String.format("Erro ao buscar o código do colaborador " +
                                        "codEmpresa: %d, cpfColaborador: %s", codEmpresa, cpfColaborador),
                  e);
            throw new RuntimeException("Erro ao buscar colaboradores com acesso a unidade");
        }
    }
}