package br.com.zalf.prolog.webservice.frota.pneu.v3.service;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class PneuV3ServiceImpl implements PneuV3Service {

    @Override
    public PneuEntity create(final PneuEntity pneu) {
        throw new NotImplementedException("metodo não implementado até o momento.");
    }
}
