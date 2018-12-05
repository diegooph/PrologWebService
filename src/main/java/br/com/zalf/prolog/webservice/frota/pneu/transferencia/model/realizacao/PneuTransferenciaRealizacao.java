package br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuTransferenciaRealizacao {

    private Long codUnidadeOrigem;
    private Long codUnidadeDestino;
    private Long codColaborador;
    private List<Long> codPneus;
    private String observacao;

    public Long getCodUnidadeOrigem() {
        return codUnidadeOrigem;
    }

    public void setCodUnidadeOrigem(Long codUnidadeOrigem) {
        this.codUnidadeOrigem = codUnidadeOrigem;
    }

    public Long getCodUnidadeDestino() {
        return codUnidadeDestino;
    }

    public void setCodUnidadeDestino(Long codUnidadeDestino) {
        this.codUnidadeDestino = codUnidadeDestino;
    }

    public Long getCodColaborador() {
        return codColaborador;
    }

    public void setCodColaborador(Long codColaborador) {
        this.codColaborador = codColaborador;
    }

    public List<Long> codPneus() {
        return codPneus;
    }

    public void setCodPneu(List<Long> codPneus) {
        this.codPneus = codPneus;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @NotNull
    public static PneuTransferenciaRealizacao createDummy() {
        final PneuTransferenciaRealizacao transferencia = new PneuTransferenciaRealizacao();
        transferencia.setCodUnidadeOrigem(5L);
        transferencia.setCodUnidadeDestino(3L);
        transferencia.setCodColaborador(190L);
        List<Long> codPneus = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            codPneus.add(Long.valueOf(i));
        }
        transferencia.setCodPneu(codPneus);
        transferencia.setObservacao("Operação Verão");

        return transferencia;
    }
}
