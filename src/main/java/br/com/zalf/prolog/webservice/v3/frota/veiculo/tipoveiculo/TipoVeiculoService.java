package br.com.zalf.prolog.webservice.v3.frota.veiculo.tipoveiculo;

import br.com.zalf.prolog.webservice.v3.frota.veiculo.tipoveiculo._model.TipoVeiculoEntity;
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
        return tipoVeiculoDao.getOne(codTipoVeiculo);
    }
}
