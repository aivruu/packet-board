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
package io.github.aivruu.packetboard.thread.impl;

import io.github.aivruu.packetboard.board.CachedBoardModel;
import io.github.aivruu.packetboard.config.object.SettingsConfigModel;
import io.github.aivruu.packetboard.placeholder.PlaceholderParsingUtils;
import io.github.aivruu.packetboard.repository.RepositoryModel;
import io.github.aivruu.packetboard.thread.CustomThreadConstants;
import io.github.aivruu.packetboard.thread.CustomThreadExecutorModel;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class AsyncLinesUpdateThread extends CustomThreadExecutorModel {
  private SettingsConfigModel config;

  public AsyncLinesUpdateThread(final RepositoryModel<CachedBoardModel> boardRepository, final SettingsConfigModel config) {
    super("scoreboard-lines-updater-thread", boardRepository, CustomThreadConstants.THREAD_POOL_EXECUTOR);
    this.config = config;
  }

  public void configModel(final SettingsConfigModel updatedConfigModel) {
    this.config = updatedConfigModel;
  }

  @Override
  public void run() {
    for (final var cachedBoardModel : this.boardRepository.findAllSync()) {
      if (!cachedBoardModel.visible()) continue;
      // Internal lines processing depending on selected scoreboard-mode.
      this.processIteratedBoard(this.config, this.index, cachedBoardModel);
    }
  }

  private byte validateIndexValue(byte index, int limit) {
    if (index++ >= (limit - 1)) index = 0;
    return index;
  }

  private void processIteratedBoard(final SettingsConfigModel config, final byte index, final CachedBoardModel cachedBoardModel) {
    final var player = cachedBoardModel.player();
    switch (config.mode) {
      case GLOBAL -> cachedBoardModel.line(index, this.process(player, config.globalLines, index));
      case WORLD -> {
        for (final var worldSection : config.scoreboardWorld) {
          if (!player.getWorld().getName().equals(worldSection.designedWorld)) continue;
          cachedBoardModel.lineWithoutMutation(index, this.process(player, worldSection.lines, index));
        }
      }
      case PERMISSION -> {
        for (final var permissionSection : config.scoreboardPermission) {
          if (!player.hasPermission(permissionSection.node)) continue;
          cachedBoardModel.lineWithoutMutation(index, this.process(player, permissionSection.lines, index));
        }
      }
      case GROUP -> {
        for (final var groupSection : config.scoreboardGroup) {
          // Group validation logic.
          cachedBoardModel.lineWithoutMutation(index, this.process(player, groupSection.lines, index));
        }
      }
    }
  }

  private Component process(final Player player, final Component[] lines, final byte index) {
    // Current given index validation for each line of the array.
    final var validatedLineIndex = this.validateIndexValue(index, lines.length);
    // Return component with placeholders-parsing.
    return PlaceholderParsingUtils.parse(player, lines[validatedLineIndex]);
  }
}
