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
package io.github.aivruu.packetboard.factory;

import io.github.aivruu.packetboard.board.RuntimeScoreboardMode;
import io.github.aivruu.packetboard.component.ComponentParserUtils;
import io.github.aivruu.packetboard.config.ConfigurationProvider;
import io.github.aivruu.packetboard.config.object.MessagesConfigModel;
import io.github.aivruu.packetboard.config.object.SettingsConfigModel;
import io.github.aivruu.packetboard.manager.BoardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ScoreboardFactory {
  private final BoardManager boardManager;
  private final ConfigurationProvider<SettingsConfigModel> settingsConfigProvider;
  private final ConfigurationProvider<MessagesConfigModel> messagesConfigProvider;

  public ScoreboardFactory(final BoardManager boardManager, final ConfigurationProvider<SettingsConfigModel> settingsConfigProvider,
                           final ConfigurationProvider<MessagesConfigModel> messagesConfigProvider) {
    this.boardManager = boardManager;
    this.settingsConfigProvider = settingsConfigProvider;
    this.messagesConfigProvider = messagesConfigProvider;
  }

  public void create(final Player player, final RuntimeScoreboardMode mode) {
    final var config = this.settingsConfigProvider.configModel();
    // Variable initialization logic to determinate if, regardless of scoreboard-mode, the scoreboard was created
    // and shown to the player.
    var scoreboardCreated = false;
    switch (mode) {
      case GLOBAL ->
        // Simplified condition with ternary-operator.
        scoreboardCreated = config.enableAnimatedTitleFeature
          ? this.boardManager.create(player, config.animatedTitleContent[0], config.globalLines)
          : this.boardManager.create(player, ComponentParserUtils.apply(config.globalTitle), config.globalLines);
      // The title-animation doesn't consider specific modes. so we avoid bugs with the title.
      case WORLD -> scoreboardCreated = !config.enableAnimatedTitleFeature && this.fromWorldSections(player);
      case PERMISSION -> scoreboardCreated = !config.enableAnimatedTitleFeature && this.fromPermissionSections(player);
      case GROUP -> scoreboardCreated = !config.enableAnimatedTitleFeature && this.fromGroupSections(player);
    };
    // Notify errors during scoreboard's creation to the player?
    if (config.notifyScoreboardCreationErrors && !scoreboardCreated) {
      player.sendMessage(ComponentParserUtils.apply(this.messagesConfigProvider.configModel().scoreboardCreationFailed));
    }
  }

  private boolean fromWorldSections(final Player player) {
    for (final var worldSection : this.settingsConfigProvider.configModel().scoreboardWorld) {
      if (!player.getWorld().getName().equals(worldSection.designedWorld)) continue;
      return this.boardManager.create(player, ComponentParserUtils.apply(worldSection.title), worldSection.lines);
    }
    return false;
  }

  private boolean fromPermissionSections(final Player player) {
    for (final var permissionSection : this.settingsConfigProvider.configModel().scoreboardPermission) {
      if (!player.hasPermission(permissionSection.node)) continue;
      return this.boardManager.create(player, ComponentParserUtils.apply(permissionSection.title), permissionSection.lines);
    }
    return false;
  }

  private boolean fromGroupSections(final Player player) {
    // This mode requires LuckPerms available for groups settings.
    if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
      return false;
    }
    for (final var groupSection : this.settingsConfigProvider.configModel().scoreboardGroup) {
      // Group check by LuckPerms' API.
      return this.boardManager.create(player, ComponentParserUtils.apply(groupSection.title), groupSection.lines);
    }
    return false;
  }
}
