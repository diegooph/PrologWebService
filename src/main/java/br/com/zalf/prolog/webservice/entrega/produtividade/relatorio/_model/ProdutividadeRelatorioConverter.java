package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio._model;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2020-08-11
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ProdutividadeRelatorioConverter {

    private ProdutividadeRelatorioConverter() {
        throw new IllegalStateException(
                ProdutividadeRelatorioConverter.class.getSimpleName() + " cannot be instantiated");
    }

    @NotNull
    public static ProdutividadeColaboradorRelatorio createProdutividadeColaboradorRelatorio(
            @NotNull final Long cpfColaborador,
            @NotNull final String nomeColaborador,
            @NotNull final List<ProdutividadeColaboradorDia> relatorioDias) {
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(cpfColaborador);
        colaborador.setNome(nomeColaborador);
        return new ProdutividadeColaboradorRelatorio(colaborador, relatorioDias);
    }

    @NotNull
    public static ProdutividadeColaboradorDia createProdutividadeColaboradorDia(
            @NotNull final ResultSet rSet) throws SQLException {
        return new ProdutividadeColaboradorDia(
                rSet.getObject("DATA", LocalDate.class),
                rSet.getDouble("CAIXAS_ENTREGUES"),
                rSet.getInt("FATOR"),
                rSet.getBigDecimal("VALOR"));
    }
}
