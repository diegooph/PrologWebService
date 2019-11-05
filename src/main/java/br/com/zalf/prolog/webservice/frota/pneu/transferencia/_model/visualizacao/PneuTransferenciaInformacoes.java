package br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.visualizacao;

import br.com.zalf.prolog.webservice.frota.pneu._model.Sulcos;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 06/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuTransferenciaInformacoes {
    /**
     * Código das informações do pneu transferido.
     */
    @NotNull
    private final Long codPneuTransferenciaInformacoes;
    /**
     * Código de identificação do pneu, normalmente, número de fogo do pneu.
     */
    @NotNull
    private final String codPneuCliente;
    /**
     * Objeto que contém as medidas de milimetragem do pneu quando ele foi transferido.
     */
    @NotNull
    private final Sulcos sulcosMomentoTransferencia;
    /**
     * Pressão do pneu no momento que ele foi transferido.
     */
    private final double pressaoMomentoTransferencia;
    /**
     * Vida do pneu no momento que ele foi transferido.
     */
    private final int vidaMomentoTransferencia;

    public PneuTransferenciaInformacoes(@NotNull final Long codPneuTransferenciaInformacoes,
                                        @NotNull final String codPneuCliente,
                                        @NotNull final Sulcos sulcosMomentoTransferencia,
                                        final double pressaoMomentoTransferencia,
                                        final int vidaMomentoTransferencia) {
        this.codPneuTransferenciaInformacoes = codPneuTransferenciaInformacoes;
        this.codPneuCliente = codPneuCliente;
        this.sulcosMomentoTransferencia = sulcosMomentoTransferencia;
        this.pressaoMomentoTransferencia = pressaoMomentoTransferencia;
        this.vidaMomentoTransferencia = vidaMomentoTransferencia;
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
}