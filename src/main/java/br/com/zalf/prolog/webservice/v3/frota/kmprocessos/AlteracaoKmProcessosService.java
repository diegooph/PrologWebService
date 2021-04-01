package br.com.zalf.prolog.webservice.v3.frota.kmprocessos;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.AlteracaoKmProcesso;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.AlteracaoKmProcessoEntity;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.AlteracaoKmResponse;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.KmProcessoAtualizavel;
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

    @Transactional
    public void updateKmProcesso(@NotNull final AlteracaoKmProcesso alteracaoKmProcesso) {
        final KmProcessoAtualizavel service = serviceFactory.createService(alteracaoKmProcesso);
        final AlteracaoKmResponse response = alteradorKmProcesso.updateKmProcesso(service, alteracaoKmProcesso);
        if (response.isKmFoiAlterado()) {
            Log.d(TAG, "KM foi alterado, gerando histórico");
            final AlteracaoKmProcessoEntity entity = AlteracaoKmProcessoEntity
                    .builder()
                    .withDataHoraAlteracaoKm(Now.getOffsetDateTimeUtc())
                    .withCodColaboradorAlteracaoKm(alteracaoKmProcesso.getCodColaboradorAlteracao())
                    .withOrigemAlteracao(OrigemAcaoEnum.PROLOG_WEB)
                    .withCodProcessoAlterado(alteracaoKmProcesso.getCodProcesso())
                    .withTipoProcessoAlterado(alteracaoKmProcesso.getTipoProcesso())
                    .withKmAntigo(response.getKmAntigo())
                    .withKmNovo(alteracaoKmProcesso.getNovoKm())
                    .build();
            alteracaoKmProcessoDao.save(entity);
        } else {
            Log.d(TAG, "KM não foi alterado, nenhum histórico será gerado");
        }
    }
}
