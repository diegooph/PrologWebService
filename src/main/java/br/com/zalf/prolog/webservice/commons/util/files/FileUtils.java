package br.com.zalf.prolog.webservice.commons.util.files;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

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

    @NotNull
    public static File getTempDir() {
        return new File(TEMP_DIR_REF);
    }

    public static boolean isOutdated(@NotNull final File file) {
        final FileTime fileTime = getFileTimeFromFile(file)
                .orElseThrow();
        final LocalDateTime fileTimeToTimestamp = LocalDateTime.ofInstant(fileTime.toInstant(),
                                                                          ZoneId.systemDefault());
        return DateUtils.isBeforeNDays(fileTimeToTimestamp, DAYS_TO_OUTDATED);
    }

    @NotNull
    public static Optional<FileTime> getFileTimeFromFile(@NotNull final File file) {
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