package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.AmazonCredentialsProvider;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.LoginHolder;
import br.com.zalf.prolog.webservice.colaborador.model.LoginRequest;
import br.com.zalf.prolog.webservice.errorhandling.exception.AmazonCredentialsException;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ControleIntervaloDao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ControleIntervaloDaoImpl;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ControleIntervaloService;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.IntervaloOfflineSupport;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.seguranca.relato.RelatoDao;
import br.com.zalf.prolog.webservice.seguranca.relato.RelatoDaoImpl;
import com.sun.istack.internal.NotNull;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Classe ColaboradorService responsavel por comunicar-se com a interface DAO
 */
public class ColaboradorService {

	private final ColaboradorDao dao = Injection.provideColaboradorDao();

	public boolean insert(Colaborador colaborador) {
		try {
			dao.insert(colaborador, Injection.provideDadosIntervaloChangedListener());
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean update(Long cpfAntigo, Colaborador colaborador) {
		try {
			dao.update(cpfAntigo, colaborador, Injection.provideDadosIntervaloChangedListener());
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean delete(Long cpf) {
		try {
			dao.delete(cpf, Injection.provideDadosIntervaloChangedListener());
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	public Colaborador getByCod(Long cpf) {
		try {
			return dao.getByCpf(cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Colaborador> getAll(Long codUnidade) {
		try {
			return dao.getAll(codUnidade);
		} catch (SQLException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	public List<Colaborador> getMotoristasAndAjudantes(Long codUnidade) {
		try {
			return dao.getMotoristasAndAjudantes(codUnidade);
		}catch (SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public LoginHolder getLoginHolder(LoginRequest loginRequest) {
		final LoginHolder loginHolder = new LoginHolder();
		try {
			loginHolder.setColaborador(dao.getByCpf(loginRequest.getCpf()));
			final Colaborador colaborador = loginHolder.getColaborador();

			// Se usuário tem acesso aos relatos, precisamos também setar essas informações no LoginHolder.
			if (colaborador.getVisao().hasAccessToFunction(Pilares.SEGURANCA, Pilares.Seguranca.Relato.REALIZAR)) {
				loginHolder.setAmazonCredentials(new AmazonCredentialsProvider().getAmazonCredentials());
				final RelatoDao relatoDao = new RelatoDaoImpl();
				loginHolder.setAlternativasRelato(relatoDao.getAlternativas(
						colaborador.getCodUnidade(),
						colaborador.getSetor().getCodigo()));
			}

			final ControleIntervaloService intervaloService = new ControleIntervaloService();
			final IntervaloOfflineSupport intervaloOfflineSupport = intervaloService.getIntervaloOfflineSupport(
					loginRequest.getVersaoDadosIntervalo(),
					colaborador.getUnidade().getCodigo(),
					this);
			loginHolder.setIntervaloOfflineSupport(intervaloOfflineSupport);

		} catch (SQLException | AmazonCredentialsException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao criar LoginHolder");
		}

		return loginHolder;
	}

	public List<Colaborador> getColaboradoresComAcessoFuncaoByUnidade(final int codFuncaoProLog,
																	  @NotNull final Long codUnidade) {
		try {
			return dao.getColaboradoresComAcessoFuncaoByUnidade(codFuncaoProLog, codUnidade);
		}catch (SQLException e){
			e.printStackTrace();
			throw new RuntimeException("Erro ao buscar colaboradores com acesso a unidade");
		}
	}

	@Deprecated
	public LoginHolder getLoginHolder(Long cpf) {
		final LoginHolder loginHolder = new LoginHolder();
		try {
			loginHolder.setColaborador(dao.getByCpf(cpf));
			final Colaborador colaborador = loginHolder.getColaborador();

			// Se usuário tem acesso aos relatos, precisamos também setar essas informações no LoginHolder.
			if (colaborador.getVisao().hasAccessToFunction(Pilares.SEGURANCA, Pilares.Seguranca.Relato.REALIZAR)) {
				loginHolder.setAmazonCredentials(new AmazonCredentialsProvider().getAmazonCredentials());
				final RelatoDao relatoDao = new RelatoDaoImpl();
				loginHolder.setAlternativasRelato(relatoDao.getAlternativas(
						colaborador.getCodUnidade(),
						colaborador.getSetor().getCodigo()));
			}

			// Se usuário tem acesso a marcação de intervalo, precisamos setar os tipos de intervalo também.
			if (colaborador.getVisao().hasAccessToFunction(Pilares.GENTE, Pilares.Gente.Intervalo.MARCAR_INTERVALO)) {
				final ControleIntervaloDao dao = new ControleIntervaloDaoImpl();
				final List<TipoIntervalo> tiposIntervalo = dao.getTiposIntervalosByUnidade(
						colaborador.getUnidade().getCodigo(),
						true);
				loginHolder.setTiposIntervalos(tiposIntervalo);
			}
		} catch (SQLException | AmazonCredentialsException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao criar LoginHolder");
		}
		return loginHolder;
	}
}