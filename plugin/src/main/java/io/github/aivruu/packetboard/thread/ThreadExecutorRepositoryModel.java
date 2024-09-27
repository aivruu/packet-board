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
    final var id = model.id();
    this.threadExecutors.remove(id);
    this.threadExecutors.put(id, model);
  }

  @Override
  public boolean deleteSync(final String id) {
    final var customThreadExecutorModel = this.threadExecutors.get(id);
    // If thread-executor is cached, at shutdown, the status-provider must return the 'SHUTDOWN_SUCCESS' status.
    return (customThreadExecutorModel != null) && customThreadExecutorModel.shutdown().shutdownSuccess();
  }

  @Override
  public void clearRegistry() {
    for (final var customThreadExecutorModel : this.threadExecutors.values()) {
      final var threadExecutorShutdownStatus = customThreadExecutorModel.shutdown();
      final var threadExecutorId = customThreadExecutorModel.id();
      // Simply verification to notify about thread-executors' shutdown status.
      if (threadExecutorShutdownStatus.shutdownSuccess()) {
        this.logger.info("{} has been successfully shutdown.", threadExecutorId);
      } else if (threadExecutorShutdownStatus.shutdownImmediate()) {
        this.logger.warn("{} couldn't terminate before time-out.", threadExecutorId);
      } else {
        this.logger.error("{} couldn't be shutdown correctly.", threadExecutorId);
      }
    }
    this.threadExecutors.clear();
  }
}
