package br.com.zalf.prolog.webservice.imports.escala_diaria;

import java.util.Date;

/**
 * Created on 10/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class EscalaDiariaItem {

    private long codEscala;
    private Date data;
    private String placa;
    private boolean isPlacaOk;
    private int codMapa;
    private boolean isMapaOk;
    private Long cpfMotorista;
    private String nomeMotorista;
    private boolean isCpfMotoristaOk;
    private Long cpfAjudante1;
    private String nomeAjudante1;
    private boolean isCpfAjudante1Ok;
    private Long cpfAjudante2;
    private String nomeAjudante2;
    private boolean isCpfAjudante2Ok;

    public EscalaDiariaItem() {
    }

    public long getCodEscala() {
        return codEscala;
    }

    public void setCodEscala(final long codEscala) {
        this.codEscala = codEscala;
    }

    public Date getData() {
        return data;
    }

    public void setData(final Date data) {
        this.data = data;
    }

    public boolean isPlacaOk() {
        return isPlacaOk;
    }

    public void setPlacaOk(final boolean placaOk) {
        isPlacaOk = placaOk;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(final String placa) {
        this.placa = placa;
    }

    public int getCodMapa() {
        return codMapa;
    }

    public void setCodMapa(final int codMapa) {
        this.codMapa = codMapa;
    }

    public boolean isMapaOk() {
        return isMapaOk;
    }

    public void setMapaOk(final boolean mapaOk) {
        isMapaOk = mapaOk;
    }

    public Long getCpfMotorista() {
        return cpfMotorista;
    }

    public void setCpfMotorista(final Long cpfMotorista) {
        this.cpfMotorista = cpfMotorista;
    }

    public String getNomeMotorista() {
        return nomeMotorista;
    }

    public void setNomeMotorista(final String nomeMotorista) {
        this.nomeMotorista = nomeMotorista;
    }

    public boolean isCpfMotoristaOk() {
        return isCpfMotoristaOk;
    }

    public void setCpfMotoristaOk(final boolean cpfMotoristaOk) {
        isCpfMotoristaOk = cpfMotoristaOk;
    }

    public Long getCpfAjudante1() {
        return cpfAjudante1;
    }

    public void setCpfAjudante1(final Long cpfAjudante1) {
        this.cpfAjudante1 = cpfAjudante1;
    }

    public String getNomeAjudante1() {
        return nomeAjudante1;
    }

    public void setNomeAjudante1(final String nomeAjudante1) {
        this.nomeAjudante1 = nomeAjudante1;
    }

    public boolean isCpfAjudante1Ok() {
        return isCpfAjudante1Ok;
    }

    public void setCpfAjudante1Ok(final boolean cpfAjudante1Ok) {
        isCpfAjudante1Ok = cpfAjudante1Ok;
    }

    public Long getCpfAjudante2() {
        return cpfAjudante2;
    }

    public void setCpfAjudante2(final Long cpfAjudante2) {
        this.cpfAjudante2 = cpfAjudante2;
    }

    public String getNomeAjudante2() {
        return nomeAjudante2;
    }

    public void setNomeAjudante2(final String nomeAjudante2) {
        this.nomeAjudante2 = nomeAjudante2;
    }

    public boolean isCpfAjudante2Ok() {
        return isCpfAjudante2Ok;
    }

    public void setCpfAjudante2Ok(final boolean cpfAjudante2Ok) {
        isCpfAjudante2Ok = cpfAjudante2Ok;
    }

    @Override
    public String toString() {
        return "EscalaDiariaItem{" +
                "codEscala=" + codEscala +
                ", data=" + data +
                ", placa='" + placa + '\'' +
                ", isPlacaOk=" + isPlacaOk +
                ", codMapa=" + codMapa +
                ", isMapaOk=" + isMapaOk +
                ", cpfMotorista=" + cpfMotorista +
                ", nomeMotorista='" + nomeMotorista + '\'' +
                ", isCpfMotoristaOk=" + isCpfMotoristaOk +
                ", cpfAjudante1=" + cpfAjudante1 +
                ", nomeAjudante1='" + nomeAjudante1 + '\'' +
                ", isCpfAjudante1Ok=" + isCpfAjudante1Ok +
                ", cpfAjudante2=" + cpfAjudante2 +
                ", nomeAjudante2='" + nomeAjudante2 + '\'' +
                ", isCpfAjudante2Ok=" + isCpfAjudante2Ok +
                '}';
    }
}
