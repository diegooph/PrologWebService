package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 29/08/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuNomenclaturaCadastro {
    @NotNull
    private final Long codDiagrama;
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final List<PneuNomenclaturaItemCadastro> nomenclaturas;

    public PneuNomenclaturaCadastro(@NotNull final Long codDiagrama,
                                    @NotNull final Long codEmpresa,
                                    @NotNull final List<PneuNomenclaturaItemCadastro> nomenclaturas) {
        this.codDiagrama = codDiagrama;
        this.codEmpresa = codEmpresa;
        this.nomenclaturas = nomenclaturas;
    }

    @NotNull
    public Long getCodDiagrama() {
        return codDiagrama;
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }

    @NotNull
    public List<PneuNomenclaturaItemCadastro> getNomenclaturas() {
        return nomenclaturas;
    }

    @NotNull
    public int[] getPosicoesNaoEstepes() {
        return nomenclaturas
                .stream()
                // Apenas não esteps = < 900
                .filter(n -> n.getPosicaoProLog() < 900)
                .mapToInt(PneuNomenclaturaItemCadastro::getPosicaoProLog)
                .toArray();
    }
}