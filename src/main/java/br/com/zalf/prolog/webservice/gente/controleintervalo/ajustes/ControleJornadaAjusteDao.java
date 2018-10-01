package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

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
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoMarcacao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ControleJornadaAjusteDao {

    /**
     * Método utilizado para buscar os totais de marcações de um dia, respeitando os filtros aplicados.
     *
     * @param codUnidade      Código da {@link Unidade unidade} que serão buscados os dados.
     * @param codTipoMarcacao Código do {@link TipoMarcacao tipo de intervalo} que será buscado.
     * @param codColaborador  Código do {@link Colaborador colaborador} que será buscado as marcações.
     * @param dataInicial     Data inicial da filtragem dos dados.
     * @param dataFinal       Data final da filtragem dos dados.
     * @return Uma {@link List lista} de {@link ConsolidadoMarcacoesDia marcações} contendo os dados filtrados.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    @NotNull
    List<ConsolidadoMarcacoesDia> getMarcacoesConsolidadasParaAjuste(
            @NotNull final Long codUnidade,
            @Nullable final Long codTipoMarcacao,
            @Nullable final Long codColaborador,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Busca todas as marcações realizadas pelo {@link Colaborador colaborador} no dia filtrado,
     * respeitando o {@code codTipoMarcacao} selecionado para filtragem.
     * Estas marcações serão listadas para possibilitar o ajuste das mesmas.
     *
     * @param codColaborador  Código do {@link Colaborador} que será buscado as marcações.
     * @param codTipoMarcacao Código do {@link TipoMarcacao} que será buscado.
     * @param dia             Dia da qual as marcações serão buscadas.
     * @return Um {@link List<MarcacaoColaboradorAjuste>} contendo os dados filtrados.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    @NotNull
    List<MarcacaoColaboradorAjuste> getMarcacoesColaboradorParaAjuste(
            @NotNull final Long codColaborador,
            @Nullable final Long codTipoMarcacao,
            @NotNull final LocalDate dia) throws Throwable;

    /**
     * Método para vincular uma marcação a um {@link Colaborador colaborador}. Isso ocorre quando é criado
     * uma marcação de fim para um início já existente, ou a criação de uma marcação de início
     * para uma marcação de fim já existente.
     * Para mais informações sobre os dados de uma adição consulte {@link MarcacaoAjusteAdicao este objeto}.
     *
     * @param token          Identificador do usuário que realizou a adição.
     * @param marcacaoAjuste Objeto contendo os novos dados da marcação.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void adicionarMarcacaoAjuste(@NotNull final String token,
                                 @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws Throwable;

    /**
     * Este método é utilizado para criar uma marcação completa, contendo início e fim. Para inserções de início ou fim
     * de forma separada utilize {@link #adicionarMarcacaoAjuste(String, MarcacaoAjusteAdicao)}.
     * Para mais informações sobre os dados de uma adição consulte {@link MarcacaoAjusteAdicaoInicioFim este objeto}.
     *
     * @param token          Identificador do usuário que realizou a adição.
     * @param marcacaoAjuste Objeto contendo os dados da marcação de início e fim.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void adicionarMarcacaoAjusteInicioFim(@NotNull final String token,
                                          @NotNull final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste) throws Throwable;

    /**
     * Método para editar uma marcação de início ou fim.
     * Para mais informações sobre os dados de uma edição consulte {@link MarcacaoAjusteEdicao este objeto}.
     *
     * @param token          Identificador do usuário que realizou a edição.
     * @param marcacaoAjuste Objeto contendo os novos dados da marcação.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void editarMarcacaoAjuste(@NotNull final String token,
                              @NotNull final MarcacaoAjusteEdicao marcacaoAjuste) throws Throwable;

    /**
     * Método utilizado para ativar ou inativar uma marcação.
     * Para mais informações sobre os dados de uma ativação ou inativação consulte
     * {@link MarcacaoAjusteAtivacaoInativacao este objeto}.
     *
     * @param token          Identificador do usuário que realizou a ativação ou inativação.
     * @param marcacaoAjuste Objeto contendo as informações necessárias para se ativar ou inativar uma marcação.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void ativarInativarMarcacaoAjuste(@NotNull final String token,
                                      @NotNull final MarcacaoAjusteAtivacaoInativacao marcacaoAjuste) throws Throwable;

    @NotNull
    List<MarcacaoAjusteHistoricoExibicao> getMarcacaoAjusteHistorio(@NotNull final Long codMarcacao) throws Throwable;

    @NotNull
    List<MarcacaoInconsistenciaExibicao> getMarcacoesInconsistentes(@NotNull final Long codMarcacao) throws Throwable;
}