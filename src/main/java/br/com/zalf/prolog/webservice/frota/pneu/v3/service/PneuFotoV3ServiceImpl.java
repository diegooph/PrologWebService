package br.com.zalf.prolog.webservice.frota.pneu.v3.service;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuFotoEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3.dao.PneuFotoV3Dao;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class PneuFotoV3ServiceImpl implements PneuFotoV3Service {

    private static final String TAG = PneuV3ServiceImpl.class.getSimpleName();

    private final PneuFotoV3Dao dao;

    @Autowired
    public PneuFotoV3ServiceImpl(@NotNull final PneuFotoV3Dao dao) {
        this.dao = dao;
    }

    @Override
    @NotNull
    public List<PneuFotoEntity> createPneuFotos(@NotNull final PneuEntity pneu, @NotNull final List<String> urls) {
        return urls.stream()
                .map(url -> PneuFotoEntity.createPneuFotoEntity(pneu, url))
                .filter(this::notCreated)
                .map(dao::save)
                .peek(foto -> Log.d(TAG, "Foi inserido a imagem: " + foto + " para o pneu de id: " + pneu.getId()))
                .collect(Collectors.toList());
    }
}
