package br.com.zalf.prolog.webservice.frota.pneu.relatorios._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-05-21
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class PneuKmRodadoPorVida {
    @NotNull
    private final String unidadeAlocado;
    @NotNull
    private final Long codPneu;
    @NotNull
    private final String codPneuCliente;
    @NotNull
    private final String marca;
    @NotNull
    private final String modelo;
    @NotNull
    private final String dimensao;
    private final String vida;
    private final String valorVida;
    private final String kmRodadoVida;
    private final String valorPorKmVida;
    private final String kmRodadoTodasVidas;
}
