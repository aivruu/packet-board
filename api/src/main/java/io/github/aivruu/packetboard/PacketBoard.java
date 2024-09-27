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
import io.github.aivruu.packetboard.manager.BoardManager;
import io.github.aivruu.packetboard.board.BoardRepositoryModel;
import io.github.aivruu.packetboard.repository.RepositoryModel;

/**
 * This is used as interface-model for API communication and utilities access. The provided functions
 * by this interface are supposed to be used once the API has been initialized totally.
 *
 * @since 1.0.0
 */
public interface PacketBoard {
  /**
   * Returns the {@link BoardRepositoryModel} instance.
   *
   * @return The {@link BoardRepositoryModel} implementation used for {@link CachedBoardModel}s handling.
   * @since 1.0.0
   */
  RepositoryModel<CachedBoardModel> boardRepository();

  /**
   * Returns the {@link BoardManager} instance.
   *
   * @return The {@link BoardManager}.
   * @since 1.0.0
   */
  BoardManager boardManager();
}
