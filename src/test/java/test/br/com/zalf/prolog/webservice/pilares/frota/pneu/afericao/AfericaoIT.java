package test.br.com.zalf.prolog.webservice.pilares.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.AfericaoEntity;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.AfericaoPlacaDto;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3.dao.AfericaoV3Dao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 2021-03-05
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class AfericaoIT extends IntegrationTest {

    @Autowired
    private AfericaoApiClient client;

    @Autowired
    private AfericaoV3Dao dao;

    private List<AfericaoEntity> afericoes;

    @BeforeEach
    void setUp() {
        afericoes = dao.findAll();
    }
}
