package br.com.zalf.prolog.webservice.gente.empresa;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Equipe;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Setor;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.permissao.Visao;

import javax.ws.rs.core.NoContentException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe EmpresaService responsavel por comunicar-se com a interface DAO
 */
public class EmpresaService {

    private final EmpresaDao dao = Injection.provideEmpresaDao();
    private static final String TAG = EmpresaService.class.getSimpleName();

    public AbstractResponse insertEquipe(Long codUnidade, Equipe equipe) {
        try {
            return dao.insertEquipe(codUnidade, equipe);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inserir a equipe", e);
            return Response.error("Erro ao inserir a equipe");
        }
    }

    public Equipe getEquipe(Long codUnidade, Long codEquipe) {
        try {
            return dao.getEquipe(codUnidade, codEquipe);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a equipe. \n" +
                    "Código: %d \n" +
                    "Unidade: %d", codEquipe, codUnidade), e);
            return null;
        }
    }

    public boolean updateEquipe(Long codUnidade, Long codEquipe, Equipe equipe) {
        try {
            return dao.updateEquipe(codUnidade, codEquipe, equipe);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao atualizar a equipe. \n" +
                    "Código: %d \n" +
                    "Unidade: %d", codEquipe, codUnidade), e);
            return false;
        }
    }

    public AbstractResponse insertSetor(Long codUnidade, Setor setor) {
        try {
            return dao.insertSetor(codUnidade, setor);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao inserir o setor na unidade %d", codUnidade), e);
            return Response.error("Erro ao inserir o setor");
        }
    }

    public boolean updateSetor(Long codUnidade, Long codSetor, Setor setor) {
        try {
            return dao.updateSetor(codUnidade, codSetor, setor);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao atualizar o setor %d da unidade %d", codSetor, codUnidade), e);
            return false;
        }
    }

    public Setor getSetor(Long codUnidade, Long codSetor) {
        try {
            return dao.getSetor(codUnidade, codSetor);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o setor %d da unidade %d", codSetor, codUnidade), e);
            return null;
        }
    }

    public List<Equipe> getEquipesByCodUnidade(Long codUnidade) {
        try {
            return dao.getEquipesByCodUnidade(codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar as equipes da unidade %d", codUnidade), e);
            return null;
        }
    }

    public Cargo getCargoByCodEmpresa(Long codEmpresa, Long codCargo) {
        try {
            return dao.getCargo(codEmpresa, codCargo);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o cargo %d da empresa %d", codCargo, codEmpresa), e);
            return null;
        }
    }

    public Visao getVisaoCargo(Long codUnidade, Long codCargo) {
        try {
            return dao.getVisaoCargo(codUnidade, codCargo);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a visão do cargo %d da unidade %d", codCargo, codUnidade), e);
            return null;
        }
    }

    public Visao getVisaoUnidade(Long codUnidade) {
        try {
            return dao.getVisaoUnidade(codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a visão da unidade %d", codUnidade), e);
            return null;
        }
    }

    public List<Setor> getSetorByCodUnidade(Long codUnidade) {
        try {
            return dao.getSetorByCodUnidade(codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os setores da unidade %d", codUnidade), e);
            return null;
        }
    }

    public List<HolderMapaTracking> getResumoAtualizacaoDados(int ano, int mes, Long codUnidade) {
        try {
            return dao.getResumoAtualizacaoDados(ano, mes, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o resumo de atualização dos dados do ano: %d e mês %d da unidade %d", ano, mes, codUnidade), e);
            return new ArrayList<>();
        } catch (NoContentException e) {
            Log.e(TAG, String.format("Erro ao buscar o resumo de atualização dos dados do ano: %d e mês %d  da unidade %d", ano, mes, codUnidade), e);
            return new ArrayList<>();
        }
    }

    public List<Empresa> getFiltros(Long cpf) {
        try {
            return dao.getFiltros(cpf);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os filtrod do cpf %d", cpf), e);
            return null;
        }
    }

    public boolean alterarVisaoCargo(Visao visao, Long codUnidade, Long codCargo) {
        try {
            dao.alterarVisaoCargo(
                    codUnidade,
                    codCargo,
                    visao,
                    Injection.provideDadosIntervaloChangedListener(),
                    Injection.provideDadosChecklistOfflineChangedListener());
            return true;
        } catch (Throwable e) {
            Log.e(TAG, String.format("Erro ao alterar a visão do cargo %d da unidade %d", codCargo, codUnidade), e);
            return false;
        }
    }

    public Long getCodEquipeByCodUnidadeByNome(Long codUnidade, String nomeEquipe) {
        try {
            return dao.getCodEquipeByCodUnidadeByNome(codUnidade, nomeEquipe);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o código da equipe %s da unidade %d", nomeEquipe, codUnidade), e);
            return null;
        }
    }

    public AbstractResponse insertFuncao(Cargo cargo, Long codUnidade) {
        try {
            final Long codFuncaoInserida = dao.insertFuncao(cargo, codUnidade);
            if (codFuncaoInserida != null) {
                return ResponseWithCod.ok("Cargo inserido com sucesso", codFuncaoInserida);
            } else {
                return Response.error("Erro ao inserir o cargo");
            }
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao inserir o cargo na unidade %d", codUnidade), e);
            return Response.error("Erro ao inserir o cargo");
        }
    }
}