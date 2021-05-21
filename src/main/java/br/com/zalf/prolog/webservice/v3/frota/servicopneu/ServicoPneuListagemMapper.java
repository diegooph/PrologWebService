package br.com.zalf.prolog.webservice.v3.frota.servicopneu;

import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuListagemDto;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class ServicoPneuListagemMapper {

    List<ServicoPneuListagemDto> toDto(final List<ServicoPneuEntity> servicoPneu) {
        throw new NotImplementedException("Método não implementado!");
    }
}
