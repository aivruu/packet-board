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
package io.github.aivruu.packetboard.thread;

import io.github.aivruu.packetboard.repository.RepositoryModel;
import io.github.aivruu.packetboard.thread.status.ThreadShutdownStatusProvider;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link RepositoryModel} implementation used for {@link CustomThreadExecutorModel}'s.
 *
 * @since 1.0.0
 */
public class ThreadExecutorRepositoryModel implements RepositoryModel<CustomThreadExecutorModel> {
  private final Map<String, CustomThreadExecutorModel> threadExecutors = new HashMap<>();
  private final ComponentLogger logger;

  public ThreadExecutorRepositoryModel(final ComponentLogger logger) {
    this.logger = logger;
  }

  @Override
  public @Nullable CustomThreadExecutorModel findSync(final String id) {
    return this.threadExecutors.get(id);
  }

  @Override
  public Collection<CustomThreadExecutorModel> findAllSync() {
    return List.copyOf(this.threadExecutors.values());
  }

  @Override
  public void saveSync(final CustomThreadExecutorModel model) {
    this.threadExecutors.put(model.id(), model);
  }

  @Override
  public void updateSync(final CustomThreadExecutorModel model) {
    throw new UnsupportedOperationException("CustomThreadExecutorModel's are mutable and don't require this update-type.");
  }

  @Override
  public boolean deleteSync(final String id) {
    final var customThreadExecutorModel = this.threadExecutors.remove(id);
    if (customThreadExecutorModel == null) {
      return false;
    }
    final var shutdownStatusProvider = customThreadExecutorModel.shutdown();
    this.processExecutorGivenShutdownStatus(id, shutdownStatusProvider);
    // At shutdown, the status-provider must return the 'SHUTDOWN_SUCCESS' status.
    return shutdownStatusProvider.shutdownSuccess();
  }

  public void processExecutorGivenShutdownStatus(final String id, final ThreadShutdownStatusProvider threadShutdownStatusProvider) {
    if (threadShutdownStatusProvider.shutdownSuccess()) {
      this.logger.info("{} has been successfully shutdown.", id);
    } else if (threadShutdownStatusProvider.shutdownImmediate()) {
      this.logger.warn("{} couldn't terminate before time-out.", id);
    } else {
      this.logger.error("{} couldn't be shutdown correctly.", id);
    }
  }

  @Override
  public void clearRegistry() {
    for (final var customThreadExecutorModel : this.threadExecutors.values()) {
      this.processExecutorGivenShutdownStatus(customThreadExecutorModel.id(), customThreadExecutorModel.shutdown());
    }
    this.threadExecutors.clear();
  }
}
