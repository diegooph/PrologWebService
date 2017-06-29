package br.com.zalf.prolog.webservice.frota.pneu.pneu;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.ModeloBanda;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu.Dimensao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe PneuService responsavel por comunicar-se com a interface DAO
 */
public class PneuService {

    private PneuDao dao = new PneuDaoImpl();


    public boolean insert(Pneu pneu, Long codUnidade) {
        try {
            return dao.insert(pneu, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Pneu pneu, Long codUnidade, Long codOriginal) {
        try {
            return dao.update(pneu, codUnidade, codOriginal);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertModeloPneu(Modelo modelo, long codEmpresa, long codMarca) {
        try {
            return dao.insertModeloPneu(modelo, codEmpresa, codMarca);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Pneu> getPneuByCodUnidadeByStatus(Long codUnidade, String status) {
        try {
            return dao.getPneuByCodUnidadeByStatus(codUnidade, status);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Marca> getMarcaModeloPneuByCodEmpresa(Long codEmpresa) {
        try {
            return dao.getMarcaModeloPneuByCodEmpresa(codEmpresa);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Dimensao> getDimensoes() {
        try {
            return dao.getDimensoes();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean vinculaPneuVeiculo(String placaVeiculo, List<Pneu> pneus) {
        try {
            return dao.vinculaPneuVeiculo(placaVeiculo, pneus);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Marca> getMarcaModeloBanda(Long codEmpresa) {
        try {
            return dao.getMarcaModeloBanda(codEmpresa);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AbstractResponse insertMarcaBanda(Marca marca, Long codEmpresa) {
        try {
            return ResponseWithCod.Ok("Marca inserida com sucesso", dao.insertMarcaBanda(marca, codEmpresa));
        } catch (SQLException e) {
            return Response.Error("Erro ao inserir a marca da banda");
        }
    }

    public AbstractResponse insertModeloBanda(ModeloBanda modelo, Long codMarcaBanda, Long codEmpresa) {
        try {
            return ResponseWithCod.Ok("Modelo inserido com sucesso", dao.insertModeloBanda(modelo, codMarcaBanda, codEmpresa));
        } catch (SQLException e) {
            return Response.Error("Erro ao inserir o modelo da banda");
        }
    }

    public boolean updateMarcaBanda(Marca marca, Long codEmpresa) {
        try {
            return dao.updateMarcaBanda(marca, codEmpresa);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateModeloBanda(Modelo modelo) {
        try {
            return dao.updateModeloBanda(modelo);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Pneu getPneuByCod(Long codPneu, Long codUnidade) {
        try {
            return dao.getPneuByCod(codPneu, codUnidade);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public Modelo getModeloPneu(Long codModelo) {
        try {
            return dao.getModeloPneu(codModelo);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}