package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
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
    private static final String TAG = VeiculoService.class.getSimpleName();

    public List<Veiculo> getVeiculosAtivosByUnidade(String userToken, Long codUnidade) {
        try {
            return RouterVeiculo
                    .create(dao, userToken)
                    .getVeiculosAtivosByUnidade(codUnidade);
        } catch (Exception e) {
            Log.e(TAG, String.format("Erro ao buscar os veículos ativos da unidade. \n" +
                    "Unidade: %d \n" +
                    "userToken: %s", codUnidade, userToken), e);
            throw new RuntimeException("Erro ao buscar os veículos ativos da unidade: " + codUnidade);
        }
    }

    public List<TipoVeiculo> getTipoVeiculosByUnidade(String userToken, Long codUnidade) {
        try {
            return RouterVeiculo
                    .create(dao, userToken)
                    .getTiposVeiculosByUnidade(codUnidade);
        } catch (Exception e) {
            Log.e(TAG, String.format("Erro ao buscar os tipos de veículos ativos da unidade. \n" +
                    "Unidade: %d \n" +
                    "userToken: %s", codUnidade, userToken), e);
            throw new RuntimeException("Erro ao buscar os tipos de veículo da unidade: " + codUnidade);
        }
    }

    public Veiculo getVeiculoByPlaca(String placa, boolean withPneus) {
        try {
            return dao.getVeiculoByPlaca(placa, withPneus);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o veículo. \n" +
                    "Placa: %s \n" +
                    "withPneus: %b", placa, withPneus), e);
            return null;
        }
    }

    public boolean insertTipoVeiculo(TipoVeiculo tipoVeiculo, Long codUnidade) {
        try {
            return dao.insertTipoVeiculo(tipoVeiculo, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao inserir o tipo de veículo. \n" +
                    "Unidade: %d \n", codUnidade), e);
            return false;
        }
    }

    public List<Eixos> getEixos() {
        try {
            return dao.getEixos();
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar os eixos", e);
            return null;
        }
    }

    public List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(Long cpf) {
        try {
            return dao.getVeiculosAtivosByUnidadeByColaborador(cpf);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os veículos ativos da unidade do colaborador. \n" +
                    "cpf: %s", cpf), e);
            return null;
        }
    }

    public boolean update(Veiculo veiculo, String placaOriginal) {
        try {
            return dao.update(veiculo, placaOriginal);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao atualizar o veículo. \n" +
                    "placaOriginal: %s", placaOriginal), e);
            return false;
        }
    }

    public boolean delete(String placa) {
        try {
            return dao.delete(placa);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao deletar o veículo. \n" +
                    "placa: %s", placa), e);
            return false;
        }
    }

    public boolean insert(Veiculo veiculo, Long codUnidade) {
        try {
            return dao.insert(veiculo, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao inserir o veículo. \n" +
                    "Unidade: %d", codUnidade), e);
            return false;
        }
    }

    public List<Marca> getMarcaModeloVeiculoByCodEmpresa(Long codEmpresa) {
        try {
            return dao.getMarcaModeloVeiculoByCodEmpresa(codEmpresa);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar as marcas e modelos dos veículos. \n" +
                    "Empresa: %d", codEmpresa), e);
            return new ArrayList<>();
        }
    }

    public boolean insertModeloVeiculo(Modelo modelo, long codEmpresa, long codMarca) {
        try {
            return dao.insertModeloVeiculo(modelo, codEmpresa, codMarca);
        } catch (SQLException | NullPointerException e) {
            Log.e(TAG, String.format("Erro ao inserir o modelo de veículo. \n" +
                    "Empresa: %d \n" +
                    "codMarca: %d", codEmpresa, codMarca), e);
            return false;
        }
    }

    public Set<DiagramaVeiculo> getDiagramasVeiculo() {
        try {
            return dao.getDiagramasVeiculos();
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar os diagramas dos veículos.", e);
            return null;
        }
    }

    public List<String> getVeiculosByTipo(Long codUnidade, String codTipo, String userToken) {
        try {
            return RouterVeiculo
                    .create(dao, userToken)
                    .getPlacasVeiculosByTipo(codUnidade, codTipo);
        } catch (Exception e) {
            Log.e(TAG, String.format("Erro ao buscar os veículos de um tipo específico. \n" +
                    "codUnidade: %d \n" +
                    "codTipo: %s \n" +
                    "userToken: %s", codUnidade, codTipo, userToken), e);
            throw new RuntimeException("Erro ao buscar placas dos veículos para o tipo: " + codTipo + " e unidade: " + codUnidade);
        }
    }

    public Modelo getModeloVeiculo(Long codUnidade, Long codModelo) {
        try {
            return dao.getModeloVeiculo(codUnidade, codModelo);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar um modelo de veículo. \n" +
                    "codUnidade: %d \n" +
                    "codModelo: %s \n", codUnidade, codModelo), e);
            return null;
        }
    }

    public boolean updateModelo(Modelo modelo, Long codUnidade, Long codMarca) {
        try {
            return dao.updateModelo(modelo, codUnidade, codMarca);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao atualizar o modelo de veículo. \n" +
                    "codUnidade: %d \n" +
                    "codMarca: %d", codUnidade, codMarca), e);
            return false;
        }
    }

    public boolean deleteModelo(Long codModelo, Long codUnidade) {
        try {
            return dao.deleteModelo(codModelo, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao deletar o modelo de veículo. \n" +
                    "codUnidade: %d \n" +
                    "codModelo: %d", codUnidade, codModelo), e);
            return false;
        }
    }

    public boolean updateTipoVeiculo(TipoVeiculo tipo, Long codUnidade) {
        try {
            return dao.updateTipoVeiculo(tipo, codUnidade);
        } catch (SQLException e) {
          Log.e(TAG, String.format("Erro ao atualizar o tipo de veículo. \n" +
                  "codUnidade: %d", codUnidade), e);
            return false;
        }
    }

    public boolean deleteTipoVeiculo(Long codTipo, Long codUnidade) {
        try {
            return dao.deleteTipoVeiculo(codTipo, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao deletar o tipo de veículo. \n" +
                    "codUnidade: %d \n" +
                    "codTipo: %d", codUnidade, codTipo), e);
            return false;
        }
    }

    public TipoVeiculo getTipoVeiculo(Long codTipo, Long codUnidade) {
        try {
            return dao.getTipoVeiculo(codTipo, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o tipo de veículo. \n" +
                    "codUnidade: %d \n" +
                    "codTipo: %d", codUnidade, codTipo), e);
            return null;
        }
    }
}
