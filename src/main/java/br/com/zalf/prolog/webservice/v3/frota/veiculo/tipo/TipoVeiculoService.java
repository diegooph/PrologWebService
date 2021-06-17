package br.com.zalf.prolog.webservice.v3.frota.veiculo.tipo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.v3.frota.veiculo.tipo._model.TipoVeiculoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TipoVeiculoService {
    @NotNull
    private static final String TAG = TipoVeiculoService.class.getSimpleName();
    @NotNull
    private final TipoVeiculoDao tipoVeiculoDao;

    @Autowired
    public TipoVeiculoService(@NotNull final TipoVeiculoDao tipoVeiculoDao) {
        this.tipoVeiculoDao = tipoVeiculoDao;
    }

    @NotNull
    public TipoVeiculoEntity getByCod(@NotNull final Long codTipoVeiculo) {
        try {
            return tipoVeiculoDao.getOne(codTipoVeiculo);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar tipo de veículo %d", codTipoVeiculo), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar tipo de veículo, tente novamente.");
        }
    }
}
