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
package io.github.aivruu.packetboard.board;

import io.github.aivruu.packetboard.repository.RepositoryModel;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link RepositoryModel} implementation used for {@link CachedBoardModel}'s.
 *
 * @since 1.0.0
 */
public class BoardRepositoryModel implements RepositoryModel<CachedBoardModel> {
  private final Map<String, CachedBoardModel> scoreboards = new ConcurrentHashMap<>();

  @Override
  public @Nullable CachedBoardModel findSync(final String id) {
    return this.scoreboards.get(id);
  }

  @Override
  public Collection<CachedBoardModel> findAllSync() {
    return List.copyOf(this.scoreboards.values());
  }

  @Override
  public void saveSync(final CachedBoardModel model) {
    this.scoreboards.put(model.id(), model);
  }

  @Override
  public void updateSync(final CachedBoardModel model) {
    final var id = model.id();
    // No matter if the model exists or not, we will replace it of either way.
    this.scoreboards.remove(id);
    this.scoreboards.put(id, model);
  }

  @Override
  public boolean deleteSync(final String id) {
    final var cachedBoardModel = this.scoreboards.remove(id);
    // Check if the player has never joined the server, or the scoreboard is turned-off.
    if ((cachedBoardModel == null) || !cachedBoardModel.visible()) {
      return false;
    }
    // Once the scoreboard is deleted, the status-provider must return the 'DELETED' status as
    // confirmation.
    return cachedBoardModel.delete().deleted();
  }

  @Override
  public void clearRegistry() {
    this.scoreboards.clear();
  }
}
