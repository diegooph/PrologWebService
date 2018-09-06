package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by jean on 25/07/16.
 */
public class OrdemServico {

    public enum Status{
        ABERTA("A"),
        FECHADA("F");

        private final String s;

        Status(String s){this.s = s;}
        public String asString(){return s;}

        public static Status fromString(String text) throws IllegalArgumentException{
            if (text != null) {
                for (Status b : Status.values()) {
                    if (text.equalsIgnoreCase(b.s)) {
                        return b;
                    }
                }
            }
            throw new IllegalArgumentException("Nenhum enum com esse valor encontrado");
        }
    }

    private Long codigo;
    private Veiculo veiculo;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private Long codChecklist;
    private Status status;
    private List<ItemOrdemServico> itens;

    public OrdemServico() {
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public Long getCodChecklist() {
        return codChecklist;
    }

    public void setCodChecklist(Long codChecklist) {
        this.codChecklist = codChecklist;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<ItemOrdemServico> getItens() {
        return itens;
    }

    public void setItens(List<ItemOrdemServico> itens) {
        this.itens = itens;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    @Override
    public String toString() {
        return "OrdemServico{" +
                "codigo=" + codigo +
                ", veiculo=" + veiculo +
                ", dataAbertura=" + dataAbertura +
                ", dataFechamento=" + dataFechamento +
                ", codChecklist=" + codChecklist +
                ", status=" + status +
                ", itens=" + itens +
                '}';
    }
}
