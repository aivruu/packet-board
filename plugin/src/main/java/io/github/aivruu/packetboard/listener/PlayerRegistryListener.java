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
package io.github.aivruu.packetboard.listener;

import io.github.aivruu.packetboard.config.ConfigurationProvider;
import io.github.aivruu.packetboard.config.object.SettingsConfigModel;
import io.github.aivruu.packetboard.factory.ScoreboardFactory;
import io.github.aivruu.packetboard.manager.BoardManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerRegistryListener implements Listener {
  private final ComponentLogger logger;
  private final BoardManager boardManager;
  private final ScoreboardFactory scoreboardFactory;
  private SettingsConfigModel configModel;

  public PlayerRegistryListener(final ComponentLogger logger, final BoardManager boardManager,
                                final ScoreboardFactory scoreboardFactory, final SettingsConfigModel configModel) {
    this.logger = logger;
    this.boardManager = boardManager;
    this.scoreboardFactory = scoreboardFactory;
    this.configModel = configModel;
  }

  public void configModel(final SettingsConfigModel updatedConfigModel) {
    this.configModel = updatedConfigModel;
  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    this.scoreboardFactory.create(event.getPlayer(), this.configModel);
  }

  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {
    if (!this.boardManager.delete(event.getPlayer())) {
      this.logger.error("An error occurred while deleting the board for player");
    }
  }
}
