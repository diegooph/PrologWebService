package test.br.com.zalf.prolog.webservice.pilares.gente.controlejornada;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.gente.unidade._model.Unidade;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Icone;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.FormulaCalculoJornada;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoDescontadoJornada;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacaoService;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created on 30/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class TipoMarcacaoTest extends BaseTest {
    @NotNull
    private final TipoMarcacaoService service = new TipoMarcacaoService();

    @Before
    public void initialize() {
        DatabaseManager.init();
    }

    @After
    public void destroy() {
        DatabaseManager.finish();
    }

    @Test
    public void testInsertTipoMarcacao() throws Throwable {
        final TipoMarcacao tipoMarcacao = createTipoMarcacao();
        final AbstractResponse response = service.insertTipoMarcacao(tipoMarcacao);
        assertNotNull(response);
        assertTrue(response.isOk());
    }

    @Test
    public void testGetTipoMarcacao() throws Throwable {
        final TipoMarcacao tipoMarcacao = createTipoMarcacao();
        service.insertTipoMarcacao(tipoMarcacao);

        final TipoMarcacao tipoBuscado = service.getTipoMarcacao(tipoMarcacao.getCodigo());
        assertNotNull(tipoBuscado);

        // Começa comparação dos dois.
        assertEquals(tipoMarcacao.getNome(), tipoBuscado.getNome());
        assertEquals(tipoMarcacao.getIcone(), tipoBuscado.getIcone());
        assertEquals(tipoMarcacao.getTempoRecomendado(), tipoBuscado.getTempoRecomendado());
        assertEquals(tipoMarcacao.getTempoLimiteEstouro(), tipoBuscado.getTempoLimiteEstouro());
        assertEquals(tipoMarcacao.getHorarioSugerido(), tipoBuscado.getHorarioSugerido());

        assertEquals(tipoMarcacao.isAtivo(), tipoBuscado.isAtivo());
        assertEquals(tipoMarcacao.isTipoJornada(), tipoBuscado.isTipoJornada());
        assertEquals(tipoMarcacao.getUnidade(), tipoBuscado.getUnidade());
        assertEquals(tipoMarcacao.getCargos().size(), tipoMarcacao.getCargos().size());

        final List<Cargo> cargosInseridos = tipoMarcacao.getCargos();
        final List<Cargo> cargosBuscados = tipoBuscado.getCargos();
        for (int i = 0; i < cargosInseridos.size(); i++) {
            final Cargo inserido = cargosInseridos.get(i);
            final Cargo buscado = cargosBuscados.get(i);
            assertEquals(inserido.getCodigo(), buscado.getCodigo());
        }

        final FormulaCalculoJornada descontosInseridos = tipoMarcacao.getFormulaCalculoJornada();
        final FormulaCalculoJornada descontosBuscados = tipoBuscado.getFormulaCalculoJornada();
        assertNotNull(descontosInseridos);
        assertNotNull(descontosBuscados);

        final List<TipoDescontadoJornada> brutaInseridos = descontosInseridos.getTiposDescontadosJornadaBruta();
        final List<TipoDescontadoJornada> brutaBuscados = descontosBuscados.getTiposDescontadosJornadaBruta();
        assertEquals(brutaInseridos.size(), brutaBuscados.size());
        for (int i = 0; i < brutaInseridos.size(); i++) {
            final Long codInserido = brutaInseridos.get(i).getCodTipo();
            final Long codBuscado = brutaBuscados.get(i).getCodTipo();
            assertEquals(codInserido, codBuscado);
        }

        final List<TipoDescontadoJornada> liquidaInseridos = descontosInseridos.getTiposDescontadosJornadaLiquida();
        final List<TipoDescontadoJornada> liquidaBuscados = descontosBuscados.getTiposDescontadosJornadaLiquida();
        assertEquals(liquidaInseridos.size(), liquidaBuscados.size());
        for (int i = 0; i < liquidaInseridos.size(); i++) {
            final Long codInserido = liquidaInseridos.get(i).getCodTipo();
            final Long codBuscado = liquidaBuscados.get(i).getCodTipo();
            assertEquals(codInserido, codBuscado);
        }
    }

    @Test
    public void testUpdateTipoMarcacao() throws Throwable {
        final TipoMarcacao tipoInserido = createTipoMarcacao();
        service.insertTipoMarcacao(tipoInserido);

        tipoInserido.setNome(tipoInserido.getNome() + " UPDATE");
        service.updateTipoMarcacao(tipoInserido);

        final TipoMarcacao tipoBuscado = service.getTipoMarcacao(tipoInserido.getCodigo());
        assertEquals(tipoInserido.getNome(), tipoBuscado.getNome());
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
        tipo.setAtivo(true);
        tipo.setTipoJornada(true);

        final Unidade unidade = new Unidade();
        unidade.setCodigo(5L);
        tipo.setUnidade(unidade);

        final List<Cargo> cargos = new ArrayList<>();
        cargos.add(new Cargo(158L, "Ajudante"));
        cargos.add(new Cargo(159L, "Motorista"));
        tipo.setCargos(cargos);


        final List<TipoDescontadoJornada> descontadosBruta = new ArrayList<>();
        descontadosBruta.add(new TipoDescontadoJornada(15L, "Refeição"));
        final List<TipoDescontadoJornada> descontadosLiquida = new ArrayList<>();
        descontadosLiquida.add(new TipoDescontadoJornada(19L, "Descanso"));
        final FormulaCalculoJornada tiposDescontados = new FormulaCalculoJornada(descontadosBruta, descontadosLiquida);
        tipo.setFormulaCalculoJornada(tiposDescontados);

        return tipo;
    }
}