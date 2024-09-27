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
import io.github.aivruu.packetboard.config.ConfigurationProvider;
import io.github.aivruu.packetboard.config.object.SettingsConfigModel;
import io.github.aivruu.packetboard.repository.RepositoryModel;
import io.github.aivruu.packetboard.thread.CustomThreadConstants;
import io.github.aivruu.packetboard.thread.CustomThreadExecutorModel;

public class AsyncLinesUpdateThread extends CustomThreadExecutorModel {
  private ConfigurationProvider<SettingsConfigModel> settingsConfigProvider;

  public AsyncLinesUpdateThread(final RepositoryModel<CachedBoardModel> boardRepository,
                                final ConfigurationProvider<SettingsConfigModel> settingsConfigProvider) {
    super("scoreboard-lines-updater-thread", boardRepository, CustomThreadConstants.THREAD_POOL_EXECUTOR);
    this.settingsConfigProvider = settingsConfigProvider;
  }

  public void settingsConfigProvider(final ConfigurationProvider<SettingsConfigModel> settingsConfigProvider) {
    this.settingsConfigProvider = settingsConfigProvider;
  }

  @Override
  public void run() {
    // Avoid on-runtime errors due to out of range for content-array index.
    final var config = this.settingsConfigProvider.configModel();
    for (final var cachedBoardModel : this.boardRepository.findAllSync()) {
      if (!cachedBoardModel.visible()) continue;
      // Don't update lines for toggled-off scoreboards.
      this.processIteratedBoard(config, this.index, cachedBoardModel);
    }
  }

  private byte validateIndexValue(byte index, int limit) {
    if (index++ >= limit - 1) index = 0;
    return index;
  }

  private void processIteratedBoard(final SettingsConfigModel config, final byte index, final CachedBoardModel cachedBoardModel) {
    final var player = cachedBoardModel.player();
    switch (config.mode) {
      case GLOBAL ->
        cachedBoardModel.line(index, config.globalLines[this.validateIndexValue(index, config.globalLines.length)]);
      case WORLD -> {
        for (final var worldSection : config.scoreboardWorld) {
          if (!player.getWorld().getName().equals(worldSection.designedWorld)) continue;
          cachedBoardModel.lineWithoutUpdate(index, worldSection.lines[this.validateIndexValue(index, worldSection.lines.length)]);
        }
      }
      case PERMISSION -> {
        for (final var permissionSection : config.scoreboardPermission) {
          if (!player.hasPermission(permissionSection.node)) continue;
          cachedBoardModel.lineWithoutUpdate(index, permissionSection.lines[this.validateIndexValue(index, permissionSection.lines.length)]);
        }
      }
      case GROUP -> {
        for (final var groupSection : config.scoreboardGroup) {
          // Group validation logic.
          cachedBoardModel.lineWithoutUpdate(index, groupSection.lines[index]);
        }
      }
    }
  }
}
