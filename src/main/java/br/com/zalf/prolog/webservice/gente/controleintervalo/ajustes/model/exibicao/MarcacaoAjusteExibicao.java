package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoMarcacao;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Representa uma marcação para exibição na tela de ajustes.
 *
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class MarcacaoAjusteExibicao {
    /**
     * Código único da marcação.
     */
    private Long codMarcacao;

    /**
     * Data e hora de realização da marcação.
     */
    private LocalDateTime dataHoraMarcacao;

    /**
     * Código do {@link TipoMarcacao tipo de marcação} utilizado.
     */
    private Long codTipoMarcacao;

    /**
     * Nome do {@link TipoMarcacao tipo de marcação} utilizado.
     */
    private String nomeTipoMarcacao;

    /**
     * Indica se esta marcação está ativa.
     * <code>true</code> se estiver ativa, <code>false</code> caso contrário.
     */
    private boolean isAtiva;

    public MarcacaoAjusteExibicao() {

    }

    @NotNull
    public static MarcacaoAjusteExibicao createDummyInicio() {
        final MarcacaoAjusteExibicao intervalo = new MarcacaoAjusteExibicao();
        intervalo.setCodMarcacao(10101L);
        intervalo.setCodTipoMarcacao(10L);
        intervalo.setNomeTipoMarcacao("Refeição");
        intervalo.setAtiva(true);
        intervalo.setDataHoraMarcacao(LocalDateTime.now());
        return intervalo;
    }

    @NotNull
    public static MarcacaoAjusteExibicao createDummyFim() {
        final MarcacaoAjusteExibicao intervalo = new MarcacaoAjusteExibicao();
        intervalo.setCodMarcacao(10101L);
        intervalo.setCodTipoMarcacao(10L);
        intervalo.setNomeTipoMarcacao("Refeição");
        intervalo.setAtiva(true);
        intervalo.setDataHoraMarcacao(LocalDateTime.now().plus(30, ChronoUnit.MINUTES));
        return intervalo;
    }

    public Long getCodMarcacao() {
        return codMarcacao;
    }

    public void setCodMarcacao(final Long codMarcacao) {
        this.codMarcacao = codMarcacao;
    }

    public LocalDateTime getDataHoraMarcacao() {
        return dataHoraMarcacao;
    }

    public void setDataHoraMarcacao(final LocalDateTime dataHoraMarcacao) {
        this.dataHoraMarcacao = dataHoraMarcacao;
    }

    public Long getCodTipoMarcacao() {
        return codTipoMarcacao;
    }

    public void setCodTipoMarcacao(final Long codTipoMarcacao) {
        this.codTipoMarcacao = codTipoMarcacao;
    }

    public String getNomeTipoMarcacao() {
        return nomeTipoMarcacao;
    }

    public void setNomeTipoMarcacao(final String nomeTipoMarcacao) {
        this.nomeTipoMarcacao = nomeTipoMarcacao;
    }

    public boolean isAtiva() {
        return isAtiva;
    }

    public void setAtiva(final boolean ativa) {
        isAtiva = ativa;
    }
}
