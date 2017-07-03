package br.com.zalf.prolog.webservice.gente.prontuario.model.ocorrencia;

/**
 * Created by Zart on 03/07/2017.
 */
public class Situacao {

    private static final String BLOQUEADO = "Bloqueado";
    private static final String LIBERADO = "Liberado";

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
