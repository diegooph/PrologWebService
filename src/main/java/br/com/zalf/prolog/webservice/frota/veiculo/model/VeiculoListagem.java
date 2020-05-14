package br.com.zalf.prolog.webservice.frota.veiculo.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 04/05/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoListagem {

    @NotNull
    private final Long codigo;
    @NotNull
    private final String placa;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final String km;
    @NotNull
    private final String statusAtivo;
    @NotNull
    private final Long codTipo;
    @NotNull
    private final Long codModelo;
    @NotNull
    private final Long codDiagrama;
    @Nullable
    private final String identificadorFrota;
    @NotNull
    private final Long codRegionalAlocado;
    @NotNull
    private final String modelo;
    @NotNull
    private final String nomeDiagrama;
    @NotNull
    private final String dianteiro;
    @NotNull
    private final String traseiro;
    @NotNull
    private final String tipo;
    @NotNull
    private final String marca;
    @NotNull
    private final Long codMarca;

    public VeiculoListagem(@NotNull final Long codigo,
                           @NotNull final String placa,
                           @NotNull final Long codUnidade,
                           @NotNull final String km,
                           @NotNull final String statusAtivo,
                           @NotNull final Long codTipo,
                           @NotNull final Long codModelo,
                           @NotNull final Long codDiagrama,
                           @Nullable final String identificadorFrota,
                           @NotNull final Long codRegionalAlocado,
                           @NotNull final String modelo,
                           @NotNull final String nomeDiagrama,
                           @NotNull final String dianteiro,
                           @NotNull final String traseiro,
                           @NotNull final String tipo,
                           @NotNull final String marca,
                           @NotNull final Long codMarca) {
        this.codigo = codigo;
        this.placa = placa;
        this.codUnidade = codUnidade;
        this.km = km;
        this.statusAtivo = statusAtivo;
        this.codTipo = codTipo;
        this.codModelo = codModelo;
        this.codDiagrama = codDiagrama;
        this.identificadorFrota = identificadorFrota;
        this.codRegionalAlocado = codRegionalAlocado;
        this.modelo = modelo;
        this.nomeDiagrama = nomeDiagrama;
        this.dianteiro = dianteiro;
        this.traseiro = traseiro;
        this.tipo = tipo;
        this.marca = marca;
        this.codMarca = codMarca;
    }

    public Long getCodigo() {
        return codigo;
    }

    public String getPlaca() {
        return placa;
    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public String getKm() {
        return km;
    }

    public String getStatusAtivo() {
        return statusAtivo;
    }

    public Long getCodTipo() {
        return codTipo;
    }

    public Long getCodModelo() {
        return codModelo;
    }

    public Long getCodDiagrama() {
        return codDiagrama;
    }

    public String getIdentificadorFrota() {
        return identificadorFrota;
    }

    public Long getCodRegionalAlocado() {
        return codRegionalAlocado;
    }

    public String getModelo() {
        return modelo;
    }

    public String getNomeDiagrama() {
        return nomeDiagrama;
    }

    public String getDianteiro() {
        return dianteiro;
    }

    public String getTraseiro() {
        return traseiro;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMarca() {
        return marca;
    }

    public Long getCodMarca() {
        return codMarca;
    }
}
