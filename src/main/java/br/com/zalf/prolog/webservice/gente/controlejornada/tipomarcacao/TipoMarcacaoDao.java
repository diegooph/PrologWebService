package br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao;

import br.com.zalf.prolog.webservice.gente.controlejornada.DadosIntervaloChangedListener;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoMarcacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 20/12/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface TipoMarcacaoDao {

    @NotNull
    Long insertTipoMarcacao(@NotNull final TipoMarcacao tipoMarcacao,
                            @NotNull final DadosIntervaloChangedListener listener) throws Throwable;

    void updateTipoMarcacao(@NotNull final TipoMarcacao tipoMarcacao,
                            @NotNull final DadosIntervaloChangedListener listener) throws Throwable;

    @NotNull
    List<TipoMarcacao> getTiposMarcacoes(@NotNull final Long codUnidade,
                                         final boolean apenasAtivos,
                                         final boolean withCargos) throws Throwable;

    @NotNull
    TipoMarcacao getTipoMarcacao(@NotNull final Long codTipoMarcacao) throws Throwable;

    void updateStatusAtivoTipoMarcacao(@NotNull final Long codTipoMarcacao,
                                       @NotNull final TipoMarcacao tipoMarcacao,
                                       @NotNull final DadosIntervaloChangedListener listener) throws Throwable;
}