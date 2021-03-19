package br.com.zalf.prolog.webservice.frota.veiculo.v3;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.veiculo.v3._model.VeiculoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VeiculoV3Service {
    @NotNull
    private static final String TAG = VeiculoV3Service.class.getSimpleName();
    @NotNull
    private final VeiculoV3Dao veiculoDao;

    @Autowired
    public VeiculoV3Service(@NotNull final VeiculoV3Dao veiculoDao) {
        this.veiculoDao = veiculoDao;
    }

    @NotNull
    public SuccessResponse insert(@NotNull final VeiculoEntity veiculoEntity) {
        try {
            final VeiculoEntity saved = veiculoDao.save(veiculoEntity);
            return new SuccessResponse(saved.getCodigo(), "Veículo inserido com sucesso.");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir veículo.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir veículo, tente novamente.");
        }
    }
}
