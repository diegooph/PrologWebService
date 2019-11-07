package br.com.zalf.prolog.webservice.integracao.praxio.afericao;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class MedicaoIntegracaoPraxio {
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
    private Long tempoRealizacaoEmSegundos;
    private Integer vidaPneuMomentoAfericao;
    private Integer posicaoPneuMomentoAfericao;
    private LocalDateTime dataHoraAfericao;
    private TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao;
    private TipoProcessoColetaAfericao tipoProcessoColetaAfericao;

    public MedicaoIntegracaoPraxio() {

    }

    @NotNull
    public static MedicaoIntegracaoPraxio createDummyAfericaoPlacaSulcoPressao() {
        final MedicaoIntegracaoPraxio medicao = new MedicaoIntegracaoPraxio();
        medicao.setCodigo(12345L);
        medicao.setCodUnidadeAfericao(5L);
        medicao.setCpfColaborador("00000000011");
        medicao.setPlacaVeiculoAferido("PRO0001");
        medicao.setCodPneuAferido(1000L);
        medicao.setNumeroFogoPneu("PN123");
        medicao.setAlturaSulcoInterno(15.5);
        medicao.setAlturaSulcoCentralInterno(15.5);
        medicao.setAlturaSulcoCentralExterno(15.5);
        medicao.setAlturaSulcoExterno(15.5);
        medicao.setPressao(120.0);
        medicao.setKmVeiculoMomentoAfericao(123456L);
        medicao.setTempoRealizacaoEmSegundos(300L);
        medicao.setVidaPneuMomentoAfericao(1);
        medicao.setPosicaoPneuMomentoAfericao(111);
        medicao.setDataHoraAfericao(LocalDateTime.now());
        medicao.setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao.SULCO_PRESSAO);
        medicao.setTipoProcessoColetaAfericao(TipoProcessoColetaAfericao.PLACA);
        return medicao;
    }

    @NotNull
    public static MedicaoIntegracaoPraxio createDummyAfericaoPlacaSulco() {
        final MedicaoIntegracaoPraxio medicao = new MedicaoIntegracaoPraxio();
        medicao.setCodigo(12345L);
        medicao.setCodUnidadeAfericao(5L);
        medicao.setCpfColaborador("00000000011");
        medicao.setPlacaVeiculoAferido("PRO0001");
        medicao.setCodPneuAferido(1000L);
        medicao.setNumeroFogoPneu("PN123");
        medicao.setAlturaSulcoInterno(15.5);
        medicao.setAlturaSulcoCentralInterno(15.5);
        medicao.setAlturaSulcoCentralExterno(15.5);
        medicao.setAlturaSulcoExterno(15.5);
        medicao.setKmVeiculoMomentoAfericao(123456L);
        medicao.setTempoRealizacaoEmSegundos(300L);
        medicao.setVidaPneuMomentoAfericao(1);
        medicao.setPosicaoPneuMomentoAfericao(111);
        medicao.setDataHoraAfericao(LocalDateTime.now());
        medicao.setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao.SULCO);
        medicao.setTipoProcessoColetaAfericao(TipoProcessoColetaAfericao.PLACA);
        return medicao;
    }

    @NotNull
    public static MedicaoIntegracaoPraxio createDummyAfericaoPlacaPressao() {
        final MedicaoIntegracaoPraxio medicao = new MedicaoIntegracaoPraxio();
        medicao.setCodigo(12345L);
        medicao.setCodUnidadeAfericao(5L);
        medicao.setCpfColaborador("00000000011");
        medicao.setPlacaVeiculoAferido("PRO0001");
        medicao.setCodPneuAferido(1000L);
        medicao.setNumeroFogoPneu("PN123");
        medicao.setPressao(120.0);
        medicao.setKmVeiculoMomentoAfericao(123456L);
        medicao.setTempoRealizacaoEmSegundos(300L);
        medicao.setVidaPneuMomentoAfericao(1);
        medicao.setPosicaoPneuMomentoAfericao(111);
        medicao.setDataHoraAfericao(LocalDateTime.now());
        medicao.setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao.PRESSAO);
        medicao.setTipoProcessoColetaAfericao(TipoProcessoColetaAfericao.PLACA);
        return medicao;
    }

    @NotNull
    public static MedicaoIntegracaoPraxio createDummyAfericaoPneuAvulsoSulco() {
        final MedicaoIntegracaoPraxio medicao = new MedicaoIntegracaoPraxio();
        medicao.setCodigo(12345L);
        medicao.setCodUnidadeAfericao(5L);
        medicao.setCpfColaborador("00000000011");
        medicao.setCodPneuAferido(1000L);
        medicao.setNumeroFogoPneu("PN123");
        medicao.setAlturaSulcoInterno(15.5);
        medicao.setAlturaSulcoCentralInterno(15.5);
        medicao.setAlturaSulcoCentralExterno(15.5);
        medicao.setAlturaSulcoExterno(15.5);
        medicao.setTempoRealizacaoEmSegundos(300L);
        medicao.setVidaPneuMomentoAfericao(1);
        medicao.setDataHoraAfericao(LocalDateTime.now());
        medicao.setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao.SULCO);
        medicao.setTipoProcessoColetaAfericao(TipoProcessoColetaAfericao.PNEU_AVULSO);
        return medicao;
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

    public Long getTempoRealizacaoEmSegundos() {
        return tempoRealizacaoEmSegundos;
    }

    public void setTempoRealizacaoEmSegundos(final Long tempoRealizacaoEmSegundos) {
        this.tempoRealizacaoEmSegundos = tempoRealizacaoEmSegundos;
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

    public LocalDateTime getDataHoraAfericao() {
        return dataHoraAfericao;
    }

    public void setDataHoraAfericao(final LocalDateTime dataHoraAfericao) {
        this.dataHoraAfericao = dataHoraAfericao;
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