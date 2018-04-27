package br.com.zalf.prolog.webservice.commons.util;

import com.google.common.base.Preconditions;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created on 27/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class XlsxConverter {

    public void convertFileToCsv(@NotNull final File file, final int sheetIndex) throws IOException {
        Preconditions.checkNotNull(file);
        Preconditions.checkArgument(sheetIndex >= 0);

        final FileInputStream fileInStream = new FileInputStream(file);

        // Open the xlsx and get the requested sheet from the workbook.
        final XSSFWorkbook workBook = new XSSFWorkbook(fileInStream);
        final XSSFSheet selSheet = workBook.getSheetAt(sheetIndex);

        // Iterate through all the rows in the selected sheet.
        try {
            for (final Row row : selSheet) {
                // Iterate through all the columns in the row and build ","
                // separated string.
                final Iterator<Cell> cellIterator = row.cellIterator();
                final StringBuilder sb = new StringBuilder();
                while (cellIterator.hasNext()) {
                    final Cell cell = cellIterator.next();
                    if (sb.length() != 0) {
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
                                sb.append(cell.getDateCellValue());
                            } else {
                                sb.append(cell.getNumericCellValue());
                            }
                            break;
                        case BOOLEAN:
                            sb.append(cell.getBooleanCellValue());
                            break;
                        default:
                    }
                }
                System.out.println(sb.toString());
            }
        } finally {
            workBook.close();
        }
    }
}