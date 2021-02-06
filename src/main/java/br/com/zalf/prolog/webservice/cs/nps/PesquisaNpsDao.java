package br.com.zalf.prolog.webservice.cs.nps;

import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsBloqueio;
import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsDisponivel;
import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsRealizada;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created on 2019-10-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface PesquisaNpsDao {
    @NotNull
    Optional<PesquisaNpsDisponivel> getPesquisaNpsColaborador(@NotNull final Long codColaborador) throws Throwable;

    @NotNull
    Long insereRespostasPesquisaNps(@NotNull final PesquisaNpsRealizada pesquisaRealizada) throws Throwable;

    void bloqueiaPesquisaNpsColaborador(@NotNull final PesquisaNpsBloqueio pesquisaBloqueio) throws Throwable;
}