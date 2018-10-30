package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.AmazonCredentialsProvider;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.error.ColaboradorExceptionHandler;
import br.com.zalf.prolog.webservice.colaborador.error.ColaboradorValidator;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.LoginHolder;
import br.com.zalf.prolog.webservice.colaborador.model.LoginRequest;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.AmazonCredentialsException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ControleJornadaDao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ControleIntervaloService;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.IntervaloOfflineSupport;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoMarcacao;
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
    private final ColaboradorDao dao = Injection.provideColaboradorDao();
    private final ColaboradorExceptionHandler exceptionHandler = Injection.provideColaboradorExceptionHandler();

    public void insert(Colaborador colaborador) throws ProLogException {
        try {
            ColaboradorValidator.validacaoAtributosColaborador(colaborador);
            dao.insert(colaborador, Injection.provideDadosIntervaloChangedListener());
        } catch (Throwable e) {
            final String errorMessage = "Erro ao inserir o colaborador";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    public void update(Long cpfAntigo, Colaborador colaborador) throws ProLogException {
        try {
            ColaboradorValidator.validacaoAtributosColaborador(colaborador);
            dao.update(cpfAntigo, colaborador, Injection.provideDadosIntervaloChangedListener());
        } catch (Throwable e) {
            final String errorMessage = "Erro ao inserir colaborador";
            Log.e(TAG, String.format("Erro ao atualizar o colaborador com o cpfAntigo: %d", cpfAntigo), e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    public boolean updateStatus(Long cpf, Colaborador colaborador) {
        try {
            dao.updateStatus(cpf, colaborador);
            return true;
        } catch (Throwable e) {
            Log.e(TAG, String.format("Erro ao atualizar o status do colaborador %d", cpf), e);
            return false;
        }
    }

    public boolean delete(Long cpf) {
        try {
            dao.delete(cpf, Injection.provideDadosIntervaloChangedListener());
            return true;
        } catch (Throwable e) {
            Log.e(TAG, String.format("Erro ao deletar o colaborador %d", cpf), e);
            return false;
        }
    }

    public Colaborador getByCpf(Long cpf) {
        try {
            return dao.getByCpf(cpf, false);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o colaborador %d", cpf), e);
            return null;
        }
    }

    public Long getCodUnidadeByCpf(@NotNull final Long cpf) {
        try {
            return dao.getCodUnidadeByCpf(cpf);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o código da unidade para o CPF: %d", cpf), e);
            return null;
        }
    }

    @NotNull
    public List<Colaborador> getAllByUnidade(Long codUnidade, boolean apenasAtivos) throws ProLogException {
        try {
            return dao.getAllByUnidade(codUnidade, apenasAtivos);
        } catch (final Throwable e) {
            final String errorMessage = "Erro ao buscar os colaboradores";
            Log.e(TAG, String.format("Erro ao buscar todos os colaboradores da unidade %d", codUnidade), e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    public List<Colaborador> getAllByEmpresa(final Long codEmrpesa, final boolean apenasAtivos) throws ProLogException {
        try {
            return dao.getAllByEmpresa(codEmrpesa, apenasAtivos);
        } catch (final Throwable e) {
            final String errorMessage = "Erro ao buscar os colaboradores";
            Log.e(TAG, String.format("Erro ao buscar todos os colaboradores da empresa %d", codEmrpesa), e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    public List<Colaborador> getMotoristasAndAjudantes(Long codUnidade) {
        try {
            return dao.getMotoristasAndAjudantes(codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar todos os ajudantes e motoristas da unidade %d", codUnidade), e);
            return null;
        }
    }

    public LoginHolder getLoginHolder(LoginRequest loginRequest) {
        final LoginHolder loginHolder = new LoginHolder();
        try {
            loginHolder.setColaborador(dao.getByCpf(loginRequest.getCpf(), true));
            final Colaborador colaborador = loginHolder.getColaborador();

            // Se usuário tem acesso aos relatos, precisamos também setar essas informações no LoginHolder.
            if (colaborador.getVisao().hasAccessToFunction(Pilares.SEGURANCA, Pilares.Seguranca.Relato.REALIZAR)) {
                loginHolder.setAmazonCredentials(new AmazonCredentialsProvider().getAmazonCredentials());
                final RelatoDao relatoDao = Injection.provideRelatoDao();
                loginHolder.setAlternativasRelato(relatoDao.getAlternativas(
                        colaborador.getUnidade().getCodigo(),
                        colaborador.getSetor().getCodigo()));
            } else if (colaborador.getVisao().hasAccessToFunction(Pilares.FROTA, Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_GERAL)) {
                loginHolder.setAmazonCredentials(new AmazonCredentialsProvider().getAmazonCredentials());
            } else if (colaborador.getVisao().hasAccessToFunction(Pilares.FROTA, Pilares.Frota.Pneu.CADASTRAR)) {
                loginHolder.setAmazonCredentials(new AmazonCredentialsProvider().getAmazonCredentials());
            }

            final ControleIntervaloService intervaloService = new ControleIntervaloService();
            final IntervaloOfflineSupport intervaloOfflineSupport = intervaloService.getIntervaloOfflineSupport(
                    this,
                    colaborador.getUnidade().getCodigo(),
                    loginRequest.getVersaoDadosIntervalo());
            loginHolder.setIntervaloOfflineSupport(intervaloOfflineSupport);

        } catch (SQLException | AmazonCredentialsException e) {
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
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar colaboradores com acesso a uma determinada " +
                    "função(%d) de uma determinada unidade(%d)", codFuncaoProLog, codUnidade), e);
            throw new RuntimeException("Erro ao buscar colaboradores com acesso a unidade");
        }
    }

    @Deprecated
    public LoginHolder getLoginHolder(Long cpf) {
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
                final ControleJornadaDao dao = Injection.provideControleJornadaDao();
                final List<TipoMarcacao> tiposIntervalo = dao.getTiposIntervalosByUnidade(
                        colaborador.getUnidade().getCodigo(),
                        true,
                        true);
                loginHolder.setTiposIntervalos(tiposIntervalo);
            }
        } catch (SQLException | AmazonCredentialsException e) {
            Log.e(TAG, String.format("Erro ao criar LoginHolder para o cpf %d", cpf), e);
            throw new RuntimeException("Erro ao criar LoginHolder");
        }
        return loginHolder;
    }
}