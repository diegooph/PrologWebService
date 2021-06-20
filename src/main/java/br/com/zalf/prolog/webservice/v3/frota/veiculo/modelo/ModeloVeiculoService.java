package br.com.zalf.prolog.webservice.v3.frota.veiculo.modelo;

import br.com.zalf.prolog.webservice.v3.frota.veiculo.modelo._model.ModeloVeiculoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-06-16
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Service
public class ModeloVeiculoService {
    @NotNull
    private final ModeloVeiculoDao dao;

    @Autowired
    public ModeloVeiculoService(@NotNull final ModeloVeiculoDao dao) {
        this.dao = dao;
    }

    public ModeloVeiculoEntity getByCod(@NotNull final Long codModeloVeiculo) {
        return dao.getOne(codModeloVeiculo);
    }
}
