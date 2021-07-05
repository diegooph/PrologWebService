package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.EixoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.InfosVeiculoEditado;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoDadosColetaKm;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoVisualizacao;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoCadastroDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Contém os métodos para manipular os veículos
 */
public interface VeiculoDao {

    void insert(@NotNull final VeiculoCadastroDto veiculo,
                @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    @NotNull
    InfosVeiculoEditado update(@NotNull final Long codColaboradorResponsavelEdicao,
                               @NotNull final VeiculoEdicao veiculo,
                               @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener)
            throws Throwable;

    List<VeiculoListagem> getVeiculosByUnidades(@NotNull final List<Long> codUnidades,
                                                final boolean apenasAtivos,
                                                @Nullable final Long codTipoVeiculo) throws Throwable;

    List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(Long cpf)
            throws SQLException;

    @NotNull
    VeiculoVisualizacao getVeiculoByCodigo(@NotNull final Long codVeiculo) throws Throwable;

    @NotNull
    List<Long> getCodVeiculosByPlacas(@NotNull final Long codColaborador,
                                      @NotNull final List<String> placas) throws Throwable;

    @Deprecated
    @NotNull
    Veiculo getVeiculoByPlaca(@NotNull final String placa,
                              @NotNull final Long codUnidade,
                              final boolean withPneus) throws SQLException;

    @Deprecated
    @NotNull
    Veiculo getVeiculoByPlaca(@NotNull final Connection conn,
                              @NotNull final String placa,
                              @NotNull final Long codUnidade,
                              final boolean withPneus) throws Throwable;

    void updateKmByPlaca(String placa, long km, Connection conn) throws SQLException;

    @NotNull
    Long updateKmByCodVeiculo(@NotNull final Connection conn,
                              @NotNull final Long codUnidade,
                              @NotNull final Long codVeiculo,
                              @NotNull final Long veiculoCodProcesso,
                              @NotNull final VeiculoTipoProcesso veiculoTipoProcesso,
                              @NotNull final OffsetDateTime dataHoraProcesso,
                              final long kmVeiculo,
                              final boolean devePropagarKmParaReboques);

    @Deprecated
    List<Marca> getMarcaModeloVeiculoByCodEmpresa(Long codEmpresa) throws SQLException;

    @NotNull
    List<Marca> getMarcasVeiculosNivelProLog() throws Throwable;

    @NotNull
    List<Marca> 
    getMarcasModelosVeiculosByEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    @NotNull
    Long insertModeloVeiculo(@NotNull final Modelo modelo,
                             @NotNull final Long codEmpresa,
                             @NotNull final Long codMarca) throws Throwable;

    int getTotalVeiculosByUnidade(Long codUnidade, Connection conn) throws SQLException;

    List<String> getPlacasVeiculosByTipo(Long codUnidade, String codTipo) throws SQLException;

    Optional<DiagramaVeiculo> getDiagramaVeiculoByPlaca(@NotNull final String placa) throws SQLException;

    Optional<DiagramaVeiculo> getDiagramaVeiculoByPlaca(@NotNull final Connection conn,
                                                        @NotNull final String placa) throws SQLException;

    Optional<DiagramaVeiculo> getDiagramaVeiculoByCod(@NotNull final Short codDiagrama) throws SQLException;

    Set<DiagramaVeiculo> getDiagramasVeiculos() throws SQLException;

    Modelo getModeloVeiculo(Long codUnidade, Long codModelo) throws SQLException;

    boolean updateModelo(Modelo modelo, Long codUnidade, Long codMarca) throws SQLException;

    boolean deleteModelo(Long codModelo, Long codUnidade) throws SQLException;

    void adicionaPneuVeiculo(@NotNull final Connection conn,
                             @NotNull final Long codUnidade,
                             @NotNull final String placa,
                             @NotNull final Long codPneu,
                             final int posicaoPneuVeiculo) throws Throwable;

    void removePneuVeiculo(@NotNull final Connection conn,
                           @NotNull final Long codUnidade,
                           @NotNull final Long codVeiculo,
                           @NotNull final Long codPneu) throws Throwable;

    @NotNull
    Optional<List<Long>> getCodPneusAplicadosVeiculo(@NotNull final Connection conn,
                                                     @NotNull final Long codVeiculo) throws Throwable;

    Long getCodUnidadeByPlaca(@NotNull final Connection conn, @NotNull final String placaVeiculo) throws Throwable;

    @NotNull
    Long getCodVeiculoByPlaca(@NotNull final Connection conn,
                              @NotNull final String placaVeiculo) throws Throwable;

    @Deprecated
    List<Veiculo> getVeiculosAtivosByUnidade(Long codUnidade, @Nullable Boolean ativos) throws SQLException;

    @NotNull
    VeiculoDadosColetaKm getDadosColetaKmByCodigo(@NotNull final Long codVeiculo) throws Throwable;

    @NotNull
    Set<EixoVeiculo> getEixosDiagrama(final int codDiagrama, final Connection conn) throws SQLException;
}
