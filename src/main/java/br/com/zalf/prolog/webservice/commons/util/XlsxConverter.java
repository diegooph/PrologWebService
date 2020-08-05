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

/**
 * Created on 27/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class XlsxConverter {
    private static final int PROCESS_ALL_COLUMNS = Integer.MIN_VALUE;

    public void convertFileToCsv(@NotNull final File file,
                                 final int sheetIndex,
                                 final int numberOfColumnsToProcess,
                                 @Nullable final SimpleDateFormat dateFormat) throws IOException {
        internalConvertFileToCsv(file, sheetIndex, numberOfColumnsToProcess, dateFormat);
    }

    public void convertFileToCsv(@NotNull final File file,
                                 final int sheetIndex,
                                 @Nullable final SimpleDateFormat dateFormat) throws IOException {
        internalConvertFileToCsv(file, sheetIndex, PROCESS_ALL_COLUMNS, dateFormat);
    }

    private void internalConvertFileToCsv(@NotNull final File file,
                                          final int sheetIndex,
                                          int numberOfColumnsToProcess,
                                          @Nullable final SimpleDateFormat dateFormat) throws IOException {
        Preconditions.checkNotNull(file);
        Preconditions.checkArgument(sheetIndex >= 0);

        final FileInputStream fileInputStream = new FileInputStream(file);
        final XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream);
        final XSSFSheet sheet = workBook.getSheetAt(sheetIndex);

        // Iterate through all the rows in the selected sheet.
        try {
            final StringBuilder sb = new StringBuilder();
            final int rowEnd = sheet.getLastRowNum();
            for (int rowNum = 0; rowNum <= rowEnd; rowNum++) {
                final Row row = sheet.getRow(rowNum);
                if (row != null) {
                    // Iterate through all the columns in the row and build "," separated string.
                    numberOfColumnsToProcess = numberOfColumnsToProcess == PROCESS_ALL_COLUMNS
                            ? row.getLastCellNum()
                            : numberOfColumnsToProcess;
                    for (int colNum = 0, lastCellNum = numberOfColumnsToProcess; colNum < lastCellNum; colNum++) {
                        final Cell cell = row.getCell(colNum);
                        if (colNum != 0) {
                            sb.append(",");
                        }

                        if (cell != null) {
                            // If you are using poi 4.0 or over, change it to
                            // cell.getCellType().
                            switch (cell.getCellTypeEnum()) {
                                case STRING:
                                    sb.append(cell.getStringCellValue());
                                    break;
                                case NUMERIC:
                                    if (DateUtil.isCellDateFormatted(cell)) {
                                        // Assumimos que quem chama o conversor sabe se a planilha tem ou não um campo de

                                        // data/hora para ser convertido. E caso tenha, irá enviar um
                                        // SimpleDateFormat não nulo.
                                        //noinspection ConstantConditions
                                        sb.append(dateFormat.format(cell.getDateCellValue()));
                                    } else {
                                        // Excel stores integer values as double values
                                        // read an integer if the double value equals the
                                        // integer value.
                                        final double x = cell.getNumericCellValue();
                                        if (x == Math.rint(x) && !Double.isNaN(x) && !Double.isInfinite(x)) {
                                            sb.append(String.valueOf((long) x));
                                        } else {
                                            sb.append(String.valueOf(x));
                                        }
                                    }
                                    break;
                                case BOOLEAN:
                                    sb.append(cell.getBooleanCellValue());
                                    break;
                                default:
                            }
                        }
                    }
                    sb.append("\r\n");
                }
            }
            IOUtils.write(sb.toString(), new FileOutputStream(file), Charsets.UTF_8);
        } finally {
            IOUtils.closeQuietly(fileInputStream, workBook);
        }
    }
}