package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 29/08/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuNomenclaturaCadastro {
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final Long codDiagrama;
    @NotNull
    private final List<PneuNomenclaturaItemCadastro> nomenclaturas;

    public PneuNomenclaturaCadastro(@NotNull final Long codEmpresa,
                                    @NotNull final Long codDiagrama,
                                    @NotNull final List<PneuNomenclaturaItemCadastro> nomenclaturas) {
        this.codEmpresa = codEmpresa;
        this.codDiagrama = codDiagrama;
        this.nomenclaturas = nomenclaturas;
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }

    @NotNull
    public Long getCodDiagrama() {
        return codDiagrama;
    }

    @NotNull
    public List<PneuNomenclaturaItemCadastro> getNomenclaturas() {
        return nomenclaturas;
    }

    @NotNull
    public int[] getPosicoesNaoEstepes() {
        return nomenclaturas
                .stream()
                // Apenas não estepes, então < 900
                .filter(n -> n.getPosicaoProLog() < 900)
                .mapToInt(PneuNomenclaturaItemCadastro::getPosicaoProLog)
                .toArray();
    }

    @NotNull
    public static PneuNomenclaturaCadastro createDummy(@NotNull final List<PneuNomenclaturaItemCadastro> nomenclaturas) {
        return new PneuNomenclaturaCadastro(1L, 3L, nomenclaturas);
    }
}