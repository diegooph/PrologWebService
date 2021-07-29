package br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehicletype;

import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehicletype._model.VehicleTypeEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleTypeService {
    @NotNull
    private final VehicleTypeDao vehicleTypeDao;

    @Autowired
    public VehicleTypeService(@NotNull final VehicleTypeDao vehicleTypeDao) {
        this.vehicleTypeDao = vehicleTypeDao;
    }

    @NotNull
    public VehicleTypeEntity getById(@NotNull final Long vehicleTypeId) {
        return vehicleTypeDao.getOne(vehicleTypeId);
    }
}
