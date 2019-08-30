package br.com.zalf.prolog.webservice.frota.pneu.pneu;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.error.PneuValidator;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.importar.PneuImportReader;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu.Dimensao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * Classe PneuService responsavel por comunicar-se com a interface DAO
 */
public class PneuService {
    private static final String TAG = PneuService.class.getSimpleName();
    private final PneuDao dao = Injection.providePneuDao();

    public AbstractResponse insert(Pneu pneu, Long codUnidade) throws ProLogException {
        try {
            PneuValidator.validacaoAtributosPneu(pneu, codUnidade);
            return ResponseWithCod.ok("Pneu inserido com sucesso", dao.insert(pneu, codUnidade));
        } catch (Throwable e) {
            final String errorMessage = "Erro ao inserir o pneu";
            Log.e(TAG, "Erro ao inserir pneu para unidade: " + codUnidade, e);
            throw Injection
                    .providePneuExceptionHandler()
                    .map(e, errorMessage);
        }
    }

    @NotNull
    public List<Long> insert(@NotNull final InputStream fileInputStream) throws ProLogException {
        try {
            return dao.insert(PneuImportReader.readFromCsv(fileInputStream));
        } catch (final Throwable throwable) {
            final String errorMessage = "Erro ao inserir pneus -- " + throwable.getMessage();
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .providePneuExceptionHandler()
                    .map(throwable, errorMessage);
        }
    }

    public void update(@NotNull final Pneu pneu, @NotNull final Long codUnidade, @NotNull final Long codOriginal) {
        try {
            dao.update(pneu, codUnidade, codOriginal);
        } catch (final Throwable throwable) {
            final String errorMessage = "Erro ao atualizar pneu: " + codOriginal;
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .providePneuExceptionHandler()
                    .map(throwable, errorMessage);
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

    public List<Pneu> getPneusByCodUnidadeByStatus(@NotNull final Long codUnidade, @NotNull final String status)
            throws ProLogException {
        try {
            if (status.equals("%")) {
                return dao.getTodosPneus(codUnidade);
            } else {
                final StatusPneu statusPneu = StatusPneu.fromString(status);
                switch (statusPneu) {
                    case ANALISE:
                        return dao.getPneusAnalise(codUnidade);
                    case EM_USO:
                    case ESTOQUE:
                    case DESCARTE:
                        return dao.getPneusByCodUnidadeByStatus(codUnidade, statusPneu);
                    default:
                        throw new IllegalArgumentException("Status de Pneu não existente: " + status);
                }
            }
        } catch (Throwable t) {
            Log.e(TAG, "Erro ao buscar os pneus da unidade: " + codUnidade + " com status: " + status, t);
            throw Injection
                    .providePneuExceptionHandler()
                    .map(t, "Erro ao buscar pneus, tente novamente");
        }
    }

    @NotNull
    public Pneu getPneuByCod(final Long codPneu, final Long codUnidade) throws ProLogException {
        try {
            return dao.getPneuByCod(codPneu, codUnidade);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar pneu com código: " + codPneu + " da unidade: " + codUnidade, e);
            throw Injection
                    .providePneuExceptionHandler()
                    .map(e, "Erro ao buscar o pneu, tente novamente");
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

    public boolean vinculaPneuVeiculo(String placaVeiculo, List<PneuComum> pneus) {
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

    public Modelo getModeloPneu(Long codModelo) {
        try {
            return dao.getModeloPneu(codModelo);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar modelo de pneu com código: " + codModelo, e);
            return null;
        }
    }

    public void marcarFotoComoSincronizada(@NotNull final Long codPneu,
                                           @NotNull final String urlFotoPneu) {
        try {
            dao.marcarFotoComoSincronizada(codPneu, urlFotoPneu);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao marcar a foto como sincronizada com URL: " + urlFotoPneu, e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public Response insertOrUpdateNomenclatura(final List<PneuNomenclaturaItem> pneuNomenclaturaItem, final String userToken) throws ProLogException {
        try {
            dao.insertOrUpdateNomenclatura(pneuNomenclaturaItem, userToken);
            return Response.ok("Nomenclatura inserida com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir nomenclatura", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir a nomenclatura, tente novamente");

        }

    }
}