package test.br.com.zalf.prolog.webservice.pilares.entrega.mapa;

import br.com.zalf.prolog.webservice.entrega.mapa.PlanilhaMapaReader;
import br.com.zalf.prolog.webservice.entrega.mapa._model.CelulaPlanilhaMapaErro;
import br.com.zalf.prolog.webservice.entrega.mapa.validator.PlanilhaMapaValidator;
import br.com.zalf.prolog.webservice.entrega.mapa.validator.RegrasPlanilhaMapaLoader;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 2020-05-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class PlanilhaMapaImportTest extends BaseTest {
    private static final int TOTAL_COLUNAS_PLANILHA_MAPA = 115;
    private static final String PLANILHA_MAPA_OK_CSV = "planilha_mapa_ok.csv";

    @Test
    void testParsePlanilhaMapa() {
        final InputStream stream = getPlanilhaMapa();
        final List<String[]> data = PlanilhaMapaReader.readFromCsv(stream);
        assertThat(data).isNotNull();
        assertThat(data).hasSize(2);
        data.forEach(columns -> {
            assertThat(columns).isNotNull();
            assertThat(columns).hasLength(TOTAL_COLUNAS_PLANILHA_MAPA);
        });
    }

    @Test
    void testUmaColunaDeCadaVezComProblema() {
        final InputStream stream = getPlanilhaMapa();
        final List<String[]> data = PlanilhaMapaReader.readFromCsv(stream);

        final PlanilhaMapaValidator validator = new PlanilhaMapaValidator(RegrasPlanilhaMapaLoader.getRegras());
        final String[] columnsLinha2 = data.get(1);
        for (int i = 0; i < columnsLinha2.length; i++) {
            final String valorOriginal = columnsLinha2[i];
            columnsLinha2[i] = "-1";
            final Optional<List<CelulaPlanilhaMapaErro>> optional = validator.findErrors(data);
            assertThat(optional.isPresent()).isTrue();
            //noinspection OptionalGetWithoutIsPresent
            final List<CelulaPlanilhaMapaErro> errors = optional.get();
            assertThat(errors).hasSize(validator.isColunaTipoTexto(i) ? 0 : 1);
            // Retorna o valor original
            columnsLinha2[i] = valorOriginal;
        }
    }

    @Test
    void testTodosDadosComErro() {
        final InputStream stream = getPlanilhaMapa();
        final List<String[]> data = PlanilhaMapaReader.readFromCsv(stream);

        // Coloca um valor incorreto em todas as células.
        data.forEach(columns -> Arrays.fill(columns, "-1"));

        final PlanilhaMapaValidator validator = new PlanilhaMapaValidator(RegrasPlanilhaMapaLoader.getRegras());
        final Optional<List<CelulaPlanilhaMapaErro>> optional = validator.findErrors(data);
        assertThat(optional.isPresent()).isTrue();
        //noinspection OptionalGetWithoutIsPresent
        final List<CelulaPlanilhaMapaErro> errors = optional.get();
        // Pegamos o total de colunas que não são TEXTO, pois essas colunas são as que irão falhar com "-1", as tipo
        // TEXTO aceitam qualquer coisa.
        assertThat(errors).hasSize(validator.getTotalColunasQueNaoSaoTexto() * data.size());
    }

    @SneakyThrows
    @NotNull
    private InputStream getPlanilhaMapa() {
        final Path path = Paths.get("src", "test", "resources", PLANILHA_MAPA_OK_CSV);
        return Files.newInputStream(path);
    }
}
