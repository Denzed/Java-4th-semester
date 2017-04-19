package ru.spbau.daniil.smirnov.mygit.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class which builds a context for the {@link org.apache.logging.log4j.Logger} associated with {@link ru.spbau.daniil.smirnov.mygit.InternalUpdater}
 */
public class Log4j2ContextBuilder {
        /**
         * Generates a context with a single logger and a RollingFile appender.
         * @param directory log storage directory
         * @return the generated context
         */
        @NotNull
        public static LoggerContext createContext(@NotNull Path directory) {
            final ConfigurationBuilder<BuiltConfiguration> builder =
                    ConfigurationBuilderFactory.newConfigurationBuilder();
            builder.setConfigurationName("MyGitLogger");
            builder.setStatusLevel(Level.OFF);

            final LayoutComponentBuilder layoutBuilder =
                    builder
                            .newLayout("PatternLayout")
                            .addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable");

            final ComponentBuilder<?> triggeringPolicy =
                    builder
                            .newComponent("Policies")
                            .addComponent(
                                    builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "1MB"));

            final ComponentBuilder<?> rolloverStrategy =
                    builder
                            .newComponent("DefaultRolloverStrategy")
                            .addAttribute("max", 3);

            final Path logsPath = Paths.get(directory.toAbsolutePath().toString(), ".mygit", "logs");

            final AppenderComponentBuilder appenderBuilder =
                    builder
                            .newAppender("file", "ROLLINGFILE")
                            .addAttribute("fileName", Paths.get(logsPath.toString(), "mygit0.log").toString())
                            .addAttribute("filePattern", Paths.get(logsPath.toString(), "mygit%i.log").toString())
                            .add(layoutBuilder)
                            .addComponent(triggeringPolicy)
                            .addComponent(rolloverStrategy);
            builder.add(appenderBuilder);

            final RootLoggerComponentBuilder rootLogger =
                    builder
                            .newRootLogger(Level.TRACE)
                            .add(builder.newAppenderRef("file"))
                            .addAttribute("additivity", false);
            builder.add(rootLogger);
            
            return Configurator.initialize(builder.build());
        }

        private Log4j2ContextBuilder() {}
}
