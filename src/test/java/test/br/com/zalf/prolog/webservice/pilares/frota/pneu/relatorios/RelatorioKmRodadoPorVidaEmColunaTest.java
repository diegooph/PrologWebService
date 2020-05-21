package test.br.com.zalf.prolog.webservice.pilares.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.frota.pneu.relatorios._model.PneuKmRodadoPorVida;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios._model.RelatorioKmRodadoPorVidaEmColuna;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.*;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 2020-05-21
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class RelatorioKmRodadoPorVidaEmColunaTest extends BaseTest {

    @SuppressWarnings("unchecked")
    @Test
    void testUmPneuComUmaVida() {
        final PneuKmRodadoPorVida vidaPneu = new PneuKmRodadoPorVida(
                "Unidade A",
                1L,
                "1A",
                "Dimensão",
                "Marca 1",
                "Modelo 1",
                "1",
                "1.500",
                "560",
                "0.003",
                "560");

        final RelatorioKmRodadoPorVidaEmColuna relatorio =
                new RelatorioKmRodadoPorVidaEmColuna(Collections.singletonList(vidaPneu));

        final Iterable<List<String>> rows = (Iterable<List<String>>) relatorio.getData();
        assertThat(rows).isNotNull();
        assertThat(rows).hasSize(1);

        final List<String> columns = rows.iterator().next();
        assertThat(columns).isNotNull();
        assertThat(columns).hasSize(relatorio.getTotalColunasRelatorio());

        // O código do pneu (código Prolog) não vai no relatório.
        assertThat(columns.get(0)).isEqualTo("Unidade A");
        assertThat(columns.get(1)).isEqualTo("1A");
        assertThat(columns.get(2)).isEqualTo("Dimensão");

        // Vida 1.
        assertThat(columns.get(3)).isEqualTo("Marca 1");
        assertThat(columns.get(4)).isEqualTo("Modelo 1");
        assertThat(columns.get(5)).isEqualTo("1.500");
        assertThat(columns.get(6)).isEqualTo("560");
        assertThat(columns.get(7)).isEqualTo("0.003");

        // Outras vidas.
        for (int i = 8; i < relatorio.getTotalColunasRelatorio() - 2; i++) {
            assertThat(columns.get(i)).isEqualTo(RelatorioKmRodadoPorVidaEmColuna.CARACTERE_SEM_DADOS);
        }

        assertThat(columns.get(relatorio.getTotalColunasRelatorio() - 1)).isEqualTo("560");
    }

    @SuppressWarnings("unchecked")
    @Test
    void testUmPneuComDuasVidas() {
        final PneuKmRodadoPorVida vida1Pneu = new PneuKmRodadoPorVida(
                "Unidade A",
                1L,
                "1A",
                "Dimensão",
                "Marca 1",
                "Modelo 1",
                "1",
                "1.000",
                "560",
                "0.002",
                "1160");
        final PneuKmRodadoPorVida vida2Pneu = new PneuKmRodadoPorVida(
                "Unidade A",
                1L,
                "1A",
                "Dimensão",
                "Marca 2",
                "Modelo 2",
                "2",
                "500",
                "600",
                "0.003",
                "1160");

        final RelatorioKmRodadoPorVidaEmColuna relatorio =
                new RelatorioKmRodadoPorVidaEmColuna(Arrays.asList(vida1Pneu, vida2Pneu));

        final Iterable<List<String>> rows = (Iterable<List<String>>) relatorio.getData();
        assertThat(rows).isNotNull();
        assertThat(rows).hasSize(1);

        final List<String> columns = rows.iterator().next();
        assertThat(columns).isNotNull();
        assertThat(columns).hasSize(relatorio.getTotalColunasRelatorio());

        // O código do pneu (código Prolog) não vai no relatório.
        assertThat(columns.get(0)).isEqualTo("Unidade A");
        assertThat(columns.get(1)).isEqualTo("1A");
        assertThat(columns.get(2)).isEqualTo("Dimensão");

        // Vida 1.
        assertThat(columns.get(3)).isEqualTo("Marca 1");
        assertThat(columns.get(4)).isEqualTo("Modelo 1");
        assertThat(columns.get(5)).isEqualTo("1.000");
        assertThat(columns.get(6)).isEqualTo("560");
        assertThat(columns.get(7)).isEqualTo("0.002");

        // Vida 2.
        assertThat(columns.get(8)).isEqualTo("Marca 2");
        assertThat(columns.get(9)).isEqualTo("Modelo 2");
        assertThat(columns.get(10)).isEqualTo("500");
        assertThat(columns.get(11)).isEqualTo("600");
        assertThat(columns.get(12)).isEqualTo("0.003");

        // Outras vidas.
        for (int i = 13; i < relatorio.getTotalColunasRelatorio() - 2; i++) {
            assertThat(columns.get(i)).isEqualTo(RelatorioKmRodadoPorVidaEmColuna.CARACTERE_SEM_DADOS);
        }

        assertThat(columns.get(relatorio.getTotalColunasRelatorio() - 1)).isEqualTo("1160");
    }

    @SuppressWarnings("unchecked")
    @Test
    void testDoisPneusCadaUmComUmaVida() {
        final PneuKmRodadoPorVida vida1Pneu = new PneuKmRodadoPorVida(
                "Unidade A",
                1L,
                "1A",
                "Dimensão",
                "Marca 1",
                "Modelo 1",
                "1",
                "1.000",
                "560",
                "0.002",
                "560");
        final PneuKmRodadoPorVida vida2Pneu = new PneuKmRodadoPorVida(
                "Unidade B",
                2L,
                "2A",
                "Dimensão 2",
                "Marca 2",
                "Modelo 2",
                "2",
                "500",
                "600",
                "0.003",
                "650");

        final RelatorioKmRodadoPorVidaEmColuna relatorio =
                new RelatorioKmRodadoPorVidaEmColuna(Arrays.asList(vida1Pneu, vida2Pneu));

        final Iterable<List<String>> rows = (Iterable<List<String>>) relatorio.getData();
        assertThat(rows).isNotNull();
        assertThat(rows).hasSize(2);

        final Iterator<List<String>> iterator = rows.iterator();
        {
            final List<String> columnsPneu1 = iterator.next();
            assertThat(columnsPneu1).isNotNull();
            assertThat(columnsPneu1).hasSize(relatorio.getTotalColunasRelatorio());

            // O código do pneu (código Prolog) não vai no relatório.
            assertThat(columnsPneu1.get(0)).isEqualTo("Unidade A");
            assertThat(columnsPneu1.get(1)).isEqualTo("1A");
            assertThat(columnsPneu1.get(2)).isEqualTo("Dimensão");

            // Pneu 1 - Vida 1.
            assertThat(columnsPneu1.get(3)).isEqualTo("Marca 1");
            assertThat(columnsPneu1.get(4)).isEqualTo("Modelo 1");
            assertThat(columnsPneu1.get(5)).isEqualTo("1.000");
            assertThat(columnsPneu1.get(6)).isEqualTo("560");
            assertThat(columnsPneu1.get(7)).isEqualTo("0.002");

            // Pneu 1 - Outras vidas.
            for (int i = 8; i < relatorio.getTotalColunasRelatorio() - 2; i++) {
                assertThat(columnsPneu1.get(i)).isEqualTo(RelatorioKmRodadoPorVidaEmColuna.CARACTERE_SEM_DADOS);
            }

            assertThat(columnsPneu1.get(relatorio.getTotalColunasRelatorio() - 1)).isEqualTo("560");
        }

        {
            final List<String> columnsPneu2 = iterator.next();
            assertThat(columnsPneu2).isNotNull();
            assertThat(columnsPneu2).hasSize(relatorio.getTotalColunasRelatorio());

            // O código do pneu (código Prolog) não vai no relatório.
            assertThat(columnsPneu2.get(0)).isEqualTo("Unidade B");
            assertThat(columnsPneu2.get(1)).isEqualTo("2A");
            assertThat(columnsPneu2.get(2)).isEqualTo("Dimensão 2");

            // Pneu 1 - Vida 1.
            assertThat(columnsPneu2.get(3)).isEqualTo("Marca 2");
            assertThat(columnsPneu2.get(4)).isEqualTo("Modelo 2");
            assertThat(columnsPneu2.get(5)).isEqualTo("500");
            assertThat(columnsPneu2.get(6)).isEqualTo("600");
            assertThat(columnsPneu2.get(7)).isEqualTo("0.003");

            // Pneu 1 - Outras vidas.
            for (int i = 8; i < relatorio.getTotalColunasRelatorio() - 2; i++) {
                assertThat(columnsPneu2.get(i)).isEqualTo(RelatorioKmRodadoPorVidaEmColuna.CARACTERE_SEM_DADOS);
            }

            assertThat(columnsPneu2.get(relatorio.getTotalColunasRelatorio() - 1)).isEqualTo("650");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void testDezPneusComMaximoVidasSuportadoRelatorio() {
        final List<PneuKmRodadoPorVida> pneusVidas = new ArrayList<>();
        for (int pneu = 1; pneu < RelatorioKmRodadoPorVidaEmColuna.TOTAL_VIDAS_BUSCADAS + 1; pneu++) {
            for (int vida = 1; vida < RelatorioKmRodadoPorVidaEmColuna.TOTAL_VIDAS_BUSCADAS + 1; vida++) {
                final PneuKmRodadoPorVida vidaPneu = new PneuKmRodadoPorVida(
                        "Unidade A",
                        (long) pneu,
                        String.valueOf(pneu),
                        "Dimensão Atual",
                        "Marca Vida " + vida,
                        "Modelo Vida " + vida,
                        String.valueOf(vida),
                        String.valueOf(vida * 1000),
                        String.valueOf(vida * 500),
                        String.valueOf((vida * 1000) / (vida * 500)),
                        "123456789");
                pneusVidas.add(vidaPneu);
            }
        }

        final RelatorioKmRodadoPorVidaEmColuna relatorio = new RelatorioKmRodadoPorVidaEmColuna(pneusVidas);

        final Iterable<List<String>> rows = (Iterable<List<String>>) relatorio.getData();
        assertThat(rows).isNotNull();
        assertThat(rows).hasSize(10);

        final Iterator<List<String>> iterator = rows.iterator();
        long codPneu = 1;
        while (iterator.hasNext()) {
            final List<String> column = iterator.next();
            assertThat(column).isNotNull();
            assertThat(column).hasSize(relatorio.getTotalColunasRelatorio());

            // O código do pneu (código Prolog) não vai no relatório.
            assertThat(column.get(0)).isEqualTo("Unidade A");
            assertThat(column.get(1)).isEqualTo(String.valueOf(codPneu));
            assertThat(column.get(2)).isEqualTo("Dimensão Atual");

            // Por vida.
            int vida = 1;
            int offset = 0;
            while (vida < RelatorioKmRodadoPorVidaEmColuna.TOTAL_VIDAS_BUSCADAS) {
                assertThat(column.get(vida + offset + 2)).isEqualTo("Marca Vida " + vida);
                assertThat(column.get(vida + offset + 3)).isEqualTo("Modelo Vida " + vida);
                // Precisamos converter para String pois a tipagem também é comparada.
                assertThat(column.get(vida + offset + 4)).isEqualTo(String.valueOf(vida * 1000));
                assertThat(column.get(vida + offset + 5)).isEqualTo(String.valueOf(vida * 500));
                assertThat(column.get(vida + offset + 6)).isEqualTo(String.valueOf((vida * 1000) / (vida * 500)));
                vida++;
                offset += 4;
            }

            assertThat(column.get(relatorio.getTotalColunasRelatorio() - 1)).isEqualTo("123456789");

            codPneu++;
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void testRelatorioVazio_deveRetornarNenhumDado() {
        final RelatorioKmRodadoPorVidaEmColuna relatorio =
                new RelatorioKmRodadoPorVidaEmColuna(Collections.emptyList());

        final Iterable<List<String>> rows = (Iterable<List<String>>) relatorio.getData();
        assertThat(rows).isNotNull();
        assertThat(rows).isEmpty();
    }

    @Test
    void testOrdemColunasHeader() {
        final RelatorioKmRodadoPorVidaEmColuna relatorio =
                new RelatorioKmRodadoPorVidaEmColuna(Collections.emptyList());

        final List<String> header = relatorio.getHeader();
        assertThat(header).isNotNull();
        assertThat(header).hasSize(relatorio.getTotalColunasRelatorio());

        assertThat(header.get(0)).isEqualTo("UNIDADE ALOCADO");
        assertThat(header.get(1)).isEqualTo("PNEU");
        assertThat(header.get(2)).isEqualTo("DIMENSÃO");

        // Por vida.
        int vida = 1;
        int offset = 0;
        while (vida < RelatorioKmRodadoPorVidaEmColuna.TOTAL_VIDAS_BUSCADAS) {
            assertThat(header.get(vida + offset + 2)).isEqualTo("MARCA VIDA " + vida);
            assertThat(header.get(vida + offset + 3)).isEqualTo("MODELO VIDA " + vida);
            assertThat(header.get(vida + offset + 4)).isEqualTo("VALOR VIDA " + vida);
            assertThat(header.get(vida + offset + 5)).isEqualTo("KM VIDA " + vida);
            assertThat(header.get(vida + offset + 6)).isEqualTo("CPK VIDA " + vida);
            vida++;
            offset += 4;
        }

        assertThat(header.get(relatorio.getTotalColunasRelatorio() - 1)).isEqualTo("KM RODADO TODAS AS VIDAS");
    }
}
