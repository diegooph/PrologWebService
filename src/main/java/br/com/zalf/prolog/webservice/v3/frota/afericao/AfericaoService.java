package br.com.zalf.prolog.webservice.v3.frota.afericao;

import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
public class AfericaoService {

    @NotNull
    private final AfericaoDao afericaoDao;

    @Autowired
    public AfericaoService(@NotNull final AfericaoDao afericaoDao) {
        this.afericaoDao = afericaoDao;
    }

    @NotNull
    public AfericaoEntity getByCodigo(@NotNull final Long codigo) {
        return afericaoDao.getOne(codigo);
    }

    public void update(@NotNull final AfericaoEntity afericaoEntity) {
        afericaoDao.save(afericaoEntity);
    }
}
