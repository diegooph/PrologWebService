package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao;

import br.com.zalf.prolog.webservice.frota.pneu._model.Sulcos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 12/04/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuVeiculoTransferido {
    /**
     * Código único de identificação do pneu.
     */
    @NotNull
    private final Long codPneuTransferenciaInformacoes;
    /**
     * Código do cliente. Normalmente, o número de fogo do pneu.
     */
    @NotNull
    private final String codPneuCliente;
    /**
     * Atributo contento a informação de medida de cada sulco do pneu, quando a placa a qual ele estava associado foi
     * transferida.
     * Este atributo será <code>NULL</code> caso o pneu nunca tenha sido aferido e foi transferido.
     */
    @Nullable
    private final Sulcos sulcosMomentoTransferencia;
    /**
     * Pressão, em PSI, do pneu quando a placa foi transferida.
     */
    private final double pressaoMomentoTransferencia;
    /**
     * Vida do pneu no momento em que a placa foi transferida de unidade.
     */
    private final int vidaMomentoTransferencia;
    /**
     * Posição onde o pneu estava associado na placa, quando esta foi transferida de Unidade.
     */
    private final int posicaoNoVeiculo;

    public PneuVeiculoTransferido(@NotNull final Long codPneuTransferenciaInformacoes,
                                  @NotNull final String codPneuCliente,
                                  @Nullable final Sulcos sulcosMomentoTransferencia,
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
    public static PneuVeiculoTransferido createDummy() {
        final Sulcos sulcos = new Sulcos();
        sulcos.setInterno(10.0);
        sulcos.setCentralInterno(10.0);
        sulcos.setCentralExterno(10.0);
        sulcos.setExterno(10.0);
        return new PneuVeiculoTransferido(
                1528L,
                "PN112",
                sulcos,
                110.0,
                2,
                111);
    }

    @NotNull
    public Long getCodPneuTransferenciaInformacoes() {
        return codPneuTransferenciaInformacoes;
    }

    @NotNull
    public String getCodPneuCliente() {
        return codPneuCliente;
    }

    @Nullable
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