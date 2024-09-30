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

import io.github.aivruu.packetboard.board.CachedBoardModel;
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
import io.github.aivruu.packetboard.repository.RepositoryModel;
import io.github.aivruu.packetboard.task.LinesUpdatePluginTask;
import io.github.aivruu.packetboard.task.TitleAnimationPluginTask;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public final class PacketBoardPlugin extends JavaPlugin implements PacketBoard {
  private ComponentLogger logger;
  private ConfigurationProvider<SettingsConfigModel> settingsConfigProvider;
  private ConfigurationProvider<MessagesConfigModel> messagesConfigProvider;
  private RepositoryModel<CachedBoardModel> boardRepository;
  private BoardManager boardManager;
  private PlayerRegistryListener scoreboardsRegistryListener;
  private LinesUpdatePluginTask linesUpdatePluginTask;
  private TitleAnimationPluginTask titleAnimationPluginTask;

  @Override
  public RepositoryModel<CachedBoardModel> boardRepository() {
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
    this.logger.info(Component.text("Loading configurations...").color(NamedTextColor.AQUA));
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
    this.boardRepository = new BoardRepositoryModel();
    this.boardManager = new BoardManager(this.boardRepository);
    this.logger.info(Component.text("Initialized main plugin APIs.").color(NamedTextColor.YELLOW));
    this.registerPluginTasks();
    this.logger.info(Component.text("Initialized necessary plugin-tasks.").color(NamedTextColor.YELLOW));
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
    final var scoreboardFactory = new ScoreboardFactory(this.boardManager);
    super.getServer().getPluginManager().registerEvents(
      this.scoreboardsRegistryListener = new PlayerRegistryListener(this.logger, this.boardManager, scoreboardFactory,
        this.settingsConfigProvider.configModel()), this);
  }

  private void registerPluginTasks() {
    final var asyncScheduler = super.getServer().getAsyncScheduler();
    final var config = this.settingsConfigProvider.configModel();
    // Only register, and start thread-executors if features are enabled from configuration.
    if (config.enableLinesRefreshing) {
      this.linesUpdatePluginTask = new LinesUpdatePluginTask(this.boardRepository, config);
      asyncScheduler.runAtFixedRate(this, this.linesUpdatePluginTask, 0,
        config.linesUpdateRateSeconds, TimeUnit.SECONDS);
    }
    if (config.enableAnimatedTitleFeature) {
      this.titleAnimationPluginTask = new TitleAnimationPluginTask(this.boardRepository, config.animatedTitleContent);
      asyncScheduler.runAtFixedRate(this, this.titleAnimationPluginTask, 0,
        config.animatedTitleUpdateRateSeconds, TimeUnit.SECONDS);
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

  public boolean reload() {
    if (!this.updateConfigurations()) {
      return false;
    }
    final var config = this.settingsConfigProvider.configModel();
    // If features that requires periodic-tasks are enabled, we update these tasks' attributes.
    if (config.enableLinesRefreshing) {
      this.linesUpdatePluginTask.configModel(config);
    }
    if (config.enableAnimatedTitleFeature) {
      this.titleAnimationPluginTask.content(config.animatedTitleContent);
    }
    // Update config-model for event-listener instance.
    this.scoreboardsRegistryListener.configModel(config);
    return true;
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
    if (this.boardManager != null) {
      this.boardManager.close();
    }
    super.getServer().getAsyncScheduler().cancelTasks(this);
    this.logger.info(Component.text("Plugin disabled!").color(NamedTextColor.RED));
  }
}
