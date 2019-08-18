package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2019-08-17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ModeloChecklistSelecao {
    /**
     * Número que representa o código que o modelo de checklist possui. Identificador único do modelo de checklist.
     */
    @NotNull
    private final Long codModelo;

    /**
     * O código da versão atual desse modelo.
     */
    @NotNull
    private final Long codVersaoModelo;

    /**
     * Código da unidade a qual o modelo de checklist está vinculado.
     */
    @NotNull
    private final Long codUnidadeModelo;

    /**
     * Valor alfanumérico que representa o nome do modelo do checklist. Identificador para o usuário.
     */
    @NotNull
    private final String nomeModelo;

    /**
     * {@link VeiculoChecklistSelecao Veículos} que estão vinculados a esse modelo e disponíveis para realizarem o
     * checklist.
     */
    @NotNull
    private final List<VeiculoChecklistSelecao> veiculosVinculadosModelo;

    public ModeloChecklistSelecao(@NotNull final Long codModelo,
                                  @NotNull final Long codVersaoModelo,
                                  @NotNull final Long codUnidadeModelo,
                                  @NotNull final String nomeModelo,
                                  @NotNull final List<VeiculoChecklistSelecao> veiculosVinculadosModelo) {
        this.codModelo = codModelo;
        this.codVersaoModelo = codVersaoModelo;
        this.codUnidadeModelo = codUnidadeModelo;
        this.nomeModelo = nomeModelo;
        this.veiculosVinculadosModelo = veiculosVinculadosModelo;
    }

    @NotNull
    public Long getCodModelo() {
        return codModelo;
    }

    @NotNull
    public Long getCodVersaoModelo() {
        return codVersaoModelo;
    }

    @NotNull
    public Long getCodUnidadeModelo() {
        return codUnidadeModelo;
    }

    @NotNull
    public String getNomeModelo() {
        return nomeModelo;
    }

    @NotNull
    public List<VeiculoChecklistSelecao> getVeiculosVinculadosModelo() {
        return veiculosVinculadosModelo;
    }
}
