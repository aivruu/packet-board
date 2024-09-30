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
import io.github.aivruu.packetboard.config.object.SettingsConfigModel;
import io.github.aivruu.packetboard.manager.BoardManager;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ScoreboardFactory {
  private final BoardManager boardManager;
  private final ConfigurationProvider<SettingsConfigModel> settingsConfigProvider;
  private final UserManager luckPermsUserManager;

  public ScoreboardFactory(final BoardManager boardManager, final ConfigurationProvider<SettingsConfigModel> settingsConfigProvider,
                           final UserManager luckPermsUserManager) {
    this.boardManager = boardManager;
    this.settingsConfigProvider = settingsConfigProvider;
    this.luckPermsUserManager = luckPermsUserManager;
  }

  public void create(final Player player, final RuntimeScoreboardMode mode) {
    final var config = this.settingsConfigProvider.configModel();
    switch (mode) {
      case GLOBAL -> {
        if (config.enableAnimatedTitleFeature) {
          this.boardManager.create(player, config.animatedTitleContent[0], config.globalLines);
        } else {
          this.boardManager.create(player, ComponentParserUtils.apply(config.globalTitle), config.globalLines);
        }
      }
      // The title-animation doesn't consider specific modes. so we avoid bugs with the title.
      case WORLD -> {
        if (!config.enableAnimatedTitleFeature) this.fromWorldSections(player);
      }
      case PERMISSION -> {
        if (!config.enableAnimatedTitleFeature) this.fromPermissionSections(player);
      }
      case GROUP -> {
        if (!config.enableAnimatedTitleFeature) this.fromGroupSections(player);
      }
    };
  }

  private void fromWorldSections(final Player player) {
    for (final var worldSection : this.settingsConfigProvider.configModel().scoreboardWorld) {
      if (!player.getWorld().getName().equals(worldSection.designedWorld)) continue;
      // Create scoreboard using this world-section's title and defined content.
      this.boardManager.create(player, ComponentParserUtils.apply(worldSection.title), worldSection.lines);
    }
  }

  private void fromPermissionSections(final Player player) {
    for (final var permissionSection : this.settingsConfigProvider.configModel().scoreboardPermission) {
      if (!player.hasPermission(permissionSection.node)) continue;
      this.boardManager.create(player, ComponentParserUtils.apply(permissionSection.title), permissionSection.lines);
    }
  }

  private void fromGroupSections(final Player player) {
    // This mode requires LuckPerms available for groups settings.
    if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
      return;
    }
    for (final var groupSection : this.settingsConfigProvider.configModel().scoreboardGroup) {
      final var luckPermsUser = this.luckPermsUserManager.getUser(player.getUniqueId());
      // Check if player's user information is available, and its group can see this scoreboard.
      if ((luckPermsUser == null) || !luckPermsUser.getPrimaryGroup().equals(groupSection.designedGroup)) {
        continue;
      }
      // Group check by LuckPerms' API.
      this.boardManager.create(player, ComponentParserUtils.apply(groupSection.title), groupSection.lines);
    }
  }
}
