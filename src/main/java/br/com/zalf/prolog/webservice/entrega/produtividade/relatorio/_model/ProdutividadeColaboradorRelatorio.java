package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio._model;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created on 23/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public class ProdutividadeColaboradorRelatorio {
    @NotNull
    private final Colaborador colaborador;
    @NotNull
    private final BigDecimal valorTotal;
    @NotNull
    private final List<ProdutividadeColaboradorDia> produtividadeDias;

    public ProdutividadeColaboradorRelatorio(@NotNull final Colaborador colaborador,
                                             @NotNull final List<ProdutividadeColaboradorDia> produtividadeDias) {
        this.colaborador = colaborador;
        this.produtividadeDias = produtividadeDias;
        this.valorTotal =
                produtividadeDias
                        .stream()
                        .map(ProdutividadeColaboradorDia::getValor)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}