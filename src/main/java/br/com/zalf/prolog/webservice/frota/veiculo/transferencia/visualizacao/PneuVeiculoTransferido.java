package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.visualizacao;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 12/04/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuVeiculoTransferido {
    @NotNull
    private final Long codPneuTransferenciaInformacoes;
    /**
     * Código do cliente, número de fogo do pneu.
     */
    @NotNull
    private final String codPneuCliente;
    @NotNull
    private final Sulcos sulcosMomentoTransferencia;
    private final double pressaoMomentoTransferencia;
    private final int vidaMomentoTransferencia;
    private final int posicaoNoVeiculo;

    public PneuVeiculoTransferido(@NotNull final Long codPneuTransferenciaInformacoes,
                                  @NotNull final String codPneuCliente,
                                  @NotNull final Sulcos sulcosMomentoTransferencia,
                                  final double pressaoMomentoTransferencia,
                                  final int vidaMomentoTransferencia,
                                  final int posicaoNoVeiculo) {
        this.codPneuTransferenciaInformacoes = codPneuTransferenciaInformacoes;
        this.codPneuCliente = codPneuCliente;
        this.sulcosMomentoTransferencia = sulcosMomentoTransferencia;
        this.pressaoMomentoTransferencia = pressaoMomentoTransferencia;
        this.vidaMomentoTransferencia = vidaMomentoTransferencia;
        this.posicaoNoVeiculo = posicaoNoVeiculo;
    }

    @NotNull
    public Long getCodPneuTransferenciaInformacoes() {
        return codPneuTransferenciaInformacoes;
    }

    @NotNull
    public String getCodPneuCliente() {
        return codPneuCliente;
    }

    @NotNull
    public Sulcos getSulcosMomentoTransferencia() {
        return sulcosMomentoTransferencia;
    }

    public double getPressaoMomentoTransferencia() {
        return pressaoMomentoTransferencia;
    }

    public int getVidaMomentoTransferencia() {
        return vidaMomentoTransferencia;
    }

    public int getPosicaoNoVeiculo() {
        return posicaoNoVeiculo;
    }
}