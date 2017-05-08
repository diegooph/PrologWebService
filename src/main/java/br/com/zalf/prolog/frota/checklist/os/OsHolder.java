package br.com.zalf.prolog.frota.checklist.os;

import java.util.List;

/**
 * Created by jean on 30/07/16.
 */
public class OsHolder {

    private List<OrdemServico> os;
    private String placa;

    public OsHolder() {
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public List<OrdemServico> getOs() {
        return os;
    }

    public void setOs(List<OrdemServico> os) {
        this.os = os;
    }


    @Override
    public String toString() {
        return "OsHolder{" +
                "os=" + os +
                ", placa='" + placa + '\'' +
                '}';
    }
}
