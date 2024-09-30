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

import io.github.aivruu.packetboard.board.BoardRepositoryModel;
import io.github.aivruu.packetboard.board.CachedBoardModel;
import io.github.aivruu.packetboard.repository.CachableModel;
import io.github.aivruu.packetboard.repository.RepositoryModel;
import io.github.aivruu.packetboard.thread.status.ThreadShutdownStatusProvider;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class is used as model for implementations that need to run periodically-tasks in a separate thread
 * during plugin's runtime.
 *
 * @since 1.0.0
 */
public abstract class CustomThreadExecutorModel implements Runnable, CachableModel {
  private final String id;
  private final ScheduledExecutorService executorService;
  protected final RepositoryModel<CachedBoardModel> boardRepository;
  // Initial index-value for any created custom-thread-executor model.
  protected byte index = 0;

  /**
   * Creates a new {@link CustomThreadExecutorModel} with the given parameters.
   *
   * @param id this thread-executor's identifier.
   * @param boardRepository a {@link BoardRepositoryModel} instance.
   * @param executorService the {@link ScheduledExecutorService} for this thread-executor instance.
   * @since 1.0.0
   */
  public CustomThreadExecutorModel(final String id, final RepositoryModel<CachedBoardModel> boardRepository, final ScheduledExecutorService executorService) {
    this.id = id;
    this.boardRepository = boardRepository;
    this.executorService = executorService;
  }

  /**
   * Returns this thread-executor's identifier.
   *
   * @return This {@link CustomThreadExecutorModel}'s id.
   * @since 1.0.0
   */
  @Override
  public String id() {
    return this.id;
  }

  /**
   * Schedules and run this thread-executor at a fixed-rate using the given period-rate value.
   *
   * @param periodRate the value for this thread-executor's execution period.
   * @since 1.0.0
   */
  public void schedule(final byte periodRate) {
    this.executorService.scheduleAtFixedRate(this, 0, periodRate, TimeUnit.SECONDS);
  }

  /**
   * Shutdowns this thread-executor and provides an status for this operation.
   *
   * @return The {@link ThreadShutdownStatusProvider} with the status for the operation.
   *     - {@link ThreadShutdownStatusProvider#withShutdownFailure()} if an exception ocurred during thread's
   *     shutdown-process.
   *     <p>
   *     - {@link ThreadShutdownStatusProvider#withShutdownSuccess()} if the executor was shutdown correctly and
   *     during thread's termination-time {@code (5 seconds)}.
   *     <p>
   *     - {@link ThreadShutdownStatusProvider#withShutdownImmediate()} if the executor couldn't terminate before
   *      given termination-time.
   * @since 1.0.0
   */
  public ThreadShutdownStatusProvider shutdown() {
    try {
      this.executorService.shutdown();
      final var terminatedBeforeTimeout = this.executorService.awaitTermination(5, TimeUnit.SECONDS);
      if (terminatedBeforeTimeout) {
        return ThreadShutdownStatusProvider.withShutdownSuccess();
      }
      this.executorService.shutdownNow();
      return ThreadShutdownStatusProvider.withShutdownImmediate();
    } catch (final InterruptedException exception) {
      return ThreadShutdownStatusProvider.withShutdownFailure();
    }
  }
}
