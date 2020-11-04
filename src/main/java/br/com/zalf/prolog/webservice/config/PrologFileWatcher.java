package br.com.zalf.prolog.webservice.config;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.entrega.mapa.validator.RegrasPlanilhaMapaLoader;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils.FamiliaModeloBloqueadoLoader;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
public class PrologFileWatcher implements FileChangeListener {
    @NotNull
    private static final String TAG = PrologFileWatcher.class.getSimpleName();
    @NotNull
    private static final String WATCH_DIRECTORY = "configs/";
    @NotNull
    private static final Duration TIME_BETWEEN_CHECKS =
            BuildConfig.DEBUG ? Duration.ofMillis(5000L) : Duration.ofMinutes(5);
    @NotNull
    private static final Duration TIME_TO_APPLY_CHANGES =
            BuildConfig.DEBUG ? Duration.ofMillis(3000L) : Duration.ofMinutes(1);
    @NotNull
    private static final Map<String, Watchable> listeners = setupListeners();

    public interface Watchable {
        @NotNull
        String getFileName();
        void onFileChanged();
    }

    @Bean
    public FileSystemWatcher fileSystemWatcher() {
        final URL resource = getClass().getClassLoader().getResource(WATCH_DIRECTORY);
        if (resource == null) {
            return null;
        }
        final FileSystemWatcher watcher =
                new FileSystemWatcher(true, TIME_BETWEEN_CHECKS, TIME_TO_APPLY_CHANGES);
        watcher.addSourceDirectory(new File(resource.getFile()));
        watcher.addListener(this);
        watcher.start();
        Log.d(TAG, "starting watcher");
        return watcher;
    }

    @PreDestroy
    public void onDestroy() {
        Log.d(TAG, "stopping watcher");
        fileSystemWatcher().stop();
    }

    @Override
    public void onChange(final Set<ChangedFiles> changeSet) {
        Log.d(TAG, "File changed");
        for (final ChangedFiles changedFiles : changeSet) {
            for (final ChangedFile file : changedFiles.getFiles()) {
                listeners.get(file.getFile().getName()).onFileChanged();
            }
        }
    }

    @NotNull
    private static Map<String, Watchable> setupListeners() {
        return new HashMap<String, Watchable>() {{
            put(FamiliaModeloBloqueadoLoader.of().getFileName(), FamiliaModeloBloqueadoLoader.of());
            put(RegrasPlanilhaMapaLoader.of().getFileName(), RegrasPlanilhaMapaLoader.of());
        }};
    }
}