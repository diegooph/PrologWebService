package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.inconsistencias;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 18/10/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class MarcacaoInconsistencia {

    /**
     * Uma descrição, humanamente legível, especificando a inconsistência.
     */
    private String descricaoInconsistencia;

    @Exclude
    @NotNull
    private final TipoInconsistenciaMarcacao tipoInconsistenciaMarcacao;

    public MarcacaoInconsistencia(@NotNull final TipoInconsistenciaMarcacao tipoInconsistenciaMarcacao) {
        this.tipoInconsistenciaMarcacao = tipoInconsistenciaMarcacao;
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<MarcacaoInconsistencia> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(MarcacaoInconsistencia.class, "tipoInconsistenciaMarcacao")
                .registerSubtype(InconsistenciaSemVinculo.class, TipoInconsistenciaMarcacao.SEM_VINCULO.asString())
                .registerSubtype(InconsistenciaInicioAntesFim.class, TipoInconsistenciaMarcacao.FIM_ANTES_INICIO.asString());
    }

    public String getDescricaoInconsistencia() {
        return descricaoInconsistencia;
    }

    public void setDescricaoInconsistencia(final String descricaoInconsistencia) {
        this.descricaoInconsistencia = descricaoInconsistencia;
    }
}