package br.com.zalf.prolog.webservice.v3.fleet.kmprocessos;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AlteracaoKmProcessosService {
    private static final String TAG = AlteracaoKmProcessosService.class.getSimpleName();
    @NotNull
    private final AlteracaoKmProcessoDao alteracaoKmProcessoDao;
    @NotNull
    private final AlteracaoKmProcessosServiceFactory serviceFactory;
    @NotNull
    private final AlteradorKmProcesso alteradorKmProcesso;
    @NotNull
    private final AlteracaoKmProcessoMapper mapper;

    @Transactional
    public void updateKmProcesso(@NotNull final AlteracaoKmProcesso alteracaoKmProcesso) {
        final KmProcessoAtualizavel service = serviceFactory.createService(alteracaoKmProcesso);
        final AlteracaoKmResponse response = alteradorKmProcesso.updateKmProcesso(service, alteracaoKmProcesso);
        if (response.isKmFoiAlterado()) {
            Log.d(TAG, "KM foi alterado, gerando histórico");
            final AlteracaoKmProcessoEntity entity = mapper.toEntity(alteracaoKmProcesso, response.getKmAntigo());
            alteracaoKmProcessoDao.save(entity);
        } else {
            Log.d(TAG, "KM não foi alterado, nenhum histórico será gerado");
        }
    }
}
