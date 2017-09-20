package br.com.zalf.prolog.webservice.empresa;

import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.colaborador.model.Equipe;
import br.com.zalf.prolog.webservice.colaborador.model.Setor;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Request;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.gente.controleintervalo.VersaoDadosIntervaloAtualizador;
import br.com.zalf.prolog.webservice.permissao.Visao;

import javax.ws.rs.core.NoContentException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe EmpresaService responsavel por comunicar-se com a interface DAO
 */
public class EmpresaService {

    private EmpresaDao dao = new EmpresaDaoImpl();

    public AbstractResponse insertEquipe(Long codUnidade, Equipe equipe) {
        try {
            return dao.insertEquipe(codUnidade, equipe);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Erro ao inserir a equipe");
        }
    }

    public Equipe getEquipe(Long codUnidade, Long codEquipe) {
        try {
            return dao.getEquipe(codUnidade, codEquipe);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateEquipe(Long codUnidade, Long codEquipe, Equipe equipe) {
        try {
            return dao.updateEquipe(codUnidade, codEquipe, equipe);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public AbstractResponse insertSetor(Long codUnidade, Setor setor) {
        try {
            return dao.insertSetor(codUnidade, setor);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Erro ao inserir o setor");
        }
    }

    public boolean updateSetor(Long codUnidade, Long codSetor, Setor setor) {
        try {
            return dao.updateSetor(codUnidade, codSetor, setor);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Setor getSetor(Long codUnidade, Long codSetor) {
        try {
            return dao.getSetor(codUnidade, codSetor);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Equipe> getEquipesByCodUnidade(Long codUnidade) {
        try {
            return dao.getEquipesByCodUnidade(codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Cargo> getFuncoesByCodUnidade(long codUnidade) {
        try {
            return dao.getFuncoesByCodUnidade(codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Visao getVisaoCargo(Long codUnidade, Long codCargo) {
        try {
            return dao.getVisaoCargo(codUnidade, codCargo);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Visao getVisaoUnidade(Long codUnidade) {
        try {
            return dao.getVisaoUnidade(codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Setor> getSetorByCodUnidade(Long codUnidade) {
        try {
            return dao.getSetorByCodUnidade(codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<HolderMapaTracking> getResumoAtualizacaoDados(int ano, int mes, Long codUnidade) {
        try {
            return dao.getResumoAtualizacaoDados(ano, mes, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (NoContentException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Empresa> getFiltros(Long cpf) {
        try {
            return dao.getFiltros(cpf);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean alterarVisaoCargo(Visao visao, Long codUnidade, Long codCargo) {
        try {
            return dao.alterarVisaoCargo(visao, codUnidade, codCargo, new VersaoDadosIntervaloAtualizador());
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public Long getCodEquipeByCodUnidadeByNome(Long codUnidade, String nomeEquipe) {
        try {
            return dao.getCodEquipeByCodUnidadeByNome(codUnidade, nomeEquipe);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }



    @Deprecated
    public boolean createEquipe(Request<Equipe> request) {
        try {
            return dao.createEquipe(request);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Deprecated
    public boolean updateEquipe(Request<Equipe> request) {
        try {
            return dao.updateEquipe(request);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public AbstractResponse insertFuncao(Cargo cargo, Long codUnidade) {
        try {
            Long codFuncaoInserida = dao.insertFuncao(cargo, codUnidade);
            if(codFuncaoInserida != null){
                return ResponseWithCod.ok("Cargo inserido com sucesso", codFuncaoInserida);
            }else{
                return Response.error("Erro ao inserir o cargo");
            }
        }catch (SQLException e) {
            return Response.error("Erro ao inserir o cargo");
        }
    }
}