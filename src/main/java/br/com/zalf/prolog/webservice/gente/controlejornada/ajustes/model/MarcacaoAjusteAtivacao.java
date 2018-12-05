package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Classe utilizada quando uma marcação for ativada ou inativada, seja ela de início ou de fim.
 *
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class MarcacaoAjusteAtivacao extends MarcacaoAjuste {
    /**
     * O código da marcação que será ativada.
     */
    private Long codMarcacaoAtivacao;

    public MarcacaoAjusteAtivacao() {
        super(TipoAcaoAjuste.ATIVACAO);
    }

    @NotNull
    public static MarcacaoAjusteAtivacao createDummy() {
        final MarcacaoAjusteAtivacao ajusteAtivacaoAtivacao = new MarcacaoAjusteAtivacao();
        ajusteAtivacaoAtivacao.setCodMarcacaoAtivacao(20L);
        ajusteAtivacaoAtivacao.setCodJustificativaAjuste(5L);
        ajusteAtivacaoAtivacao.setObservacaoAjuste("Dummy Data");
        ajusteAtivacaoAtivacao.setDataHoraAjuste(LocalDateTime.now());
        return ajusteAtivacaoAtivacao;
    }

    public Long getCodMarcacaoAtivacao() {
        return codMarcacaoAtivacao;
    }

    public void setCodMarcacaoAtivacao(final Long codMarcacaoAtivacao) {
        this.codMarcacaoAtivacao = codMarcacaoAtivacao;
    }
}