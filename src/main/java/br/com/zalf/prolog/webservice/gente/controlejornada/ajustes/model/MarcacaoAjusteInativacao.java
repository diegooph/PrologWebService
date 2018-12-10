package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 17/10/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class MarcacaoAjusteInativacao extends MarcacaoAjuste {
    /**
     * O código da marcação que será inativada.
     */
    private Long codMarcacaoInativacao;

    public MarcacaoAjusteInativacao() {
        super(TipoAcaoAjuste.INATIVACAO);
    }

    @NotNull
    public static MarcacaoAjusteInativacao createDummy() {
        final MarcacaoAjusteInativacao ajusteAtivacaoInativacao = new MarcacaoAjusteInativacao();
        ajusteAtivacaoInativacao.setCodMarcacaoInativacao(20L);
        ajusteAtivacaoInativacao.setCodJustificativaAjuste(5L);
        ajusteAtivacaoInativacao.setObservacaoAjuste("Dummy Data");
        ajusteAtivacaoInativacao.setDataHoraAjuste(LocalDateTime.now());
        return ajusteAtivacaoInativacao;
    }

    public Long getCodMarcacaoInativacao() {
        return codMarcacaoInativacao;
    }

    public void setCodMarcacaoInativacao(final Long codMarcacaoInativacao) {
        this.codMarcacaoInativacao = codMarcacaoInativacao;
    }
}
