package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 14/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ColaboradorFolhaPontoDb {
    @NotNull
    private final String cpfColaborador;
    @NotNull
    private final String nomeColaborador;

    public ColaboradorFolhaPontoDb(@NotNull final String cpfColaborador, @NotNull final String nomeColaborador) {
        this.cpfColaborador = cpfColaborador;
        this.nomeColaborador = nomeColaborador;
    }

    @NotNull
    public String getCpfColaborador() {
        return cpfColaborador;
    }

    @NotNull
    public String getNomeColaborador() {
        return nomeColaborador;
    }
}
