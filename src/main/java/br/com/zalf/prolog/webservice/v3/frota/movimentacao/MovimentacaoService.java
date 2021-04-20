package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-04-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Service
public final class MovimentacaoService {
    private final MovimentacaoDao dao;

    @Autowired
    public MovimentacaoService(final MovimentacaoDao dao) {
        this.dao = dao;
    }

    public String getMovimentacoes() {
        return null;
    }
}
