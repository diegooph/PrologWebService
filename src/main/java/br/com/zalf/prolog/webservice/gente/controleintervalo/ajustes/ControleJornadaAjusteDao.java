package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.Filtros;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicaoInicioFim;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAtivacaoInativacao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteEdicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.ConsolidadoMarcacoesDia;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoAjusteHistoricoExibicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoColaboradorAjuste;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoInconsistenciaExibicao;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ControleJornadaAjusteDao {

    /**
     * Método para vincular uma marcação a um {@link Colaborador}. Isso ocorre quando é criado
     * uma marcação de fim para um início já existente, ou a criação de uma marcação de início
     * para uma marcação de fim avulsa.
     * Para mais informações sobre os dados de uma adição consulte {@link MarcacaoAjusteAdicao}.
     *
     * @param token          Identificador do usuário que realizou a edição.
     * @param marcacaoAjuste Objeto contendo os novos dados da marcação.
     * @throws Throwable Caso algum erro no processamento ou conexão com o Banco de Dados ocorrer.
     */
    void adicionarMarcacaoAjuste(@NotNull final String token,
                                 @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws Throwable;

    /**
     * Este método é utilizado para criar um intervalo completo, contendo uma marcação de início
     * e uma marcação de fim. Não é possível, para este método a existência de apenas uma marcação.
     * Para mais informações sobre os dados de uma adição consulte {@link MarcacaoAjusteAdicaoInicioFim}.
     *
     * @param token          Identificador do usuário que realizou a edição.
     * @param marcacaoAjuste Objeto contendo os novos dados da marcação.
     * @throws Throwable Caso algum erro no processamento ou conexão com o Banco de Dados ocorrer.
     */
    void adicionarMarcacaoAjusteInicioFim(@NotNull final String token,
                                          @NotNull final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste) throws Throwable;

    /**
     * Método utilizado para ativar ou inativar uma marcação.
     * Para mais informações sobre os dados de uma adição consulte {@link MarcacaoAjusteAtivacaoInativacao}.
     *
     * @param marcacaoAjuste Objeto contendo os novos dados da marcação.
     * @param token          Identificador do usuário que realizou a edição.
     * @throws Throwable Caso algum erro no processamento ou conexão com o Banco de Dados ocorrer.
     */
    void ativarInativarMarcacaoAjuste(@NotNull final MarcacaoAjusteAtivacaoInativacao marcacaoAjuste,
                                      @NotNull final String token) throws Throwable;

    /**
     * Método para inserir os novos dados da marcação. Isso ocorre quando uma edição de marcação é realizada
     * na aplicação. Para mais informações sobre os dados de uma edição consulte {@link MarcacaoAjusteEdicao}.
     *
     * @param marcacaoAjuste Objeto contendo os novos dados da marcação.
     * @param token          Identificador do usuário que realizou a edição.
     * @throws Throwable Caso algum erro no processamento ou conexão com o Banco de Dados ocorrer.
     */
    void editarMarcacaoAjuste(@NotNull final MarcacaoAjusteEdicao marcacaoAjuste,
                              @NotNull final String token) throws Throwable;

    /**
     * Método utilizado para buscar os totais de marcações de um dia, respeitando os filtros aplicados.
     * Para filtrar por marcações de TODOS os Colaboradores, o parâmetro {@code codColaborador}
     * deve ser {@link Filtros#SELECIONOU_TODOS}.
     * Para filtrar por TODOS os tipos de marcações executados no dia, o parâmetro {@code codTipoIntervalo}
     * deve ser {@link Filtros#SELECIONOU_TODOS}.
     *
     * @param codUnidade       Código da {@link Unidade} que serão buscados os dados.
     * @param codColaborador   Código do {@link Colaborador} que será buscado as marcações.
     * @param codTipoIntervalo Código do Tipo de Intervalo que será buscado.
     * @param dataInicial      Data inicial da filtragem dos dados.
     * @param dataFinal        Data final da filtragem dos dados.
     * @return Um {@link List<ConsolidadoMarcacoesDia>} contendo os dados filtrados.
     * @throws Throwable Caso algum erro no processamento ou conexão com o Banco de Dados ocorrer.
     */
    @NotNull
    List<ConsolidadoMarcacoesDia> getMarcacoesConsolidadasParaAjuste(
            @NotNull final Long codUnidade,
            @NotNull final String codColaborador,
            @NotNull final String codTipoIntervalo,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Busca todas as marcações realizadas pelo {@link Colaborador} na {@code data} filtrada.
     * Estas marcações serão listadas para possibilitar a posterior edição destas marcações.
     *
     * @param codUnidade     Código da {@link Unidade} que serão buscados os dados.
     * @param codColaborador Código do {@link Colaborador} que será buscado as marcações.
     * @param data           Data da qual as marcações serão buscadas.
     * @return Um {@link List<MarcacaoColaboradorAjuste>} contendo os dados filtrados.
     * @throws Throwable Caso algum erro no processamento ou conexão com o Banco de Dados ocorrer.
     */
    @NotNull
    List<MarcacaoColaboradorAjuste> getMarcacoesColaboradorParaAjuste(
            @NotNull final Long codUnidade,
            @NotNull final String codColaborador,
            @NotNull final LocalDate data) throws Throwable;

    @NotNull
    List<MarcacaoAjusteHistoricoExibicao> getMarcacaoAjusteHistorio(@NotNull final Long codMarcacao) throws Throwable;

    @NotNull
    List<MarcacaoInconsistenciaExibicao> getMarcacoesInconsistentes(@NotNull final Long codMarcacao) throws Throwable;
}