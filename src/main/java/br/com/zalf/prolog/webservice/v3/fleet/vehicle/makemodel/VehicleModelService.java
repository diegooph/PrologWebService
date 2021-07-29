package br.com.zalf.prolog.webservice.v3.fleet.vehicle.makemodel;

import br.com.zalf.prolog.webservice.v3.fleet.vehicle.makemodel._model.VehicleModelEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-06-16
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Service
public class VehicleModelService {
    @NotNull
    private final VehicleModelDao dao;

    @Autowired
    public VehicleModelService(@NotNull final VehicleModelDao dao) {
        this.dao = dao;
    }

    public VehicleModelEntity getById(@NotNull final Long vehicleModelId) {
        return dao.getOne(vehicleModelId);
    }
}
