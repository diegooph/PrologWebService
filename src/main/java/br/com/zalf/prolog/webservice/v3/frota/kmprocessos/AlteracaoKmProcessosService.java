package br.com.zalf.prolog.webservice.v3.frota.kmprocessos;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.AlteracaoKmProcesso;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.AlteracaoKmProcessoEntity;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor.AlteracaoKmProcessoVisitor;
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
public class AlteracaoKmProcessosService {
    @NotNull
    private final AlteracaoKmProcessoDao alteracaoKmProcessoDao;
    @NotNull
    private final AlteracaoKmProcessoVisitor visitor;

    @Autowired
    public AlteracaoKmProcessosService(@NotNull final AlteracaoKmProcessoDao alteracaoKmProcessoDao,
                                       @NotNull final AlteracaoKmProcessoVisitor visitor) {
        this.alteracaoKmProcessoDao = alteracaoKmProcessoDao;
        this.visitor = visitor;
    }

    @Transactional
    public void updateKmProcesso(@NotNull final AlteracaoKmProcesso alteracaoKmProcesso) {
        alteracaoKmProcesso.accept(visitor);
        final AlteracaoKmProcessoEntity entity = AlteracaoKmProcessoEntity
                .builder()
                .withDataHoraAlteraoKm(Now.getOffsetDateTimeUtc())
                .withCodColaboradorAlteracaoKm(alteracaoKmProcesso.getCodColaboradorAlteracao())
                .withOrigemAlteracao(OrigemAcaoEnum.PROLOG_WEB)
                .withCodProcessoAlterado(alteracaoKmProcesso.getCodProcesso())
                .withTipoProcessoAlterado(alteracaoKmProcesso.getTipoProcesso())
                .withKmAntigo(-1)
                .withKmNovo(alteracaoKmProcesso.getNovoKm())
                .build();
        alteracaoKmProcessoDao.save(entity);
    }
}
