package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MarcacaoAjusteAtivacaoInativacao extends MarcacaoAjuste {
    private boolean isAtivo;

    public MarcacaoAjusteAtivacaoInativacao() {
        super(TipoMarcacaoAjuste.ATIVACAO_INATIVACAO);
    }

    @NotNull
    public static MarcacaoAjusteAtivacaoInativacao createDummy() {
        final MarcacaoAjusteAtivacaoInativacao ajusteAtivacaoInativacao = new MarcacaoAjusteAtivacaoInativacao();
        ajusteAtivacaoInativacao.setAtivo(true);
        ajusteAtivacaoInativacao.setCodJustificativaAjuste(5L);
        ajusteAtivacaoInativacao.setObservacaoAjuste("Dummy Data");
        ajusteAtivacaoInativacao.setDataHoraAjuste(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        return ajusteAtivacaoInativacao;
    }

    public boolean isAtivo() {
        return isAtivo;
    }

    public void setAtivo(final boolean ativo) {
        isAtivo = ativo;
    }

    @Override
    public String toString() {
        return "MarcacaoAjusteAtivacaoInativacao{" +
                "isAtivo=" + isAtivo +
                '}';
    }
}
