package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistAlternativaResposta;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistResposta;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 16/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ChecklistOfflineConverter {

    private ChecklistOfflineConverter() {
        throw new IllegalStateException(ChecklistOfflineConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static AlternativaModeloChecklistOffline createAlternativaModeloChecklistOffline(
            @NotNull final ResultSet rSet) throws SQLException {
        return new AlternativaModeloChecklistOffline(
                rSet.getLong("COD_ALTERNATIVA"),
                rSet.getLong("COD_CONTEXTO_ALTERNATIVA"),
                rSet.getString("DESCRICAO_ALTERNATIVA"),
                rSet.getBoolean("TIPO_OUTROS"),
                rSet.getInt("ALTERNATIVA_ORDEM_EXIBICAO"),
                PrioridadeAlternativa.fromString(rSet.getString("PRIORIDADE_ALTERNATIVA")),
                AnexoMidiaChecklistEnum.fromString(rSet.getString("ANEXO_MIDIA")));
    }

    @NotNull
    public static PerguntaModeloChecklistOffline createPerguntaModeloChecklistOffline(
            @NotNull final ResultSet rSet,
            @NotNull final List<AlternativaModeloChecklistOffline> alternativas) throws SQLException {
        return new PerguntaModeloChecklistOffline(
                rSet.getLong("COD_PERGUNTA"),
                rSet.getLong("COD_CONTEXTO_PERGUNTA"),
                rSet.getString("DESCRICAO_PERGUNTA"),
                rSet.getLong("COD_IMAGEM"),
                rSet.getString("URL_IMAGEM"),
                rSet.getInt("PERGUNTA_ORDEM_EXIBICAO"),
                rSet.getBoolean("SINGLE_CHOICE"),
                AnexoMidiaChecklistEnum.fromString(rSet.getString("ANEXO_MIDIA_RESPOSTA_OK")),
                alternativas);
    }

    @NotNull
    public static ModeloChecklistOffline createModeloChecklistOffline(
            @NotNull final Long codUnidadeModeloChecklist,
            @NotNull final Long codModeloCheklist,
            @NotNull final Long codVersaoModeloChecklist,
            @NotNull final String nomeModeloChecklist,
            @NotNull final List<CargoChecklistOffline> cargosLiberados,
            @NotNull final List<TipoVeiculoChecklistOffline> tiposVeiculosLiberados,
            @NotNull final List<PerguntaModeloChecklistOffline> perguntas) {
        return new ModeloChecklistOffline(
                codModeloCheklist,
                codVersaoModeloChecklist,
                nomeModeloChecklist,
                codUnidadeModeloChecklist,
                cargosLiberados,
                tiposVeiculosLiberados,
                perguntas);
    }

    @NotNull
    public static CargoChecklistOffline createCargoChecklistOffline(
            @NotNull final ResultSet rSet) throws SQLException {
        return new CargoChecklistOffline(rSet.getLong("COD_CARGO"));
    }

    @NotNull
    public static TipoVeiculoChecklistOffline createTipoVeiculoChecklistOffline(
            @NotNull final ResultSet rSet) throws SQLException {
        return new TipoVeiculoChecklistOffline(rSet.getLong("COD_TIPO_VEICULO"));
    }

    @NotNull
    public static ColaboradorChecklistOffline createColaboradorChecklistOffline(
            @NotNull final ResultSet rSet) throws SQLException {
        return new ColaboradorChecklistOffline(
                rSet.getLong("COD_UNIDADE_COLABORADOR"),
                rSet.getLong("COD_COLABORADOR"),
                rSet.getString("NOME_COLABORADOR"),
                rSet.getString("CPF_COLABORADOR"),
                rSet.getObject("DATA_NASCIMENTO", LocalDate.class),
                rSet.getLong("COD_CARGO_COLABORADOR"),
                rSet.getInt("COD_PERMISSAO_COLABORADOR"));
    }

    @NotNull
    public static VeiculoChecklistOffline createVeiculoChecklistOffline(
            @NotNull final ResultSet rSet) throws SQLException {
        return new VeiculoChecklistOffline(
                rSet.getLong("COD_VEICULO"),
                rSet.getString("PLACA_VEICULO"),
                rSet.getLong("COD_TIPO_VEICULO"),
                rSet.getLong("KM_ATUAL_VEICULO"));
    }

    @NotNull
    public static UnidadeChecklistOffline createEmpresaChecklistOffline(
            @NotNull final ResultSet rSet) throws SQLException {
        return new UnidadeChecklistOffline(
                rSet.getLong("COD_EMPRESA"),
                rSet.getString("NOME_EMPRESA"),
                rSet.getLong("COD_REGIONAL"),
                rSet.getString("NOME_REGIONAL"),
                rSet.getLong("COD_UNIDADE"),
                rSet.getString("NOME_UNIDADE"));
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @NotNull
    private static List<PerguntaRespostaChecklist> toPerguntasRespostas(
            @NotNull final List<ChecklistResposta> respostas) {
        final List<PerguntaRespostaChecklist> perguntasRespostas = new ArrayList<>();
        for (int i = 0; i < respostas.size(); i++) {
            perguntasRespostas.add(toPerguntaResposta(respostas.get(i)));
        }
        return perguntasRespostas;
    }

    @NotNull
    private static PerguntaRespostaChecklist toPerguntaResposta(@NotNull final ChecklistResposta checklistResposta) {
        final PerguntaRespostaChecklist perguntaResposta = new PerguntaRespostaChecklist();
        perguntaResposta.setCodigo(checklistResposta.getCodPergunta());
        perguntaResposta.setAlternativasResposta(toAlternativasChecklist(checklistResposta.getAlternativasRespostas()));
        return perguntaResposta;
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @NotNull
    private static List<AlternativaChecklist> toAlternativasChecklist(
            @NotNull final List<ChecklistAlternativaResposta> alternativasResposta) {
        final List<AlternativaChecklist> alternativas = new ArrayList<>();
        for (int i = 0; i < alternativasResposta.size(); i++) {
            alternativas.add(toAlternativaChecklist(alternativasResposta.get(i)));
        }
        return alternativas;
    }

    @NotNull
    private static AlternativaChecklist toAlternativaChecklist(
            @NotNull final ChecklistAlternativaResposta checklistAlternativaResposta) {
        final AlternativaChecklist alternativa = new AlternativaChecklist();
        alternativa.setCodigo(checklistAlternativaResposta.getCodAlternativa());
        alternativa.setSelected(checklistAlternativaResposta.isAlternativaSelecionada());
        return alternativa;
    }
}
