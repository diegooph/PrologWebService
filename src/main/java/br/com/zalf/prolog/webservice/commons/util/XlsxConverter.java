package br.com.zalf.prolog.webservice.commons.util;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

/**
 * Created on 27/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class XlsxConverter {

    public void convertFileToCsv(@NotNull final File file,
                                 final int sheetIndex,
                                 @Nullable final SimpleDateFormat dateFormat) throws IOException {
        Preconditions.checkNotNull(file);
        Preconditions.checkArgument(sheetIndex >= 0);

        final FileInputStream fileInStream = new FileInputStream(file);

        // Open the xlsx and get the requested sheet from the workbook.
        final XSSFWorkbook workBook = new XSSFWorkbook(fileInStream);
        final XSSFSheet selSheet = workBook.getSheetAt(sheetIndex);

        // Iterate through all the rows in the selected sheet.
        try {
            final StringBuilder sb = new StringBuilder();
            for (final Row row : selSheet) {
                // Iterate through all the columns in the row and build ","
                // separated string.
                final Iterator<Cell> cellIterator = row.cellIterator();
                int count = 0;
                while (cellIterator.hasNext()) {
                    final Cell cell = cellIterator.next();
                    if (count != 0) {
                        sb.append(",");
                    }

                    // If you are using poi 4.0 or over, change it to
                    // cell.getCellType().
                    switch (cell.getCellTypeEnum()) {
                        case STRING:
                            sb.append(cell.getStringCellValue());
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                // Assumimos que quem chama o conversor sabe se a planilha tem ou não um campo de
                                // data/hora para ser convertido. E caso tenha, irá enviar um SimpleDateFormat não nulo.
                                //noinspection ConstantConditions
                                sb.append(dateFormat.format(cell.getDateCellValue()));
                            } else {
                                sb.append(cell.getNumericCellValue());
                            }
                            break;
                        case BOOLEAN:
                            sb.append(cell.getBooleanCellValue());
                            break;
                        default:
                    }
                    // Para previnir de adicionar vírgula onde não se deve.
                    count++;
                }
                sb.append("\r\n");
            }
            IOUtils.write(sb.toString(), new FileOutputStream(file), Charsets.UTF_8);
        } finally {
            IOUtils.closeQuietly(fileInStream, workBook);
        }
    }
}