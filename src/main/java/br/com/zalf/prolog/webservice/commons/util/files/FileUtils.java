package br.com.zalf.prolog.webservice.commons.util.files;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created on 17/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FileUtils {

    private static final String TEMP_DIR_REF = System.getProperty("java.io.tmpdir");

    private FileUtils() {
        throw new IllegalStateException(FileUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static String normalizeNameOrThrow(@NotNull final String fileName) {
        final String normalized = FilenameUtils.normalize(fileName);
        if (normalized == null) {
            throw new IllegalStateException("Error to normalize filename: " + fileName);
        }

        return normalized;
    }

    @NotNull
    public static File createTempDir() {
        return com.google.common.io.Files.createTempDir();
    }

    public static File getTempDir() {
        final String baseName = getBaseName();
        return new File(TEMP_DIR_REF, baseName);
    }

    private static String getBaseName() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}