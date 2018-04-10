package br.com.zalf.prolog.webservice.imports.escala_diaria;

import java.util.Date;

/**
 * Created on 10/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class EscalaDiaria {

    private long codEscala;
    private Date data;
    private String placa;
    private int codMapa;
    private boolean isCdoMapaOk;
    private Long cpfMotorista;
    private boolean isCpfMotoristaOk;
    private Long cpfAjudante1;
    private boolean isCpfAjudante1Ok;
    private Long cpfAjudante2;
    private boolean isCpfAjudante2Ok;

    public EscalaDiaria() {
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

    public boolean isCdoMapaOk() {
        return isCdoMapaOk;
    }

    public void setCdoMapaOk(final boolean cdoMapaOk) {
        isCdoMapaOk = cdoMapaOk;
    }

    public Long getCpfMotorista() {
        return cpfMotorista;
    }

    public void setCpfMotorista(final Long cpfMotorista) {
        this.cpfMotorista = cpfMotorista;
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

    public boolean isCpfAjudante2Ok() {
        return isCpfAjudante2Ok;
    }

    public void setCpfAjudante2Ok(final boolean cpfAjudante2Ok) {
        isCpfAjudante2Ok = cpfAjudante2Ok;
    }

    @Override
    public String toString() {
        return "EscalaDiaria{" +
                "codEscala=" + codEscala +
                ", data=" + data +
                ", placa='" + placa + '\'' +
                ", codMapa=" + codMapa +
                ", isCdoMapaOk=" + isCdoMapaOk +
                ", cpfMotorista=" + cpfMotorista +
                ", isCpfMotoristaOk=" + isCpfMotoristaOk +
                ", cpfAjudante1=" + cpfAjudante1 +
                ", isCpfAjudante1Ok=" + isCpfAjudante1Ok +
                ", cpfAjudante2=" + cpfAjudante2 +
                ", isCpfAjudante2Ok=" + isCpfAjudante2Ok +
                '}';
    }
}
