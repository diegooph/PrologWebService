package br.com.zalf.prolog.webservice.v3.fleet.tireservice.servicetype;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class TireServiceTypeService {
    @NotNull
    private final TireServiceTypeDao dao;

    @Autowired
    public TireServiceTypeService(@NotNull final TireServiceTypeDao dao) {
        this.dao = dao;
    }

    @NotNull
    @Transactional
    public TireServiceTypeEntity getTireServiceTypeIncreaseLifeCycle() {
        return dao.getTireServiceTypeIncreaseLifeCycle();
    }
}
