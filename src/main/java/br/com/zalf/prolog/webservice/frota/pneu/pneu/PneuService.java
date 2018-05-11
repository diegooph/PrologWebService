package br.com.zalf.prolog.webservice.frota.pneu.pneu;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.ModeloBanda;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.ModeloPneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu.Dimensao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Classe PneuService responsavel por comunicar-se com a interface DAO
 */
public class PneuService {
    private static final String TAG = PneuService.class.getSimpleName();
    private final PneuDao dao = Injection.providePneuDao();

    public boolean insert(Pneu pneu, Long codUnidade) {
        try {
            return dao.insert(pneu, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inserir pneu para unidade: " + codUnidade, e);
            return false;
        }
    }

    public boolean update(Pneu pneu, Long codUnidade, Long codOriginal) {
        try {
            return dao.update(pneu, codUnidade, codOriginal);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao atualizar pneu com código: " + codOriginal + " da unidade: " + codUnidade, e);
            return false;
        }
    }

    public AbstractResponse insertModeloPneu(ModeloPneu modelo, Long codEmpresa, Long codMarca) {
        try {
            return ResponseWithCod.ok("Modelo inserido com sucesso", dao.insertModeloPneu(modelo, codEmpresa,
                    codMarca));
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inserir modelo de pneu. Empresa: " + codEmpresa + " Marca: " + codMarca, e);
            return Response.error("Erro ao inserir o modelo de pneu");
        }
    }

    public List<Pneu> getPneuByCodUnidadeByStatus(Long codUnidade, String status) {
        try {
            return dao.getPneusByCodUnidadeByStatus(codUnidade, status);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar os pneus da unidade: " + codUnidade + " com status: " + status, e);
            return null;
        }
    }

    public List<Marca> getMarcaModeloPneuByCodEmpresa(Long codEmpresa) {
        try {
            return dao.getMarcaModeloPneuByCodEmpresa(codEmpresa);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar as marcas de pneu da empresa: " + codEmpresa, e);
            return null;
        }
    }

    public List<Dimensao> getDimensoes() {
        try {
            return dao.getDimensoes();
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar dimensões de pneus", e);
            return null;
        }
    }

    public boolean vinculaPneuVeiculo(String placaVeiculo, List<Pneu> pneus) {
        try {
            return dao.vinculaPneuVeiculo(placaVeiculo, pneus);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao vincular pneus ao veículo: " + placaVeiculo, e);
            return false;
        }
    }

    public List<Marca> getMarcaModeloBanda(Long codEmpresa) {
        try {
            return dao.getMarcaModeloBanda(codEmpresa);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar marcas de banda da empresa: " + codEmpresa, e);
            return null;
        }
    }

    public AbstractResponse insertMarcaBanda(Marca marca, Long codEmpresa) {
        try {
            return ResponseWithCod.ok("Marca inserida com sucesso", dao.insertMarcaBanda(marca, codEmpresa));
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inserir marca de banda para empresa: " + codEmpresa, e);
            return Response.error("Erro ao inserir a marca da banda");
        }
    }

    public AbstractResponse insertModeloBanda(ModeloBanda modelo, Long codMarcaBanda, Long codEmpresa) {
        try {
            return ResponseWithCod.ok("Modelo inserido com sucesso", dao.insertModeloBanda(modelo, codMarcaBanda,
                    codEmpresa));
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inserir modelo de banda para marca: " + codMarcaBanda + " Empresa: " + codEmpresa, e);
            return Response.error("Erro ao inserir o modelo da banda");
        }
    }

    public boolean updateMarcaBanda(Marca marca, Long codEmpresa) {
        try {
            return dao.updateMarcaBanda(marca, codEmpresa);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao atualizar marca de banda da empresa: " + codEmpresa, e);
            return false;
        }
    }

    public boolean updateModeloBanda(Modelo modelo) {
        try {
            return dao.updateModeloBanda(modelo);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao atualizar modelo de banda", e);
            return false;
        }
    }

    public Pneu getPneuByCod(Long codPneu, Long codUnidade) {
        try {
            return dao.getPneuByCod(codPneu, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar pneu com código: " + codPneu + " da unidade: " + codUnidade, e);
            return null;
        }
    }

    public Modelo getModeloPneu(Long codModelo) {
        try {
            return dao.getModeloPneu(codModelo);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar modelo de pneu com código: " + codModelo, e);
            return null;
        }
    }

    public void marcarFotoComoSincronizada(@NotNull final Long codUnidade,
                                           @NotNull final Long codPneu,
                                           @NotNull final String urlFotoPneu) {
        try {
            dao.marcarFotoComoSincronizada(codUnidade, codPneu, urlFotoPneu);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao marcar a foto como sincronizada com URL: " + urlFotoPneu, e);
            throw new RuntimeException(e);
        }
    }
}