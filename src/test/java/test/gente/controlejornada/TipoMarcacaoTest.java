package test.gente.controlejornada;

import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Icone;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoMarcacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacaoService;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import test.BaseTest;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created on 30/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class TipoMarcacaoTest extends BaseTest {
    @NotNull
    private final TipoMarcacaoService service = new TipoMarcacaoService();


    @Test
    public void testInsertTipoMarcacao() throws Throwable {
        final TipoMarcacao tipoMarcacao = createTipoMarcacao();
        final AbstractResponse response = service.insertTipoMarcacao(tipoMarcacao);
        assertNotNull(response);
        assertTrue(response.isOk());
    }

    @Test
    public void testUpdateTipoMarcacao() throws Throwable {
        final TipoMarcacao tipoMarcacao = createTipoMarcacao();
        // Seta código para poder atualizar
        tipoMarcacao.setCodigo(93L);
        tipoMarcacao.setNome(tipoMarcacao.getNome() + " UPDATE");
        final AbstractResponse response = service.updateTipoMarcacao(tipoMarcacao);
        assertNotNull(response);
        assertTrue(response.isOk());
    }

    @NotNull
    private TipoMarcacao createTipoMarcacao() {
        final TipoMarcacao tipo = new TipoMarcacao();
        // Para evitar erro de nomes iguais no BD.
        tipo.setNome("Jornada " + LocalDateTime.now());
        tipo.setIcone(Icone.JORNADA);
        tipo.setTempoRecomendado(Duration.ofHours(8));
        tipo.setTempoLimiteEstouro(Duration.ofHours(9));
        tipo.setHorarioSugerido(Time.valueOf(LocalTime.of(7, 0, 0)));
        tipo.setTipoJornada(true);

        final Unidade unidade = new Unidade();
        unidade.setCodigo(5L);
        tipo.setUnidade(unidade);

        final List<Cargo> cargos = new ArrayList<>();
        cargos.add(new Cargo(158L, "Ajudante"));
        cargos.add(new Cargo(159L, "Motorista"));
        tipo.setCargos(cargos);

        final List<Long> descontadosJornadaBruta = new ArrayList<>();
        descontadosJornadaBruta.add(15L);
        tipo.setCodTiposDescontadosJornadaBruta(descontadosJornadaBruta);

        final List<Long> descontadosJornadaLiquida = new ArrayList<>();
        descontadosJornadaLiquida.add(19L);
        tipo.setCodTiposDescontadosJornadaLiquida(descontadosJornadaLiquida);

        return tipo;
    }
}