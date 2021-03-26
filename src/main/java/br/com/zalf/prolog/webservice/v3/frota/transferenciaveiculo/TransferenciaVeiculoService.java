package br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo;

import br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo._model.TransferenciaVeiculoProcessoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
public class TransferenciaVeiculoService {

    @NotNull
    private final TransferenciaVeiculoDao transferenciaVeiculoDao;

    @Autowired
    public TransferenciaVeiculoService(@NotNull final TransferenciaVeiculoDao transferenciaVeiculoDao) {
        this.transferenciaVeiculoDao = transferenciaVeiculoDao;
    }

    @NotNull
    public TransferenciaVeiculoProcessoEntity getByCodigo(@NotNull final Long codigo) {
        return transferenciaVeiculoDao.getOne(codigo);
    }
}
