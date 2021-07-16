package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura.PneuNomenclaturaService;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculoNomenclatura;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculoPosicaoNomenclatura;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicaoStatus;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoDadosColetaKm;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.validator.VehicleValidator;
import br.com.zalf.prolog.webservice.integracao.router.RouterVeiculo;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VeiculoCreateDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Classe VeiculoService responsável por comunicar-se com a interface DAO
 */
public final class VeiculoService {
    private static final String TAG = VeiculoService.class.getSimpleName();
    @NotNull
    private final VeiculoDao dao = Injection.provideVeiculoDao();

    @NotNull
    public List<VeiculoListagem> getVeiculosByUnidades(@NotNull final String userToken,
                                                       @NotNull final List<Long> codUnidades,
                                                       final boolean apenasAtivos,
                                                       @Nullable final Long codTipoVeiculo) {
        try {
            return RouterVeiculo
                    .create(dao, userToken)
                    .getVeiculosByUnidades(codUnidades, apenasAtivos, codTipoVeiculo);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar os veículos.\n" +
                                             "codUnidades: %s\n" +
                                             "apenasAtivos: %b\n" +
                                             "codTipoVeiculo: %d\n", codUnidades, apenasAtivos, codTipoVeiculo), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar os veículos da unidade, tente novamente.");
        }
    }

    @NotNull
    public VeiculoVisualizacao getVeiculoByCodigo(@NotNull final String userToken,
                                                  @NotNull final Long codVeiculo) {
        try {
            return RouterVeiculo
                    .create(dao, userToken)
                    .getVeiculoByCodigo(codVeiculo);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar o veículo.\n" +
                                             "código: %d", codVeiculo), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar o veículo, tente novamente.");
        }
    }

    @Deprecated
    public Veiculo getVeiculoByPlaca(@NotNull final String userToken,
                                     @NotNull final String placa,
                                     @NotNull final Long codUnidade,
                                     final boolean withPneus) {
        try {
            return RouterVeiculo
                    .create(dao, userToken)
                    .getVeiculoByPlaca(placa, codUnidade, withPneus);
        } catch (final Throwable throwable) {
            final String errorMessage = String.format("Erro ao buscar o veículo. \n" +
                                                              "Placa: %s \n" +
                                                              "codUnidade: %s \n" +
                                                              "withPneus: %b \n" +
                                                              "userToken: %s", placa, codUnidade, withPneus, userToken);
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar o veículo, tente novamente.");
        }
    }

    public List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(final Long cpf) {
        try {
            return dao.getVeiculosAtivosByUnidadeByColaborador(cpf);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os veículos ativos da unidade do colaborador. \n" +
                                             "cpf: %s", cpf), e);
            return null;
        }
    }

    @NotNull
    public Response insert(@NotNull final String userToken,
                           @NotNull final VeiculoCreateDto veiculo) throws ProLogException {
        try {
            VehicleValidator.validacaoAtributosVeiculo(veiculo);
            RouterVeiculo
                    .create(dao, userToken)
                    .insert(veiculo, Injection.provideDadosChecklistOfflineChangedListener());
            return Response.ok("Veículo inserido com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao inserir o veículo. \n" +
                                             "userToken: %s\n" +
                                             "codUnidade: %d", userToken, veiculo.getBranchId()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir o veículo, tente novamente");
        }
    }

    @NotNull
    public Response update(@NotNull final Long codColaboradorResponsavelEdicao,
                           @NotNull final String userToken,
                           @NotNull final VeiculoEdicao veiculo) {
        try {
            VehicleValidator.validacaoAtributosVeiculo(veiculo);
            RouterVeiculo
                    .create(dao, userToken)
                    .update(codColaboradorResponsavelEdicao,
                            veiculo,
                            Injection.provideDadosChecklistOfflineChangedListener());
            return Response.ok("Veículo atualizado com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao atualizar o veículo de código: %d", veiculo.getCodigo()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar veículo, tente novamente");
        }
    }

    @NotNull
    public Response updateStatus(@NotNull final Long codColaboradorResponsavelEdicao,
                                 @NotNull final String userToken,
                                 @NotNull final VeiculoEdicaoStatus veiculo) {
        try {
            VehicleValidator.validacaoAtributosVeiculo(veiculo);
            final VeiculoEdicao edicao = dao
                    .getVeiculoByCodigo(veiculo.getCodigo())
                    .toVeiculoEdicao(veiculo.isStatusAtivo());
            update(codColaboradorResponsavelEdicao, userToken, edicao);
            return Response.ok(veiculo.isStatusAtivo()
                                       ? "Veículo ativado com sucesso"
                                       : "Veículo inativado com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG,
                  String.format("Erro ao atualizar o status do veículo:\n" +
                                        "codColaboradorResponsavelEdicao: %d\n" +
                                        "codVeiculo: %d\n", codColaboradorResponsavelEdicao, veiculo.getCodigo()),
                  t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar o status do veículo, tente novamente.");
        }
    }

    @Deprecated
    public List<Marca> getMarcaModeloVeiculoByCodEmpresa(final Long codEmpresa) {
        try {
            return dao.getMarcaModeloVeiculoByCodEmpresa(codEmpresa);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar as marcas e modelos dos veículos. \n" +
                                             "Empresa: %d", codEmpresa), e);
            return new ArrayList<>();
        }
    }

    @NotNull
    public List<Marca> getMarcasVeiculosNivelProLog() throws ProLogException {
        try {
            return dao.getMarcasVeiculosNivelProLog();
        } catch (final Throwable t) {
            final String errorMessage = "Erro ao buscar marcas de veículos";
            Log.e(TAG, errorMessage, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, errorMessage);
        }
    }

    @NotNull
    public List<Marca> getMarcasModelosVeiculosByEmpresa(final Long codEmpresa) throws ProLogException {
        try {
            return dao.getMarcasModelosVeiculosByEmpresa(codEmpresa);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar marcas e modelos de veículos da empresa %d", codEmpresa), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar marcas e modelos de veículos");
        }
    }

    @NotNull
    public Long insertModeloVeiculo(final Modelo modelo,
                                    final Long codEmpresa,
                                    final Long codMarca) throws ProLogException {
        try {
            if (modelo.getNome().trim().isEmpty()) {
                throw new NullPointerException("Erro!\nModelo sem nome.");
            }
            return dao.insertModeloVeiculo(modelo, codEmpresa, codMarca);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao inserir o modelo de veículo.\n" +
                                             "Empresa: %d\n" +
                                             "codMarca: %d", codEmpresa, codMarca), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao cadastrar modelo de veículo, tente novamente");
        }
    }

    @NotNull
    public Set<DiagramaVeiculo> getDiagramasVeiculo() {
        try {
            return dao.getDiagramasVeiculos();
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar os diagramas dos veículos.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar diagramas, tente novamente");
        }
    }

    @NotNull
    public List<DiagramaVeiculoNomenclatura> getDiagramasVeiculosNomenclaturas(@NotNull final Long codEmpresa) {
        final PneuNomenclaturaService nomenclaturaService = new PneuNomenclaturaService();
        final List<DiagramaVeiculoNomenclatura> diagramas = new ArrayList<>();
        getDiagramasVeiculo()
                .forEach(diagrama -> {
                    final List<DiagramaVeiculoPosicaoNomenclatura> nomenclaturas = new ArrayList<>();
                    nomenclaturaService
                            .getPneuNomenclaturaItemVisualizacao(codEmpresa, Long.valueOf(diagrama.getCodigo()))
                            .forEach(nomenclatura -> nomenclaturas.add(new DiagramaVeiculoPosicaoNomenclatura(
                                    nomenclatura.getNomenclatura(),
                                    nomenclatura.getCodAuxiliar(),
                                    nomenclatura.getPosicaoProlog())));
                    diagramas.add(new DiagramaVeiculoNomenclatura(
                            diagrama.getCodigo(),
                            diagrama.getNome(),
                            nomenclaturas));
                });
        return diagramas;
    }

    public List<String> getVeiculosByTipo(final Long codUnidade, final String codTipo, final String userToken) {
        try {
            return RouterVeiculo
                    .create(dao, userToken)
                    .getPlacasVeiculosByTipo(codUnidade, codTipo);
        } catch (final Exception e) {
            Log.e(TAG, String.format("Erro ao buscar os veículos de um tipo específico. \n" +
                                             "codUnidade: %d \n" +
                                             "codTipo: %s \n" +
                                             "userToken: %s", codUnidade, codTipo, userToken), e);
            throw new RuntimeException("Erro ao buscar placas dos veículos para o tipo: " + codTipo + " e unidade: " + codUnidade);
        }
    }

    public Modelo getModeloVeiculo(final Long codUnidade, final Long codModelo) {
        try {
            return dao.getModeloVeiculo(codUnidade, codModelo);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar um modelo de veículo. \n" +
                                             "codUnidade: %d \n" +
                                             "codModelo: %s \n", codUnidade, codModelo), e);
            return null;
        }
    }

    public boolean updateModelo(final Modelo modelo, final Long codUnidade, final Long codMarca) {
        try {
            return dao.updateModelo(modelo, codUnidade, codMarca);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao atualizar o modelo de veículo. \n" +
                                             "codUnidade: %d \n" +
                                             "codMarca: %d", codUnidade, codMarca), e);
            return false;
        }
    }

    public boolean deleteModelo(final Long codModelo, final Long codUnidade) {
        try {
            return dao.deleteModelo(codModelo, codUnidade);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao deletar o modelo de veículo. \n" +
                                             "codUnidade: %d \n" +
                                             "codModelo: %d", codUnidade, codModelo), e);
            return false;
        }
    }

    @Deprecated
    public List<Veiculo> getVeiculosAtivosByUnidade(final String userToken,
                                                    final Long codUnidade,
                                                    final Boolean ativos) {
        try {
            return RouterVeiculo
                    .create(dao, userToken)
                    .getVeiculosAtivosByUnidade(codUnidade, ativos);
        } catch (final Exception e) {
            Log.e(TAG, String.format("Erro ao buscar os veículos ativos da unidade. \n" +
                                             "Unidade: %d \n" +
                                             "userToken: %s", codUnidade, userToken), e);
            throw new RuntimeException("Erro ao buscar os veículos ativos da unidade: " + codUnidade);
        }
    }

    @NotNull
    public VeiculoDadosColetaKm getDadosColetaKmByCodigo(@NotNull final String userToken,
                                                         @NotNull final Long codVeiculo) {
        try {
            return RouterVeiculo
                    .create(dao, userToken)
                    .getDadosColetaKmByCodigo(codVeiculo);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar o estado do veículo.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar estado do veiculo, tente novamente");
        }
    }
}