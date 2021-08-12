package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireSizeEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeCreation;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeStatusChange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class TireSizeService {
    @NotNull
    private final TireSizeDao dao;
    @NotNull
    private final TireSizeMapper mapper;

    @Autowired
    public TireSizeService(@NotNull final TireSizeDao dao,
                           @NotNull final TireSizeMapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    @NotNull
    public TireSizeEntity insert(@NotNull final TireSizeCreation tireSizeCreation,
                                 @NotNull final ColaboradorAutenticado colaboradorAutenticado) {
        return dao.save(mapper.toEntity(tireSizeCreation, colaboradorAutenticado));
    }

    public List<TireSizeEntity> getAll(@NotNull final Long companyId,
                                       @Nullable final Boolean statusActive) {
        return dao.findAll(companyId, statusActive);
    }

    public void updateStatus(@NotNull final TireSizeStatusChange tireSizeStatusChange) {
        final int rowsUpdated =
                dao.updateStatus(tireSizeStatusChange.getTireSizeId(), tireSizeStatusChange.getActive());
        if (rowsUpdated == 0) {
            throw new EntityNotFoundException(
                    String.format("The tire size of id %d was not found!", tireSizeStatusChange.getTireSizeId()));
        }
    }
}
