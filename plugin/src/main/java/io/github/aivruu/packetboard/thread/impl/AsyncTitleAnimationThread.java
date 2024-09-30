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
import io.github.aivruu.packetboard.placeholder.PlaceholderParsingUtils;
import io.github.aivruu.packetboard.repository.RepositoryModel;
import io.github.aivruu.packetboard.thread.CustomThreadConstants;
import io.github.aivruu.packetboard.thread.CustomThreadExecutorModel;
import net.kyori.adventure.text.Component;

public class AsyncTitleAnimationThread extends CustomThreadExecutorModel {
  private Component[] content;

  public AsyncTitleAnimationThread(final RepositoryModel<CachedBoardModel> boardRepository, final Component[] content) {
    super("scoreboard-title-animation-thread", boardRepository, CustomThreadConstants.THREAD_POOL_EXECUTOR);
    this.content = content;
  }

  public void content(final Component[] content) {
    this.content = content;
  }

  @Override
  public void run() {
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
