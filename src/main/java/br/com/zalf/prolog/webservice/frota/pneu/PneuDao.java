package br.com.zalf.prolog.webservice.frota.pneu;

import br.com.zalf.prolog.webservice.frota.pneu._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PneuDao {
    @NotNull
    List<Pneu> getPneusByPlaca(@NotNull final String placa, @NotNull final Long codUnidade) throws SQLException;

    @NotNull
    Long insert(@NotNull final Long codigoColaboradorCadastro,
                @NotNull final Pneu pneu,
                @NotNull final Long codUnidade,
                @NotNull final OrigemAcaoEnum origemCadastro) throws Throwable;

    @NotNull
    List<Long> insert(@NotNull final Long codigoColaboradorCadastro,
                      @NotNull final List<Pneu> pneus) throws Throwable;

    @CanIgnoreReturnValue
    boolean updateMedicoes(@NotNull final Connection conn,
                           @NotNull final Long codPneu,
                           @NotNull final Sulcos novosSulcos,
                           final double novaPressao) throws Throwable;

    void update(@NotNull final Long codigoColaboradorEdicao,
                @NotNull final Pneu pneu,
                @NotNull final Long codUnidade,
                @NotNull final Long codOriginalPneu) throws Throwable;

    @CanIgnoreReturnValue
    boolean updatePressao(@NotNull final Connection conn,
                          @NotNull final Long codPneu,
                          final double pressao) throws Throwable;

    void updateSulcos(@NotNull final Connection conn,
                      @NotNull final Long codPneu,
                      @NotNull final Sulcos novosSulcos) throws Throwable;

    void updateStatus(@NotNull final Connection conn,
                      @NotNull final Pneu pneu,
                      @NotNull final StatusPneu status) throws SQLException;

    void incrementaVidaPneu(@NotNull final Connection conn,
                            @NotNull final Long codPneu,
                            @NotNull final Long codModeloBanda) throws Throwable;

    @NotNull
    List<Pneu> getPneusByCodUnidadesByStatus(@NotNull final List<Long> codUnidades,
                                             @NotNull final StatusPneu status) throws Throwable;

    @NotNull
    List<Pneu> getTodosPneus(@NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    List<Pneu> getPneusAnalise(@NotNull final Long codUnidade) throws Throwable;

    List<Marca> getMarcaModeloPneuByCodEmpresa(Long codEmpresa) throws SQLException;

    List<PneuComum.Dimensao> getDimensoes() throws SQLException;

    @NotNull
    Pneu getPneuByCod(@NotNull final Long codPneu, @NotNull final Long codUnidade) throws Throwable;

    @NotNull
    Pneu getPneuByCod(@NotNull final Connection conn,
                      @NotNull final Long codUnidade,
                      @NotNull final Long codPneu) throws Throwable;

    void marcarFotoComoSincronizada(@NotNull final Long codPneu,
                                    @NotNull final String urlFotoPneu) throws SQLException;

    @NotNull
    List<Long> getCodPneuByCodCliente(@NotNull final Connection conn,
                                      @NotNull final Long codEmpresa,
                                      @NotNull final List<String> codigoClientePneus) throws Throwable;

    @NotNull
    PneuRetornoDescarteSuccess retornarPneuDescarte(@NotNull final PneuRetornoDescarte pneuRetornoDescarte)
            throws Throwable;
}