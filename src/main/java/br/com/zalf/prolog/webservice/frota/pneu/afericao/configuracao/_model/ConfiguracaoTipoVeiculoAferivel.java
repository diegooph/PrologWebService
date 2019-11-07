package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ConfiguracaoTipoVeiculoAferivel {
    private Long codigo;
    private Long codUnidade;
    private TipoVeiculo tipoVeiculo;
    private boolean podeAferirPressao;
    private boolean podeAferirSulco;
    private boolean podeAferirSulcoPressao;
    private boolean podeAferirEstepe;

    public ConfiguracaoTipoVeiculoAferivel() {

    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(final Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    public TipoVeiculo getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(final TipoVeiculo tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public boolean isPodeAferirPressao() {
        return podeAferirPressao;
    }

    public void setPodeAferirPressao(final boolean podeAferirPressao) {
        this.podeAferirPressao = podeAferirPressao;
    }

    public boolean isPodeAferirSulco() {
        return podeAferirSulco;
    }

    public void setPodeAferirSulco(final boolean podeAferirSulco) {
        this.podeAferirSulco = podeAferirSulco;
    }

    public boolean isPodeAferirSulcoPressao() {
        return podeAferirSulcoPressao;
    }

    public void setPodeAferirSulcoPressao(final boolean podeAferirSulcoPressao) {
        this.podeAferirSulcoPressao = podeAferirSulcoPressao;
    }

    public boolean isPodeAferirEstepe() {
        return podeAferirEstepe;
    }

    public void setPodeAferirEstepe(final boolean podeAferirEstepe) {
        this.podeAferirEstepe = podeAferirEstepe;
    }

    @Override
    public String toString() {
        return "ConfiguracaoTipoVeiculoAfericao{" +
                "codigo=" + codigo +
                ", codUnidade=" + codUnidade +
                ", tipoVeiculo=" + tipoVeiculo +
                ", podeAferirPressao=" + podeAferirPressao +
                ", podeAferirSulco=" + podeAferirSulco +
                ", podeAferirSulcoPressao=" + podeAferirSulcoPressao +
                ", podeAferirEstepe=" + podeAferirEstepe +
                '}';
    }
}