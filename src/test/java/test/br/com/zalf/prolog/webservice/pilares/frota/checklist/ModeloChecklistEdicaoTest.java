package test.br.com.zalf.prolog.webservice.pilares.frota.checklist;

import br.com.zalf.prolog.webservice.cargo.CargoService;
import br.com.zalf.prolog.webservice.cargo.model.CargoSelecao;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloService;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.AlternativaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.PerguntaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.TipoVeiculoService;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2019-09-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ModeloChecklistEdicaoTest extends BaseTest {
    private static final String DEFAULT_DESCRICAO_TIPO_OUTROS = "Outros";
    private static final String CPF_TOKEN = "03383283194";
    private static final Long COD_EMPRESA = 3L;
    private static final Long COD_UNIDADE = 5L;
    private ChecklistModeloService service;
    private String token;
    private ResultInsertModeloChecklist result;

    @Override
    public void initialize() throws Throwable {
        DatabaseManager.init();
        token = getValidToken(CPF_TOKEN);
        service = new ChecklistModeloService();
//        result = criaModeloBase();
    }

    @Override
    public void destroy() {
        DatabaseManager.finish();
        service = null;
    }

    @Test
    public void criaModeloBase() {
        final List<PerguntaModeloChecklistInsercao> perguntas = new ArrayList<>();
        {
            // P1.
            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();

            // A1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Fora de foco",
                    PrioridadeAlternativa.CRITICA,
                    false,
                    1,
                    true));
            // A2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Lâmpada queimada",
                    PrioridadeAlternativa.CRITICA,
                    false,
                    1,
                    true));
            // A3.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Fora de foco",
                    PrioridadeAlternativa.CRITICA,
                    false,
                    1,
                    true));
            // A4.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    DEFAULT_DESCRICAO_TIPO_OUTROS,
                    PrioridadeAlternativa.CRITICA,
                    true,
                    1,
                    true));

            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P1",
                    1L,
                    1,
                    true,
                    alternativas));
        }

        {
            // P2.
            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // B1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    DEFAULT_DESCRICAO_TIPO_OUTROS,
                    PrioridadeAlternativa.BAIXA,
                    true,
                    1,
                    false));

            // B2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "B2",
                    PrioridadeAlternativa.ALTA,
                    false,
                    2,
                    true));

            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P2",
                    null,
                    2,
                    false,
                    alternativas));
        }

        // Cargos.
        final List<Long> cargos = new CargoService()
                .getTodosCargosUnidade(COD_UNIDADE)
                .stream()
                .map(CargoSelecao::getCodigo)
                .limit(2)
                .collect(Collectors.toList());

        // Tipos de Veículo.
        final List<Long> tiposVeiculo = new TipoVeiculoService()
                .getTiposVeiculosByEmpresa(token, COD_EMPRESA)
                .stream()
                .map(TipoVeiculo::getCodigo)
                .limit(2)
                .collect(Collectors.toList());


        final String nomeModelo = "MODELO BASE";
        final ModeloChecklistInsercao modelo = new ModeloChecklistInsercao(
                nomeModelo,
                COD_UNIDADE,
                Collections.emptyList(),
                Collections.emptyList(),
                perguntas);

        System.out.println(GsonUtils.getGson().toJson(modelo));

        return;
    }
}

