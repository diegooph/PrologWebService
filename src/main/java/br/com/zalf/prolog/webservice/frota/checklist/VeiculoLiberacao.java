package br.com.zalf.prolog.webservice.frota.checklist;

import java.util.List;

/**
 * Created by luiz on 4/20/16.
 */
public class VeiculoLiberacao {
    public static final String STATUS_LIBERADO = "LIBERADO";
    public static final String STATUS_NAO_LIBERADO = "NAO_LIBERADO";
    public static final String STATUS_PENDENTE = "PENDENTE";
    private String placa;
    private String status;
    private List<PerguntaRespostaChecklist> itensCriticos;

    public VeiculoLiberacao() {
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PerguntaRespostaChecklist> getItensCriticos() {
        return itensCriticos;
    }

    public void setItensCriticos(List<PerguntaRespostaChecklist> itensCriticos) {
        this.itensCriticos = itensCriticos;
    }

    @Override
    public String toString() {
        return "VeiculoLiberacao{" +
                "placa='" + placa + '\'' +
                ", status='" + status + '\'' +
                ", itensCriticos=" + itensCriticos +
                '}';
    }
}
