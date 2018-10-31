package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.justificativa;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

/**
 * Created on 31/10/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class JustificativaAjusteConverter {

    public JustificativaAjusteConverter() {
        throw new IllegalStateException(JustificativaAjusteConverter.class.getSimpleName() + " cannot be instatiated!");
    }

    @NotNull
    static JustificativaAjuste createJustificativaAjuste(@NotNull final ResultSet rSet) throws Throwable {
        final JustificativaAjuste justificativa = new JustificativaAjuste();
        justificativa.setNomeJustificativaAjuste(rSet.getString("NOME"));
        justificativa.setCodigo(rSet.getLong("CODIGO"));
        justificativa.setCodEmpresa(rSet.getLong("COD_EMPRESA") == 0
                ? null
                : rSet.getLong("COD_EMPRESA"));
        justificativa.setObrigatorioObservacao(rSet.getBoolean("OBRIGA_OBSERVACAO"));
        justificativa.setAtiva(rSet.getBoolean("STATUS_ATIVO"));
        return justificativa;
    }
}