package br.com.zalf.prolog.webservice.schedules;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.files.FileUtils;
import br.com.zalf.prolog.webservice.schedules.time.EveryTwoDaysAtTwoHours;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Stream;

/**
 * Created on 2020-11-30
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Profile("prod")
@Component
public class DeleteTempFileScheduler implements Scheduler {

    private static final String TAG = DeleteTempFileScheduler.class.getSimpleName();
    private static final int DAYS_TO_OUTDATED = 2;

    @Override
    @EveryTwoDaysAtTwoHours
    public void doWork() {
        final File baseTempDir = FileUtils.getTempDir();
        deleteFiles(baseTempDir);
    }

    public boolean isOutdated(@NotNull final File file) {
        final FileTime fileTime = FileUtils.getFileTimeFromFile(file)
                .orElseThrow();
        final LocalDateTime fileTimeToTimestamp = LocalDateTime.ofInstant(fileTime.toInstant(),
                                                                          ZoneId.systemDefault());
        return DateUtils.isAfterNDays(fileTimeToTimestamp, DAYS_TO_OUTDATED);
    }

    private void deleteFiles(@NotNull final File dir) {
        Log.i(TAG, "Diretório analisado: " + dir.getAbsolutePath());
        final Path absolutePath = Paths.get(dir.toURI());
        try (final Stream<Path> walk = Files.walk(absolutePath)) {
            final boolean allFillesDeleted = walk
                    .map(Path::toFile)
                    .peek(file -> Log.i(TAG, "Arquivos ou diretórios em analise: " + file.getAbsolutePath()))
                    .filter(this::canDeleteFileOrDir)
                    .peek(file -> Log.i(TAG, "Arquivos ou diretórios para deleção: " + file.getAbsolutePath()))
                    .allMatch(FileUtils::delete);
            if (allFillesDeleted) {
                Log.i(TAG, "Arquivos e diretórios temporários deletados com sucesso!");
                return;
            }
            throw new IllegalStateException("Não foi possivel deletar todos os arquivos e diretórios!");
        } catch (final IOException exception) {
            Log.e(TAG, "Erro ao realizar deleção dos arquivos", exception);
        }
    }

    private boolean canDeleteFileOrDir(@NotNull final File fileOrDir) {
        return fileOrDir.canRead()
                && this.isOutdated(fileOrDir)
                && (FileUtils.isAFile(fileOrDir) || this.isAValidDirForDeletion(fileOrDir));
    }

    private boolean isAValidDirForDeletion(@NotNull final File directory) {
        try {
            return directory.isDirectory() && FileUtils.isDirEmpty(directory);
        } catch (final IOException e) {
            Log.e(TAG, "Erro ao analisar o diretório: " + directory.getAbsolutePath(), e);
            return false;
        }
    }
}
