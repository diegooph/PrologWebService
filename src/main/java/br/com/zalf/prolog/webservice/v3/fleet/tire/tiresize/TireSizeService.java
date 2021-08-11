package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireSizeEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                                 @NotNull final ColaboradorAutenticado colaboradorAutenticado) throws Throwable {
        return dao.save(mapper.toEntity(tireSizeCreation, colaboradorAutenticado));
    }
}
