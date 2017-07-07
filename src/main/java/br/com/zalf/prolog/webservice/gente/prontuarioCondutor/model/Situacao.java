package br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model;

/**
 * Created by Zart on 03/07/2017.
 */
public class Situacao {

    private static final String BLOQUEADO = "BLOQUEADO";
    private static final String LIBERADO = "LIBERADO";

    private String status;
    private String motivo;

    public Situacao() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
