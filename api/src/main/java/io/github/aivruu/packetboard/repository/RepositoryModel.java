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
package io.github.aivruu.packetboard.repository;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * This interface works as base-model for cache-handler implementations that uses the repository-design.
 *
 * @param <M> an object which implements the {@link CachableModel} interface.
 * @since 1.0.0
 */
public interface RepositoryModel<M extends CachableModel> {
  /**
   * Tries to find the model based-on the given id.
   *
   * @param id the model's id.
   * @return The model, or {@code null} if the model wasn't found.
   * @since 1.0.0
   */
  @Nullable M findSync(final String id);

  /**
   * Returns a non-modifiable copy of the repository's {@link Map}'s collection of model's.
   *
   * @return A non-modifiable {@link Collection} of models.
   * @since 1.0.0
   */
  Collection<M> findAllSync();

  /**
   * Saves the given model into repository's cache for fast-access during the necessary time.
   *
   * @param model the model to store.
   * @since 1.0.0
   */
  void saveSync(final M model);

  /**
   * Updates the old-model in repository's cache assigned to the given id with a new
   * model.
   *
   * @param model the new model to store.
   * @since 1.0.0
   */
  void updateSync(final M model);

  /**
   * Removes the cached-model from repository's cache, and run some additional logic.
   *
   * @param id the model's id.
   * @return A {@code boolean} value which indicates if the model was removed, depending on implementation it
   *     could depend on of extra operations.
   * @since 1.0.0
   */
  boolean deleteSync(final String id);

  /**
   * Removes all the cached-models from the repository's cache.
   *
   * @since 1.0.0
   */
  void clearRegistry();
}
