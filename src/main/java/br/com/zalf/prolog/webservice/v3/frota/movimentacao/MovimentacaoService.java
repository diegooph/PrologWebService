package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
public class MovimentacaoService {

    @NotNull
    private final MovimentacaoDao movimentacaoDao;

    @Autowired
    public MovimentacaoService(@NotNull final MovimentacaoDao movimentacaoDao) {
        this.movimentacaoDao = movimentacaoDao;
    }

    @NotNull
    public MovimentacaoEntity getByCodigo(@NotNull final Long codigo) {
        return movimentacaoDao.getOne(codigo);
    }

    public void update(@NotNull final MovimentacaoEntity movimentacaoEntity) {
        movimentacaoDao.save(movimentacaoEntity);
    }
}
