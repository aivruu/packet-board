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

import io.github.aivruu.packetboard.board.RuntimeScoreboardMode;
import io.github.aivruu.packetboard.command.MainCommand;
import io.github.aivruu.packetboard.command.RegistrableCommandModel;
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
import org.bukkit.plugin.java.JavaPlugin;

public final class PacketBoardPlugin extends JavaPlugin implements PacketBoard {
  private ComponentLogger logger;
  private ConfigurationProvider<SettingsConfigModel> settingsConfigProvider;
  private ConfigurationProvider<MessagesConfigModel> messagesConfigProvider;
  private ThreadExecutorRepositoryModel threadExecutorRepository;
  private BoardRepositoryModel boardRepository;
  private BoardManager boardManager;
  private ScoreboardFactory scoreboardFactory;

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
    if (this.checkInvalidSettings()) {
      this.logger.error(Component.text("If you selected the 'GROUP' mode, you require have LuckPerms installed. Otherwise, you specified an invalid update-rate value for the title-animation.").color(NamedTextColor.YELLOW));
      return;
    }
    PacketBoardProvider.set(this);
    this.threadExecutorRepository = new ThreadExecutorRepositoryModel(this.logger);
    this.registerThreadExecutors();
    this.logger.info(Component.text("Initialized and cached required thread-executors.").color(NamedTextColor.YELLOW));
    this.boardRepository = new BoardRepositoryModel();
    this.boardManager = new BoardManager(this.boardRepository);
    this.scoreboardFactory = new ScoreboardFactory(this.boardManager, this.settingsConfigProvider, this.messagesConfigProvider);
    this.logger.info(Component.text("Initialized API main scoreboard controllers.").color(NamedTextColor.YELLOW));
    this.registerCommands(new MainCommand(this.boardManager));
    this.logger.info(Component.text("Registered commands.").color(NamedTextColor.YELLOW));
    super.getServer()
      .getPluginManager()
      .registerEvents(new PlayerRegistryListener(this.logger, this.boardManager, this.scoreboardFactory, this.settingsConfigProvider), this);
    this.logger.info(Component.text("Registered event listener.").color(NamedTextColor.AQUA));
    this.logger.info(Component.text("Plugin enabled!").color(NamedTextColor.GREEN));
  }

  private boolean checkInvalidSettings() {
    final var config = this.settingsConfigProvider.configModel();
    final var luckPermsNotInstalled = super.getServer().getPluginManager().getPlugin("LuckPerms") == null;
    return ((config.mode == RuntimeScoreboardMode.GROUP) && !luckPermsNotInstalled)
      || (config.enableAnimatedTitleFeature && config.animatedTitleUpdateRate > config.animatedTitleContent.length);
  }

  public void registerThreadExecutors() {
    final var config = this.settingsConfigProvider.configModel();
    // Only register, and start thread-executors if features are enabled from configuration.
    if (config.enableLinesRefreshing) {
      final var scoreboardLinesUpdaterThread = new AsyncLinesUpdateThread(this.boardRepository, this.settingsConfigProvider);
      scoreboardLinesUpdaterThread.schedule(config.linesUpdateRate);
      this.threadExecutorRepository.saveSync(scoreboardLinesUpdaterThread);
    }
    if (config.enableAnimatedTitleFeature) {
      final var scoreboardTitleAnimationThread = new AsyncTitleAnimationThread(this.boardRepository, config.animatedTitleContent);
      scoreboardTitleAnimationThread.schedule(config.animatedTitleUpdateRate);
      this.threadExecutorRepository.saveSync(scoreboardTitleAnimationThread);
    }
  }

  @SuppressWarnings("UnstableApiUsage")
  private void registerCommands(final RegistrableCommandModel... registrableCommandModels) {
    final var lifecycleEventManager = super.getLifecycleManager();
    for (final var registrableCommandModel : registrableCommandModels) {
      lifecycleEventManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
        final var commands = event.registrar();
        commands.register(registrableCommandModel.register());
      });
    }
  }

  public boolean reload() {
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
