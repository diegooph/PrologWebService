package br.com.zalf.prolog.webservice.imports.escala_diaria;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.Now;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ParseDadosEscalaException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogErrorCodes;
import com.google.common.base.Preconditions;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 10/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class EscalaDiariaService {

    private static final String TAG = EscalaDiariaService.class.getSimpleName();
    private final EscalaDiariaDao dao = Injection.provideEscalaDiariaDao();

    public void uploadMapa(@NotNull final Long codUnidade,
                           @NotNull final InputStream fileInputStream,
                           @NotNull final FormDataContentDisposition fileDetail)
            throws ParseDadosEscalaException {
        final File file = createFileFromImport(codUnidade, fileInputStream, fileDetail);
        readAndInsertImport(codUnidade, file.getPath());
    }

    public void insertOrUpdateEscalaDiaria(@NotNull final Long codUnidade,
                                           @NotNull final EscalaDiariaItem escalaDiariaItem,
                                           boolean isInsert) throws SQLException {
        Preconditions.checkNotNull(escalaDiariaItem, "escalaDiariaItem não pode ser nulla!");
        dao.insertOrUpdateEscalaDiariaItem(codUnidade, escalaDiariaItem, isInsert);
    }

    public List<EscalaDiaria> getEscalasDiarias(@NotNull final Long codUnidade,
                                                @NotNull final String dataInicial,
                                                @NotNull final String dataFinal) {
        try {
            return dao.getEscalasDiarias(
                    codUnidade,
                    ProLogDateParser.validateAndParse(dataInicial),
                    ProLogDateParser.validateAndParse(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar Escalas Diarias para a Unidade: " + codUnidade, e);
            throw new RuntimeException(e);
        }
    }

    public void deleteEscalaDiariaItens(@NotNull final Long codUnidade,
                                        @NotNull final List<Long> codEscalas) throws SQLException {
        dao.deleteEscalaDiariaItens(codUnidade, codEscalas);
    }

    private File createFileFromImport(@NotNull final Long codUnidade,
                                      @NotNull final InputStream fileInputStream,
                                      @NotNull final FormDataContentDisposition fileDetail) throws ParseDadosEscalaException {
        try {
            final String fileName = String.valueOf(Now.utcMillis()) + "_" + codUnidade
                    + "_" + fileDetail.getFileName().replace(" ", "_");
            // Pasta temporária da JVM
            final File tmpDir = new File(System.getProperty("java.io.tmpdir"), "escalaDiaria");
            if (!tmpDir.exists()) {
                tmpDir.mkdir();
            }
            final File file = new File(tmpDir, fileName);
            final FileOutputStream out = new FileOutputStream(file);
            IOUtils.copy(fileInputStream, out);
            IOUtils.closeQuietly(out);
            return file;
        } catch (IOException e) {
            Log.e(TAG, "Erro ao ler arquivo binário do import", e);
            throw new ParseDadosEscalaException(
                    javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode(),
                    ProLogErrorCodes.PARSE_DADOS_ESCALA.errorCode(),
                    "O arquivo importado possui inconsistências",
                    "Erro ao ler arquivo binário do import",
                    e);
        }
    }

    private void readAndInsertImport(@NotNull final Long codUnidade, @NotNull final String path)
            throws ParseDadosEscalaException {
        final List<EscalaDiariaItem> escalaItens = new ArrayList<>();
        try {
            final Reader in = new FileReader(path);
            final List<CSVRecord> tabela = CSVFormat.DEFAULT.withDelimiter(';').parse(in).getRecords();
            for (int i = 1; i < tabela.size(); i++) {
                final EscalaDiariaItem item = createEscalaDiariaItem(tabela.get(i));
                if (item != null) {
                    escalaItens.add(item);
                }
            }
            dao.insertOrUpdateEscalaDiaria(codUnidade, escalaItens);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inserir dados da escala no BD", e);
            throw new ParseDadosEscalaException(
                    javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode(),
                    ProLogErrorCodes.PARSE_DADOS_ESCALA.errorCode(),
                    "",
                    "",
                    e);
        } catch (ParseException e) {
            Log.e(TAG, "Erro ao fazer o parse dos dados do import", e);
            throw new ParseDadosEscalaException(
                    javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode(),
                    ProLogErrorCodes.PARSE_DADOS_ESCALA.errorCode(),
                    "O arquivo importado está formatado de forma errada",
                    "Erro ao fazer o parse dos dados do import",
                    e);
        } catch (IOException e) {
            Log.e(TAG, "Erro ao ler arquivo no servidor", e);
            throw new ParseDadosEscalaException(
                    javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode(),
                    ProLogErrorCodes.PARSE_DADOS_ESCALA.errorCode(),
                    "O arquivo enviado está com problemas, tente novamente",
                    "Erro ao ler arquivo no servidor",
                    e);
        }
    }

    private EscalaDiariaItem createEscalaDiariaItem(final CSVRecord linha) throws ParseException {
        if (linha.get(0).isEmpty()) {
            return null;
        }
        final EscalaDiariaItem item = new EscalaDiariaItem();
        // PLACA
        if (!linha.get(0).trim().isEmpty()) {
            item.setPlaca(linha.get(0).trim());
        }
        // CODIGO DO MAPA
        if (!linha.get(1).trim().isEmpty()) {
            item.setCodMapa(Integer.parseInt(linha.get(1).trim()));
        }
        // DATA DA ESCALA
        if (!linha.get(2).trim().isEmpty()) {
            final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            final Date data = new Date(format.parse(linha.get(2).trim()).getTime());
            item.setData(data);
        }
        // CPF MOTORISTA
        if (!linha.get(3).trim().replaceAll("[^\\d]", "").isEmpty()) {
            item.setCpfMotorista(Long.parseLong(linha.get(3).trim().replaceAll("[^\\d]", "")));
        }
        // CPF AJUDANTE 1
        if (!linha.get(4).trim().replaceAll("[^\\d]", "").isEmpty()) {
            item.setCpfAjudante1(Long.parseLong(linha.get(4).trim().replaceAll("[^\\d]", "")));
        }
        // CPF AJUDANTE 2
        if (!linha.get(5).trim().replaceAll("[^\\d]", "").isEmpty()) {
            item.setCpfAjudante2(Long.parseLong(linha.get(5).trim().replaceAll("[^\\d]", "")));
        }
        return item;
    }

}
