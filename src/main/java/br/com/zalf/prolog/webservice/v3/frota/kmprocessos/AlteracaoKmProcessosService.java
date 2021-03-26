package br.com.zalf.prolog.webservice.v3.frota.kmprocessos;

import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.AlteracaoKmProcessoDto;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.AlteracaoKmProcessoEntity;
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

    @Autowired
    public AlteracaoKmProcessosService(@NotNull final AlteracaoKmProcessoDao alteracaoKmProcessoDao) {
        this.alteracaoKmProcessoDao = alteracaoKmProcessoDao;
    }

    @Transactional
    public void updateKmProcesso(@NotNull final AlteracaoKmProcessoDto alteracaoKmProcesso) {
        alteracaoKmProcessoDao.save(AlteracaoKmProcessoEntity.builder().build());
    }
}
