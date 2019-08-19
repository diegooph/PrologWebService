package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.commons.imagens.ImagemProLog;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.VeiculoChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.AlternativaModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.PerguntaModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Created on 2019-08-18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistModeloConverter {

    public ChecklistModeloConverter() {
        throw new IllegalStateException(ChecklistModeloConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static VeiculoChecklistSelecao createVeiculoChecklistSelecao(
            @NotNull final ResultSet rSet) throws Throwable {
        return new VeiculoChecklistSelecao(
                rSet.getLong("COD_VEICULO"),
                rSet.getString("PLACA_VEICULO"),
                rSet.getLong("KM_ATUAL_VEICULO"));
    }

    @NotNull
    public static ModeloChecklistSelecao createModeloChecklistSelecao(
            @NotNull final ResultSet rSet,
            @NotNull final List<VeiculoChecklistSelecao> veiculosSelecao) throws Throwable {
        return new ModeloChecklistSelecao(
                rSet.getLong("COD_MODELO"),
                rSet.getLong("COD_VERSAO_ATUAL_MODELO"),
                rSet.getLong("COD_UNIDADE_MODELO"),
                rSet.getString("NOME_MODELO"),
                veiculosSelecao);
    }

    @NotNull
    public static PerguntaModeloChecklistVisualizacao createPerguntaModeloChecklist(
            @NotNull final ResultSet rSet) throws SQLException {
        final PerguntaModeloChecklistVisualizacao pergunta = new PerguntaModeloChecklistVisualizacao();
        pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
        pergunta.setOrdemExibicao(rSet.getInt("ORDEM_PERGUNTA"));
        pergunta.setDescricao(rSet.getString("PERGUNTA"));
        pergunta.setSingleChoice(rSet.getBoolean("SINGLE_CHOICE"));
        pergunta.setCodImagem(rSet.getLong("COD_IMAGEM"));
        pergunta.setUrlImagem(rSet.getString("URL_IMAGEM"));
        return pergunta;
    }

    @NotNull
    public static AlternativaModeloChecklistVisualizacao createAlternativaModeloChecklist(
            @NotNull final ResultSet rSet) throws SQLException {
        final AlternativaModeloChecklistVisualizacao alternativa = new AlternativaModeloChecklistVisualizacao();
        alternativa.setCodigo(rSet.getLong("COD_ALTERNATIVA"));
        alternativa.setDescricao(rSet.getString("ALTERNATIVA"));
        alternativa.setPrioridade(PrioridadeAlternativa.fromString(rSet.getString("PRIORIDADE")));
        alternativa.setTipoOutros(rSet.getBoolean("ALTERNATIVA_TIPO_OUTROS"));
        alternativa.setDeveAbrirOrdemServico(rSet.getBoolean("DEVE_ABRIR_ORDEM_SERVICO"));
        return alternativa;
    }

    @NotNull
    @Deprecated
    public static PerguntaRespostaChecklist createPergunta(@NotNull final ResultSet rSet) throws SQLException {
        final PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
        pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
        pergunta.setOrdemExibicao(rSet.getInt("ORDEM_PERGUNTA"));
        pergunta.setPergunta(rSet.getString("PERGUNTA"));
        pergunta.setSingleChoice(rSet.getBoolean("SINGLE_CHOICE"));
        pergunta.setCodImagem(rSet.getLong("COD_IMAGEM"));
        pergunta.setUrl(rSet.getString("URL_IMAGEM"));
        return pergunta;
    }

    @NotNull
    @Deprecated
    public static AlternativaChecklist createAlternativa(@NotNull final ResultSet rSet) throws SQLException {
        final AlternativaChecklist alternativa = new AlternativaChecklist();
        alternativa.codigo = rSet.getLong("COD_ALTERNATIVA");
        alternativa.alternativa = rSet.getString("ALTERNATIVA");
        alternativa.prioridade = PrioridadeAlternativa.fromString(rSet.getString("PRIORIDADE"));
        if (rSet.getBoolean("ALTERNATIVA_TIPO_OUTROS")) {
            alternativa.tipo = AlternativaChecklist.TIPO_OUTROS;
        }
        return alternativa;
    }

    @NotNull
    public static ImagemProLog createImagemProLog(@NotNull final ResultSet rSet) throws Throwable {
        final ImagemProLog imagemProLog = new ImagemProLog();
        imagemProLog.setCodImagem(rSet.getLong("COD_IMAGEM"));
        imagemProLog.setUrlImagem(rSet.getString("URL_IMAGEM"));
        imagemProLog.setDataHoraCadastro(rSet.getObject("DATA_HORA_CADASTRO", LocalDateTime.class));
        imagemProLog.setStatusImagem(rSet.getBoolean("STATUS_ATIVO"));
        return imagemProLog;
    }

    @NotNull
    public static TipoVeiculo createTipoVeiculo(@NotNull final ResultSet rSet) throws Throwable {
        final TipoVeiculo tipo = new TipoVeiculo();
        tipo.setCodigo(rSet.getLong("CODIGO"));
        tipo.setNome(rSet.getString("TIPO_VEICULO"));
        return tipo;
    }

    @NotNull
    public static ModeloChecklistListagem createModeloChecklistListagem(
            @NotNull final ResultSet rSet,
            @NotNull final Long codModeloChecklistAtual,
            @NotNull final Set<String> setCargos,
            @NotNull final Set<String> setTiposVeiculos) throws SQLException {
        final ModeloChecklistListagem modeloChecklist = new ModeloChecklistListagem();
        modeloChecklist.setCodigo(codModeloChecklistAtual);
        modeloChecklist.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        modeloChecklist.setNome(rSet.getString("MODELO"));
        modeloChecklist.setCargosLiberados(setCargos);
        modeloChecklist.setTiposVeiculoLiberados(setTiposVeiculos);
        modeloChecklist.setQtdPerguntas(rSet.getInt("TOTAL_PERGUNTAS"));
        modeloChecklist.setAtivo(rSet.getBoolean("STATUS_ATIVO"));
        return modeloChecklist;
    }
}