package br.com.zalf.prolog.webservice.geral.dispositivomovel;

import br.com.zalf.prolog.webservice.geral.dispositivomovel.model.DispositivoMovel;
import br.com.zalf.prolog.webservice.geral.dispositivomovel.model.DispositivoMovelInsercao;
import br.com.zalf.prolog.webservice.geral.dispositivomovel.model.MarcaDispositivoMovelSelecao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface DispositivoMovelDao {

    @NotNull
    Long insertDispositivoMovel(@NotNull final DispositivoMovelInsercao dispositivoMovel) throws Throwable;

    void updateDispositivoMovel(@NotNull final DispositivoMovel dispositivoMovel) throws Throwable;

    @NotNull
    List<DispositivoMovel> getDispositivosPorEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    @NotNull
    DispositivoMovel getDispositivoMovel(@NotNull final Long codEmpresa, @NotNull final Long codDispositivo) throws Throwable;

    @NotNull
    List<MarcaDispositivoMovelSelecao> getMarcasDispositivos() throws Throwable;

    void deleteDispositivoMovel(@NotNull final Long codEmpresa,
                                @NotNull final Long codDispositivo) throws Throwable;

}