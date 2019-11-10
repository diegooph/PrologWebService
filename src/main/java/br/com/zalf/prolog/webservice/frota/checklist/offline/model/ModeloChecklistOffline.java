package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ModeloChecklistOffline {
    /**
     * Número que representa o código que o modelo de checklist possui. Identificador único do modelo de checklist.
     */
    @NotNull
    private final Long codModelo;

    /**
     * Número que representa o código da versão em que o modelo de checklist se encontra.
     * O código da versão também é um identificador único no banco de dados. Mesmo modelos diferentes que estejam
     * ambos na primeira versão, terão códigos de versão diferentes.
     */
    @NotNull
    private final Long codVersaoAtualModelo;

    /**
     * Valor alfanumérico que representa o nome do modelo do checklist. Identificador para o usuário.
     */
    @NotNull
    private final String nomeModelo;

    /**
     * Código da unidade a qual o modelo de checklist está vinculado.
     */
    @NotNull
    private final Long codUnidadeModelo;

    /**
     * {@link CargoChecklistOffline Cargos} que estão configurados para realizar este modelo de checklist.
     */
    @NotNull
    private final List<CargoChecklistOffline> cargosLiberados;

    /**
     * {@link TipoVeiculoChecklistOffline Tipos de veículos} que possuem vínculo com este modelo de checklist.
     */
    @NotNull
    private final List<TipoVeiculoChecklistOffline> tiposVeiculosLiberados;

    /**
     * {@link PerguntaModeloChecklistOffline Perguntas} que o modelo de checklist offline possui.
     */
    @NotNull
    private final List<PerguntaModeloChecklistOffline> perguntasModeloChecklistOffline;

    public ModeloChecklistOffline(@NotNull final Long codModelo,
                                  @NotNull final Long codVersaoAtualModelo,
                                  @NotNull final String nomeModelo,
                                  @NotNull final Long codUnidadeModelo,
                                  @NotNull final List<CargoChecklistOffline> cargosLiberados,
                                  @NotNull final List<TipoVeiculoChecklistOffline> tiposVeiculosLiberados,
                                  @NotNull final List<PerguntaModeloChecklistOffline> perguntasModeloChecklistOffline) {
        this.codModelo = codModelo;
        this.codVersaoAtualModelo = codVersaoAtualModelo;
        this.nomeModelo = nomeModelo;
        this.codUnidadeModelo = codUnidadeModelo;
        this.cargosLiberados = cargosLiberados;
        this.tiposVeiculosLiberados = tiposVeiculosLiberados;
        this.perguntasModeloChecklistOffline = perguntasModeloChecklistOffline;
    }

    @NotNull
    public Long getCodModelo() {
        return codModelo;
    }

    @NotNull
    public Long getCodVersaoAtualModelo() {
        return codVersaoAtualModelo;
    }

    @NotNull
    public String getNomeModelo() {
        return nomeModelo;
    }

    @NotNull
    public Long getCodUnidadeModelo() {
        return codUnidadeModelo;
    }

    @NotNull
    public List<CargoChecklistOffline> getCargosLiberados() {
        return cargosLiberados;
    }

    @NotNull
    public List<TipoVeiculoChecklistOffline> getTiposVeiculosLiberados() {
        return tiposVeiculosLiberados;
    }

    @NotNull
    public List<PerguntaModeloChecklistOffline> getPerguntasModeloChecklistOffline() {
        return perguntasModeloChecklistOffline;
    }
}