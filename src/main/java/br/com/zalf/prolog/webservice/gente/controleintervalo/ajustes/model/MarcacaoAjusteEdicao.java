package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Classe utilizada quando uma edição é feita em alguma marcação, de início ou fim.
 *
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class MarcacaoAjusteEdicao extends MarcacaoAjuste {
    /**
     * O código da marcação que será editada.
     */
    private Long codMarcacaoEdicao;

    /**
     * A nova data e hora da marcação.
     */
    private LocalDateTime dataHoraNovaInserida;

    public MarcacaoAjusteEdicao() {
        super(TipoMarcacaoAjuste.EDICAO);
    }

    @NotNull
    public static MarcacaoAjusteEdicao createDummy() {
        final MarcacaoAjusteEdicao ajusteEdicao = new MarcacaoAjusteEdicao();
        ajusteEdicao.setDataHoraNovaInserida(LocalDateTime.now());
        ajusteEdicao.setCodJustificativaAjuste(5L);
        ajusteEdicao.setObservacaoAjuste("Dummy Data");
        ajusteEdicao.setDataHoraAjuste(LocalDateTime.now());
        ajusteEdicao.setCodMarcacaoEdicao(20L);
        return ajusteEdicao;
    }

    public Long getCodMarcacaoEdicao() {
        return codMarcacaoEdicao;
    }

    public void setCodMarcacaoEdicao(final Long codMarcacaoEdicao) {
        this.codMarcacaoEdicao = codMarcacaoEdicao;
    }

    public LocalDateTime getDataHoraNovaInserida() {
        return dataHoraNovaInserida;
    }

    public void setDataHoraNovaInserida(final LocalDateTime dataHoraNovaInserida) {
        this.dataHoraNovaInserida = dataHoraNovaInserida;
    }

    @Override
    public String toString() {
        return "MarcacaoAjusteEdicao{" +
                "codMarcacaoEdicao=" + codMarcacaoEdicao +
                ", dataHoraNovaInserida=" + dataHoraNovaInserida +
                '}';
    }
}