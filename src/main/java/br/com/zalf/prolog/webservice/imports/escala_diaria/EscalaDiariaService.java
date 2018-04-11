package br.com.zalf.prolog.webservice.imports.escala_diaria;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.Now;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
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

    public Response uploadMapa(@NotNull final Long codUnidade,
                               @NotNull final InputStream fileInputStream,
                               @NotNull final FormDataContentDisposition fileDetail) {
        try {
            final File file = createFileFromImport(codUnidade, fileInputStream, fileDetail);
            readAndInsertImport(codUnidade, file.getPath());
            return Response.ok("Dados da escala inseridos com sucesso");
        } catch (IOException e) {
            Log.e(TAG, "Erro ao inserir os dados da escala na unidade: " + codUnidade, e);
            return Response.error("Erro ao inserir os dados da escala");
        } catch (ParseException e) {
            e.printStackTrace();
            return Response.error("Erro ao inserir os dados da escala");
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Erro ao inserir os dados da escala");
        }
    }

    public Response insertOrUpdateEscalaDiaria(@NotNull final Long codUnidade,
                                               @NotNull final EscalaDiariaItem escalaDiariaItem,
                                               boolean isInsert) {
        Preconditions.checkNotNull(escalaDiariaItem, "escalaDiariaItem não pode ser nulla!");
        try {
            dao.insertOrUpdateEscalaDiariaItem(codUnidade, escalaDiariaItem, isInsert);
            if (isInsert) {
                return Response.ok("Escala inserida com sucesso!");
            } else {
                return Response.ok("Escala atualizada com sucesso!");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao atualizar escala: " + escalaDiariaItem.getCodEscala(), e);
            if (isInsert) {
                return Response.error("Erro ao inserir escala!");
            } else {
                return Response.error("Erro ao atualizar escala!");
            }
        }
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

    public Response deleteEscalaDiariaItens(@NotNull final Long codUnidade,
                                            @NotNull final List<Long> codEscalas) {
        try {
            dao.deleteEscalaDiariaItens(codUnidade, codEscalas);
            return Response.ok("Escalas deletadas com sucesso!");
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao deletar Escalas: " + codEscalas, e);
            return Response.error("Erro ao deletar escalas!");
        }
    }

    private File createFileFromImport(@NotNull final Long codUnidade,
                                      @NotNull final InputStream fileInputStream,
                                      @NotNull final FormDataContentDisposition fileDetail) throws IOException {
        final String fileName = String.valueOf(Now.utcMillis()) + "_" + codUnidade
                + "_" + fileDetail.getFileName().replace(" ", "_");
        // Pasta temporária da JVM
        final File tmpDir = new File(System.getProperty("java.io.tmpdir"), "escalaDiaria");
        if (!tmpDir.exists()) {
            // Cria a pasta mapas se não existe
            tmpDir.mkdir();
        }
        // Cria o arquivo
        final File file = new File(tmpDir, fileName);
        final FileOutputStream out = new FileOutputStream(file);
        IOUtils.copy(fileInputStream, out);
        IOUtils.closeQuietly(out);
        return file;
    }

    private void readAndInsertImport(@NotNull final Long codUnidade, @NotNull final String path)
            throws IOException, SQLException, ParseException {
        final List<EscalaDiariaItem> escalaItens = new ArrayList<>();
        final Reader in = new FileReader(path);
        final List<CSVRecord> tabela = CSVFormat.DEFAULT.withDelimiter(';').parse(in).getRecords();
        for (int i = 1; i < tabela.size(); i++) {
            final EscalaDiariaItem item = createEscalaDiariaItem(tabela.get(i));
            if (item != null) {
                escalaItens.add(item);
            }
        }
        dao.insertOrUpdateEscalaDiaria(codUnidade, escalaItens);
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
