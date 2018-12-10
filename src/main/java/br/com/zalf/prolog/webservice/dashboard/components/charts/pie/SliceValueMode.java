package br.com.zalf.prolog.webservice.dashboard.components.charts.pie;

/**
 * Indica como o valor de cada fatia do gráfico deve ser representado. Atualmente suportando os modos de porcentagem
 * e de valor direto.
 *
 * Created on 24/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum SliceValueMode {
    /**
     * Indica que devemos exibir o valor de cada fatia do gráfico em forma de porcentagem, referente a parte que cada
     * fatia ocupa do total.
     */
    SLICE_PERCENTAGE,

    /**
     * Indica que devemos exibir em cada fatia a {@link PieEntry#representacaoValor representação de valor} enviada do
     * servidor para a respectiva fatia.
     */
    VALUE_REPRESENTATION
}