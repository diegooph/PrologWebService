package br.com.zalf.prolog.webservice.frota.pneu.v3.service;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuFotoEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class PneuFotoV3ServiceImpl implements PneuFotoV3Service {

    @Override
    @NotNull
    public List<PneuFotoEntity> addPhotosToPneu(@NotNull final PneuEntity pneu, @NotNull final List<String> urls) {
        throw new NotImplementedException("metodo não implementado até o momento.");
    }
}
