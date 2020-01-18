package br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo;

import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiMarcaBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiMarcaPneu;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiModeloBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiModeloPneu;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 19/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class ApiMarcaModeloCreator {

    private ApiMarcaModeloCreator() {
        throw new IllegalStateException(ApiMarcaModeloCreator.class.getSimpleName() + "cannot be instantiated");
    }

    @NotNull
    static List<ApiMarcaPneu> createMarcasPneu(@NotNull final ResultSet rSet) throws SQLException {
        if (rSet.next()) {
            final List<ApiMarcaPneu> marcasPneu = new ArrayList<>();
            List<ApiModeloPneu> modelosPneu = new ArrayList<>();
            ApiMarcaPneu marcaPneu = null;
            do {
                if (marcaPneu == null) {
                    marcaPneu = ApiMarcaModeloCreator.createApiMarcaPneu(rSet, modelosPneu);
                    if (temModeloPneu(rSet)) {
                        modelosPneu.add(ApiMarcaModeloCreator.createApiModeloPneu(rSet));
                    }
                } else {
                    if (marcaPneu.getCodigo() == rSet.getLong("COD_MARCA_PNEU")) {
                        if (temModeloPneu(rSet)) {
                            modelosPneu.add(ApiMarcaModeloCreator.createApiModeloPneu(rSet));
                        }
                    } else {
                        marcasPneu.add(marcaPneu);
                        modelosPneu = new ArrayList<>();
                        if (temModeloPneu(rSet)) {
                            modelosPneu.add(ApiMarcaModeloCreator.createApiModeloPneu(rSet));
                        }
                        marcaPneu = ApiMarcaModeloCreator.createApiMarcaPneu(rSet, modelosPneu);
                    }
                }
            } while (rSet.next());
            marcasPneu.add(marcaPneu);
            return marcasPneu;
        } else {
            return Collections.emptyList();
        }
    }

    @NotNull
    static List<ApiModeloPneu> createModelosPneu(@NotNull final ResultSet rSet) throws SQLException {
        if (rSet.next()) {
            final List<ApiModeloPneu> modelosPneu = new ArrayList<>();
            do {
                modelosPneu.add(ApiMarcaModeloCreator.createApiModeloPneu(rSet));
            } while (rSet.next());
            return modelosPneu;
        } else {
            return Collections.emptyList();
        }
    }

    @NotNull
    static List<ApiMarcaBanda> createMarcasBanda(@NotNull final ResultSet rSet) throws SQLException {
        if (rSet.next()) {
            final List<ApiMarcaBanda> marcasBanda = new ArrayList<>();
            List<ApiModeloBanda> modelosBanda = new ArrayList<>();
            ApiMarcaBanda marcaBanda = null;
            do {
                if (marcaBanda == null) {
                    marcaBanda = ApiMarcaModeloCreator.createApiMarcaBanda(rSet, modelosBanda);
                    if (temModeloBanda(rSet)) {
                        modelosBanda.add(ApiMarcaModeloCreator.createApiModeloBanda(rSet));
                    }
                } else {
                    if (marcaBanda.getCodigo() == rSet.getLong("COD_MARCA_BANDA")) {
                        if (temModeloBanda(rSet)) {
                            modelosBanda.add(ApiMarcaModeloCreator.createApiModeloBanda(rSet));
                        }
                    } else {
                        marcasBanda.add(marcaBanda);
                        modelosBanda = new ArrayList<>();
                        if (temModeloBanda(rSet)) {
                            modelosBanda.add(ApiMarcaModeloCreator.createApiModeloBanda(rSet));
                        }
                        marcaBanda = ApiMarcaModeloCreator.createApiMarcaBanda(rSet, modelosBanda);
                    }
                }
            } while (rSet.next());
            marcasBanda.add(marcaBanda);
            return marcasBanda;
        } else {
            return Collections.emptyList();
        }
    }

    @NotNull
    static List<ApiModeloBanda> createModelosBanda(@NotNull final ResultSet rSet) throws SQLException {
        if (rSet.next()) {
            final List<ApiModeloBanda> modelosBanda = new ArrayList<>();
            do {
                modelosBanda.add(ApiMarcaModeloCreator.createApiModeloBanda(rSet));
            } while (rSet.next());
            return modelosBanda;
        } else {
            return Collections.emptyList();
        }
    }

    @NotNull
    private static ApiMarcaBanda createApiMarcaBanda(
            @NotNull final ResultSet rSet,
            @NotNull final List<ApiModeloBanda> modelosBanda) throws SQLException {
        return new ApiMarcaBanda(
                rSet.getLong("COD_MARCA_BANDA"),
                rSet.getString("NOME_MARCA_BANDA"),
                modelosBanda,
                rSet.getBoolean("STATUS_ATIVO_MARCA_BADA"));
    }

    @NotNull
    private static ApiModeloBanda createApiModeloBanda(@NotNull final ResultSet rSet) throws SQLException {
        return new ApiModeloBanda(
                rSet.getLong("COD_MARCA_BANDA"),
                rSet.getLong("COD_MODELO_BANDA"),
                rSet.getString("NOME_MODELO_BANDA"),
                rSet.getInt("QTD_SULCOS_MODELO_BANDA"),
                rSet.getDouble("ALTURA_SULCOS_MODELO_BANDA"),
                rSet.getBoolean("STATUS_ATIVO_MODELO_BANDA"));
    }

    @NotNull
    private static ApiModeloPneu createApiModeloPneu(@NotNull final ResultSet rSet) throws SQLException {
        return new ApiModeloPneu(
                rSet.getLong("COD_MARCA_PNEU"),
                rSet.getLong("COD_MODELO_PNEU"),
                rSet.getString("NOME_MODELO_PNEU"),
                rSet.getInt("QTD_SULCOS_MODELO_PNEU"),
                rSet.getDouble("ALTURA_SULCOS_NODELO_PNEU"),
                rSet.getBoolean("STATUS_ATIVO_MODELO_PNEU"));
    }

    @NotNull
    private static ApiMarcaPneu createApiMarcaPneu(
            @NotNull final ResultSet rSet,
            @NotNull final List<ApiModeloPneu> modelosPneu) throws SQLException {
        return new ApiMarcaPneu(
                rSet.getLong("COD_MARCA_PNEU"),
                rSet.getString("NOME_MARCA_PNEU"),
                modelosPneu,
                rSet.getBoolean("STATUS_ATIVO_MARCA_PNEU"));
    }

    private static boolean temModeloBanda(@NotNull final ResultSet rSet) throws SQLException {
        return rSet.getLong("COD_MODELO_BANDA") > 0;
    }

    private static boolean temModeloPneu(@NotNull final ResultSet rSet) throws SQLException {
        return rSet.getLong("COD_MODELO_PNEU") > 0;
    }
}
