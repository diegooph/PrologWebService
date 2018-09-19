package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Essa é a superclasse de qualquer ajuste que seja realizado no sistema. Os ajustes podem ser de qualquer um dos tipos
 * presentes no {@code enum} {@link TipoMarcacaoAjuste}. Cada tipo no {@code enum} tem uma subclasse específica que
 * contém informações próprias do ajuste sendo realizado.
 *
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class MarcacaoAjuste {
    private Long codJustificativaAjuste;
    private String observacaoAjuste;
    private LocalDateTime dataHoraAjuste;
    @Exclude
    @NotNull
    private final TipoMarcacaoAjuste tipoMarcacaoAjuste;

    public MarcacaoAjuste(@NotNull final TipoMarcacaoAjuste tipoMarcacaoAjuste) {
        this.tipoMarcacaoAjuste = tipoMarcacaoAjuste;
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<MarcacaoAjuste> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(MarcacaoAjuste.class, "tipoMarcacaoAjuste")
                .registerSubtype(MarcacaoAjusteAdicao.class, TipoMarcacaoAjuste.ADICAO.asString())
                .registerSubtype(MarcacaoAjusteAdicaoInicioFim.class, TipoMarcacaoAjuste.ADICAO_INICIO_FIM.asString())
                .registerSubtype(MarcacaoAjusteEdicao.class, TipoMarcacaoAjuste.EDICAO.asString())
                .registerSubtype(MarcacaoAjusteAtivacaoInativacao.class, TipoMarcacaoAjuste.ATIVACAO_INATIVACAO.asString());
    }

    public Long getCodJustificativaAjuste() {
        return codJustificativaAjuste;
    }

    public void setCodJustificativaAjuste(final Long codJustificativaAjuste) {
        this.codJustificativaAjuste = codJustificativaAjuste;
    }

    public String getObservacaoAjuste() {
        return observacaoAjuste;
    }

    public void setObservacaoAjuste(final String observacaoAjuste) {
        this.observacaoAjuste = observacaoAjuste;
    }

    public LocalDateTime getDataHoraAjuste() {
        return dataHoraAjuste;
    }

    public void setDataHoraAjuste(final LocalDateTime dataHoraAjuste) {
        this.dataHoraAjuste = dataHoraAjuste;
    }

    @NotNull
    public TipoMarcacaoAjuste getTipoMarcacaoAjuste() {
        return tipoMarcacaoAjuste;
    }

    @Override
    public String toString() {
        return "MarcacaoAjuste{" +
                "codJustificativaAjuste=" + codJustificativaAjuste +
                ", observacaoAjuste='" + observacaoAjuste + '\'' +
                ", dataHoraAjuste=" + dataHoraAjuste +
                ", tipoMarcacaoAjuste=" + tipoMarcacaoAjuste +
                '}';
    }
}
