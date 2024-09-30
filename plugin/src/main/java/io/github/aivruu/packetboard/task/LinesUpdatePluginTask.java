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
package io.github.aivruu.packetboard.task;

import io.github.aivruu.packetboard.board.CachedBoardModel;
import io.github.aivruu.packetboard.config.object.SettingsConfigModel;
import io.github.aivruu.packetboard.util.PlaceholderParsingUtils;
import io.github.aivruu.packetboard.repository.RepositoryModel;
import io.github.aivruu.packetboard.factory.ScoreboardFactory;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class LinesUpdatePluginTask implements Consumer<ScheduledTask> {
  private final RepositoryModel<CachedBoardModel> boardRepository;
  private SettingsConfigModel config;
  private byte index = 0;

  public LinesUpdatePluginTask(final RepositoryModel<CachedBoardModel> boardRepository, final SettingsConfigModel config) {
    this.boardRepository = boardRepository;
    this.config = config;
  }

  public void configModel(final SettingsConfigModel updatedConfigModel) {
    this.config = updatedConfigModel;
  }

  @Override
  public void accept(final ScheduledTask task) {
    for (final var cachedBoardModel : this.boardRepository.findAllSync()) {
      if (!cachedBoardModel.visible()) continue;
      // Internal lines processing depending on selected scoreboard-mode.
      this.processIteratedBoard(this.config, cachedBoardModel);
    }
  }

  private void validateIndexValue(final int limit) {
    this.index = (this.index++ >= (limit - 1)) ? 0 : this.index;
  }

  private void processIteratedBoard(final SettingsConfigModel config, final CachedBoardModel cachedBoardModel) {
    final var player = cachedBoardModel.player();
    switch (config.mode) {
      case GLOBAL -> cachedBoardModel.lineWithoutMutation(this.index, this.process(player, config.globalLines));
      case WORLD -> {
        for (final var worldSection : config.scoreboardWorld) {
          if (!player.getWorld().getName().equals(worldSection.designedWorld)) continue;
          cachedBoardModel.lineWithoutMutation(this.index, this.process(player, worldSection.lines));
        }
      }
      case PERMISSION -> {
        for (final var permissionSection : config.scoreboardPermission) {
          if (!player.hasPermission(permissionSection.node)) continue;
          cachedBoardModel.lineWithoutMutation(this.index, this.process(player, permissionSection.lines));
        }
      }
      case GROUP -> {
        for (final var groupSection : config.scoreboardGroup) {
          cachedBoardModel.lineWithoutMutation(this.index, this.process(player, groupSection.lines));
        }
      }
    }
  }

  private Component process(final Player player, Component[] lines) {
    // Current given index validation for each line of the array.
    this.validateIndexValue(lines.length);
    // Return component with placeholders-parsing.
    return PlaceholderParsingUtils.parse(player, lines[this.index]);
  }
}
