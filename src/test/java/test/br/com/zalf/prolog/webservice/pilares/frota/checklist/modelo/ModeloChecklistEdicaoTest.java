package test.br.com.zalf.prolog.webservice.pilares.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.cargo.CargoService;
import br.com.zalf.prolog.webservice.cargo.model.CargoListagemEmpresa;
import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloService;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.PerguntaModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.PerguntaModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.TipoVeiculoService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

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
    // Mesmo não sendo uma constante, usamos maiúsculo para facilitar a diferenciação nos testes.
    private ModeloChecklistInsercao BASE;

    @Override
    public void initialize() throws Throwable {
        DatabaseManager.init();
        token = getValidToken(CPF_TOKEN);
        service = new ChecklistModeloService();
        BASE = readJsonResource(
                ModeloChecklistEdicaoTest.class,
                "modelo_base_edicao.json",
                ModeloChecklistInsercao.class);
    }

    @Override
    public void destroy() {
        DatabaseManager.finish();
        service = null;
    }

    @Test
    public void caso1_atualizaSemAlterarNada_deveFuncionar() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = service.insertModeloChecklist(BASE, token);

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Então, sem alterar nada, inserimos novamente o modelo.
        final List<PerguntaModeloChecklistEdicao> perguntas = jsonToCollection(
                GsonUtils.getGson(),
                GsonUtils.getGson().toJson(modeloBuscado.getPerguntas()));
        final List<Long> cargos = modeloBuscado
                .getCargosLiberados()
                .stream()
                .map(Cargo::getCodigo)
                .collect(Collectors.toList());
        final List<Long> tiposVeiculo = modeloBuscado
                .getTiposVeiculoLiberados()
                .stream()
                .map(TipoVeiculo::getCodigo)
                .collect(Collectors.toList());
        final ModeloChecklistEdicao editado = new ModeloChecklistEdicao(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                modeloBuscado.getCodVersaoModelo(),
                modeloBuscado.getNome(),
                tiposVeiculo,
                cargos,
                perguntas,
                modeloBuscado.isAtivo());
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por último, buscamos novamente o modelo e comparamos com o base. Tudo deve bater.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(editado.getNome()).isEqualTo(buscado.getNome());
        assertThat(editado.getCodUnidade()).isEqualTo(buscado.getCodUnidade());
        assertThat(editado.getCodModelo()).isEqualTo(buscado.getCodModelo());
        assertThat(editado.getCodVersaoModelo()).isEqualTo(buscado.getCodVersaoModelo());
        buscado
                .getCargosLiberados()
                .forEach(c -> assertThat(editado.getCargosLiberados()).contains(c.getCodigo()));
        buscado
                .getTiposVeiculoLiberados()
                .forEach(t -> assertThat(editado.getTiposVeiculoLiberados()).contains(t.getCodigo()));
        assertThat(editado.getPerguntas()).hasSize(2);
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);
            ensureAllAttributesEqual(p1Antes, p1Depois, 4);
            ensureAllAttributesEqual(p1Antes.getAlternativas().get(0), p1Depois.getAlternativas().get(0));
            ensureAllAttributesEqual(p1Antes.getAlternativas().get(1), p1Depois.getAlternativas().get(1));
            ensureAllAttributesEqual(p1Antes.getAlternativas().get(2), p1Depois.getAlternativas().get(2));
            ensureAllAttributesEqual(p1Antes.getAlternativas().get(3), p1Depois.getAlternativas().get(3));
        }

        {
            // P2.
            final PerguntaModeloChecklistEdicao p2Antes = editado.getPerguntas().get(1);
            final PerguntaModeloChecklistVisualizacao p2Depois = buscado.getPerguntas().get(1);
            ensureAllAttributesEqual(p2Antes, p2Depois, 3);
            ensureAllAttributesEqual(p2Antes.getAlternativas().get(0), p2Depois.getAlternativas().get(0));
            ensureAllAttributesEqual(p2Antes.getAlternativas().get(1), p2Depois.getAlternativas().get(1));
            ensureAllAttributesEqual(p2Antes.getAlternativas().get(2), p2Depois.getAlternativas().get(2));
        }
    }

    @Test
    public void caso2_atualizaOsCargos_deveFuncionar() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = service.insertModeloChecklist(BASE, token);

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Então, alteramos os cargos vinculados e atualizamos novamente o modelo.
        final List<PerguntaModeloChecklistEdicao> perguntas = jsonToCollection(
                GsonUtils.getGson(),
                GsonUtils.getGson().toJson(modeloBuscado.getPerguntas()));

        // Buscamos um outro código de cargo da mesma empresa que o modelo pertence e que ainda não esteja vinculado.
        final Long codNovoCargo = new CargoService()
                .getTodosCargosEmpresa(COD_EMPRESA)
                .stream()
                .map(CargoListagemEmpresa::getCodigo)
                .filter(c -> !BASE.getCargosLiberados().contains(c))
                .limit(1)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Empresa não possui outro cargo além dos já vinculados ao modelo"));
        final List<Long> cargos = Collections.singletonList(codNovoCargo);

        final List<Long> tiposVeiculo = modeloBuscado
                .getTiposVeiculoLiberados()
                .stream()
                .map(TipoVeiculo::getCodigo)
                .collect(Collectors.toList());

        final ModeloChecklistEdicao editado = new ModeloChecklistEdicao(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                modeloBuscado.getCodVersaoModelo(),
                modeloBuscado.getNome(),
                tiposVeiculo,
                cargos,
                perguntas,
                modeloBuscado.isAtivo());
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por último, buscamos novamente o modelo e comparamos com o base. Tudo deve bater.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(editado.getNome()).isEqualTo(buscado.getNome());
        assertThat(editado.getCodUnidade()).isEqualTo(buscado.getCodUnidade());
        assertThat(editado.getCodModelo()).isEqualTo(buscado.getCodModelo());
        assertThat(editado.getCodVersaoModelo()).isEqualTo(buscado.getCodVersaoModelo());
        // Agora temos apena um cargo liberado, não mais dois.
        assertThat(editado.getCargosLiberados()).hasSize(1);
        assertThat(buscado.getCargosLiberados()).hasSize(1);
        buscado
                .getCargosLiberados()
                .forEach(c -> assertThat(editado.getCargosLiberados()).contains(c.getCodigo()));
        buscado
                .getTiposVeiculoLiberados()
                .forEach(t -> assertThat(editado.getTiposVeiculoLiberados()).contains(t.getCodigo()));
        assertThat(editado.getPerguntas()).hasSize(2);
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);
            ensureAllAttributesEqual(p1Antes, p1Depois, 4);
            ensureAllAttributesEqual(p1Antes.getAlternativas().get(0), p1Depois.getAlternativas().get(0));
            ensureAllAttributesEqual(p1Antes.getAlternativas().get(1), p1Depois.getAlternativas().get(1));
            ensureAllAttributesEqual(p1Antes.getAlternativas().get(2), p1Depois.getAlternativas().get(2));
            ensureAllAttributesEqual(p1Antes.getAlternativas().get(3), p1Depois.getAlternativas().get(3));
        }

        {
            // P2.
            final PerguntaModeloChecklistEdicao p2Antes = editado.getPerguntas().get(1);
            final PerguntaModeloChecklistVisualizacao p2Depois = buscado.getPerguntas().get(1);
            ensureAllAttributesEqual(p2Antes, p2Depois, 3);
            ensureAllAttributesEqual(p2Antes.getAlternativas().get(0), p2Depois.getAlternativas().get(0));
            ensureAllAttributesEqual(p2Antes.getAlternativas().get(1), p2Depois.getAlternativas().get(1));
            ensureAllAttributesEqual(p2Antes.getAlternativas().get(2), p2Depois.getAlternativas().get(2));
        }
    }

    @Test
    public void caso3_atualizaOsTiposDeVeiculos_deveFuncionar() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = service.insertModeloChecklist(BASE, token);

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Então, alteramos os tipos de veículos vinculados e atualizamos novamente o modelo.
        final List<PerguntaModeloChecklistEdicao> perguntas = jsonToCollection(
                GsonUtils.getGson(),
                GsonUtils.getGson().toJson(modeloBuscado.getPerguntas()));

        final List<Long> cargos = modeloBuscado
                .getCargosLiberados()
                .stream()
                .map(Cargo::getCodigo)
                .collect(Collectors.toList());

        // Buscamos um outro código de tipo de veículo da mesma empresa que o modelo pertence e que ainda não esteja
        // vinculado.
        final Long codNovoTipoVeiculo = new TipoVeiculoService()
                .getTiposVeiculosByEmpresa(token, COD_EMPRESA)
                .stream()
                .map(TipoVeiculo::getCodigo)
                .filter(c -> !BASE.getTiposVeiculoLiberados().contains(c))
                .limit(1)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Empresa não possui outro tipo de veículo além dos já vinculados ao modelo"));
        final List<Long> tiposVeiculo = Collections.singletonList(codNovoTipoVeiculo);

        final ModeloChecklistEdicao editado = new ModeloChecklistEdicao(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                modeloBuscado.getCodVersaoModelo(),
                modeloBuscado.getNome(),
                tiposVeiculo,
                cargos,
                perguntas,
                modeloBuscado.isAtivo());
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por último, buscamos novamente o modelo e comparamos com o base. Tudo deve bater.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(editado.getNome()).isEqualTo(buscado.getNome());
        assertThat(editado.getCodUnidade()).isEqualTo(buscado.getCodUnidade());
        assertThat(editado.getCodModelo()).isEqualTo(buscado.getCodModelo());
        assertThat(editado.getCodVersaoModelo()).isEqualTo(buscado.getCodVersaoModelo());
        buscado
                .getCargosLiberados()
                .forEach(c -> assertThat(editado.getCargosLiberados()).contains(c.getCodigo()));
        // Agora temos apena um tipo de veículo, não mais dois.
        assertThat(editado.getTiposVeiculoLiberados()).hasSize(1);
        assertThat(buscado.getTiposVeiculoLiberados()).hasSize(1);
        buscado
                .getTiposVeiculoLiberados()
                .forEach(t -> assertThat(editado.getTiposVeiculoLiberados()).contains(t.getCodigo()));
        assertThat(editado.getPerguntas()).hasSize(2);
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);
            ensureAllAttributesEqual(p1Antes, p1Depois, 4);
            ensureAllAttributesEqual(p1Antes.getAlternativas().get(0), p1Depois.getAlternativas().get(0));
            ensureAllAttributesEqual(p1Antes.getAlternativas().get(1), p1Depois.getAlternativas().get(1));
            ensureAllAttributesEqual(p1Antes.getAlternativas().get(2), p1Depois.getAlternativas().get(2));
            ensureAllAttributesEqual(p1Antes.getAlternativas().get(3), p1Depois.getAlternativas().get(3));
        }

        {
            // P2.
            final PerguntaModeloChecklistEdicao p2Antes = editado.getPerguntas().get(1);
            final PerguntaModeloChecklistVisualizacao p2Depois = buscado.getPerguntas().get(1);
            ensureAllAttributesEqual(p2Antes, p2Depois, 3);
            ensureAllAttributesEqual(p2Antes.getAlternativas().get(0), p2Depois.getAlternativas().get(0));
            ensureAllAttributesEqual(p2Antes.getAlternativas().get(1), p2Depois.getAlternativas().get(1));
            ensureAllAttributesEqual(p2Antes.getAlternativas().get(2), p2Depois.getAlternativas().get(2));
        }
    }

    private void ensureAllAttributesEqual(@NotNull final PerguntaModeloChecklist antes,
                                          @NotNull final PerguntaModeloChecklist depois,
                                          final int qtdAlternativas) {
        assertThat(antes).isNotSameInstanceAs(depois);
        assertThat(antes.getCodigo()).isEqualTo(depois.getCodigo());
        assertThat(antes.getCodigoFixo()).isEqualTo(depois.getCodigoFixo());
        assertThat(antes.getDescricao()).isEqualTo(depois.getDescricao());
        assertThat(antes.getCodImagem()).isEqualTo(depois.getCodImagem());
        assertThat(antes.getOrdemExibicao()).isEqualTo(depois.getOrdemExibicao());
        assertThat(antes.isSingleChoice()).isEqualTo(depois.isSingleChoice());
        assertThat(antes.getAlternativas()).hasSize(qtdAlternativas);
        assertThat(depois.getAlternativas()).hasSize(qtdAlternativas);
    }

    private void ensureAllAttributesEqual(@NotNull final AlternativaModeloChecklist antes,
                                          @NotNull final AlternativaModeloChecklist depois) {
        assertThat(antes).isNotSameInstanceAs(depois);
        assertThat(antes.getCodigo()).isEqualTo(depois.getCodigo());
        assertThat(antes.getCodigoFixo()).isEqualTo(depois.getCodigoFixo());
        assertThat(antes.getDescricao()).isEqualTo(depois.getDescricao());
        assertThat(antes.getPrioridade()).isEqualTo(depois.getPrioridade());
        assertThat(antes.isTipoOutros()).isEqualTo(depois.isTipoOutros());
        assertThat(antes.getOrdemExibicao()).isEqualTo(depois.getOrdemExibicao());
        assertThat(antes.isDeveAbrirOrdemServico()).isEqualTo(depois.isDeveAbrirOrdemServico());
    }

    @NotNull
    private static List<PerguntaModeloChecklistEdicao> jsonToCollection(@NotNull final Gson gson,
                                                                        @NotNull final String json) {
        final Type type = new TypeToken<List<PerguntaModeloChecklistEdicao>>(){}.getType();
        return gson.fromJson(json, type);
    }
}