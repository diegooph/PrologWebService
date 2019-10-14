package test.br.com.zalf.prolog.webservice.pilares.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.cargo.CargoService;
import br.com.zalf.prolog.webservice.cargo.model.CargoListagemEmpresa;
import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloService;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.*;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.AlternativaModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.PerguntaModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.TipoVeiculoService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Para esse teste funcionar corretamente em repetidas execuções, é necessário dropar um index da tabela
 * CHECKLIST_MODELO:
 * > drop index checklist_modelo_data_nome_index;
 *
 * Created on 2019-09-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class ModeloChecklistEdicaoTest extends BaseTest {
    private static final String CPF_TOKEN = "03383283194";
    private static final Long COD_EMPRESA = 3L;
    private static final Long COD_UNIDADE = 5L;
    private ChecklistModeloService service;
    private String token;
    // Mesmo não sendo uma constante, usamos maiúsculo para facilitar a diferenciação nos testes.
    private ModeloChecklistInsercao BASE;

    @BeforeAll
    public void initialize() throws Throwable {
        DatabaseManager.init();
        token = getValidToken(CPF_TOKEN);
        service = new ChecklistModeloService();
        BASE = readJsonResource(
                ModeloChecklistEdicaoTest.class,
                "modelo_base_edicao.json",
                ModeloChecklistInsercao.class);
    }

    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
        service = null;
    }

    @Test
    @DisplayName("Atualiza o modelo sem alterar nada, tudo fica igual")
    public void caso1_atualizaSemAlterarNada_deveManterTudoIgual() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());

        // 4, 5 - Então, sem alterar nada, inserimos novamente o modelo.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
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
        assertCodCargosIguais(editado, buscado);
        assertCodTiposVeiculosIguais(editado, buscado);
        assertThat(editado.getPerguntas()).hasSize(2);
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);
            ensureAllAttributesEqual(p1Antes, p1Depois, 4);

            for (int i = 0; i < 4; i++) {
                ensureAllAttributesEqual(p1Antes.getAlternativas().get(i), p1Depois.getAlternativas().get(i));
            }
        }

        {
            // P2.
            final PerguntaModeloChecklistEdicao p2Antes = editado.getPerguntas().get(1);
            final PerguntaModeloChecklistVisualizacao p2Depois = buscado.getPerguntas().get(1);
            ensureAllAttributesEqual(p2Antes, p2Depois, 3);

            for (int i = 0; i < 3; i++) {
                ensureAllAttributesEqual(p2Antes.getAlternativas().get(i), p2Depois.getAlternativas().get(i));
            }
        }
    }

    @Test
    @DisplayName("Altera os cargos liberados, mantém a versão")
    public void caso2_atualizaOsCargos_deveFuncionar() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Então, alteramos os cargos vinculados e atualizamos novamente o modelo.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);

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

        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por último, buscamos novamente o modelo e comparamos com o base. Tudo deve bater.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(editado.getCodVersaoModelo()).isEqualTo(buscado.getCodVersaoModelo());
        // Agora temos apena um cargo liberado, não mais dois.
        assertThat(buscado.getCargosLiberados()).hasSize(1);
        assertCodCargosIguais(editado, buscado);
    }

    @Test
    @DisplayName("Altera os tipos de veículos liberados, mantém a versão")
    public void caso3_atualizaOsTiposDeVeiculo_deveFuncionar() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Então, alteramos os tipos de veículos vinculados e atualizamos novamente o modelo.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);

        final List<Long> cargos = getCodigosCargos(modeloBuscado);

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

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por último, buscamos novamente o modelo e comparamos com o base. Tudo deve bater.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(editado.getCodVersaoModelo()).isEqualTo(buscado.getCodVersaoModelo());
        // Agora temos apena um tipo de veículo liberado, não mais dois.
        assertThat(editado.getTiposVeiculoLiberados()).hasSize(1);
        assertThat(buscado.getTiposVeiculoLiberados()).hasSize(1);
        assertCodTiposVeiculosIguais(editado, buscado);
    }

    @Test
    @DisplayName("Altera os cargos e os tipos de veículos liberados, mantém a versão")
    public void caso4_atualizaOsCargosEOsTiposDeVeiculo_deveFuncionar() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Então, alteramos os cargos e os tipos de veículo vinculados e atualizamos novamente o modelo.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);

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

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por último, buscamos novamente o modelo e comparamos com o base. Tudo deve bater.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(editado.getCodVersaoModelo()).isEqualTo(buscado.getCodVersaoModelo());
        // Agora temos apena um cargo liberado, não mais dois.
        assertThat(editado.getCargosLiberados()).hasSize(1);
        assertThat(buscado.getCargosLiberados()).hasSize(1);
        assertCodCargosIguais(editado, buscado);
        // Agora temos apena um tipo de veículo liberado, não mais dois.
        assertThat(editado.getTiposVeiculoLiberados()).hasSize(1);
        assertThat(buscado.getTiposVeiculoLiberados()).hasSize(1);
        assertCodTiposVeiculosIguais(editado, buscado);
    }

    @Test
    @DisplayName("Altera descrição de todas as perguntas e alternativas mantendo o contexto, mantém a versão")
    public void caso5_atualizaOsTextosDeTodasPerguntasEAlternativasSemMudarContexto_deveFuncionarMantendoAVersao() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Então, atualizamos os textos de todas as perguntas e alternativas e aí atualizamos.
        assertThat(modeloBuscado.getPerguntas()).hasSize(2);
        final List<PerguntaModeloChecklistEdicao> perguntas = new ArrayList<>(2);
        {
            // P1.
            final PerguntaModeloChecklistVisualizacao p1 = modeloBuscado.getPerguntas().get(0);
            assertThat(p1.getAlternativas()).hasSize(4);

            final List<AlternativaModeloChecklistEdicao> alternativas = new ArrayList<>(4);
            alternativas.add(
                    copyFrom((AlternativaModeloChecklistVisualizacao) p1.getAlternativas().get(0),
                            "Fora de foco",
                            "Forá de  FÓCO "));
            alternativas.add(
                    copyFrom((AlternativaModeloChecklistVisualizacao) p1.getAlternativas().get(1),
                            "Lâmpada queimada",
                            "  LAMPADAS QUEIMAHDA"));
            alternativas.add(
                    copyFrom((AlternativaModeloChecklistVisualizacao) p1.getAlternativas().get(2),
                            "Lanterna quebrada",
                            "Lanterna quebada"));
            alternativas.add(
                    copyFrom((AlternativaModeloChecklistVisualizacao) p1.getAlternativas().get(3),
                            "Outros",
                            // A tipo_outros não alteramos.
                            "Outros"));

            perguntas.add(new PerguntaModeloChecklistEdicaoAtualiza(
                    p1.getCodigo(),
                    p1.getCodigoFixo(),
                    "FáROLL",
                    p1.getCodImagem(),
                    p1.getOrdemExibicao(),
                    p1.isSingleChoice(),
                    alternativas));
        }

        {
            // P2.
            final PerguntaModeloChecklistVisualizacao p2 = modeloBuscado.getPerguntas().get(1);
            assertThat(p2.getAlternativas()).hasSize(3);

            final List<AlternativaModeloChecklistEdicao> alternativas = new ArrayList<>(3);
            alternativas.add(
                    copyFrom((AlternativaModeloChecklistVisualizacao) p2.getAlternativas().get(0),
                            "Não trava",
                            "  NAO TRÁVVA"));
            alternativas.add(
                    copyFrom((AlternativaModeloChecklistVisualizacao) p2.getAlternativas().get(1),
                            "Sensor não funciona",
                            " SENSORR   nao FuNSiOnAa "));
            alternativas.add(
                    copyFrom((AlternativaModeloChecklistVisualizacao) p2.getAlternativas().get(2),
                            "Outros",
                            // A tipo_outros não alteramos.
                            "Outros"));

            perguntas.add(new PerguntaModeloChecklistEdicaoAtualiza(
                    p2.getCodigo(),
                    p2.getCodigoFixo(),
                    "cinto   di SeGURANCA  ",
                    p2.getCodImagem(),
                    p2.getOrdemExibicao(),
                    p2.isSingleChoice(),
                    alternativas));
        }

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7, 8, 9 - Por último, buscamos novamente o modelo e fazemos as comparações necessárias.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(editado.getCodVersaoModelo()).isEqualTo(buscado.getCodVersaoModelo());
        assertThat(editado.getPerguntas()).hasSize(2);
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);
            ensureAllAttributesEqual(p1Antes, p1Depois, 4);

            for (int i = 0; i < 4; i++) {
                ensureAllAttributesEqual(p1Antes.getAlternativas().get(i), p1Depois.getAlternativas().get(i));
            }
        }

        {
            // P2.
            final PerguntaModeloChecklistEdicao p2Antes = editado.getPerguntas().get(1);
            final PerguntaModeloChecklistVisualizacao p2Depois = buscado.getPerguntas().get(1);
            ensureAllAttributesEqual(p2Antes, p2Depois, 3);

            for (int i = 0; i < 3; i++) {
                ensureAllAttributesEqual(p2Antes.getAlternativas().get(i), p2Depois.getAlternativas().get(i));
            }
        }
    }

    @Test
    @DisplayName("Remove uma alternativa da P1, aumenta a versão")
    public void caso6_removeUmaAlternativaDaP1_deveMudarVersaoModeloECodigoFixoPergunta() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Então, removemos uma alternativa da P1 e aí atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);

        // Removemos a alternativa 'Fora de foco' da P1.
        perguntas.get(0).getAlternativas().remove(0);

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por último, buscamos novamente o modelo e fazemos as comparações necessárias.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Versão do modelo tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(editado.getPerguntas()).hasSize(2);
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 8 - Código fixo se mantém e variável troca.
            // Código fixa da pergunta não muda por alterações nas alternativas, mesmo uma remoção.
            ensureAllAttributesEqual(p1Antes, p1Depois, 3, true, false);

            // 9 - Garante que a alternativa 'Fora de foco' não está mais presente.
            p1Depois.getAlternativas()
                    .stream()
                    .filter(p -> p.getDescricao().equals("Fora de foco"))
                    .findAny()
                    .ifPresent(a -> {throw new RuntimeException("Alternativa 'Fora de foco' deletada ainda está presente");});
        }
    }

    @Test
    @DisplayName("Remove uma alternativa da P1 e adiciona outra, aumenta a versão")
    public void caso7_removeUmaAlternativaDaP1AdicionaOutraAlternativaNaP1_deveMudarVersaoModeloECodigoFixoDaP1() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Então, removemos uma alternativa da P1 e adicionamos outra na P1, aí atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);

        // Removemos a alternativa 'Fora de foco' da P1 e adicionamos 'Piscando sozinho'.
        perguntas.get(0).getAlternativas().remove(0);
        perguntas.get(0).getAlternativas().add(
                new AlternativaModeloChecklistEdicaoInsere(
                        "Piscando sozinho",
                        PrioridadeAlternativa.BAIXA,
                        false,
                        // Mesma ordem da alternativa removida para evitar qualquer outro problema.
                        1,
                        true));

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por último, buscamos novamente o modelo e fazemos as comparações necessárias.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 8 - Versão do modelo tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(editado.getPerguntas()).hasSize(2);
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 9 - Código fixo igual, variável troca.
            ensureAllAttributesEqual(p1Antes, p1Depois, 4, true, false);

            // 10 - Garante que a alternativa 'Fora de foco' não está mais presente.
            Assertions
                    .assertThat(p1Depois.getAlternativas())
                    .extracting("descricao")
                    .doesNotContain("Fora de foco");

            // 11 - Garante que a nova alternativa está presente.
            Assertions
                    .assertThat(p1Depois.getAlternativas())
                    .extracting("descricao")
                    .contains("Piscando sozinho");
        }
    }

    @Test
    @DisplayName("Remove uma alternativa da P1 e adiciona outra na P2, aumenta a versão")
    public void caso8_removeUmaAlternativaDaP1AdicionaOutraAlternativaNaP2_deveMudarVersaoModeloECodigoFixoDasPerguntas() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Então, removemos uma alternativa de uma pergunta e adicionamos outra em outra pergunta, aí atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);

        // Removemos a alternativa 'Fora de foco' da P1 e adicionamos 'Rasgado' na P2.
        perguntas.get(0).getAlternativas().remove(0);

        // P2 é 'Cinto de Segurança' e possui 3 alternativas.
        // A tipo_outros tem que ser realocada para a última posição.
        final AlternativaModeloChecklist outros = perguntas.get(1).getAlternativas().remove(2);
        perguntas.get(1).getAlternativas().add(
                new AlternativaModeloChecklistEdicaoInsere(
                        "Rasgado",
                        PrioridadeAlternativa.BAIXA,
                        false,
                        3,
                        true));
        perguntas.get(1).getAlternativas().add(
                new AlternativaModeloChecklistEdicaoInsere(
                        "Outros",
                        outros.getPrioridade(),
                        true,
                        4,
                        outros.isDeveAbrirOrdemServico()));

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por último, buscamos novamente o modelo e fazemos as comparações necessárias.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 8 - Versão do modelo tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(editado.getPerguntas()).hasSize(2);
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 9 - Código fixo igual, variável troca.
            ensureAllAttributesEqual(p1Antes, p1Depois, 3, true, false);

            // 10 - Garante que a alternativa 'Fora de foco' não está mais presente.
            Assertions
                    .assertThat(p1Depois.getAlternativas())
                    .extracting("descricao")
                    .doesNotContain("Fora de foco");
        }

        {
            // P2.
            final PerguntaModeloChecklistEdicao p2Antes = editado.getPerguntas().get(1);
            final PerguntaModeloChecklistVisualizacao p2Depois = buscado.getPerguntas().get(1);

            // 11 - Código fixo igual, variável troca.
            ensureAllAttributesEqual(p2Antes, p2Depois, 4, true, false);

            // 12 - Garante que a nova alternativa está presente.
            Assertions
                    .assertThat(p2Depois.getAlternativas())
                    .extracting("descricao")
                    .contains("Rasgado");
        }
    }

    @Test
    @DisplayName("Remove P1, aumenta versão")
    public void caso9_removeP1_deveMudarVersaoModelo() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Então, removemos a P1 e atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);

        // Removemos a pergunta P1 (Farol).
        perguntas.remove(0);

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por último, buscamos novamente o modelo e comparamos com o base. Tudo deve bater.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Versão do modelo tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());

        // 8, 9 - Garante que temos apenas uma pergunta.
        assertThat(editado.getPerguntas()).hasSize(1);
        assertThat(buscado.getPerguntas()).hasSize(1);
        {
            // P2.
            final PerguntaModeloChecklistEdicao p2Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p2Depois = buscado.getPerguntas().get(0);
            assertThat(p2Depois.getDescricao()).isEqualTo("Cinto de segurança");

            ensureAllAttributesEqual(p2Antes, p2Depois, 3, true, false);

            for (int i = 0; i < 3; i++) {
                ensureAllAttributesEqual(p2Antes.getAlternativas().get(i), p2Depois.getAlternativas().get(i), true, false);
            }
        }
    }

    @Test
    @DisplayName("Remove P1 e P2, dá erro")
    public void caso10_removeP1EP2_deveDarErro() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Então, removemos a P1 e P2 e aí atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);

        // Removemos a pergunta P1 (Farol).
        perguntas.remove(0);
        // Removemos a pergunta P2 (Cinto de segurança).
        perguntas.remove(0);

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        assertThrows(ProLogException.class, () -> service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token));
    }

    @Test
    @DisplayName("Remove todas as alternativas da P1, dá erro")
    public void caso11_removeTodasAlternativasP1_deveDarErro() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Então, removemos todas as alternativas da P1 e aí atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);

        // Removemos todas as alternativas da pergunta P1.
        perguntas.get(0).getAlternativas().clear();

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        assertThrows(ProLogException.class, () -> service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token));
    }

    @Test
    @DisplayName("Remove alternativa tipo_outros da P2, dá erro")
    public void caso12_removeAlternativaTipoOutrosDaP2_deveDarErro() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Então, removemos a alternativa tipo_outros da P2 e aí atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);

        // Removemos a alternativa tipo_outros da P2.
        perguntas.get(1).getAlternativas().remove(2);

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        assertThrows(ProLogException.class, () -> service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token));
    }

    @Test
    @DisplayName("Muda contexto da P1, aumenta versão")
    public void caso13_alteraTextoDaP1MudandoContexto_deveMudarVersaoModeloECodigoFixoDaP1() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Alteramos o texto da P1 mudando o contexto e aí atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        // P1.
        final PerguntaModeloChecklistVisualizacao p1 = modeloBuscado.getPerguntas().get(0);
        perguntas.set(
                0,
                // P1 é substituída com uma nova descrição.
                copyFrom(
                        p1,
                        "Farol",
                        "Extintor de incêndio"));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por último, buscamos novamente o modelo e comparamos com o base.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Versão do modelo tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(editado.getPerguntas()).hasSize(2);
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);
            // 8 - Compara P1 garantindo que o código fixo e variável mudaram.
            ensureAllAttributesEqual(p1Antes, p1Depois, 4, false, false);

            // 9 - Garante que a descrição foi atualizada para a nova.
            assertThat(p1Depois.getDescricao()).isEqualTo("Extintor de incêndio");
        }
    }

    @Test
    @DisplayName("Muda contexto da P1 e altera P2 mantendo contexto, aumenta versão")
    public void caso14_alteraContextoDaP1_alteraP2MantendoContexto_deveMudarVersaoModeloECodigoFixoDaP1EManterDaP2() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5, 6 - Alteramos o texto da P1 mudando o contexto e o da P2 mantendo e aí atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);

        {
            // P1.
            final PerguntaModeloChecklistVisualizacao p1 = modeloBuscado.getPerguntas().get(0);
            perguntas.set(
                    0,
                    // P1 é substituída com uma nova descrição mudando contexto.
                    copyFrom(
                            p1,
                            "Farol",
                            "Extintor de incêndio"));
        }

        {
            // P2.
            final PerguntaModeloChecklistVisualizacao p2 = modeloBuscado.getPerguntas().get(1);
            perguntas.set(
                    1,
                    // P2 é substituída com uma nova descrição mantendo contexto.
                    copyFrom(
                            p2,
                            "Cinto de segurança",
                            "  Cinto  di  SEGURANSA "));
        }

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 7 - Por último, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 8 - A versão do modelo tem que estar uma maior.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(editado.getPerguntas()).hasSize(2);
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);
            // 9 - O código fixo da pergunta P1 tem que estar diferente.
            ensureAllAttributesEqual(p1Antes, p1Depois, 4, false, false);
            // 11 - Garante o texto atualizado.
            assertThat(p1Depois.getDescricao()).isEqualTo("Extintor de incêndio");
        }

        {
            // P2.
            final PerguntaModeloChecklistEdicao p2Antes = editado.getPerguntas().get(1);
            final PerguntaModeloChecklistVisualizacao p2Depois = buscado.getPerguntas().get(1);
            // 10 - O código fixo da pergunta P2 tem que ser o mesmo, variável mudou.
            ensureAllAttributesEqual(p2Antes, p2Depois, 3, true, false);
            // 11 - Garante o texto atualizado.
            assertThat(p2Depois.getDescricao()).isEqualTo("  Cinto  di  SEGURANSA ");
        }
    }

    @Test
    @DisplayName("Muda contexto da A1 e deleta A2, aumenta versão")
    public void caso15_alteraContextoDaA1_deletaA2_deveMudarVersaoModelo_CodigoFixoP1Diferente_CodigoFixoA1Diferente() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5, 6 - Alteramos o texto da P1 mudando o contexto, deletamos a P2 e aí atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);

        // A1 - Altera por uma com novo contexto.
        final AlternativaModeloChecklist a1 = modeloBuscado.getPerguntas().get(0).getAlternativas().get(0);
        perguntas.get(0).getAlternativas().set(
                0,
                // A1 é substituída com uma nova descrição mudando contexto.
                copyFrom(
                        (AlternativaModeloChecklistVisualizacao) a1,
                        "Fora de foco",
                        "Desfocado"));
        // A2 - Remove a alternativa A2 (Lâmpada queimada).
        perguntas.get(0).getAlternativas().remove(1);

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 7 - Por último, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 8 - A versão do modelo tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 9, 10 - Garante código fixo igual e variável diferente.
            ensureAllAttributesEqual(p1Antes, p1Depois, 3, true, false);

            // 11 - Garante que a alternativa 'Lâmpada queimada' não está mais presente.
            Assertions
                    .assertThat(p1Depois.getAlternativas())
                    .extracting("descricao")
                    .doesNotContain("Lâmpada queimada");

            // 12 - Garante que a alternativa 'Fora de foco' não está mais presente.
            Assertions
                    .assertThat(p1Depois.getAlternativas())
                    .extracting("descricao")
                    .doesNotContain("Fora de foco");

            // 12 - Garante que a alternativa A1 possui a nova descrição.
            assertThat(p1Depois.getAlternativas().get(0).getDescricao()).isEqualTo("Desfocado");
        }
    }

    @Test
    @DisplayName("Muda contexto da A1 e adiciona alternativa na P1, aumenta versão")
    public void caso16_alteraContextoA1_adicionaAlternativaP1_deveMudarVersaoModeloCodigoFixoP1DiferenteCodigoFixoA1Diferente() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5, 6 - Alteramos o texto da A1 mudando o contexto, adicionamos uma alternativa na P1 e aí atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);

        // A1 - Altera por uma com novo contexto.
        final AlternativaModeloChecklist a1 = modeloBuscado.getPerguntas().get(0).getAlternativas().get(0);
        perguntas.get(0).getAlternativas().set(
                0,
                // A1 é substituída com uma nova descrição mudando contexto.
                copyFrom(
                        (AlternativaModeloChecklistVisualizacao) a1,
                        "Fora de foco",
                        "Desfocado"));
        // P1 - Adiciona nova alternativa - P1 já tem 4.
        // A tipo_outros tem que ser realocada para a última posição.
        final AlternativaModeloChecklist outros = perguntas.get(0).getAlternativas().remove(3);
        perguntas.get(0).getAlternativas().add(
                new AlternativaModeloChecklistEdicaoInsere(
                        "Piscando sozinho",
                        PrioridadeAlternativa.BAIXA,
                        false,
                        4,
                        true));
        perguntas.get(0).getAlternativas().add(
                new AlternativaModeloChecklistEdicaoInsere(
                        "Outros",
                        outros.getPrioridade(),
                        true,
                        5,
                        outros.isDeveAbrirOrdemServico()));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 7 - Por último, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 8 - Garante uma versão maior.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 9, 10 - Garante código fixo igual e variável diferente.
            ensureAllAttributesEqual(p1Antes, p1Depois, 5, true, false);

            // 11 - Garante que a alternativa A5 está presente.
            final AlternativaModeloChecklist a5 = p1Depois.getAlternativas().get(3);
            assertThat(a5.getDescricao()).isEqualTo("Piscando sozinho");
            assertThat(a5.getPrioridade()).isEqualTo(PrioridadeAlternativa.BAIXA);
            assertThat(a5.isTipoOutros()).isFalse();
            assertThat(a5.getOrdemExibicao()).isEqualTo(4);
            assertThat(a5.isDeveAbrirOrdemServico()).isTrue();

            // 12 - Garante que a alternativa 'Fora de foco' não está mais presente.
            Assertions
                    .assertThat(p1Depois.getAlternativas())
                    .extracting("descricao")
                    .doesNotContain("Fora de foco");

            // 12 - Garante que a alternativa A1 possui a nova descrição.
            assertThat(p1Depois.getAlternativas().get(0).getDescricao()).isEqualTo("Desfocado");

            assertThat(p1Depois.getDescricao()).isEqualTo("Farol");
        }
    }

    @Test
    @DisplayName("Altera P1 para single_choice, aumenta versão")
    public void caso17_alteraP1ParaSingleChoice_deveMudarVersaoModeloECodigoFixoP1() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Alteramos P1 para single_choice e aí atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        // P1.
        final PerguntaModeloChecklistVisualizacao p1 = modeloBuscado.getPerguntas().get(0);


        perguntas.set(
                0,
                // P1 é substituída agora sendo single_choice..
                new PerguntaModeloChecklistEdicaoAtualiza(
                        p1.getCodigo(),
                        p1.getCodigoFixo(),
                        p1.getDescricao(),
                        p1.getCodImagem(),
                        p1.getOrdemExibicao(),
                        true,
                        toAlternativaAtualiza(p1.getAlternativas())));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por último, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Versão tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 8, 9 - Código fixo mudou.
            ensureAllAttributesEqual(p1Antes, p1Depois, 4, false, false);

            // 10 - Está single_choice.
            assertThat(p1Depois.isSingleChoice()).isTrue();
        }
    }

    @Test
    @DisplayName("Altera A1 para não abrir OS, aumenta versão")
    public void caso18_alteraA1ParaNaoAbrirOS_deveMudarVersaoModeloECodigoFixoA1() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Alteramos P1 para não abrir OS e aí atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        // A1.
        final PerguntaModeloChecklistEdicao p1 = perguntas.get(0);
        final AlternativaModeloChecklist a1 = p1.getAlternativas().get(0);
        p1.getAlternativas().set(
                0,
                new AlternativaModeloChecklistEdicaoAtualiza(
                        a1.getCodigo(),
                        a1.getCodigoFixo(),
                        a1.getDescricao(),
                        a1.getPrioridade(),
                        a1.isTipoOutros(),
                        a1.getOrdemExibicao(),
                        false));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por último, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Versão tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 8, 9 - Código fixo da pergunta igual da A1 diferente.
            assertThat(p1Depois.getCodigoFixo()).isEqualTo(p1Antes.getCodigoFixo());
            assertThat(p1Depois.getAlternativas().get(0).getCodigoFixo())
                    .isGreaterThan(p1Antes.getAlternativas().get(0).getCodigoFixo());

            // 10 - Está como não deve abrir OS.
            assertThat(p1Depois.getAlternativas().get(0).isDeveAbrirOrdemServico()).isFalse();
        }
    }

    @Test
    @DisplayName("Altera A1 para prioridade BAIXA, aumenta versão")
    public void caso19_alteraA1ParaPrioridadeBaixa_deveMudarVersaoModeloECodigoFixoA1() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Alteramos A1 para prioridade baixa e aí atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        // A1.
        final PerguntaModeloChecklistEdicao p1 = perguntas.get(0);
        final AlternativaModeloChecklist a1 = p1.getAlternativas().get(0);
        p1.getAlternativas().set(
                0,
                new AlternativaModeloChecklistEdicaoAtualiza(
                        a1.getCodigo(),
                        a1.getCodigoFixo(),
                        a1.getDescricao(),
                        PrioridadeAlternativa.BAIXA,
                        a1.isTipoOutros(),
                        a1.getOrdemExibicao(),
                        a1.isDeveAbrirOrdemServico()));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por último, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Versão tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 8, 9 - Código fixo da pergunta igual, da alternativa mudou.
            assertThat(p1Depois.getCodigoFixo()).isEqualTo(p1Antes.getCodigoFixo());
            assertThat(p1Depois.getAlternativas().get(0).getCodigoFixo())
                    .isGreaterThan(p1Antes.getAlternativas().get(0).getCodigoFixo());

            // 10 - Está como prioridade BAIXA.
            assertThat(p1Depois.getAlternativas().get(0).getPrioridade()).isEqualTo(PrioridadeAlternativa.BAIXA);
        }
    }

    @Test
    @DisplayName("Altera ordem de exibição da A1 com a A3, mantém versão")
    public void caso20_alteraOrdemExibicaoA1ComA3_deveMudarVersaoModeloEManterCodigoFixo() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Alteramos A1 para prioridade baixa e aí atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final PerguntaModeloChecklistEdicao p1 = perguntas.get(0);
        final AlternativaModeloChecklist a1 = p1.getAlternativas().get(0);
        final AlternativaModeloChecklist a3 = p1.getAlternativas().get(2);

        // A1 (Fora de foco).
        p1.getAlternativas().set(
                2,
                new AlternativaModeloChecklistEdicaoAtualiza(
                        a1.getCodigo(),
                        a1.getCodigoFixo(),
                        a1.getDescricao(),
                        a1.getPrioridade(),
                        a1.isTipoOutros(),
                        3,
                        a1.isDeveAbrirOrdemServico()));
        // A3 (Lanterna quebrada).
        p1.getAlternativas().set(
                0,
                new AlternativaModeloChecklistEdicaoAtualiza(
                        a3.getCodigo(),
                        a3.getCodigoFixo(),
                        a3.getDescricao(),
                        a3.getPrioridade(),
                        a3.isTipoOutros(),
                        1,
                        a3.isDeveAbrirOrdemServico()));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por último, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Versão tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isEqualTo(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 8, 9 - Código fixo da pergunta e da alternativa se mantiveram.
            assertThat(p1Depois.getCodigoFixo()).isEqualTo(p1Antes.getCodigoFixo());
            assertThat(p1Depois.getAlternativas().get(0).getCodigoFixo())
                    .isEqualTo(p1Antes.getAlternativas().get(0).getCodigoFixo());
            assertThat(p1Depois.getAlternativas().get(2).getCodigoFixo())
                    .isEqualTo(p1Antes.getAlternativas().get(2).getCodigoFixo());

            // 10 - 'Fora de foco' está com ordem de exibição 3.
            assertThat(p1Depois.getAlternativas().get(2).getDescricao()).isEqualTo("Fora de foco");
            assertThat(p1Depois.getAlternativas().get(2).getOrdemExibicao()).isEqualTo(3);

            // 11 - 'Lanterna quebrada' está com ordem de exibição 1.
            assertThat(p1Depois.getAlternativas().get(0).getDescricao()).isEqualTo("Lanterna quebrada");
            assertThat(p1Depois.getAlternativas().get(0).getOrdemExibicao()).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("Atualiza o nome do modelo, mantém a versão")
    public void caso21_atualizaNomeDoModelo_deveManterVersao() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());

        // 4, 5 - Então, sem alterar nada, inserimos novamente o modelo.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(
                modeloBuscado,
                "NOVO NOME",
                perguntas,
                cargos,
                tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por último, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(buscado.getNome()).isEqualTo("NOVO NOME");
        assertThat(editado.getCodVersaoModelo()).isEqualTo(buscado.getCodVersaoModelo());
    }

    @NotNull
    private List<AlternativaModeloChecklistEdicao> toAlternativaAtualiza(
            @NotNull final List<AlternativaModeloChecklist> alternativas) {
        // Força o cast para garantir que é do tipo Visualização.
        @SuppressWarnings("unchecked")
        final List<AlternativaModeloChecklistVisualizacao> visu =
                (List<AlternativaModeloChecklistVisualizacao>) (List<?>) alternativas;

        final List<AlternativaModeloChecklistEdicao> novas = new ArrayList<>(alternativas.size());
        visu.forEach(a -> novas.add(
                new AlternativaModeloChecklistEdicaoAtualiza(
                        a.getCodigo(),
                        a.getCodigoFixo(),
                        a.getDescricao(),
                        a.getPrioridade(),
                        a.isTipoOutros(),
                        a.getOrdemExibicao(),
                        a.isDeveAbrirOrdemServico())));
        return novas;
    }

    @Test
    @DisplayName("Altera ordem de exibição da P1 com a P2, muda versão")
    public void caso22_alteraOrdemExibicaoP1comP2_deveMudarVersaoModeloEManterCodigoFixo() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Alteramos A1 para prioridade baixa e aí atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final PerguntaModeloChecklistEdicao p1 = perguntas.get(0);
        final PerguntaModeloChecklistEdicao p2 = perguntas.get(1);
        perguntas.set(0,
                new PerguntaModeloChecklistEdicaoAtualiza(
                        p2.getCodigo(),
                        p2.getCodigoFixo(),
                        p2.getDescricao(),
                        p2.getCodImagem(),
                        1,
                        p2.isSingleChoice(),
                        p2.getAlternativas()
                                .stream()
                                .map(a -> new AlternativaModeloChecklistEdicaoAtualiza(
                                        a.getCodigo(),
                                        a.getCodigoFixo(),
                                        a.getDescricao(),
                                        a.getPrioridade(),
                                        a.isTipoOutros(),
                                        a.getOrdemExibicao(),
                                        a.isDeveAbrirOrdemServico()))
                                .collect(Collectors.toList())));
        perguntas.set(1,
                new PerguntaModeloChecklistEdicaoAtualiza(
                        p1.getCodigo(),
                        p1.getCodigoFixo(),
                        p1.getDescricao(),
                        p1.getCodImagem(),
                        2,
                        p1.isSingleChoice(),
                        p1.getAlternativas()
                                .stream()
                                .map(a -> new AlternativaModeloChecklistEdicaoAtualiza(
                                        a.getCodigo(),
                                        a.getCodigoFixo(),
                                        a.getDescricao(),
                                        a.getPrioridade(),
                                        a.isTipoOutros(),
                                        a.getOrdemExibicao(),
                                        a.isDeveAbrirOrdemServico()))
                                .collect(Collectors.toList())));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por último, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Versão tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // Código fixo da pergunta se manteve.
            assertThat(p1Depois.getCodigoFixo()).isEqualTo(p1Antes.getCodigoFixo());

            // 'Cinto de segurança' está com ordem de exibição 1.
            assertThat(p1Depois.getDescricao()).isEqualTo("Cinto de segurança");
            assertThat(p1Depois.getOrdemExibicao()).isEqualTo(1);
        }
        {
            // P2.
            final PerguntaModeloChecklistEdicao p2Antes = editado.getPerguntas().get(1);
            final PerguntaModeloChecklistVisualizacao p2Depois = buscado.getPerguntas().get(1);

            // Código fixo da pergunta se manteve.
            assertThat(p2Depois.getCodigoFixo()).isEqualTo(p2Antes.getCodigoFixo());

            // 'Farol' está com ordem de exibição 2.
            assertThat(p2Depois.getDescricao()).isEqualTo("Farol");
            assertThat(p2Depois.getOrdemExibicao()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("Remove última pergunta, aumenta versão")
    public void caso23_removeUltimaPergunta_deveMudarVersaoModelo() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Então buscamos o modelo inserido.
        // Nós não garantimos que a busca é igual ao inserido pois isso é feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Então, removemos a P1 e atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);

        // Removemos a última pergunta (Cinto de segurança).
        perguntas.remove(perguntas.size() - 1);

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por último, buscamos novamente o modelo e comparamos com o base. Tudo deve bater.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Versão do modelo tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());

        // 8, 9 - Garante que temos apenas uma pergunta.
        assertThat(editado.getPerguntas()).hasSize(1);
        assertThat(buscado.getPerguntas()).hasSize(1);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);
            assertThat(p1Depois.getDescricao()).isEqualTo("Farol");

            ensureAllAttributesEqual(p1Antes, p1Depois, 4, true, false);

            for (int i = 0; i < 4; i++) {
                ensureAllAttributesEqual(p1Antes.getAlternativas().get(i), p1Depois.getAlternativas().get(i), true, false);
            }
        }
    }

    // TODO: Talvez faça mais sentido (KISS) remover esse método. Usado apenas em 3 lugares mascara que usamos sempre
    // uma AlternativaModeloChecklistEdicaoAtualiza.
    @NotNull
    private PerguntaModeloChecklistEdicao copyFrom(@NotNull final PerguntaModeloChecklistVisualizacao p,
                                                   @NotNull final String descricaoAtual,
                                                   @NotNull final String novaDescricao) {
        assertThat(p.getDescricao()).isEqualTo(descricaoAtual);
        final List<AlternativaModeloChecklistEdicao> alternativas = new ArrayList<>();
        for (final AlternativaModeloChecklist a : p.getAlternativas()) {
            alternativas.add(new AlternativaModeloChecklistEdicaoAtualiza(
                    a.getCodigo(),
                    a.getCodigoFixo(),
                    a.getDescricao(),
                    a.getPrioridade(),
                    a.isTipoOutros(),
                    a.getOrdemExibicao(),
                    a.isDeveAbrirOrdemServico()));
        }
        return new PerguntaModeloChecklistEdicaoAtualiza(
                p.getCodigo(),
                p.getCodigoFixo(),
                novaDescricao,
                p.getCodImagem(),
                p.getOrdemExibicao(),
                p.isSingleChoice(),
                alternativas);
    }

    @NotNull
    private AlternativaModeloChecklistEdicao copyFrom(@NotNull final AlternativaModeloChecklistVisualizacao a,
                                                      @NotNull final String descricaoAtual,
                                                      @NotNull final String novaDescricao) {
        assertThat(a.getDescricao()).isEqualTo(descricaoAtual);
        return new AlternativaModeloChecklistEdicaoAtualiza(
                a.getCodigo(),
                a.getCodigoFixo(),
                novaDescricao,
                a.getPrioridade(),
                a.isTipoOutros(),
                a.getOrdemExibicao(),
                a.isDeveAbrirOrdemServico());
    }

    private void ensureAllAttributesEqual(@NotNull final PerguntaModeloChecklist antes,
                                          @NotNull final PerguntaModeloChecklist depois,
                                          final int qtdAlternativas,
                                          final boolean mesmoCodigoFixo,
                                          final boolean mesmoCodigoVariavel) {
        assertThat(antes).isNotSameInstanceAs(depois);
        if (mesmoCodigoVariavel) {
            assertThat(antes.getCodigo()).isEqualTo(depois.getCodigo());
        } else {
            assertThat(antes.getCodigo()).isLessThan(depois.getCodigo());
        }
        if (mesmoCodigoFixo) {
            assertThat(antes.getCodigoFixo()).isEqualTo(depois.getCodigoFixo());
        } else {
            assertThat(antes.getCodigoFixo()).isLessThan(depois.getCodigoFixo());
        }
        assertThat(antes.getDescricao()).isEqualTo(depois.getDescricao());
        assertThat(antes.getCodImagem()).isEqualTo(depois.getCodImagem());
        assertThat(antes.getOrdemExibicao()).isEqualTo(depois.getOrdemExibicao());
        assertThat(antes.isSingleChoice()).isEqualTo(depois.isSingleChoice());
        assertThat(antes.getAlternativas()).hasSize(qtdAlternativas);
        assertThat(depois.getAlternativas()).hasSize(qtdAlternativas);
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

    //ESTE MÉTODO TORNA OPCIONAL A VALIDAÇÃO DOS CÓDIGOS DE ALTERNATIVAS
    private void ensureAllAttributesEqual(@NotNull final AlternativaModeloChecklist antes,
                                          @NotNull final AlternativaModeloChecklist depois,
                                          final boolean mesmoCodigoFixo,
                                          final boolean mesmoCodigoVariavel) {
        assertThat(antes).isNotSameInstanceAs(depois);

        if(mesmoCodigoVariavel){
            assertThat(antes.getCodigo()).isEqualTo(depois.getCodigo());
        }

        if(mesmoCodigoFixo){
            assertThat(antes.getCodigoFixo()).isEqualTo(depois.getCodigoFixo());
        }

        assertThat(antes.getDescricao()).isEqualTo(depois.getDescricao());
        assertThat(antes.getPrioridade()).isEqualTo(depois.getPrioridade());
        assertThat(antes.isTipoOutros()).isEqualTo(depois.isTipoOutros());
        assertThat(antes.getOrdemExibicao()).isEqualTo(depois.getOrdemExibicao());
        assertThat(antes.isDeveAbrirOrdemServico()).isEqualTo(depois.isDeveAbrirOrdemServico());
    }

    @NotNull
    private static List<PerguntaModeloChecklistEdicao> jsonToCollection(@NotNull final Gson gson,
                                                                        @NotNull final String json) {
        final Type type = new TypeToken<List<PerguntaModeloChecklistEdicao>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    @NotNull
    private ResultInsertModeloChecklist insertModeloBase() {
        return service.insertModeloChecklist(BASE, token);
    }

    @NotNull
    private List<PerguntaModeloChecklistEdicao> toPerguntasEdicao(
            @NotNull final ModeloChecklistVisualizacao modeloBuscado) {
        return jsonToCollection(
                GsonUtils.getGson(),
                GsonUtils.getGson().toJson(modeloBuscado.getPerguntas()));
    }

    @NotNull
    private List<Long> getCodigosTiposVeiculos(@NotNull final ModeloChecklistVisualizacao modeloBuscado) {
        return modeloBuscado
                .getTiposVeiculoLiberados()
                .stream()
                .map(TipoVeiculo::getCodigo)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<Long> getCodigosCargos(@NotNull final ModeloChecklistVisualizacao modeloBuscado) {
        return modeloBuscado
                .getCargosLiberados()
                .stream()
                .map(Cargo::getCodigo)
                .collect(Collectors.toList());
    }

    @NotNull
    private ModeloChecklistEdicao createModeloEdicao(
            @NotNull final ModeloChecklistVisualizacao modeloBuscado,
            @NotNull final String novoNome,
            @NotNull final List<PerguntaModeloChecklistEdicao> perguntas,
            @NotNull final List<Long> cargos,
            @NotNull final List<Long> tiposVeiculo) {
        return new ModeloChecklistEdicao(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                modeloBuscado.getCodVersaoModelo(),
                novoNome,
                tiposVeiculo,
                cargos,
                perguntas,
                modeloBuscado.isAtivo());
    }

    @NotNull
    private ModeloChecklistEdicao createModeloEdicao(
            @NotNull final ModeloChecklistVisualizacao modeloBuscado,
            @NotNull final List<PerguntaModeloChecklistEdicao> perguntas,
            @NotNull final List<Long> cargos,
            @NotNull final List<Long> tiposVeiculo) {
        return new ModeloChecklistEdicao(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                modeloBuscado.getCodVersaoModelo(),
                modeloBuscado.getNome(),
                tiposVeiculo,
                cargos,
                perguntas,
                modeloBuscado.isAtivo());
    }

    private void assertCodCargosIguais(@NotNull final ModeloChecklistEdicao editado,
                                       @NotNull final ModeloChecklistVisualizacao buscado) {
        buscado
                .getCargosLiberados()
                .forEach(c -> assertThat(editado.getCargosLiberados()).contains(c.getCodigo()));
    }

    private void assertCodTiposVeiculosIguais(@NotNull final ModeloChecklistEdicao editado,
                                              @NotNull final ModeloChecklistVisualizacao buscado) {
        buscado
                .getTiposVeiculoLiberados()
                .forEach(t -> assertThat(editado.getTiposVeiculoLiberados()).contains(t.getCodigo()));
    }
}