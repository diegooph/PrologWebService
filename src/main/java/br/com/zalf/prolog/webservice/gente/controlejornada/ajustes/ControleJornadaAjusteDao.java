package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.MarcacaoAjuste;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.MarcacaoAjusteAdicao;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.MarcacaoAjusteAdicaoInicioFim;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.MarcacaoAjusteEdicao;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.exibicao.ConsolidadoMarcacoesDia;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.exibicao.MarcacaoColaboradorAjuste;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.historico.MarcacaoAjusteHistoricoExibicao;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.inconsistencias.MarcacaoInconsistencia;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.inconsistencias.TipoInconsistenciaMarcacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
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
     * @param tokenResponsavelAjuste Identificador do usuário que realizou a adição.
     * @param marcacaoAjuste         Objeto contendo os novos dados da marcação.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void adicionarMarcacaoAjuste(@NotNull final String tokenResponsavelAjuste,
                                 @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws Throwable;

    /**
     * Este método é utilizado para criar uma marcação completa, contendo início e fim. Para inserções de início ou fim
     * de forma separada utilize {@link #adicionarMarcacaoAjuste(String, MarcacaoAjusteAdicao)}.
     * Para mais informações sobre os dados de uma adição consulte {@link MarcacaoAjusteAdicaoInicioFim este objeto}.
     *
     * @param tokenResponsavelAjuste Identificador do usuário que realizou a adição.
     * @param marcacaoAjuste         Objeto contendo os dados da marcação de início e fim.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void adicionarMarcacaoAjusteInicioFim(@NotNull final String tokenResponsavelAjuste,
                                          @NotNull final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste) throws Throwable;

    /**
     * Método para editar uma marcação de início ou fim.
     * Para mais informações sobre os dados de uma edição consulte {@link MarcacaoAjusteEdicao este objeto}.
     *
     * @param tokenResponsavelAjuste Identificador do usuário que realizou a edição.
     * @param marcacaoAjuste         Objeto contendo os novos dados da marcação.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void editarMarcacaoAjuste(@NotNull final String tokenResponsavelAjuste,
                              @NotNull final MarcacaoAjusteEdicao marcacaoAjuste) throws Throwable;

    /**
     * Método utilizado para ativar ou inativar uma marcação.
     *
     * @param tokenResponsavelAjuste Identificador do usuário que realizou a ativação ou inativação.
     * @param marcacaoAjuste         Objeto contendo as informações necessárias para se ativar ou inativar uma marcação.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void ativarInativarMarcacaoAjuste(@NotNull final String tokenResponsavelAjuste,
                                      @NotNull final MarcacaoAjuste marcacaoAjuste,
                                      @NotNull final Long codMarcacao,
                                      final boolean deveAtivar) throws Throwable;

    /**
     * Método para buscar o histórico de todos os ajustes das marcações que se tenha interesse. Ele recebe uma lista
     * de códigos de marcações que se deseja buscar para facilitar o caso de uso de marcações vinculadas, onde se tem
     * interesse no histórico de ajuste das duas marcações.
     *
     * @param codMarcacoes O código das marcações das quais queremos buscar os históricos de ajustes.
     * @return Uma lista contendo o histórico de ajustes das marcações buscadas ou uma lista vazia caso as
     * marcações nunca tenham sido ajustadas.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    @NotNull
    List<MarcacaoAjusteHistoricoExibicao> getHistoricoAjusteMarcacoes(
            @NotNull final List<Long> codMarcacoes) throws Throwable;

    /**
     * Método para buscar todos os {@link TipoInconsistenciaMarcacao tipos de inconsistência} que possa existir para o
     * colaborador e dia filtrados.
     *
     * @param codColaborador     O código do colaborador para o qual queremos buscar as inconsistências.
     * @param dia                O dia do qual queremos buscar as inconsistências.
     * @param tipoInconsistencia {@link TipoInconsistenciaMarcacao} que estamos buscando.
     * @return Uma lista contendo as inconsistências, se existirem, para o colaborador, dia e tipo de inconsistência
     * especificado. Uma lista vazia caso não existam inconsistências.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    @NotNull
    List<MarcacaoInconsistencia> getInconsistenciasColaboradorDia(
            @NotNull final Long codColaborador,
            @NotNull final LocalDate dia,
            @NotNull final TipoInconsistenciaMarcacao tipoInconsistencia) throws Throwable;
}