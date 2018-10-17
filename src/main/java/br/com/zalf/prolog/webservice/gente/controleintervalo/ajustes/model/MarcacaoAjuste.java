package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Essa é a superclasse de qualquer ajuste que seja realizado no sistema. Os ajustes podem ser de qualquer um dos tipos
 * presentes no {@code enum} {@link TipoAcaoAjuste}. Cada tipo no {@code enum} tem uma subclasse específica que
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
    private final TipoAcaoAjuste tipoAcaoAjuste;

    public MarcacaoAjuste(@NotNull final TipoAcaoAjuste tipoAcaoAjuste) {
        this.tipoAcaoAjuste = tipoAcaoAjuste;
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<MarcacaoAjuste> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(MarcacaoAjuste.class, "tipoMarcacaoAjuste")
                .registerSubtype(MarcacaoAjusteAdicao.class, TipoAcaoAjuste.ADICAO.asString())
                .registerSubtype(MarcacaoAjusteAdicaoInicioFim.class, TipoAcaoAjuste.ADICAO_INICIO_FIM.asString())
                .registerSubtype(MarcacaoAjusteEdicao.class, TipoAcaoAjuste.EDICAO.asString())
                .registerSubtype(MarcacaoAjusteAtivacao.class, TipoAcaoAjuste.ATIVACAO.asString())
                .registerSubtype(MarcacaoAjusteInativacao.class, TipoAcaoAjuste.INATIVACAO.asString());
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
    public TipoAcaoAjuste getTipoAcaoAjuste() {
        return tipoAcaoAjuste;
    }

    @Override
    public String toString() {
        return "MarcacaoAjuste{" +
                "codJustificativaAjuste=" + codJustificativaAjuste +
                ", observacaoAjuste='" + observacaoAjuste + '\'' +
                ", dataHoraAjuste=" + dataHoraAjuste +
                ", tipoMarcacaoAjuste=" + tipoAcaoAjuste +
                '}';
    }
}
