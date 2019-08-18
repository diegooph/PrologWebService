package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.VeiculoChecklistSelecao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.util.List;

/**
 * Created on 2019-08-18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistModeloConverter {

    public ChecklistModeloConverter() {
        throw new IllegalStateException(ChecklistModeloConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static VeiculoChecklistSelecao createVeiculoChecklistSelecao(
            @NotNull final ResultSet rSet) throws Throwable {
        return new VeiculoChecklistSelecao(
                rSet.getLong("COD_VEICULO"),
                rSet.getString("PLACA_VEICULO"),
                rSet.getLong("KM_ATUAL_VEICULO"));
    }

    @NotNull
    public static ModeloChecklistSelecao createModeloChecklistSelecao(
            @NotNull final ResultSet rSet,
            @NotNull final List<VeiculoChecklistSelecao> veiculosSelecao) throws Throwable {
        return new ModeloChecklistSelecao(
                rSet.getLong("COD_MODELO"),
                rSet.getLong("COD_VERSAO_ATUAL_MODELO"),
                rSet.getLong("COD_UNIDADE_MODELO"),
                rSet.getString("NOME_MODELO"),
                veiculosSelecao);
    }
}