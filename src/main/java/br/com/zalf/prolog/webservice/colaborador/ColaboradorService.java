package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ControleIntervaloService;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.IntervaloOfflineSupport;
import br.com.zalf.prolog.webservice.permissao.Visao;
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
			return dao.insert(colaborador);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean update(Long cpfAntigo, Colaborador colaborador) {
		try {
			return dao.update(cpfAntigo, colaborador);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean delete(Long cpf) {
		try {
			return dao.delete(cpf);
		} catch (SQLException e) {
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
			final Visao visao = loginHolder.getColaborador().getVisao();

			// Se usuário tem acesso aos relatos, precisamos também setar essas informações no LoginHolder.
			if (visao.hasAccessToFunction(Pilares.SEGURANCA, Pilares.Seguranca.Relato.REALIZAR)) {
//				loginHolder.setAmazonCredentials(getAmazonCredentials()); TODO!!
				final RelatoDao relatoDao = new RelatoDaoImpl();
				loginHolder.setAlternativasRelato(relatoDao.getAlternativas(
						loginHolder.getColaborador().getCodUnidade(),
						loginHolder.getColaborador().getSetor().getCodigo()));
			}

			final ControleIntervaloService intervaloService = new ControleIntervaloService();
			final IntervaloOfflineSupport intervaloOfflineSupport = intervaloService.getIntervaloOfflineSupport(
					loginRequest.getVersaoDadosIntervalo(),
					dao.getCodUnidadeByCpf(loginRequest.getCpf()),
					this);
			loginHolder.setIntervaloOfflineSupport(intervaloOfflineSupport);

		} catch (SQLException e) {
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
}