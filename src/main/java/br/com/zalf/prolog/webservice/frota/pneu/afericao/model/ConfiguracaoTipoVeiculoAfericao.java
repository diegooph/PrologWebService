package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ConfiguracaoTipoVeiculoAfericao {

    private long codigo;
    private long codUnidade;
    private TipoVeiculo tipoVeiculo;
    private boolean deveAferirPressao;
    private boolean deveAferirSulco;
    private boolean deveAferirSulcoPressao;
    private boolean deveAferirEstepe;

    public ConfiguracaoTipoVeiculoAfericao() {
    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(final long codigo) {
        this.codigo = codigo;
    }

    public long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(final long codUnidade) {
        this.codUnidade = codUnidade;
    }

    public TipoVeiculo getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(final TipoVeiculo tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public boolean isDeveAferirPressao() {
        return deveAferirPressao;
    }

    public void setDeveAferirPressao(final boolean deveAferirPressao) {
        this.deveAferirPressao = deveAferirPressao;
    }

    public boolean isDeveAferirSulco() {
        return deveAferirSulco;
    }

    public void setDeveAferirSulco(final boolean deveAferirSulco) {
        this.deveAferirSulco = deveAferirSulco;
    }

    public boolean isDeveAferirSulcoPressao() {
        return deveAferirSulcoPressao;
    }

    public void setDeveAferirSulcoPressao(final boolean deveAferirSulcoPressao) {
        this.deveAferirSulcoPressao = deveAferirSulcoPressao;
    }

    public boolean isDeveAferirEstepe() {
        return deveAferirEstepe;
    }

    public void setDeveAferirEstepe(final boolean deveAferirEstepe) {
        this.deveAferirEstepe = deveAferirEstepe;
    }

    @Override
    public String toString() {
        return "ConfiguracaoTipoVeiculoAfericao{" +
                "codigo=" + codigo +
                ", codUnidade=" + codUnidade +
                ", tipoVeiculo=" + tipoVeiculo +
                ", deveAferirPressao=" + deveAferirPressao +
                ", deveAferirSulco=" + deveAferirSulco +
                ", deveAferirSulcoPressao=" + deveAferirSulcoPressao +
                ", deveAferirEstepe=" + deveAferirEstepe +
                '}';
    }
}
