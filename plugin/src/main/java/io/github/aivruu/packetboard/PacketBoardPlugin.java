// This file is part of packet-board, licensed under the GNU License.
//
// Copyright (c) 2024 aivruu
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.
package io.github.aivruu.packetboard;

import io.github.aivruu.packetboard.command.MainCommand;
import io.github.aivruu.packetboard.command.RegistrableCommandModel;
import io.github.aivruu.packetboard.command.ScoreboardControlCommand;
import io.github.aivruu.packetboard.config.ConfigurationProvider;
import io.github.aivruu.packetboard.config.object.MessagesConfigModel;
import io.github.aivruu.packetboard.config.object.SettingsConfigModel;
import io.github.aivruu.packetboard.factory.ScoreboardFactory;
import io.github.aivruu.packetboard.listener.PlayerRegistryListener;
import io.github.aivruu.packetboard.manager.BoardManager;
import io.github.aivruu.packetboard.board.BoardRepositoryModel;
import io.github.aivruu.packetboard.thread.ThreadExecutorRepositoryModel;
import io.github.aivruu.packetboard.thread.impl.AsyncLinesUpdateThread;
import io.github.aivruu.packetboard.thread.impl.AsyncTitleAnimationThread;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class PacketBoardPlugin extends JavaPlugin implements PacketBoard {
  public static final byte SUCCESSFUL_RELOAD_STATUS = 0;
  public static final byte THREAD_SHUTDOWN_ANOMALY_STATUS = 1;
  public static final byte CONFIGURATION_RELOAD_ANOMALY_STATUS = 2;
  private static final String TITLE_ANIMATION_EXECUTOR_NAME = "scoreboard-title-animation-thread";
  private static final String LINES_REFRESHER_EXECUTOR_NAME = "scoreboard-lines-updater-thread";
  private ComponentLogger logger;
  private ConfigurationProvider<SettingsConfigModel> settingsConfigProvider;
  private ConfigurationProvider<MessagesConfigModel> messagesConfigProvider;
  private ThreadExecutorRepositoryModel threadExecutorRepository;
  private BoardRepositoryModel boardRepository;
  private BoardManager boardManager;
  private PlayerRegistryListener scoreboardsRegistryListener;

  @Override
  public BoardRepositoryModel boardRepository() {
    return this.boardRepository;
  }

  @Override
  public BoardManager boardManager() {
    return this.boardManager;
  }

  @Override
  public void onLoad() {
    final var directory = super.getDataFolder().toPath();
    this.logger = super.getComponentLogger();
    this.logger.info(Component.text("Loading essential plugin components...").color(NamedTextColor.AQUA));
    this.settingsConfigProvider = ConfigurationProvider.of(directory, "config.conf", SettingsConfigModel.class);
    this.messagesConfigProvider = ConfigurationProvider.of(directory, "messages.conf", MessagesConfigModel.class);
  }

  @Override
  public void onEnable() {
    if (this.settingsConfigProvider == null || this.messagesConfigProvider == null) {
      this.logger.error(Component.text("Some, or both configuration files couldn't be loaded.").color(NamedTextColor.YELLOW));
      return;
    }
    PacketBoardProvider.set(this);
    // Main plugin APIs and controllers initialization process.
    this.threadExecutorRepository = new ThreadExecutorRepositoryModel(this.logger);
    this.registerThreadExecutors();
    this.logger.info(Component.text("Initialized and cached required thread-executors.").color(NamedTextColor.YELLOW));
    this.boardRepository = new BoardRepositoryModel();
    this.boardManager = new BoardManager(this.boardRepository);
    this.logger.info(Component.text("Initialized API main scoreboard controllers.").color(NamedTextColor.YELLOW));
    // Commands registration process.
    this.registerCommands(
      new MainCommand(this, this.messagesConfigProvider),
      new ScoreboardControlCommand(this.boardManager, this.messagesConfigProvider, this.settingsConfigProvider));
    this.logger.info(Component.text("Registered commands.").color(NamedTextColor.YELLOW));
    this.registerListener();
    this.logger.info(Component.text("Registered event listener.").color(NamedTextColor.AQUA));
    this.logger.info(Component.text("Plugin enabled!").color(NamedTextColor.GREEN));
  }

  private void registerListener() {
    // Listeners registration process.
    final var scoreboardFactory = new ScoreboardFactory(this.boardManager, LuckPermsProvider.get().getUserManager());
    super.getServer().getPluginManager().registerEvents(
      this.scoreboardsRegistryListener = new PlayerRegistryListener(this.logger, this.boardManager, scoreboardFactory,
        this.settingsConfigProvider.configModel()), this);
  }

  private void registerThreadExecutors() {
    final var config = this.settingsConfigProvider.configModel();
    // Only register, and start thread-executors if features are enabled from configuration.
    if (config.enableLinesRefreshing) {
      final var scoreboardLinesUpdaterThread = new AsyncLinesUpdateThread(this.boardRepository,
        this.settingsConfigProvider.configModel());
      // Schedule period-rate for thread and store it.
      scoreboardLinesUpdaterThread.schedule(config.linesUpdateRateInTicks);
      this.threadExecutorRepository.saveSync(scoreboardLinesUpdaterThread);
    }
    if (config.enableAnimatedTitleFeature) {
      final var scoreboardTitleAnimationThread = new AsyncTitleAnimationThread(this.boardRepository, config.animatedTitleContent);
      scoreboardTitleAnimationThread.schedule(config.animatedTitleUpdateRateInTicks);
      this.threadExecutorRepository.saveSync(scoreboardTitleAnimationThread);
    }
  }

  @SuppressWarnings("UnstableApiUsage")
  private void registerCommands(final RegistrableCommandModel... registrableCommandModels) {
    super.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
      for (final var registrableCommandModel : registrableCommandModels) {
        // Register commands attaching its [LiteralCommandNode] and it's alias.
        event.registrar().register(registrableCommandModel.register(), registrableCommandModel.alias());
      }
    });
  }

  public byte reload() {
    if (this.updateConfigurations()) {
      return CONFIGURATION_RELOAD_ANOMALY_STATUS;
    }
    final var config = this.settingsConfigProvider.configModel();
    final var linesUpdateThreadExecutor = this.threadExecutorRepository.findSync(LINES_REFRESHER_EXECUTOR_NAME);
    final var titleAnimationThreadExecutor = this.threadExecutorRepository.findSync(TITLE_ANIMATION_EXECUTOR_NAME);
    // We check if features-that-require-threads are enable, then we update these threads' attributes.
    // Otherwise, if these features are now disabled and threads are still active, we need shutdown them
    // to avoid unnecessary task-processing.
    //
    // If the shutdown-process for the executors isn't normal, we return the status-type for these anomaly-types.
    if (config.enableLinesRefreshing) {
      // Cast it into it's [AsyncLinesUpdateThread] implementation for config-provider modification access.
      // It will not be null at this point.
      ((AsyncLinesUpdateThread) linesUpdateThreadExecutor).configModel(config);
    } else if ((linesUpdateThreadExecutor != null) && !this.threadExecutorRepository.deleteSync(LINES_REFRESHER_EXECUTOR_NAME)) {
      return THREAD_SHUTDOWN_ANOMALY_STATUS;
    }
    if (config.enableAnimatedTitleFeature) {
      // Cast it into it's [AsyncTitleAnimationThread] implementation for content modification.
      ((AsyncTitleAnimationThread) titleAnimationThreadExecutor).content(config.animatedTitleContent);
    } else if ((titleAnimationThreadExecutor != null) && !this.threadExecutorRepository.deleteSync(TITLE_ANIMATION_EXECUTOR_NAME)) {
      return THREAD_SHUTDOWN_ANOMALY_STATUS;
    }
    // Update config-model for event-listener instance.
    this.scoreboardsRegistryListener.configModel(config);
    return SUCCESSFUL_RELOAD_STATUS;
  }

  public boolean updateConfigurations() {
    final var settingsModificationStatus = this.settingsConfigProvider.reload();
    final var messagesModificationStatus = this.messagesConfigProvider.reload();
    if (settingsModificationStatus.failedOnModificationStatus() || messagesModificationStatus.failedOnModificationStatus()) {
      this.logger.info(Component.text("One, or both configuration files couldn't be reloaded.").color(NamedTextColor.RED));
      return false;
    }
    this.settingsConfigProvider = settingsModificationStatus.result();
    this.messagesConfigProvider = messagesModificationStatus.result();
    return true;
  }

  @Override
  public void onDisable() {
    if (this.threadExecutorRepository != null) {
      this.threadExecutorRepository.clearRegistry();
    }
    if (this.boardManager != null) {
      this.boardManager.close();
    }
    this.logger.info(Component.text("Plugin disabled!").color(NamedTextColor.RED));
  }
}
