package br.com.zalf.prolog.webservice.gente.prontuarioCondutor;

import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ProntuarioCondutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Zart on 03/07/2017.
 */
public interface ProntuarioCondutorDao {

    @NotNull
    ProntuarioCondutor getProntuario(@NotNull final Long cpf) throws Throwable;

    void insertOrUpdate(@NotNull final String path) throws Throwable;

    @Nullable
    Double getPontuacaoProntuario(@NotNull final Long cpf) throws Throwable;

    @NotNull
    List<ProntuarioCondutor> getResumoProntuarios(@NotNull final Long codUnidade,
                                                  @NotNull final String equipe) throws Throwable;

}
