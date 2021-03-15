package br.com.zalf.prolog.webservice.frota.pneu.v3.service;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3.dao.PneuV3Dao;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class PneuV3ServiceImpl implements PneuV3Service {

    private final PneuV3Dao dao;


    @Autowired
    public PneuV3ServiceImpl(@NotNull final PneuV3Dao dao) {
        this.dao = dao;
    }

    @Override
    @NotNull
    public PneuEntity create(@NotNull final PneuEntity pneu) {
        throw new NotImplementedException("metodo não implementado até o momento.");
    }
}
