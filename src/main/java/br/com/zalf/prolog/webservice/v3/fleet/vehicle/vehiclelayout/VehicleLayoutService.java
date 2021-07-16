package br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehiclelayout;

import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehiclelayout._model.VehicleLayoutEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleLayoutService {
    @NotNull
    private final VehicleLayoutDao vehicleLayoutDao;

    @Autowired
    public VehicleLayoutService(@NotNull final VehicleLayoutDao vehicleLayoutDao) {
        this.vehicleLayoutDao = vehicleLayoutDao;
    }

    @NotNull
    public VehicleLayoutEntity getById(@NotNull final Short vehicleLayoutId) {
        return vehicleLayoutDao.getOne(vehicleLayoutId);
    }
}
