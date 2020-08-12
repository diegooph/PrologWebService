package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created on 23/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public class ProdutividadeColaboradorDia {
    @NotNull
    private final LocalDate data;
    private final double qtdCaixas;
    private final int fator;
    @NotNull
    private final BigDecimal valor;
}
