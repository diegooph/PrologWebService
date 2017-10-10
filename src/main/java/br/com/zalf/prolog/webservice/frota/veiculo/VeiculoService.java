package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.veiculo.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.integracao.router.RouterVeiculo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Classe VeiculoService responsavel por comunicar-se com a interface DAO
 */
public class VeiculoService {

    private final VeiculoDao dao = Injection.provideVeiculoDao();

    public List<Veiculo> getVeiculosAtivosByUnidade(String userToken, Long codUnidade) {
        try {
            return RouterVeiculo
                    .create(dao, userToken)
                    .getVeiculosAtivosByUnidade(codUnidade);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar os veículos ativos da unidade: " + codUnidade);
        }
    }

    public List<TipoVeiculo> getTipoVeiculosByUnidade(String userToken, Long codUnidade) {
        try {
            return RouterVeiculo
                    .create(dao, userToken)
                    .getTipoVeiculosByUnidade(codUnidade);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar os tipos de veículo da unidade: " + codUnidade);
        }
    }

    public Veiculo getVeiculoByPlaca(String placa, boolean withPneus) {
        try {
            return dao.getVeiculoByPlaca(placa, withPneus);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean insertTipoVeiculo(TipoVeiculo tipoVeiculo, Long codUnidade) {
        try {
            return dao.insertTipoVeiculo(tipoVeiculo, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Eixos> getEixos() {
        try {
            return dao.getEixos();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(Long cpf) {
        try {
            return dao.getVeiculosAtivosByUnidadeByColaborador(cpf);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean update(Veiculo veiculo, String placaOriginal) {
        try {
            return dao.update(veiculo, placaOriginal);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String placa) {
        try {
            return dao.delete(placa);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert(Veiculo veiculo, Long codUnidade) {
        try {
            return dao.insert(veiculo, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Marca> getMarcaModeloVeiculoByCodEmpresa(Long codEmpresa) {
        try {
            return dao.getMarcaModeloVeiculoByCodEmpresa(codEmpresa);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean insertModeloVeiculo(Modelo modelo, long codEmpresa, long codMarca) {
        try {
            return dao.insertModeloVeiculo(modelo, codEmpresa, codMarca);
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Set<DiagramaVeiculo> getDiagramasVeiculo() {
        try {
            return dao.getDiagramasVeiculos();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getVeiculosByTipo(Long codUnidade, String codTipo) {
        try {
            return dao.getVeiculosByTipo(codUnidade, codTipo);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Modelo getModeloVeiculo(Long codUnidade, Long codModelo) {
        try {
            return dao.getModeloVeiculo(codUnidade, codModelo);
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateModelo(Modelo modelo, Long codUnidade, Long codMarca) {
        try {
            return dao.updateModelo(modelo, codUnidade, codMarca);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteModelo(Long codModelo, Long codUnidade) {
        try {
            return dao.deleteModelo(codModelo, codUnidade);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTipoVeiculo(TipoVeiculo tipo, Long codUnidade) {
        try {
            return dao.updateTipoVeiculo(tipo, codUnidade);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTipoVeiculo(Long codTipo, Long codUnidade) {
        try {
            return dao.deleteTipoVeiculo(codTipo, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public TipoVeiculo getTipoVeiculo(Long codTipo, Long codUnidade) {
        try {
            return dao.getTipoVeiculo(codTipo, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
