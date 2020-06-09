package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.AmazonConstants;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.imagens.FileFormatNotSupportException;
import br.com.zalf.prolog.webservice.commons.imagens.ImagemProLog;
import br.com.zalf.prolog.webservice.commons.imagens.UploadImageHelper;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.ChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.model.FiltroRegionalUnidadeChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistUploadImagemRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.SuccessResponseChecklistUploadImagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.integracao.router.RouterChecklists;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe ChecklistService responsável por comunicar-se com a interface DAO
 */
public final class ChecklistService {
    private static final String TAG = ChecklistService.class.getSimpleName();
    @NotNull
    private final ChecklistDao dao = Injection.provideChecklistDao();

    @NotNull
    public Long insert(@NotNull final String userToken,
                       @NotNull final ChecklistInsercao checklist) throws ProLogException {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .insertChecklist(checklist, false, true);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir um checklist", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir checklist, tente novamente");
        }
    }

    @NotNull
    public SuccessResponseChecklistUploadImagem uploadImagemRealizacaoChecklist(
            @NotNull final InputStream fileInputStream,
            @NotNull final FormDataContentDisposition fileDetail,
            @NotNull final ChecklistUploadImagemRealizacao imagem) {
        try {
            if ((imagem.getCodigoPergunta() == null) == (imagem.getCodigoAlternativa() == null)) {
                throw new IllegalStateException("Apenas o código da pergunta OU da alternativa deve estar setado!\n" +
                        "codPergunta: " + imagem.getCodigoPergunta() +
                        "\ncodAlternativa: " + imagem.getCodigoAlternativa());
            }

            final String imageType = FilenameUtils.getExtension(fileDetail.getFileName());
            final ImagemProLog imagemProlog = UploadImageHelper.uploadCompressedImagem(
                    fileInputStream,
                    AmazonConstants.BUCKET_CHECKLIST_REALIZACAO_IMAGENS,
                    imageType);

            if (imagem.getCodigoPergunta() != null) {
                dao.insertImagemPerguntaChecklistRealizado(
                        imagem.getCodigoChecklist(),
                        imagem.getCodigoPergunta(),
                        imagemProlog.getUrlImagem());
            } else {
                dao.insertImagemAlternativaChecklistRealizado(
                        imagem.getCodigoChecklist(),
                        imagem.getCodigoAlternativa(),
                        imagemProlog.getUrlImagem());
            }

            return new SuccessResponseChecklistUploadImagem(imagemProlog.getUrlImagem());
        } catch (final FileFormatNotSupportException e) {
            Log.e(TAG, "Arquivo recebido não é uma imagem", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao processar imagem, tente novamente");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir a imagem", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao salvar a imagem, tente novamente");
        }
    }

    @NotNull
    public List<TipoVeiculo> getTiposVeiculosFiltroChecklist(final String userToken, final Long codEmpresa) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getTiposVeiculosFiltroChecklist(codEmpresa);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar os tipos de veículos para filtros do check.\n" +
                    "Empresa: %d\n" +
                    "userToken: %s", codEmpresa, userToken), throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar os tipos de veículos, tente novamente");
        }
    }

    @NotNull
    public FiltroRegionalUnidadeChecklist getRegionaisUnidadesSelecao(@NotNull final Long codColaborador) {
        try {
            return dao.getRegionaisUnidadesSelecao(codColaborador);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar o regionais e unidades para seleção\n" +
                    "codColaborador: %d", codColaborador), throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar unidades, tente novamente");
        }
    }

    @NotNull
    public Checklist getByCod(@NotNull final Long codigo, @NotNull final String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getChecklistByCodigo(codigo);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar um checklist específico: " + codigo, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar checklist, tente novamente");
        }
    }

    public List<Checklist> getAll(final Long codUnidade,
                                  final Long codEquipe,
                                  final Long codTipoVeiculo,
                                  final String placaVeiculo,
                                  final long dataInicial,
                                  final long dataFinal,
                                  final int limit,
                                  final long offset,
                                  final boolean resumido,
                                  final String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getTodosChecklists(codUnidade, codEquipe, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal,
                            limit, offset, resumido);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar checklists", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar checklists, tente novamente");
        }
    }

    public List<Checklist> getByColaborador(final Long cpf, final Long dataInicial, final Long dataFinal, final int limit, final long offset,
                                            final boolean resumido, final String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getChecklistsByColaborador(cpf, dataInicial, dataFinal, limit, offset, resumido);
        } catch (final Exception e) {
            Log.e(TAG, "Erro ao buscar os checklists de um colaborador específico", e);
            throw new RuntimeException("Erro ao buscar checklists para o colaborador: " + cpf);
        }
    }

    /**
     * Novos services de listagem
     */
    public List<ChecklistListagem> getListagemByColaborador(final Long cpf,
                                                            final String dataInicial,
                                                            final String dataFinal,
                                                            final int limit,
                                                            final long offset,
                                                            final String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getListagemByColaborador(
                            cpf,
                            ProLogDateParser.toLocalDate(dataInicial),
                            ProLogDateParser.toLocalDate(dataFinal),
                            limit,
                            offset);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar os checklists de um colaborador específico", t);
            throw new RuntimeException("Erro ao buscar checklists para o colaborador: " + cpf);
        }
    }

    public List<ChecklistListagem> getListagem(final Long codUnidade,
                                               final Long codEquipe,
                                               final Long codTipoVeiculo,
                                               final String placaVeiculo,
                                               final String dataInicial,
                                               final String dataFinal,
                                               final int limit,
                                               final long offset,
                                               final String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getListagem(
                            codUnidade,
                            codEquipe,
                            codTipoVeiculo,
                            placaVeiculo,
                            ProLogDateParser.toLocalDate(dataInicial),
                            ProLogDateParser.toLocalDate(dataFinal),
                            limit,
                            offset);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar checklists", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar checklists, tente novamente");
        }
    }
    /**
     * Fim novos services de listagem
     */

    @NotNull
    public DeprecatedFarolChecklist getFarolChecklist(final Long codUnidade,
                                                      final String dataInicial,
                                                      final String dataFinal,
                                                      final boolean itensCriticosRetroativos,
                                                      final String userToken) throws ProLogException {
        return internalGetFarolChecklist(
                codUnidade,
                ProLogDateParser.toLocalDate(dataInicial),
                ProLogDateParser.toLocalDate(dataFinal),
                itensCriticosRetroativos,
                userToken);
    }

    @NotNull
    public DeprecatedFarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                            final boolean itensCriticosRetroativos,
                                            @NotNull final String userToken) throws ProLogException {
        LocalDate hojeComTz = null;
        try {
            hojeComTz = Now
                    .zonedDateTimeTzAware(TimeZoneManager.getZoneIdForCodUnidade(codUnidade))
                    .toLocalDate();
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar TZ do cliente para gerar farol do checklist.\n" +
                    "Unidade: " + codUnidade, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar farol, tente novamente");
        }
        return internalGetFarolChecklist(codUnidade, hojeComTz, hojeComTz, itensCriticosRetroativos, userToken);
    }

    public boolean getChecklistDiferentesUnidadesAtivoEmpresa(@NotNull final Long codEmpresa) {
        try {
            return dao.getChecklistDiferentesUnidadesAtivoEmpresa(codEmpresa);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao verificar se empresa está bloqueada para realizar o checklist de " +
                    "diferentes unidades", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    // Mensagem propositalmente genérica para evitar de mostrar um erro sem sentido para o usuário
                    // quando ele tentar fazer Login, já que atualmente esse método é usado apenas no login.
                    .map(t, "Algo deu errado, tente novamente");
        }
    }

    @NotNull
    private DeprecatedFarolChecklist internalGetFarolChecklist(@NotNull final Long codUnidade,
                                                               @NotNull final LocalDate dataInicial,
                                                               @NotNull final LocalDate dataFinal,
                                                               final boolean itensCriticosRetroativos,
                                                               @NotNull final String userToken) throws ProLogException {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getFarolChecklist(
                            codUnidade,
                            dataInicial,
                            dataFinal,
                            itensCriticosRetroativos);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar o farol de realização dos checklists", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar o farol do checklist, tente novamente");
        }
    }
}