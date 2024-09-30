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
import io.github.aivruu.packetboard.util.PlaceholderParsingUtils;
import io.github.aivruu.packetboard.repository.RepositoryModel;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;

import java.util.function.Consumer;

public class TitleAnimationPluginTask implements Consumer<ScheduledTask> {
  private final RepositoryModel<CachedBoardModel> boardRepository;
  private Component[] content;
  private byte index = 0;

  public TitleAnimationPluginTask(final RepositoryModel<CachedBoardModel> boardRepository, final Component[] content) {
    this.boardRepository = boardRepository;
    this.content = content;
  }

  public void content(final Component[] content) {
    this.content = content;
  }

  @Override
  public void accept(final ScheduledTask task) {
    // Avoid on-runtime errors due to out of range for content-array index.
    if (this.index++ >= (this.content.length - 1)) {
      return;
    }
    for (final var cachedBoardModel : this.boardRepository.findAllSync()) {
      if (!cachedBoardModel.visible()) continue;
      // Set new scoreboard-title with placeholders-processing.
      cachedBoardModel.titleWithoutMutation(
        PlaceholderParsingUtils.parse(cachedBoardModel.player(), this.content[this.index]));
    }
  }
}
