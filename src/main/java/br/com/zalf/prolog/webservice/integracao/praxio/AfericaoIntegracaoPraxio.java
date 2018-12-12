package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoProcessoColetaAfericao;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AfericaoIntegracaoPraxio {
    private Long codigo;
    private Long codUnidadeAfericao;
    private String cpfColaborador;
    private String placaVeiculoAferido;
    private Long codPneuAferido;
    private String numeroFogoPneu;
    private Double alturaSulcoInterno;
    private Double alturaSulcoCentralInterno;
    private Double alturaSulcoCentralExterno;
    private Double alturaSulcoExterno;
    private Double pressao;
    private Long kmVeiculoMomentoAfericao;
    private Long tempoRealizacaoMilis;
    private Integer vidaPneuMomentoAfericao;
    private Integer posicaoPneuMomentoAfericao;
    private LocalDateTime dataHoraRealizacao;
    private TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao;
    private TipoProcessoColetaAfericao tipoProcessoColetaAfericao;

    public AfericaoIntegracaoPraxio() {

    }

    @NotNull
    static AfericaoIntegracaoPraxio createDummyAfericaoPlacaSulcoPressao() {
        final AfericaoIntegracaoPraxio afericao = new AfericaoIntegracaoPraxio();
        afericao.setCodigo(12345L);
        afericao.setCodUnidadeAfericao(5L);
        afericao.setCpfColaborador("00000000011");
        afericao.setPlacaVeiculoAferido("PRO0001");
        afericao.setCodPneuAferido(1000L);
        afericao.setNumeroFogoPneu("PN123");
        afericao.setAlturaSulcoInterno(15.5);
        afericao.setAlturaSulcoCentralInterno(15.5);
        afericao.setAlturaSulcoCentralExterno(15.5);
        afericao.setAlturaSulcoExterno(15.5);
        afericao.setPressao(120.0);
        afericao.setKmVeiculoMomentoAfericao(123456L);
        afericao.setTempoRealizacaoMilis(900000L);
        afericao.setVidaPneuMomentoAfericao(1);
        afericao.setPosicaoPneuMomentoAfericao(111);
        afericao.setDataHoraRealizacao(LocalDateTime.now());
        afericao.setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao.SULCO_PRESSAO);
        afericao.setTipoProcessoColetaAfericao(TipoProcessoColetaAfericao.PLACA);
        return afericao;
    }

    @NotNull
    static AfericaoIntegracaoPraxio createDummyAfericaoPlacaSulco() {
        final AfericaoIntegracaoPraxio afericao = new AfericaoIntegracaoPraxio();
        afericao.setCodigo(12345L);
        afericao.setCodUnidadeAfericao(5L);
        afericao.setCpfColaborador("00000000011");
        afericao.setPlacaVeiculoAferido("PRO0001");
        afericao.setCodPneuAferido(1000L);
        afericao.setNumeroFogoPneu("PN123");
        afericao.setAlturaSulcoInterno(15.5);
        afericao.setAlturaSulcoCentralInterno(15.5);
        afericao.setAlturaSulcoCentralExterno(15.5);
        afericao.setAlturaSulcoExterno(15.5);
        afericao.setKmVeiculoMomentoAfericao(123456L);
        afericao.setTempoRealizacaoMilis(900000L);
        afericao.setVidaPneuMomentoAfericao(1);
        afericao.setPosicaoPneuMomentoAfericao(111);
        afericao.setDataHoraRealizacao(LocalDateTime.now());
        afericao.setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao.SULCO);
        afericao.setTipoProcessoColetaAfericao(TipoProcessoColetaAfericao.PLACA);
        return afericao;
    }

    @NotNull
    static AfericaoIntegracaoPraxio createDummyAfericaoPlacaPressao() {
        final AfericaoIntegracaoPraxio afericao = new AfericaoIntegracaoPraxio();
        afericao.setCodigo(12345L);
        afericao.setCodUnidadeAfericao(5L);
        afericao.setCpfColaborador("00000000011");
        afericao.setPlacaVeiculoAferido("PRO0001");
        afericao.setCodPneuAferido(1000L);
        afericao.setNumeroFogoPneu("PN123");
        afericao.setPressao(120.0);
        afericao.setKmVeiculoMomentoAfericao(123456L);
        afericao.setTempoRealizacaoMilis(900000L);
        afericao.setVidaPneuMomentoAfericao(1);
        afericao.setPosicaoPneuMomentoAfericao(111);
        afericao.setDataHoraRealizacao(LocalDateTime.now());
        afericao.setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao.PRESSAO);
        afericao.setTipoProcessoColetaAfericao(TipoProcessoColetaAfericao.PLACA);
        return afericao;
    }

    @NotNull
    static AfericaoIntegracaoPraxio createDummyAfericaoPneuAvulsoSulco() {
        final AfericaoIntegracaoPraxio afericao = new AfericaoIntegracaoPraxio();
        afericao.setCodigo(12345L);
        afericao.setCodUnidadeAfericao(5L);
        afericao.setCpfColaborador("00000000011");
        afericao.setCodPneuAferido(1000L);
        afericao.setNumeroFogoPneu("PN123");
        afericao.setAlturaSulcoInterno(15.5);
        afericao.setAlturaSulcoCentralInterno(15.5);
        afericao.setAlturaSulcoCentralExterno(15.5);
        afericao.setAlturaSulcoExterno(15.5);
        afericao.setTempoRealizacaoMilis(900000L);
        afericao.setVidaPneuMomentoAfericao(1);
        afericao.setDataHoraRealizacao(LocalDateTime.now());
        afericao.setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao.SULCO);
        afericao.setTipoProcessoColetaAfericao(TipoProcessoColetaAfericao.PNEU_AVULSO);
        return afericao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public Long getCodUnidadeAfericao() {
        return codUnidadeAfericao;
    }

    public void setCodUnidadeAfericao(final Long codUnidadeAfericao) {
        this.codUnidadeAfericao = codUnidadeAfericao;
    }

    public String getCpfColaborador() {
        return cpfColaborador;
    }

    public void setCpfColaborador(final String cpfColaborador) {
        this.cpfColaborador = cpfColaborador;
    }

    public String getPlacaVeiculoAferido() {
        return placaVeiculoAferido;
    }

    public void setPlacaVeiculoAferido(final String placaVeiculoAferido) {
        this.placaVeiculoAferido = placaVeiculoAferido;
    }

    public Long getCodPneuAferido() {
        return codPneuAferido;
    }

    public void setCodPneuAferido(final Long codPneuAferido) {
        this.codPneuAferido = codPneuAferido;
    }

    public String getNumeroFogoPneu() {
        return numeroFogoPneu;
    }

    public void setNumeroFogoPneu(final String numeroFogoPneu) {
        this.numeroFogoPneu = numeroFogoPneu;
    }

    public Double getAlturaSulcoInterno() {
        return alturaSulcoInterno;
    }

    public void setAlturaSulcoInterno(final Double alturaSulcoInterno) {
        this.alturaSulcoInterno = alturaSulcoInterno;
    }

    public Double getAlturaSulcoCentralInterno() {
        return alturaSulcoCentralInterno;
    }

    public void setAlturaSulcoCentralInterno(final Double alturaSulcoCentralInterno) {
        this.alturaSulcoCentralInterno = alturaSulcoCentralInterno;
    }

    public Double getAlturaSulcoCentralExterno() {
        return alturaSulcoCentralExterno;
    }

    public void setAlturaSulcoCentralExterno(final Double alturaSulcoCentralExterno) {
        this.alturaSulcoCentralExterno = alturaSulcoCentralExterno;
    }

    public Double getAlturaSulcoExterno() {
        return alturaSulcoExterno;
    }

    public void setAlturaSulcoExterno(final Double alturaSulcoExterno) {
        this.alturaSulcoExterno = alturaSulcoExterno;
    }

    public Double getPressao() {
        return pressao;
    }

    public void setPressao(final Double pressao) {
        this.pressao = pressao;
    }

    public Long getKmVeiculoMomentoAfericao() {
        return kmVeiculoMomentoAfericao;
    }

    public void setKmVeiculoMomentoAfericao(final Long kmVeiculoMomentoAfericao) {
        this.kmVeiculoMomentoAfericao = kmVeiculoMomentoAfericao;
    }

    public Long getTempoRealizacaoMilis() {
        return tempoRealizacaoMilis;
    }

    public void setTempoRealizacaoMilis(final Long tempoRealizacaoMilis) {
        this.tempoRealizacaoMilis = tempoRealizacaoMilis;
    }

    public Integer getVidaPneuMomentoAfericao() {
        return vidaPneuMomentoAfericao;
    }

    public void setVidaPneuMomentoAfericao(final Integer vidaPneuMomentoAfericao) {
        this.vidaPneuMomentoAfericao = vidaPneuMomentoAfericao;
    }

    public Integer getPosicaoPneuMomentoAfericao() {
        return posicaoPneuMomentoAfericao;
    }

    public void setPosicaoPneuMomentoAfericao(final Integer posicaoPneuMomentoAfericao) {
        this.posicaoPneuMomentoAfericao = posicaoPneuMomentoAfericao;
    }

    public LocalDateTime getDataHoraRealizacao() {
        return dataHoraRealizacao;
    }

    public void setDataHoraRealizacao(final LocalDateTime dataHoraRealizacao) {
        this.dataHoraRealizacao = dataHoraRealizacao;
    }

    public TipoMedicaoColetadaAfericao getTipoMedicaoColetadaAfericao() {
        return tipoMedicaoColetadaAfericao;
    }

    public void setTipoMedicaoColetadaAfericao(final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) {
        this.tipoMedicaoColetadaAfericao = tipoMedicaoColetadaAfericao;
    }

    public TipoProcessoColetaAfericao getTipoProcessoColetaAfericao() {
        return tipoProcessoColetaAfericao;
    }

    public void setTipoProcessoColetaAfericao(final TipoProcessoColetaAfericao tipoProcessoColetaAfericao) {
        this.tipoProcessoColetaAfericao = tipoProcessoColetaAfericao;
    }
}
