package br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Zart on 03/07/2017.
 */
public final class Situacao {
    private static final String LIBERADO = "LIBERADO";
    private static final String BLOQUEADO = "BLOQUEADO";
    private static final String BLOQUEADO_INTEGRACAO = "BLOQUEADO_INTEGRACAO";

    private String status;
    private String motivo;

    public Situacao() {

    }

    public Situacao(@NotNull final String status, @NotNull final String motivo) {
        setStatus(status);
        setMotivo(motivo);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(@NotNull String status) {
        // Primeiro normalizamos o status.
        status = normalizaStatus(status);
        // Depois verificamos se é um status válido.
        Preconditions.checkArgument(
                status.equals(BLOQUEADO) || status.equals(BLOQUEADO_INTEGRACAO) || status.equals(LIBERADO),
                String.format("O status %s não é valido. Precisa ser %s ou %s ou %s.", status, BLOQUEADO, BLOQUEADO_INTEGRACAO, LIBERADO));

        this.status = status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(@NotNull final String motivo) {
        this.motivo = motivo;
    }

    private String normalizaStatus(@NotNull final String status) {
        return StringUtils.stripAccents(status.trim()).replaceAll("\\s+", "_").toUpperCase();
    }
}