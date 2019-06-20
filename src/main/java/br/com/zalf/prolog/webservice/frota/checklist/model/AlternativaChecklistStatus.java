package br.com.zalf.prolog.webservice.frota.checklist.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 08/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class AlternativaChecklistStatus {
    @NotNull
    private final Long codAlternativa;
    @Nullable
    private final Long codItemOsAlternativa;
    private final boolean temItemOsPendente;
    private final boolean deveAbrirOrdemServico;
    private final int qtdApontamentosItemOs;
    private final PrioridadeAlternativa prioridadeAlternativa;

    public AlternativaChecklistStatus(@NotNull final Long codAlternativa,
                                      @Nullable final Long codItemOsAlternativa,
                                      final boolean temItemOsPendente,
                                      final boolean deveAbrirOrdemServico,
                                      final int qtdApontamentosItemOs,
                                      final PrioridadeAlternativa prioridadeAlternativa) {
        this.codAlternativa = codAlternativa;
        this.codItemOsAlternativa = codItemOsAlternativa;
        this.temItemOsPendente = temItemOsPendente;
        this.deveAbrirOrdemServico = deveAbrirOrdemServico;
        this.qtdApontamentosItemOs = qtdApontamentosItemOs;
        this.prioridadeAlternativa = prioridadeAlternativa;
    }

    @NotNull
    public Long getCodAlternativa() {
        return codAlternativa;
    }

    @Nullable
    public Long getCodItemOsAlternativa() {
        return codItemOsAlternativa;
    }

    public boolean isTemItemOsPendente() {
        return temItemOsPendente;
    }

    public boolean isDeveAbrirOrdemServico() {
        return deveAbrirOrdemServico;
    }

    public int getQtdApontamentosItemOs() {
        return qtdApontamentosItemOs;
    }

    public PrioridadeAlternativa getPrioridadeAlternativa() {
        return prioridadeAlternativa;
    }
}
