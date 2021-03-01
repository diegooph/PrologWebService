package br.com.zalf.prolog.webservice.gente.empresa;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Equipe;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Setor;
import br.com.zalf.prolog.webservice.integracao.router.RouterEmpresa;
import br.com.zalf.prolog.webservice.permissao.Visao;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.NoContentException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe EmpresaService responsavel por comunicar-se com a interface DAO
 */
public class EmpresaService {

    private static final String TAG = EmpresaService.class.getSimpleName();
    private final EmpresaDao dao = Injection.provideEmpresaDao();

    public AbstractResponse insertEquipe(final Long codUnidade, final Equipe equipe) {
        try {
            return dao.insertEquipe(codUnidade, equipe);
        } catch (final SQLException e) {
            Log.e(TAG, "Erro ao inserir a equipe", e);
            return Response.error("Erro ao inserir a equipe");
        }
    }

    public Equipe getEquipe(final Long codUnidade, final Long codEquipe) {
        try {
            return dao.getEquipe(codUnidade, codEquipe);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a equipe. \n" +
                                             "Código: %d \n" +
                                             "Unidade: %d", codEquipe, codUnidade), e);
            return null;
        }
    }

    public boolean updateEquipe(final Long codUnidade, final Long codEquipe, final Equipe equipe) {
        try {
            return dao.updateEquipe(codUnidade, codEquipe, equipe);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao atualizar a equipe. \n" +
                                             "Código: %d \n" +
                                             "Unidade: %d", codEquipe, codUnidade), e);
            return false;
        }
    }

    public AbstractResponse insertSetor(final Long codUnidade, final Setor setor) {
        try {
            return dao.insertSetor(codUnidade, setor);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao inserir o setor na unidade %d", codUnidade), e);
            return Response.error("Erro ao inserir o setor");
        }
    }

    public boolean updateSetor(final Long codUnidade, final Long codSetor, final Setor setor) {
        try {
            return dao.updateSetor(codUnidade, codSetor, setor);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao atualizar o setor %d da unidade %d", codSetor, codUnidade), e);
            return false;
        }
    }

    public Setor getSetor(final Long codUnidade, final Long codSetor) {
        try {
            return dao.getSetor(codUnidade, codSetor);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o setor %d da unidade %d", codSetor, codUnidade), e);
            return null;
        }
    }

    public List<Equipe> getEquipesByCodUnidade(final Long codUnidade) {
        try {
            return dao.getEquipesByCodUnidade(codUnidade);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar as equipes da unidade %d", codUnidade), e);
            return null;
        }
    }

    public Cargo getCargoByCodEmpresa(final Long codEmpresa, final Long codCargo) {
        try {
            return dao.getCargo(codEmpresa, codCargo);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o cargo %d da empresa %d", codCargo, codEmpresa), e);
            return null;
        }
    }

    public Visao getVisaoCargo(final Long codUnidade, final Long codCargo) {
        try {
            return dao.getVisaoCargo(codUnidade, codCargo);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a visão do cargo %d da unidade %d", codCargo, codUnidade), e);
            return null;
        }
    }

    public Visao getVisaoUnidade(final Long codUnidade) {
        try {
            return dao.getVisaoUnidade(codUnidade);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a visão da unidade %d", codUnidade), e);
            return null;
        }
    }

    public List<Setor> getSetorByCodUnidade(final Long codUnidade) {
        try {
            return dao.getSetorByCodUnidade(codUnidade);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os setores da unidade %d", codUnidade), e);
            return null;
        }
    }

    public List<HolderMapaTracking> getResumoAtualizacaoDados(final int ano, final int mes, final Long codUnidade) {
        try {
            return dao.getResumoAtualizacaoDados(ano, mes, codUnidade);
        } catch (final SQLException e) {
            Log.e(TAG,
                  String.format("Erro ao buscar o resumo de atualização dos dados do ano: %d e mês %d da unidade %d",
                                ano,
                                mes,
                                codUnidade),
                  e);
            return new ArrayList<>();
        } catch (final NoContentException e) {
            Log.e(TAG,
                  String.format("Erro ao buscar o resumo de atualização dos dados do ano: %d e mês %d  da unidade %d",
                                ano,
                                mes,
                                codUnidade),
                  e);
            return new ArrayList<>();
        }
    }

    public List<Empresa> getFiltros(@NotNull final String userToken, final Long cpf) throws ProLogException {
        try {
            return RouterEmpresa
                    .create(dao, userToken)
                    .getFiltros(cpf);
        } catch (final Throwable throwable) {
            final String errorMessage = String.format("Erro ao buscar os filtro do cpf %d", cpf);
            Log.e(TAG, errorMessage, throwable);
            throw Injection.provideProLogExceptionHandler().map(throwable, errorMessage);
        }
    }

    public boolean alterarVisaoCargo(final Visao visao, final Long codUnidade, final Long codCargo) {
        try {
            dao.alterarVisaoCargo(
                    codUnidade,
                    codCargo,
                    visao,
                    Injection.provideDadosIntervaloChangedListener(),
                    Injection.provideDadosChecklistOfflineChangedListener());
            return true;
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao alterar a visão do cargo %d da unidade %d", codCargo, codUnidade), e);
            return false;
        }
    }

    public Long getCodEquipeByCodUnidadeByNome(final Long codUnidade, final String nomeEquipe) {
        try {
            return dao.getCodEquipeByCodUnidadeByNome(codUnidade, nomeEquipe);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o código da equipe %s da unidade %d", nomeEquipe, codUnidade), e);
            return null;
        }
    }

    public AbstractResponse insertFuncao(final Cargo cargo, final Long codUnidade) {
        try {
            final Long codFuncaoInserida = dao.insertFuncao(cargo, codUnidade);
            if (codFuncaoInserida != null) {
                return ResponseWithCod.ok("Cargo inserido com sucesso", codFuncaoInserida);
            } else {
                return Response.error("Erro ao inserir o cargo");
            }
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao inserir o cargo na unidade %d", codUnidade), e);
            return Response.error("Erro ao inserir o cargo");
        }
    }
}