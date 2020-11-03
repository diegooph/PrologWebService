package br.com.zalf.prolog.webservice.config;

import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.File;
import java.time.Duration;

@Configuration
public class FileWatcherConfig {
    @Bean
    public FileSystemWatcher fileSystemWatcher() {
        final FileSystemWatcher fileSystemWatcher =
                new FileSystemWatcher(true, Duration.ofMillis(5000L), Duration.ofMillis(3000L));
        fileSystemWatcher.addSourceDirectory(
                new File("configs/familia_modelo_bloqueado_nepomuceno.yaml").getAbsoluteFile());
        fileSystemWatcher.addListener(new MyFileChangeListener());
        fileSystemWatcher.start();
        System.out.println("starting fileSystemWatcher");
        return fileSystemWatcher;
    }

    @PreDestroy
    public void onDestroy() {
        System.out.println("stopping fileSystemWatcher");
        fileSystemWatcher().stop();
    }
}