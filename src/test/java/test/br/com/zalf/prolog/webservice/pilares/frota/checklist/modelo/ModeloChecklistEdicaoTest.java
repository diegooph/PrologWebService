package test.br.com.zalf.prolog.webservice.pilares.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.gente.cargo.CargoService;
import br.com.zalf.prolog.webservice.gente.cargo._model.CargoListagemEmpresa;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloService;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.*;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.AlternativaModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.PerguntaModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.TipoVeiculoService;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Para esse teste funcionar corretamente em repetidas execu????es, ?? necess??rio dropar um index da tabela
 * CHECKLIST_MODELO:
 * > drop index checklist_modelo_data_nome_index;
 * <p>
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
    // Mesmo n??o sendo uma constante, usamos mai??sculo para facilitar a diferencia????o nos testes.
    private ModeloChecklistInsercao BASE;

    @NotNull
    private static List<PerguntaModeloChecklistEdicao> jsonToCollection(@NotNull final Gson gson,
                                                                        @NotNull final String json) {
        final Type type = new TypeToken<List<PerguntaModeloChecklistEdicao>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    @Override
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

    @Override
    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
        service = null;
    }

    @Test
    @DisplayName("Atualiza o modelo sem alterar nada, tudo fica igual")
    void caso1_atualizaSemAlterarNada_deveManterTudoIgual() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());

        // 4, 5 - Ent??o, sem alterar nada, inserimos novamente o modelo.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por ??ltimo, buscamos novamente o modelo e comparamos com o base. Tudo deve bater.
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
    @DisplayName("Altera os cargos liberados, mant??m a vers??o")
    void caso2_atualizaOsCargos_deveFuncionar() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Ent??o, alteramos os cargos vinculados e atualizamos novamente o modelo.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);

        // Buscamos um outro c??digo de cargo da mesma empresa que o modelo pertence e que ainda n??o esteja vinculado.
        final Long codNovoCargo = new CargoService()
                .getTodosCargosEmpresa(COD_EMPRESA)
                .stream()
                .map(CargoListagemEmpresa::getCodigo)
                .filter(c -> !BASE.getCargosLiberados().contains(c))
                .limit(1)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Empresa n??o possui outro cargo al??m dos j?? vinculados ao modelo"));
        final List<Long> cargos = Collections.singletonList(codNovoCargo);

        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por ??ltimo, buscamos novamente o modelo e comparamos com o base. Tudo deve bater.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(editado.getCodVersaoModelo()).isEqualTo(buscado.getCodVersaoModelo());
        // Agora temos apena um cargo liberado, n??o mais dois.
        assertThat(buscado.getCargosLiberados()).hasSize(1);
        assertCodCargosIguais(editado, buscado);
    }

    @Test
    @DisplayName("Altera os tipos de ve??culos liberados, mant??m a vers??o")
    void caso3_atualizaOsTiposDeVeiculo_deveFuncionar() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Ent??o, alteramos os tipos de ve??culos vinculados e atualizamos novamente o modelo.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);

        final List<Long> cargos = getCodigosCargos(modeloBuscado);

        // Buscamos um outro c??digo de tipo de ve??culo da mesma empresa que o modelo pertence e que ainda n??o esteja
        // vinculado.
        final Long codNovoTipoVeiculo = new TipoVeiculoService()
                .getTiposVeiculosByEmpresa(token, COD_EMPRESA)
                .stream()
                .map(TipoVeiculo::getCodigo)
                .filter(c -> !BASE.getTiposVeiculoLiberados().contains(c))
                .limit(1)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Empresa n??o possui outro tipo de ve??culo al??m dos j?? vinculados ao modelo"));
        final List<Long> tiposVeiculo = Collections.singletonList(codNovoTipoVeiculo);

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por ??ltimo, buscamos novamente o modelo e comparamos com o base. Tudo deve bater.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(editado.getCodVersaoModelo()).isEqualTo(buscado.getCodVersaoModelo());
        // Agora temos apena um tipo de ve??culo liberado, n??o mais dois.
        assertThat(editado.getTiposVeiculoLiberados()).hasSize(1);
        assertThat(buscado.getTiposVeiculoLiberados()).hasSize(1);
        assertCodTiposVeiculosIguais(editado, buscado);
    }

    @Test
    @DisplayName("Altera os cargos e os tipos de ve??culos liberados, mant??m a vers??o")
    void caso4_atualizaOsCargosEOsTiposDeVeiculo_deveFuncionar() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Ent??o, alteramos os cargos e os tipos de ve??culo vinculados e atualizamos novamente o modelo.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);

        // Buscamos um outro c??digo de cargo da mesma empresa que o modelo pertence e que ainda n??o esteja vinculado.
        final Long codNovoCargo = new CargoService()
                .getTodosCargosEmpresa(COD_EMPRESA)
                .stream()
                .map(CargoListagemEmpresa::getCodigo)
                .filter(c -> !BASE.getCargosLiberados().contains(c))
                .limit(1)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Empresa n??o possui outro cargo al??m dos j?? vinculados ao modelo"));
        final List<Long> cargos = Collections.singletonList(codNovoCargo);

        // Buscamos um outro c??digo de tipo de ve??culo da mesma empresa que o modelo pertence e que ainda n??o esteja
        // vinculado.
        final Long codNovoTipoVeiculo = new TipoVeiculoService()
                .getTiposVeiculosByEmpresa(token, COD_EMPRESA)
                .stream()
                .map(TipoVeiculo::getCodigo)
                .filter(c -> !BASE.getTiposVeiculoLiberados().contains(c))
                .limit(1)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Empresa n??o possui outro tipo de ve??culo al??m dos j?? vinculados ao modelo"));
        final List<Long> tiposVeiculo = Collections.singletonList(codNovoTipoVeiculo);

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por ??ltimo, buscamos novamente o modelo e comparamos com o base. Tudo deve bater.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(editado.getCodVersaoModelo()).isEqualTo(buscado.getCodVersaoModelo());
        // Agora temos apena um cargo liberado, n??o mais dois.
        assertThat(editado.getCargosLiberados()).hasSize(1);
        assertThat(buscado.getCargosLiberados()).hasSize(1);
        assertCodCargosIguais(editado, buscado);
        // Agora temos apena um tipo de ve??culo liberado, n??o mais dois.
        assertThat(editado.getTiposVeiculoLiberados()).hasSize(1);
        assertThat(buscado.getTiposVeiculoLiberados()).hasSize(1);
        assertCodTiposVeiculosIguais(editado, buscado);
    }

    @Test
    @DisplayName("Altera descri????o de todas as perguntas e alternativas mantendo o contexto, mant??m a vers??o")
    void caso5_atualizaOsTextosDeTodasPerguntasEAlternativasSemMudarContexto_deveFuncionarMantendoAVersao() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Ent??o, atualizamos os textos de todas as perguntas e alternativas e a?? atualizamos.
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
                            "For?? de  F??CO "));
            alternativas.add(
                    copyFrom((AlternativaModeloChecklistVisualizacao) p1.getAlternativas().get(1),
                            "L??mpada queimada",
                            "  LAMPADAS QUEIMAHDA"));
            alternativas.add(
                    copyFrom((AlternativaModeloChecklistVisualizacao) p1.getAlternativas().get(2),
                            "Lanterna quebrada",
                            "Lanterna quebada"));
            alternativas.add(
                    copyFrom((AlternativaModeloChecklistVisualizacao) p1.getAlternativas().get(3),
                            "Outros",
                            // A tipo_outros n??o alteramos.
                            "Outros"));

            perguntas.add(new PerguntaModeloChecklistEdicaoAtualiza(
                    p1.getCodigo(),
                    p1.getCodigoContexto(),
                    "F??ROLL",
                    p1.getCodImagem(),
                    p1.getOrdemExibicao(),
                    p1.isSingleChoice(),
                    p1.getAnexoMidiaRespostaOk(),
                    alternativas));
        }

        {
            // P2.
            final PerguntaModeloChecklistVisualizacao p2 = modeloBuscado.getPerguntas().get(1);
            assertThat(p2.getAlternativas()).hasSize(3);

            final List<AlternativaModeloChecklistEdicao> alternativas = new ArrayList<>(3);
            alternativas.add(
                    copyFrom((AlternativaModeloChecklistVisualizacao) p2.getAlternativas().get(0),
                            "N??o trava",
                            "  NAO TR??VVA"));
            alternativas.add(
                    copyFrom((AlternativaModeloChecklistVisualizacao) p2.getAlternativas().get(1),
                            "Sensor n??o funciona",
                            " SENSORR   nao FuNSiOnAa "));
            alternativas.add(
                    copyFrom((AlternativaModeloChecklistVisualizacao) p2.getAlternativas().get(2),
                            "Outros",
                            // A tipo_outros n??o alteramos.
                            "Outros"));

            perguntas.add(new PerguntaModeloChecklistEdicaoAtualiza(
                    p2.getCodigo(),
                    p2.getCodigoContexto(),
                    "cinto   di SeGURANCA  ",
                    p2.getCodImagem(),
                    p2.getOrdemExibicao(),
                    p2.isSingleChoice(),
                    p2.getAnexoMidiaRespostaOk(),
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

        // 6, 7, 8, 9 - Por ??ltimo, buscamos novamente o modelo e fazemos as compara????es necess??rias.
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
    @DisplayName("Remove uma alternativa da P1, aumenta a vers??o")
    void caso6_removeUmaAlternativaDaP1_deveMudarVersaoModeloECodigoContextoPergunta() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Ent??o, removemos uma alternativa da P1 e a?? atualizamos.
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

        // 6 - Por ??ltimo, buscamos novamente o modelo e fazemos as compara????es necess??rias.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Vers??o do modelo tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(editado.getPerguntas()).hasSize(2);
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 8 - C??digo contexto se mant??m e vari??vel troca.
            // C??digo fixa da pergunta n??o muda por altera????es nas alternativas, mesmo uma remo????o.
            ensureAllAttributesEqual(p1Antes, p1Depois, 3, true, false);

            // 9 - Garante que a alternativa 'Fora de foco' n??o est?? mais presente.
            p1Depois.getAlternativas()
                    .stream()
                    .filter(p -> p.getDescricao().equals("Fora de foco"))
                    .findAny()
                    .ifPresent(a -> {
                        throw new RuntimeException("Alternativa 'Fora de foco' deletada ainda est?? presente");
                    });
        }
    }

    @Test
    @DisplayName("Remove uma alternativa da P1 e adiciona outra, aumenta a vers??o")
    void caso7_removeUmaAlternativaDaP1AdicionaOutraAlternativaNaP1_deveMudarVersaoModeloECodigoContextoDaP1() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Ent??o, removemos uma alternativa da P1 e adicionamos outra na P1, a?? atualizamos.
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
                        true,
                        AnexoMidiaChecklistEnum.BLOQUEADO,
                        "TESTE"));

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por ??ltimo, buscamos novamente o modelo e fazemos as compara????es necess??rias.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 8 - Vers??o do modelo tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(editado.getPerguntas()).hasSize(2);
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 9 - C??digo contexto igual, vari??vel troca.
            ensureAllAttributesEqual(p1Antes, p1Depois, 4, true, false);

            // 10 - Garante que a alternativa 'Fora de foco' n??o est?? mais presente.
            Assertions
                    .assertThat(p1Depois.getAlternativas())
                    .extracting("descricao")
                    .doesNotContain("Fora de foco");

            // 11 - Garante que a nova alternativa est?? presente.
            Assertions
                    .assertThat(p1Depois.getAlternativas())
                    .extracting("descricao")
                    .contains("Piscando sozinho");
        }
    }

    @Test
    @DisplayName("Remove uma alternativa da P1 e adiciona outra na P2, aumenta a vers??o")
    void caso8_removeUmaAlternativaDaP1AdicionaOutraAlternativaNaP2_deveMudarVersaoModeloECodigoContextoDasPerguntas() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Ent??o, removemos uma alternativa de uma pergunta e adicionamos outra em outra pergunta, a?? atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);

        // Removemos a alternativa 'Fora de foco' da P1 e adicionamos 'Rasgado' na P2.
        perguntas.get(0).getAlternativas().remove(0);

        // P2 ?? 'Cinto de Seguran??a' e possui 3 alternativas.
        // A tipo_outros tem que ser realocada para a ??ltima posi????o.
        final AlternativaModeloChecklist outros = perguntas.get(1).getAlternativas().remove(2);
        perguntas.get(1).getAlternativas().add(
                new AlternativaModeloChecklistEdicaoInsere(
                        "Rasgado",
                        PrioridadeAlternativa.BAIXA,
                        false,
                        3,
                        true,
                        AnexoMidiaChecklistEnum.BLOQUEADO,
                        "TESTE"));
        perguntas.get(1).getAlternativas().add(
                new AlternativaModeloChecklistEdicaoInsere(
                        "Outros",
                        outros.getPrioridade(),
                        true,
                        4,
                        outros.isDeveAbrirOrdemServico(),
                        outros.getAnexoMidia(),
                        "TESTE"));

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por ??ltimo, buscamos novamente o modelo e fazemos as compara????es necess??rias.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 8 - Vers??o do modelo tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(editado.getPerguntas()).hasSize(2);
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 9 - C??digo contexto igual, vari??vel troca.
            ensureAllAttributesEqual(p1Antes, p1Depois, 3, true, false);

            // 10 - Garante que a alternativa 'Fora de foco' n??o est?? mais presente.
            Assertions
                    .assertThat(p1Depois.getAlternativas())
                    .extracting("descricao")
                    .doesNotContain("Fora de foco");
        }

        {
            // P2.
            final PerguntaModeloChecklistEdicao p2Antes = editado.getPerguntas().get(1);
            final PerguntaModeloChecklistVisualizacao p2Depois = buscado.getPerguntas().get(1);

            // 11 - C??digo contexto igual, vari??vel troca.
            ensureAllAttributesEqual(p2Antes, p2Depois, 4, true, false);

            // 12 - Garante que a nova alternativa est?? presente.
            Assertions
                    .assertThat(p2Depois.getAlternativas())
                    .extracting("descricao")
                    .contains("Rasgado");
        }
    }

    @Test
    @DisplayName("Remove P1, aumenta vers??o")
    void caso9_removeP1_deveMudarVersaoModelo() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Ent??o, removemos a P1 e atualizamos.
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

        // 6 - Por ??ltimo, buscamos novamente o modelo e comparamos com o base. Tudo deve bater.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Vers??o do modelo tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());

        // 8, 9 - Garante que temos apenas uma pergunta.
        assertThat(editado.getPerguntas()).hasSize(1);
        assertThat(buscado.getPerguntas()).hasSize(1);
        {
            // P2.
            final PerguntaModeloChecklistEdicao p2Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p2Depois = buscado.getPerguntas().get(0);
            assertThat(p2Depois.getDescricao()).isEqualTo("Cinto de seguran??a");

            ensureAllAttributesEqual(p2Antes, p2Depois, 3, true, false);

            for (int i = 0; i < 3; i++) {
                ensureAllAttributesEqual(p2Antes.getAlternativas().get(i), p2Depois.getAlternativas().get(i), true, false);
            }
        }
    }

    @Test
    @DisplayName("Remove P1 e P2, d?? erro")
    void caso10_removeP1EP2_deveDarErro() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Ent??o, removemos a P1 e P2 e a?? atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);

        // Removemos a pergunta P1 (Farol).
        perguntas.remove(0);
        // Removemos a pergunta P2 (Cinto de seguran??a).
        perguntas.remove(0);

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        assertThrows(ProLogException.class, () -> service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token));
    }

    @Test
    @DisplayName("Remove todas as alternativas da P1, d?? erro")
    void caso11_removeTodasAlternativasP1_deveDarErro() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Ent??o, removemos todas as alternativas da P1 e a?? atualizamos.
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
    @DisplayName("Remove alternativa tipo_outros da P2, d?? erro")
    void caso12_removeAlternativaTipoOutrosDaP2_deveDarErro() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Ent??o, removemos a alternativa tipo_outros da P2 e a?? atualizamos.
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
    @DisplayName("Muda contexto da P1, aumenta vers??o")
    void caso13_alteraTextoDaP1MudandoContexto_deveMudarVersaoModeloECodigoContextoDaP1() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Alteramos o texto da P1 mudando o contexto e a?? atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        // P1.
        final PerguntaModeloChecklistVisualizacao p1 = modeloBuscado.getPerguntas().get(0);
        perguntas.set(
                0,
                // P1 ?? substitu??da com uma nova descri????o.
                copyFrom(
                        p1,
                        "Farol",
                        "Extintor de inc??ndio"));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por ??ltimo, buscamos novamente o modelo e comparamos com o base.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Vers??o do modelo tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(editado.getPerguntas()).hasSize(2);
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);
            // 8 - Compara P1 garantindo que o c??digo contexto e vari??vel mudaram.
            ensureAllAttributesEqual(p1Antes, p1Depois, 4, false, false);

            // 9 - Garante que a descri????o foi atualizada para a nova.
            assertThat(p1Depois.getDescricao()).isEqualTo("Extintor de inc??ndio");
        }
    }

    @Test
    @DisplayName("Muda contexto da P1 e altera P2 mantendo contexto, aumenta vers??o")
    void caso14_alteraContextoDaP1_alteraP2MantendoContexto_deveMudarVersaoModeloECodigoContextoDaP1EManterDaP2() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5, 6 - Alteramos o texto da P1 mudando o contexto e o da P2 mantendo e a?? atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);

        {
            // P1.
            final PerguntaModeloChecklistVisualizacao p1 = modeloBuscado.getPerguntas().get(0);
            perguntas.set(
                    0,
                    // P1 ?? substitu??da com uma nova descri????o mudando contexto.
                    copyFrom(
                            p1,
                            "Farol",
                            "Extintor de inc??ndio"));
        }

        {
            // P2.
            final PerguntaModeloChecklistVisualizacao p2 = modeloBuscado.getPerguntas().get(1);
            perguntas.set(
                    1,
                    // P2 ?? substitu??da com uma nova descri????o mantendo contexto.
                    copyFrom(
                            p2,
                            "Cinto de seguran??a",
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

        // 7 - Por ??ltimo, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 8 - A vers??o do modelo tem que estar uma maior.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(editado.getPerguntas()).hasSize(2);
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);
            // 9 - O c??digo contexto da pergunta P1 tem que estar diferente.
            ensureAllAttributesEqual(p1Antes, p1Depois, 4, false, false);
            // 11 - Garante o texto atualizado.
            assertThat(p1Depois.getDescricao()).isEqualTo("Extintor de inc??ndio");
        }

        {
            // P2.
            final PerguntaModeloChecklistEdicao p2Antes = editado.getPerguntas().get(1);
            final PerguntaModeloChecklistVisualizacao p2Depois = buscado.getPerguntas().get(1);
            // 10 - O c??digo contexto da pergunta P2 tem que ser o mesmo, vari??vel mudou.
            ensureAllAttributesEqual(p2Antes, p2Depois, 3, true, false);
            // 11 - Garante o texto atualizado.
            assertThat(p2Depois.getDescricao()).isEqualTo("  Cinto  di  SEGURANSA ");
        }
    }

    @Test
    @DisplayName("Muda contexto da A1 e deleta A2, aumenta vers??o")
    void caso15_alteraContextoDaA1_deletaA2_deveMudarVersaoModelo_CodigoContextoP1Diferente_CodigoContextoA1Diferente() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5, 6 - Alteramos o texto da P1 mudando o contexto, deletamos a P2 e a?? atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);

        // A1 - Altera por uma com novo contexto.
        final AlternativaModeloChecklist a1 = modeloBuscado.getPerguntas().get(0).getAlternativas().get(0);
        perguntas.get(0).getAlternativas().set(
                0,
                // A1 ?? substitu??da com uma nova descri????o mudando contexto.
                copyFrom(
                        (AlternativaModeloChecklistVisualizacao) a1,
                        "Fora de foco",
                        "Desfocado"));
        // A2 - Remove a alternativa A2 (L??mpada queimada).
        perguntas.get(0).getAlternativas().remove(1);

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 7 - Por ??ltimo, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 8 - A vers??o do modelo tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 9, 10 - Garante c??digo contexto igual e vari??vel diferente.
            ensureAllAttributesEqual(p1Antes, p1Depois, 3, true, false);

            // 11 - Garante que a alternativa 'L??mpada queimada' n??o est?? mais presente.
            Assertions
                    .assertThat(p1Depois.getAlternativas())
                    .extracting("descricao")
                    .doesNotContain("L??mpada queimada");

            // 12 - Garante que a alternativa 'Fora de foco' n??o est?? mais presente.
            Assertions
                    .assertThat(p1Depois.getAlternativas())
                    .extracting("descricao")
                    .doesNotContain("Fora de foco");

            // 12 - Garante que a alternativa A1 possui a nova descri????o.
            assertThat(p1Depois.getAlternativas().get(0).getDescricao()).isEqualTo("Desfocado");
        }
    }

    @Test
    @DisplayName("Muda contexto da A1 e adiciona alternativa na P1, aumenta vers??o")
    void caso16_alteraContextoA1_adicionaAlternativaP1_deveMudarVersaoModeloCodigoContextoP1IgualCodigoContextoA1Diferente() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5, 6 - Alteramos o texto da A1 mudando o contexto, adicionamos uma alternativa na P1 e a?? atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);

        // A1 - Altera por uma com novo contexto.
        final AlternativaModeloChecklist a1 = modeloBuscado.getPerguntas().get(0).getAlternativas().get(0);
        perguntas.get(0).getAlternativas().set(
                0,
                // A1 ?? substitu??da com uma nova descri????o mudando contexto.
                copyFrom(
                        (AlternativaModeloChecklistVisualizacao) a1,
                        "Fora de foco",
                        "Desfocado"));
        // P1 - Adiciona nova alternativa - P1 j?? tem 4.
        // A tipo_outros tem que ser realocada para a ??ltima posi????o.
        final AlternativaModeloChecklist outros = perguntas.get(0).getAlternativas().remove(3);
        perguntas.get(0).getAlternativas().add(
                new AlternativaModeloChecklistEdicaoInsere(
                        "Piscando sozinho",
                        PrioridadeAlternativa.BAIXA,
                        false,
                        4,
                        true,
                        AnexoMidiaChecklistEnum.BLOQUEADO,
                        "TESTE"));
        perguntas.get(0).getAlternativas().add(
                new AlternativaModeloChecklistEdicaoInsere(
                        "Outros",
                        outros.getPrioridade(),
                        true,
                        5,
                        outros.isDeveAbrirOrdemServico(),
                        outros.getAnexoMidia(),
                        "TESTE"));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 7 - Por ??ltimo, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 8 - Garante uma vers??o maior.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 9, 10 - Garante c??digo contexto igual e vari??vel diferente.
            ensureAllAttributesEqual(p1Antes, p1Depois, 5, true, false);

            // 11 - Garante que a alternativa A5 est?? presente.
            final AlternativaModeloChecklist a5 = p1Depois.getAlternativas().get(3);
            assertThat(a5.getDescricao()).isEqualTo("Piscando sozinho");
            assertThat(a5.getPrioridade()).isEqualTo(PrioridadeAlternativa.BAIXA);
            assertThat(a5.isTipoOutros()).isFalse();
            assertThat(a5.getOrdemExibicao()).isEqualTo(4);
            assertThat(a5.isDeveAbrirOrdemServico()).isTrue();

            // 12 - Garante que a alternativa 'Fora de foco' n??o est?? mais presente.
            Assertions
                    .assertThat(p1Depois.getAlternativas())
                    .extracting("descricao")
                    .doesNotContain("Fora de foco");

            // 12 - Garante que a alternativa A1 possui a nova descri????o.
            assertThat(p1Depois.getAlternativas().get(0).getDescricao()).isEqualTo("Desfocado");

            assertThat(p1Depois.getDescricao()).isEqualTo("Farol");
        }
    }

    @Test
    @DisplayName("Altera P1 para single_choice, aumenta vers??o")
    void caso17_alteraP1ParaSingleChoice_deveMudarVersaoModeloEManterCodigoContextoP1() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Alteramos P1 para single_choice e a?? atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        // P1.
        final PerguntaModeloChecklistVisualizacao p1 = modeloBuscado.getPerguntas().get(0);

        perguntas.set(
                0,
                // P1 ?? substitu??da agora sendo single_choice.
                new PerguntaModeloChecklistEdicaoAtualiza(
                        p1.getCodigo(),
                        p1.getCodigoContexto(),
                        p1.getDescricao(),
                        p1.getCodImagem(),
                        p1.getOrdemExibicao(),
                        true,
                        p1.getAnexoMidiaRespostaOk(),
                        toAlternativaAtualiza(p1.getAlternativas())));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por ??ltimo, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Vers??o tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 8, 9 - Mant??m o c??digo de contexto.
            ensureAllAttributesEqual(p1Antes, p1Depois, 4, true, false);

            // 10 - Est?? single_choice.
            assertThat(p1Depois.isSingleChoice()).isTrue();
        }
    }

    @Test
    @DisplayName("Altera A1 para n??o abrir OS, aumenta vers??o")
    void caso18_alteraA1ParaNaoAbrirOS_deveMudarVersaoModeloECodigoContextoA1() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Alteramos P1 para n??o abrir OS e a?? atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        // A1.
        final PerguntaModeloChecklistEdicao p1 = perguntas.get(0);
        final AlternativaModeloChecklist a1 = p1.getAlternativas().get(0);
        p1.getAlternativas().set(
                0,
                new AlternativaModeloChecklistEdicaoAtualiza(
                        a1.getCodigo(),
                        a1.getCodigoContexto(),
                        a1.getDescricao(),
                        a1.getPrioridade(),
                        a1.isTipoOutros(),
                        a1.getOrdemExibicao(),
                        false,
                        a1.getAnexoMidia(),
                        "TESTE"));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por ??ltimo, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Vers??o tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 8, 9 - C??digo contexto da pergunta igual da A1 diferente.
            assertThat(p1Depois.getCodigoContexto()).isEqualTo(p1Antes.getCodigoContexto());
            assertThat(p1Depois.getAlternativas().get(0).getCodigoContexto())
                    .isGreaterThan(p1Antes.getAlternativas().get(0).getCodigoContexto());

            // 10 - Est?? como n??o deve abrir OS.
            assertThat(p1Depois.getAlternativas().get(0).isDeveAbrirOrdemServico()).isFalse();
        }
    }

    @Test
    @DisplayName("Altera A1 para prioridade BAIXA, aumenta vers??o")
    void caso19_alteraA1ParaPrioridadeBaixa_deveMudarVersaoModeloECodigoContextoA1() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Alteramos A1 para prioridade baixa e a?? atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        // A1.
        final PerguntaModeloChecklistEdicao p1 = perguntas.get(0);
        final AlternativaModeloChecklist a1 = p1.getAlternativas().get(0);
        p1.getAlternativas().set(
                0,
                new AlternativaModeloChecklistEdicaoAtualiza(
                        a1.getCodigo(),
                        a1.getCodigoContexto(),
                        a1.getDescricao(),
                        PrioridadeAlternativa.BAIXA,
                        a1.isTipoOutros(),
                        a1.getOrdemExibicao(),
                        a1.isDeveAbrirOrdemServico(),
                        a1.getAnexoMidia(),
                        "teste"));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por ??ltimo, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Vers??o tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 8, 9 - C??digo contexto da pergunta igual, da alternativa mudou.
            assertThat(p1Depois.getCodigoContexto()).isEqualTo(p1Antes.getCodigoContexto());
            assertThat(p1Depois.getAlternativas().get(0).getCodigoContexto())
                    .isGreaterThan(p1Antes.getAlternativas().get(0).getCodigoContexto());

            // 10 - Est?? como prioridade BAIXA.
            assertThat(p1Depois.getAlternativas().get(0).getPrioridade()).isEqualTo(PrioridadeAlternativa.BAIXA);
        }
    }

    @Test
    @DisplayName("Altera ordem de exibi????o da A1 com a A3, mant??m vers??o")
    void caso20_alteraOrdemExibicaoA1ComA3_deveMudarVersaoModeloEManterCodigoContexto() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Alteramos A1 para prioridade baixa e a?? atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final PerguntaModeloChecklistEdicao p1 = perguntas.get(0);
        final AlternativaModeloChecklist a1 = p1.getAlternativas().get(0);
        final AlternativaModeloChecklist a3 = p1.getAlternativas().get(2);

        // A1 (Fora de foco).
        p1.getAlternativas().set(
                2,
                new AlternativaModeloChecklistEdicaoAtualiza(
                        a1.getCodigo(),
                        a1.getCodigoContexto(),
                        a1.getDescricao(),
                        a1.getPrioridade(),
                        a1.isTipoOutros(),
                        3,
                        a1.isDeveAbrirOrdemServico(),
                        a1.getAnexoMidia(),
                        "teste"));
        // A3 (Lanterna quebrada).
        p1.getAlternativas().set(
                0,
                new AlternativaModeloChecklistEdicaoAtualiza(
                        a3.getCodigo(),
                        a3.getCodigoContexto(),
                        a3.getDescricao(),
                        a3.getPrioridade(),
                        a3.isTipoOutros(),
                        1,
                        a3.isDeveAbrirOrdemServico(),
                        a3.getAnexoMidia(),
                        "teste"));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por ??ltimo, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Vers??o tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isEqualTo(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 8, 9 - C??digo contexto da pergunta e da alternativa se mantiveram.
            assertThat(p1Depois.getCodigoContexto()).isEqualTo(p1Antes.getCodigoContexto());
            assertThat(p1Depois.getAlternativas().get(0).getCodigoContexto())
                    .isEqualTo(p1Antes.getAlternativas().get(0).getCodigoContexto());
            assertThat(p1Depois.getAlternativas().get(2).getCodigoContexto())
                    .isEqualTo(p1Antes.getAlternativas().get(2).getCodigoContexto());

            // 10 - 'Fora de foco' est?? com ordem de exibi????o 3.
            assertThat(p1Depois.getAlternativas().get(2).getDescricao()).isEqualTo("Fora de foco");
            assertThat(p1Depois.getAlternativas().get(2).getOrdemExibicao()).isEqualTo(3);

            // 11 - 'Lanterna quebrada' est?? com ordem de exibi????o 1.
            assertThat(p1Depois.getAlternativas().get(0).getDescricao()).isEqualTo("Lanterna quebrada");
            assertThat(p1Depois.getAlternativas().get(0).getOrdemExibicao()).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("Atualiza o nome do modelo, aumenta a vers??o mas mant??m o c??digo de contexto")
    void caso21_atualizaNomeDoModelo_deveManterVersao() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());

        // 4, 5 - Ent??o, sem alterar nada, inserimos novamente o modelo.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final String novoNomeModelo = UUID.randomUUID().toString();
        final ModeloChecklistEdicao editado = createModeloEdicao(
                modeloBuscado,
                novoNomeModelo,
                perguntas,
                cargos,
                tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6, 7 - Por ??ltimo, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(buscado.getNome()).isEqualTo(novoNomeModelo);
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
    }

    @Test
    @DisplayName("Altera ordem de exibi????o da P1 com a P2, muda vers??o")
    void caso22_alteraOrdemExibicaoP1comP2_deveMudarVersaoModeloEManterCodigoContexto() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Alteramos ordem de exibi????o da A1 e a?? atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final PerguntaModeloChecklistEdicao p1 = perguntas.get(0);
        final PerguntaModeloChecklistEdicao p2 = perguntas.get(1);
        perguntas.set(0,
                new PerguntaModeloChecklistEdicaoAtualiza(
                        p2.getCodigo(),
                        p2.getCodigoContexto(),
                        p2.getDescricao(),
                        p2.getCodImagem(),
                        1,
                        p2.isSingleChoice(),
                        p2.getAnexoMidiaRespostaOk(),
                        p2.getAlternativas()
                                .stream()
                                .map(a -> new AlternativaModeloChecklistEdicaoAtualiza(
                                        a.getCodigo(),
                                        a.getCodigoContexto(),
                                        a.getDescricao(),
                                        a.getPrioridade(),
                                        a.isTipoOutros(),
                                        a.getOrdemExibicao(),
                                        a.isDeveAbrirOrdemServico(),
                                        a.getAnexoMidia(),
                                        "teste"))
                                .collect(Collectors.toList())));
        perguntas.set(1,
                new PerguntaModeloChecklistEdicaoAtualiza(
                        p1.getCodigo(),
                        p1.getCodigoContexto(),
                        p1.getDescricao(),
                        p1.getCodImagem(),
                        2,
                        p1.isSingleChoice(),
                        p1.getAnexoMidiaRespostaOk(),
                        p1.getAlternativas()
                                .stream()
                                .map(a -> new AlternativaModeloChecklistEdicaoAtualiza(
                                        a.getCodigo(),
                                        a.getCodigoContexto(),
                                        a.getDescricao(),
                                        a.getPrioridade(),
                                        a.isTipoOutros(),
                                        a.getOrdemExibicao(),
                                        a.isDeveAbrirOrdemServico(),
                                        a.getAnexoMidia(),
                                        "teste"))
                                .collect(Collectors.toList())));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por ??ltimo, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Vers??o tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // C??digo contexto da pergunta se manteve.
            assertThat(p1Depois.getCodigoContexto()).isEqualTo(p1Antes.getCodigoContexto());

            // 'Cinto de seguran??a' est?? com ordem de exibi????o 1.
            assertThat(p1Depois.getDescricao()).isEqualTo("Cinto de seguran??a");
            assertThat(p1Depois.getOrdemExibicao()).isEqualTo(1);
        }
        {
            // P2.
            final PerguntaModeloChecklistEdicao p2Antes = editado.getPerguntas().get(1);
            final PerguntaModeloChecklistVisualizacao p2Depois = buscado.getPerguntas().get(1);

            // C??digo contexto da pergunta se manteve.
            assertThat(p2Depois.getCodigoContexto()).isEqualTo(p2Antes.getCodigoContexto());

            // 'Farol' est?? com ordem de exibi????o 2.
            assertThat(p2Depois.getDescricao()).isEqualTo("Farol");
            assertThat(p2Depois.getOrdemExibicao()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("Remove ??ltima pergunta, aumenta vers??o")
    void caso23_removeUltimaPergunta_deveMudarVersaoModelo() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Ent??o, removemos a P1 e atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);

        // Removemos a ??ltima pergunta (Cinto de seguran??a).
        perguntas.remove(perguntas.size() - 1);

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por ??ltimo, buscamos novamente o modelo e comparamos com o base. Tudo deve bater.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Vers??o do modelo tem que ter aumentado.
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

    @Test
    @DisplayName("Altera imagem da P1, aumenta vers??o")
    void caso24_alteraImagemP1_deveMudarVersaoModeloECodigoContextoP1() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4, 5 - Alteramos a imagem da P1 e a?? atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);
        final PerguntaModeloChecklistVisualizacao p1 = modeloBuscado.getPerguntas().get(0);
        perguntas.set(
                0,
                new PerguntaModeloChecklistEdicaoAtualiza(
                        p1.getCodigo(),
                        p1.getCodigoContexto(),
                        p1.getDescricao(),
                        2L,
                        p1.getOrdemExibicao(),
                        p1.isSingleChoice(),
                        p1.getAnexoMidiaRespostaOk(),
                        toAlternativaAtualiza(p1.getAlternativas())));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por ??ltimo, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Vers??o tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 8, 9 - C??digo contexto mudou.
            ensureAllAttributesEqual(p1Antes, p1Depois, 4, false, false);

            // 10 - Est?? com outra imagem.
            assertThat(p1Depois.getCodImagem()).isEqualTo(2L);
            assertThat(modeloBuscado.getPerguntas().get(0).getCodImagem()).isNotEqualTo(2L);
        }
    }

    @Test
    @DisplayName("Altera anexo m??dia da pergunta P1, muda vers??o, mant??m contexto")
    void caso25_alteraAnexoMidiaP1_deveMudarVersaoModeloEManterCodigoContexto() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao original = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(original).isNotNull();

        // 4, 5 - Alteramos P1 para anexo de m??dias liberado e a?? atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(original);
        final PerguntaModeloChecklistEdicao p1 = perguntas.get(0);
        perguntas.set(0,
                new PerguntaModeloChecklistEdicaoAtualiza(
                        p1.getCodigo(),
                        p1.getCodigoContexto(),
                        p1.getDescricao(),
                        p1.getCodImagem(),
                        p1.getOrdemExibicao(),
                        p1.isSingleChoice(),
                        AnexoMidiaChecklistEnum.OBRIGATORIO,
                        p1.getAlternativas()
                                .stream()
                                .map(a -> new AlternativaModeloChecklistEdicaoAtualiza(
                                        a.getCodigo(),
                                        a.getCodigoContexto(),
                                        a.getDescricao(),
                                        a.getPrioridade(),
                                        a.isTipoOutros(),
                                        a.getOrdemExibicao(),
                                        a.isDeveAbrirOrdemServico(),
                                        a.getAnexoMidia(),
                                        "teste"))
                                .collect(Collectors.toList())));
        final List<Long> cargos = getCodigosCargos(original);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(original);
        final ModeloChecklistEdicao editado = createModeloEdicao(original, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                original.getCodUnidade(),
                original.getCodModelo(),
                editado,
                token);

        // 6 - Por ??ltimo, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Vers??o tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistVisualizacao p1Antes = original.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // C??digo contexto da pergunta se manteve.
            assertThat(p1Depois.getCodigoContexto()).isEqualTo(p1Antes.getCodigoContexto());

            // 'Farol' est?? com captura de fotos diferente.
            assertThat(p1Depois.getDescricao()).isEqualTo("Farol");
            assertThat(p1Antes.getAnexoMidiaRespostaOk()).isEqualTo(AnexoMidiaChecklistEnum.BLOQUEADO);
            assertThat(p1Depois.getAnexoMidiaRespostaOk()).isEqualTo(AnexoMidiaChecklistEnum.OBRIGATORIO);
        }
        {
            // P2.
            final PerguntaModeloChecklistVisualizacao p2Antes = original.getPerguntas().get(1);
            final PerguntaModeloChecklistVisualizacao p2Depois = buscado.getPerguntas().get(1);

            // C??digo contexto da pergunta se manteve.
            assertThat(p2Depois.getCodigoContexto()).isEqualTo(p2Antes.getCodigoContexto());

            // 'Cinto de seguran??a' est?? com anexo de m??dia igual.
            assertThat(p2Depois.getDescricao()).isEqualTo("Cinto de seguran??a");
            assertThat(p2Depois.getAnexoMidiaRespostaOk()).isEqualTo(p2Antes.getAnexoMidiaRespostaOk());
        }
    }

    @Test
    @DisplayName("Altera anexo de m??dia da alternativa A1, muda vers??o, mant??m contexto")
    void caso26_alteraAnexoMidiaA1_deveMudarVersaoModeloEManterCodigoContexto() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao original = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(original).isNotNull();

        // 4, 5 - Alteramos A1 para anexo de m??dia liberado e a?? atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(original);
        final PerguntaModeloChecklistEdicao p1 = perguntas.get(0);
        final AlternativaModeloChecklistEdicao a1 = (AlternativaModeloChecklistEdicao) p1.getAlternativas().get(0);
        p1.getAlternativas().set(0, new AlternativaModeloChecklistEdicaoAtualiza(
                a1.getCodigo(),
                a1.getCodigoContexto(),
                a1.getDescricao(),
                a1.getPrioridade(),
                a1.isTipoOutros(),
                a1.getOrdemExibicao(),
                a1.isDeveAbrirOrdemServico(),
                AnexoMidiaChecklistEnum.OBRIGATORIO,
                "teste"));
        final List<Long> cargos = getCodigosCargos(original);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(original);
        final ModeloChecklistEdicao editado = createModeloEdicao(original, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                original.getCodUnidade(),
                original.getCodModelo(),
                editado,
                token);

        // 6 - Por ??ltimo, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 7 - Vers??o tem que ter aumentado.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistVisualizacao p1Antes = original.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // C??digo contexto da pergunta se manteve.
            assertThat(p1Depois.getCodigoContexto()).isEqualTo(p1Antes.getCodigoContexto());

            // 'Fora de foco' est?? com anexo de m??dia diferente.
            final AlternativaModeloChecklist a1Antes = p1Antes.getAlternativas().get(0);
            final AlternativaModeloChecklist a1Depois = p1Depois.getAlternativas().get(0);
            assertThat(a1Antes.getDescricao()).isEqualTo("Fora de foco");
            assertThat(a1Antes.getDescricao()).isEqualTo(a1Depois.getDescricao());
            assertThat(a1Antes.getAnexoMidia()).isEqualTo(AnexoMidiaChecklistEnum.BLOQUEADO);
            assertThat(a1Depois.getAnexoMidia()).isEqualTo(AnexoMidiaChecklistEnum.OBRIGATORIO);
        }
    }

    @Test
    @DisplayName("Adiciona alternativa na P1, aumenta vers??o e muda contexto")
    void caso27_adicionaAlternativaP1_deveMudarVersaoModeloCodigoContextoP1Igual() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4 - Adiciona nova alternativa na P1, que j?? tem 4.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);

        // A tipo_outros tem que ser realocada para a ??ltima posi????o.
        final AlternativaModeloChecklist outros = perguntas.get(0).getAlternativas().remove(3);
        perguntas.get(0).getAlternativas().add(
                new AlternativaModeloChecklistEdicaoInsere(
                        "Piscando sozinho",
                        PrioridadeAlternativa.BAIXA,
                        false,
                        4,
                        true,
                        AnexoMidiaChecklistEnum.BLOQUEADO,
                        "TESTE"));
        perguntas.get(0).getAlternativas().add(
                new AlternativaModeloChecklistEdicaoAtualiza(
                        outros.getCodigo(),
                        outros.getCodigoContexto(),
                        "Outros",
                        outros.getPrioridade(),
                        true,
                        5,
                        outros.isDeveAbrirOrdemServico(),
                        outros.getAnexoMidia(),
                        "teste"));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 5 - Por ??ltimo, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 6 - Garante uma vers??o maior.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaModeloChecklistEdicao p1Antes = editado.getPerguntas().get(0);
            final PerguntaModeloChecklistVisualizacao p1Depois = buscado.getPerguntas().get(0);

            // 7 - Garante c??digo contexto igual e vari??vel diferente.
            ensureAllAttributesEqual(p1Antes, p1Depois, 5, true, false);

            // 8 - Garante que a alternativa est?? presente.
            final AlternativaModeloChecklist novaAlt = p1Depois.getAlternativas().get(3);
            assertThat(novaAlt.getDescricao()).isEqualTo("Piscando sozinho");
            assertThat(novaAlt.getPrioridade()).isEqualTo(PrioridadeAlternativa.BAIXA);
            assertThat(novaAlt.isTipoOutros()).isFalse();
            assertThat(novaAlt.getOrdemExibicao()).isEqualTo(4);
            assertThat(novaAlt.isDeveAbrirOrdemServico()).isTrue();

            assertThat(p1Depois.getDescricao()).isEqualTo("Farol");
        }
    }

    @Test
    @DisplayName("Adiciona pergunta P3, aumenta vers??o e muda contexto")
    void caso28_adicionaPerguntaP3_deveMudarVersaoModelo() {
        // 1, 2 - Insere o modelo base.
        final ResultInsertModeloChecklist result = insertModeloBase();

        // 3 - Ent??o buscamos o modelo inserido.
        // N??s n??o garantimos que a busca ?? igual ao inserido pois isso ?? feito nos testes de insert.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado).isNotNull();

        // 4 - Adiciona nova pergunta.
        final List<PerguntaModeloChecklistEdicao> perguntas = toPerguntasEdicao(modeloBuscado);

        // A tipo_outros tem que ser realocada para a ??ltima posi????o.
        final List<AlternativaModeloChecklistEdicao> alternativas = new ArrayList<>();
        alternativas.add(new AlternativaModeloChecklistEdicaoInsere(
                "Outros",
                PrioridadeAlternativa.BAIXA,
                true,
                1,
                false,
                AnexoMidiaChecklistEnum.BLOQUEADO,
                "TESTE"));
        perguntas.add(new PerguntaModeloChecklistEdicaoInsere(
                "P3",
                null,
                3,
                true,
                AnexoMidiaChecklistEnum.OPCIONAL,
                alternativas));

        final List<Long> cargos = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculo = getCodigosTiposVeiculos(modeloBuscado);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntas, cargos, tiposVeiculo);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 5 - Por ??ltimo, buscamos novamente o modelo e comparamos.
        final ModeloChecklistVisualizacao buscado = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        // 6 - Garante uma vers??o maior.
        assertThat(editado.getCodVersaoModelo()).isLessThan(buscado.getCodVersaoModelo());
        assertThat(buscado.getPerguntas()).hasSize(3);
        {
            // P3.
            final PerguntaModeloChecklistVisualizacao p3Depois = buscado.getPerguntas().get(2);

            assertThat(p3Depois.getDescricao()).isEqualTo("P3");
            assertThat(p3Depois.getAnexoMidiaRespostaOk()).isEqualTo(AnexoMidiaChecklistEnum.OPCIONAL);
            assertThat(p3Depois.getCodImagem()).isNull();
            assertThat(p3Depois.getOrdemExibicao()).isEqualTo(3);
        }
    }

    @Test
    @DisplayName("Insere modelo base que possui codigo auxiliar e o modifica." +
            " Valida se vers??es n??o mudaram.")
    void caso29_insereModeloComCodAuxiliarEAltera() {
        // Insere o modelo base
        final ResultInsertModeloChecklist result = insertModeloBase();

        // Busca o modelo inserido
        final ModeloChecklistVisualizacao modeloInicial = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloInicial).isNotNull();
        assertThat(modeloInicial.getPerguntas().get(0).getAlternativas().get(0).getCodAuxiliar()).isNotNull();
        assertThat(modeloInicial.getPerguntas().get(0).getAlternativas().get(1).getCodAuxiliar()).isNotNull();
        assertThat(modeloInicial.getPerguntas().get(0).getAlternativas().get(2).getCodAuxiliar()).isNotNull();
        assertThat(modeloInicial.getPerguntas().get(0).getAlternativas().get(3).getCodAuxiliar()).isNotNull();

        final List<PerguntaModeloChecklistEdicao> perguntasModeloInicial = toPerguntasEdicao(modeloInicial);

        // Muda o codigo auxiliar de todas alternativas da pergunta 0
        final List<AlternativaModeloChecklistEdicao> alternativasModeloInicial =
                toAlternativaAtualizaWithOtherCodAuxiliar(
                        perguntasModeloInicial.get(0).getAlternativas(),
                        "teste");
        for (int i = 0; i < perguntasModeloInicial.get(0).getAlternativas().size(); i++) {
            perguntasModeloInicial.get(0).getAlternativas().set(i, alternativasModeloInicial.get(i));
        }

        // Monta o objeto de edi????o buscando os atributos necess??rios
        final List<Long> cargosModeloInicial = getCodigosCargos(modeloInicial);
        final List<Long> tiposVeiculoModeloInicial = getCodigosTiposVeiculos(modeloInicial);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloInicial,
                perguntasModeloInicial,
                cargosModeloInicial,
                tiposVeiculoModeloInicial);
        service.updateModeloChecklist(
                modeloInicial.getCodUnidade(),
                modeloInicial.getCodModelo(),
                editado,
                token);
        final ModeloChecklistVisualizacao modeloFinal = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());

        for (int i = 0; i < modeloFinal.getPerguntas().get(0).getAlternativas().size(); i++) {
            Assertions.assertThat(modeloFinal
                    .getPerguntas()
                    .get(0)
                    .getAlternativas()
                    .get(i).getCodAuxiliar()).isNotNull();
            Assertions.assertThat(modeloInicial
                    .getPerguntas()
                    .get(0)
                    .getAlternativas()
                    .get(i)
                    .getCodAuxiliar())
                    .isNotEqualToIgnoringCase(modeloFinal
                            .getPerguntas()
                            .get(0)
                            .getAlternativas()
                            .get(i)
                            .getCodAuxiliar());
            Assertions.assertThat(modeloInicial
                    .getPerguntas()
                    .get(0)
                    .getAlternativas()
                    .get(i)
                    .getCodigoContexto())
                    .isEqualTo(modeloFinal
                            .getPerguntas()
                            .get(0)
                            .getAlternativas()
                            .get(i)
                            .getCodigoContexto());
            Assertions.assertThat(modeloInicial
                    .getPerguntas()
                    .get(0)
                    .getAlternativas()
                    .get(i)
                    .getCodigo())
                    .isEqualTo(modeloFinal
                            .getPerguntas()
                            .get(0)
                            .getAlternativas()
                            .get(i)
                            .getCodigo());
        }
        Assertions.assertThat(modeloInicial.getPerguntas().get(0).getCodigo())
                .isEqualTo(modeloFinal.getPerguntas().get(0).getCodigo());
        Assertions.assertThat(modeloInicial.getPerguntas().get(0).getCodigoContexto())
                .isEqualTo(modeloFinal.getPerguntas().get(0).getCodigoContexto());
        Assertions.assertThat(modeloInicial.getCodVersaoModelo())
                .isEqualTo(modeloFinal.getCodVersaoModelo());
    }

    @Test
    @DisplayName("Insere modelo base que possui codigo auxiliar e o remove, inserindo null." +
            " Valida se vers??es n??o mudaram.")
    void caso30_insereModeloComCodAuxiliarEAlteraParaNulo() {
        // Insere o modelo base
        final ResultInsertModeloChecklist result = insertModeloBase();

        // Busca o modelo inserido
        final ModeloChecklistVisualizacao modeloInicial = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());
        assertThat(modeloInicial).isNotNull();
        assertThat(modeloInicial.getPerguntas().get(0).getAlternativas().get(0).getCodAuxiliar()).isNotNull();
        assertThat(modeloInicial.getPerguntas().get(0).getAlternativas().get(1).getCodAuxiliar()).isNotNull();
        assertThat(modeloInicial.getPerguntas().get(0).getAlternativas().get(2).getCodAuxiliar()).isNotNull();
        assertThat(modeloInicial.getPerguntas().get(0).getAlternativas().get(3).getCodAuxiliar()).isNotNull();

        final List<PerguntaModeloChecklistEdicao> perguntasModeloInicial = toPerguntasEdicao(modeloInicial);

        // Muda o codigo auxiliar de todas alternativas da pergunta 0
        final List<AlternativaModeloChecklistEdicao> alternativasModeloInicial =
                toAlternativaAtualizaWithOtherCodAuxiliar(
                        perguntasModeloInicial.get(0).getAlternativas(),
                        null);
        for (int i = 0; i < perguntasModeloInicial.get(0).getAlternativas().size(); i++) {
            perguntasModeloInicial.get(0).getAlternativas().set(i, alternativasModeloInicial.get(i));
        }

        // Monta o objeto de edi????o buscando os atributos necess??rios
        final List<Long> cargosModeloInicial = getCodigosCargos(modeloInicial);
        final List<Long> tiposVeiculoModeloInicial = getCodigosTiposVeiculos(modeloInicial);
        final ModeloChecklistEdicao editado = createModeloEdicao(modeloInicial,
                perguntasModeloInicial,
                cargosModeloInicial,
                tiposVeiculoModeloInicial);
        service.updateModeloChecklist(
                modeloInicial.getCodUnidade(),
                modeloInicial.getCodModelo(),
                editado,
                token);
        final ModeloChecklistVisualizacao modeloFinal = service.getModeloChecklist(
                COD_UNIDADE,
                result.getCodModeloChecklistInserido());

        for (int i = 0; i < modeloFinal.getPerguntas().get(0).getAlternativas().size(); i++) {
            Assertions.assertThat(modeloFinal
                    .getPerguntas()
                    .get(0)
                    .getAlternativas()
                    .get(i).getCodAuxiliar()).isNull();
        }
        Assertions.assertThat(modeloInicial.getPerguntas().get(0).getCodigo())
                .isEqualTo(modeloFinal.getPerguntas().get(0).getCodigo());
        Assertions.assertThat(modeloInicial.getPerguntas().get(0).getCodigoContexto())
                .isEqualTo(modeloFinal.getPerguntas().get(0).getCodigoContexto());
        Assertions.assertThat(modeloInicial.getCodVersaoModelo())
                .isEqualTo(modeloFinal.getCodVersaoModelo());
    }

    @NotNull
    private List<AlternativaModeloChecklistEdicao> toAlternativaAtualizaWithOtherCodAuxiliar(
            @NotNull final List<AlternativaModeloChecklist> alternativas,
            @Nullable final String newCodAuxiliar) {
        final List<AlternativaModeloChecklistEdicao> novas = new ArrayList<>(alternativas.size());
        alternativas.forEach(a -> novas.add(
                new AlternativaModeloChecklistEdicaoAtualiza(
                        a.getCodigo(),
                        a.getCodigoContexto(),
                        a.getDescricao(),
                        a.getPrioridade(),
                        a.isTipoOutros(),
                        a.getOrdemExibicao(),
                        a.isDeveAbrirOrdemServico(),
                        a.getAnexoMidia(),
                        newCodAuxiliar)));
        return novas;
    }

    @NotNull
    private List<AlternativaModeloChecklistEdicao> toAlternativaAtualiza(
            @NotNull final List<AlternativaModeloChecklist> alternativas) {
        // For??a o cast para garantir que ?? do tipo Visualiza????o.
        @SuppressWarnings("unchecked") final List<AlternativaModeloChecklistVisualizacao> visu =
                (List<AlternativaModeloChecklistVisualizacao>) (List<?>) alternativas;

        final List<AlternativaModeloChecklistEdicao> novas = new ArrayList<>(alternativas.size());
        visu.forEach(a -> novas.add(
                new AlternativaModeloChecklistEdicaoAtualiza(
                        a.getCodigo(),
                        a.getCodigoContexto(),
                        a.getDescricao(),
                        a.getPrioridade(),
                        a.isTipoOutros(),
                        a.getOrdemExibicao(),
                        a.isDeveAbrirOrdemServico(),
                        a.getAnexoMidia(),
                        a.getCodAuxiliar())));
        return novas;
    }

    // TODO: Talvez fa??a mais sentido (KISS) remover esse m??todo. Usado apenas em 3 lugares mascara que usamos sempre
    //       uma AlternativaModeloChecklistEdicaoAtualiza.
    @NotNull
    private PerguntaModeloChecklistEdicao copyFrom(@NotNull final PerguntaModeloChecklistVisualizacao p,
                                                   @NotNull final String descricaoAtual,
                                                   @NotNull final String novaDescricao) {
        assertThat(p.getDescricao()).isEqualTo(descricaoAtual);
        final List<AlternativaModeloChecklistEdicao> alternativas = new ArrayList<>();
        for (final AlternativaModeloChecklist a : p.getAlternativas()) {
            alternativas.add(new AlternativaModeloChecklistEdicaoAtualiza(
                    a.getCodigo(),
                    a.getCodigoContexto(),
                    a.getDescricao(),
                    a.getPrioridade(),
                    a.isTipoOutros(),
                    a.getOrdemExibicao(),
                    a.isDeveAbrirOrdemServico(),
                    a.getAnexoMidia(),
                    a.getCodAuxiliar()));
        }
        return new PerguntaModeloChecklistEdicaoAtualiza(
                p.getCodigo(),
                p.getCodigoContexto(),
                novaDescricao,
                p.getCodImagem(),
                p.getOrdemExibicao(),
                p.isSingleChoice(),
                p.getAnexoMidiaRespostaOk(),
                alternativas);
    }

    @NotNull
    private AlternativaModeloChecklistEdicao copyFrom(@NotNull final AlternativaModeloChecklistVisualizacao a,
                                                      @NotNull final String descricaoAtual,
                                                      @NotNull final String novaDescricao) {
        assertThat(a.getDescricao()).isEqualTo(descricaoAtual);
        return new AlternativaModeloChecklistEdicaoAtualiza(
                a.getCodigo(),
                a.getCodigoContexto(),
                novaDescricao,
                a.getPrioridade(),
                a.isTipoOutros(),
                a.getOrdemExibicao(),
                a.isDeveAbrirOrdemServico(),
                a.getAnexoMidia(),
                a.getCodAuxiliar());
    }

    @SuppressWarnings("SameParameterValue")
    private void ensureAllAttributesEqual(@NotNull final PerguntaModeloChecklist antes,
                                          @NotNull final PerguntaModeloChecklist depois,
                                          final int qtdAlternativas,
                                          final boolean mesmoCodigoContexto,
                                          final boolean mesmoCodigoVariavel) {
        assertThat(antes).isNotSameInstanceAs(depois);
        if (mesmoCodigoVariavel) {
            assertThat(antes.getCodigo()).isEqualTo(depois.getCodigo());
        } else {
            assertThat(antes.getCodigo()).isLessThan(depois.getCodigo());
        }
        if (mesmoCodigoContexto) {
            assertThat(antes.getCodigoContexto()).isEqualTo(depois.getCodigoContexto());
        } else {
            assertThat(antes.getCodigoContexto()).isLessThan(depois.getCodigoContexto());
        }
        assertThat(antes.getDescricao()).isEqualTo(depois.getDescricao());
        assertThat(antes.getCodImagem()).isEqualTo(depois.getCodImagem());
        assertThat(antes.getOrdemExibicao()).isEqualTo(depois.getOrdemExibicao());
        assertThat(antes.isSingleChoice()).isEqualTo(depois.isSingleChoice());
        assertThat(antes.getAnexoMidiaRespostaOk()).isEqualTo(depois.getAnexoMidiaRespostaOk());
        assertThat(antes.getAlternativas()).hasSize(qtdAlternativas);
        assertThat(depois.getAlternativas()).hasSize(qtdAlternativas);
    }

    private void ensureAllAttributesEqual(@NotNull final PerguntaModeloChecklist antes,
                                          @NotNull final PerguntaModeloChecklist depois,
                                          final int qtdAlternativas) {
        assertThat(antes).isNotSameInstanceAs(depois);
        assertThat(antes.getCodigo()).isEqualTo(depois.getCodigo());
        assertThat(antes.getCodigoContexto()).isEqualTo(depois.getCodigoContexto());
        assertThat(antes.getDescricao()).isEqualTo(depois.getDescricao());
        assertThat(antes.getCodImagem()).isEqualTo(depois.getCodImagem());
        assertThat(antes.getOrdemExibicao()).isEqualTo(depois.getOrdemExibicao());
        assertThat(antes.isSingleChoice()).isEqualTo(depois.isSingleChoice());
        assertThat(antes.getAnexoMidiaRespostaOk()).isEqualTo(depois.getAnexoMidiaRespostaOk());
        assertThat(antes.getAlternativas()).hasSize(qtdAlternativas);
        assertThat(depois.getAlternativas()).hasSize(qtdAlternativas);
    }

    private void ensureAllAttributesEqual(@NotNull final AlternativaModeloChecklist antes,
                                          @NotNull final AlternativaModeloChecklist depois) {
        assertThat(antes).isNotSameInstanceAs(depois);
        assertThat(antes.getCodigo()).isEqualTo(depois.getCodigo());
        assertThat(antes.getCodigoContexto()).isEqualTo(depois.getCodigoContexto());
        assertThat(antes.getDescricao()).isEqualTo(depois.getDescricao());
        assertThat(antes.getPrioridade()).isEqualTo(depois.getPrioridade());
        assertThat(antes.isTipoOutros()).isEqualTo(depois.isTipoOutros());
        assertThat(antes.getOrdemExibicao()).isEqualTo(depois.getOrdemExibicao());
        assertThat(antes.isDeveAbrirOrdemServico()).isEqualTo(depois.isDeveAbrirOrdemServico());
        assertThat(antes.getAnexoMidia()).isEqualTo(depois.getAnexoMidia());
    }

    // Este m??todo torna opcional a valida????o dos c??digos de alternativas.
    @SuppressWarnings("SameParameterValue")
    private void ensureAllAttributesEqual(@NotNull final AlternativaModeloChecklist antes,
                                          @NotNull final AlternativaModeloChecklist depois,
                                          final boolean mesmoCodigoContexto,
                                          final boolean mesmoCodigoVariavel) {
        assertThat(antes).isNotSameInstanceAs(depois);

        if (mesmoCodigoVariavel) {
            assertThat(antes.getCodigo()).isEqualTo(depois.getCodigo());
        }

        if (mesmoCodigoContexto) {
            assertThat(antes.getCodigoContexto()).isEqualTo(depois.getCodigoContexto());
        }

        assertThat(antes.getDescricao()).isEqualTo(depois.getDescricao());
        assertThat(antes.getPrioridade()).isEqualTo(depois.getPrioridade());
        assertThat(antes.isTipoOutros()).isEqualTo(depois.isTipoOutros());
        assertThat(antes.getOrdemExibicao()).isEqualTo(depois.getOrdemExibicao());
        assertThat(antes.isDeveAbrirOrdemServico()).isEqualTo(depois.isDeveAbrirOrdemServico());
        assertThat(antes.getAnexoMidia()).isEqualTo(depois.getAnexoMidia());
    }

    @NotNull
    private ResultInsertModeloChecklist insertModeloBase() {
        return service.insertModeloChecklist(new ModeloChecklistInsercao(
                        UUID.randomUUID().toString(),
                        BASE.getCodUnidade(),
                        BASE.getTiposVeiculoLiberados(),
                        BASE.getCargosLiberados(),
                        BASE.getPerguntas()),
                token);
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

    @SuppressWarnings("SameParameterValue")
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