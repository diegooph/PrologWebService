package br.com.zalf.prolog.webservice.commons.util.files;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created on 17/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();
    private static final long DAYS_TO_OUTDATED = 2;
    private static final String TEMP_DIR_REF = System.getProperty("java.io.tmpdir");

    private FileUtils() {
        throw new IllegalStateException(FileUtils.class.getSimpleName() + " cannot be instantiated!");
    }
    
    public static void createTempDir() {
        final String baseName = getBaseName();
        final File baseDir = new File(TEMP_DIR_REF);
        final File tmpDir = new File(baseDir, baseName);
        if (!tmpDir.exists()) {
            Log.i(TAG, "Realizando criação do diretório:" + tmpDir.getName());
            if (!tmpDir.mkdir()) {
                throw new IllegalStateException("Failed to create temp directory: " + baseName);
            }
        }
    }

    @NotNull
    public static File getTempDir() {
        return new File(TEMP_DIR_REF);
    }

    @NotNull
    public static List<File> getAllCreatedTempDirs() {
        final File baseTempDir = new File(TEMP_DIR_REF);
        return Arrays.stream(baseTempDir.listFiles())
                .filter(File::isDirectory)
                .filter(subDir -> DateUtils.isValid(subDir.getName(), "dd-MM-yyyy"))
                .peek(subDir -> Log.i(TAG, "Diretório adquirido: " + subDir.getName()))
                .collect(Collectors.toList());
    }

    public static boolean isOutdated(final File file) {
        final FileTime fileTime = getFileTimeFromFile(file)
                .orElseThrow();
        final LocalDateTime fileTimeToTimestamp = LocalDateTime.ofInstant(fileTime.toInstant(),
                                                                          ZoneId.systemDefault());
        return DateUtils.isBeforeNDays(fileTimeToTimestamp, DAYS_TO_OUTDATED);
    }

    @NotNull
    private static String getBaseName() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public static Optional<FileTime> getFileTimeFromFile(final File file) {
        try {
            final var fileTime = Files.readAttributes(file.toPath(), BasicFileAttributes.class)
                    .creationTime();
            return Optional.of(fileTime);
        } catch (final IOException e) {
            Log.e(TAG, "Erro ao tentar ler arquivo: " + file.getName());
            return Optional.empty();
        }
    }
}