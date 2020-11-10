package br.com.zalf.prolog.webservice.frota.veiculo.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 04/05/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
public class VeiculoListagem {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String placa;
    @NotNull
    private final Long codRegionalAlocado;
    @NotNull
    private final String nomeRegional;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final String nomeUnidade;
    @NotNull
    private final Long km;
    @NotNull
    private final Boolean statusAtivo;
    @NotNull
    private final Long codTipo;
    @NotNull
    private final Long codModelo;
    @NotNull
    private final Long codDiagrama;
    @Nullable
    private final String identificadorFrota;
    @NotNull
    private final String modelo;
    @NotNull
    private final String nomeDiagrama;
    @NotNull
    private final Long dianteiro;
    @NotNull
    private final Long traseiro;
    @NotNull
    private final String tipo;
    @NotNull
    private final String marca;
    @NotNull
    private final Long codMarca;
    private final boolean motorizado;
    private final boolean possuiHubodometro;
}
