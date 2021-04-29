package br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.v3;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.v3._model.TipoVeiculoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TipoVeiculoV3Service {
    @NotNull
    private static final String TAG = TipoVeiculoV3Service.class.getSimpleName();
    @NotNull
    private final TipoVeiculoV3Dao tipoVeiculoDao;

    @Autowired
    public TipoVeiculoV3Service(@NotNull final TipoVeiculoV3Dao tipoVeiculoDao) {
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
