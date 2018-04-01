package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;

import java.time.LocalDateTime;

/**
 * Created by jean on 04/04/16.
 */
public class Afericao {
    private Long codigo;

    /**
     * O código da {@link Unidade} onde a aferição foi realizada.
     */
    private Long codUnidade;
    private LocalDateTime dataHora;

    /**
     * O {@link Veiculo veiculo} no qual a aferição foi realizada.
     */
    private Veiculo veiculo;
    private Colaborador colaborador;

    /**
     * Na busca de uma aferição já realizada, para saber o KM do veículo no momento da aferição, devemos consultar este
     * atributo, ao inves do {@link Veiculo#kmAtual kmAtual} do veículo.
     */
    private long kmMomentoAfericao;

    /**
     * Armazena o tempo que o colaborador levou para realizar a aferição, em milisegundos.
     */
    private long tempoRealizacaoAfericaoInMillis;

    private TipoAfericao tipoAfericao;

    public Afericao() {

    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(final Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public long getKmMomentoAfericao() {
        return kmMomentoAfericao;
    }

    public void setKmMomentoAfericao(long kmMomentoAfericao) {
        this.kmMomentoAfericao = kmMomentoAfericao;
    }

    public long getTempoRealizacaoAfericaoInMillis() {
        return tempoRealizacaoAfericaoInMillis;
    }

    public void setTempoRealizacaoAfericaoInMillis(long tempoRealizacaoAfericaoInMillis) {
        this.tempoRealizacaoAfericaoInMillis = tempoRealizacaoAfericaoInMillis;
    }

    public TipoAfericao getTipoAfericao() {
        return tipoAfericao;
    }

    public void setTipoAfericao(TipoAfericao tipoAfericao) {
        this.tipoAfericao = tipoAfericao;
    }

    @Override
    public String toString() {
        return "Afericao{" +
                "codigo=" + codigo +
                ", dataHora=" + dataHora +
                ", veiculo=" + veiculo +
                ", colaborador=" + colaborador +
                ", kmMomentoAfericao=" + kmMomentoAfericao +
                ", tempoRealizacaoAfericaoInMillis=" + tempoRealizacaoAfericaoInMillis +
                ", tipoAfericao=" + tipoAfericao +
                '}';
    }
}