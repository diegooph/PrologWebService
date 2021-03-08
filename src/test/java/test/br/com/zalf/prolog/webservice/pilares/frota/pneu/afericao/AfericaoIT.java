package test.br.com.zalf.prolog.webservice.pilares.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3.dao.AfericaoV3Dao;
import org.springframework.beans.factory.annotation.Autowired;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

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
}
