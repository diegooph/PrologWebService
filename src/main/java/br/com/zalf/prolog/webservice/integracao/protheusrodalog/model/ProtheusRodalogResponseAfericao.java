package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

/**
 * Created on 03/04/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ProtheusRodalogResponseAfericao {

    /**
     * Valor numérico que representa o código da aferição que acabou de ser inserida.
     */
    private Long codigoAfericaoInserida;

    /**
     * Valor alfanumérico que contém uma descrição sobre o processo de inserção da aferição.
     */
    private String msg;

    /**
     * Valor booleano para representar o sucesso da operação, caso <code>TRUE</code>, ou a falha da operação,
     * caso <code>FALSE</code>.
     */
    private boolean status;

    public ProtheusRodalogResponseAfericao() {
    }

    public Long getCodigoAfericaoInserida() {
        return codigoAfericaoInserida;
    }

    public void setCodigoAfericaoInserida(final Long codigoAfericaoInserida) {
        this.codigoAfericaoInserida = codigoAfericaoInserida;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(final boolean status) {
        this.status = status;
    }
}
