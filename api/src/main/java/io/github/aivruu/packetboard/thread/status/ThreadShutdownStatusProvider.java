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
package io.github.aivruu.packetboard.thread.status;

/**
 * This record is used as status-provider for {@link io.github.aivruu.packetboard.thread.CustomThreadExecutorModel}'s
 * shutdown-process.
 *
 * @param status the status-code for this instance.
 * @since 1.0.0
 */
public record ThreadShutdownStatusProvider(byte status) {
  /**
   * The custom-thread-executor was shutdown correctly and during termination-time.
   *
   * @since 1.0.0
   */
  public static final byte SHUTDOWN_SUCCESS_STATUS = 0;
  /**
   * The custom-thread-executor couldn't terminate during termination-time.
   *
   * @since 1.0.0
   */
  public static final byte SHUTDOWN_IMMEDIATE_STATUS = 2;
  /**
   * An error occurred during the shutdown-process.
   *
   * @since 1.0.0
   */
  public static final byte SHUTDOWN_FAILURE_STATUS = 3;

  /**
   * Creates a new {@link ThreadShutdownStatusProvider} with the {@link #SHUTDOWN_SUCCESS_STATUS}.
   *
   * @return The {@link ThreadShutdownStatusProvider} with the {@link #SHUTDOWN_SUCCESS_STATUS}.
   * @since 1.0.0
   */
  public static ThreadShutdownStatusProvider withShutdownSuccess() {
    return new ThreadShutdownStatusProvider(SHUTDOWN_SUCCESS_STATUS);
  }

  /**
   * Creates a new {@link ThreadShutdownStatusProvider} with the {@link #SHUTDOWN_IMMEDIATE_STATUS}.
   *
   * @return The {@link ThreadShutdownStatusProvider} with the {@link #SHUTDOWN_IMMEDIATE_STATUS}.
   * @since 1.0.0
   */
  public static ThreadShutdownStatusProvider withShutdownImmediate() {
    return new ThreadShutdownStatusProvider(SHUTDOWN_IMMEDIATE_STATUS);
  }

  /**
   * Creates a new {@link ThreadShutdownStatusProvider} with the {@link #SHUTDOWN_FAILURE_STATUS}.
   *
   * @return The {@link ThreadShutdownStatusProvider} with the {@link #SHUTDOWN_FAILURE_STATUS}.
   * @since 1.0.0
   */
  public static ThreadShutdownStatusProvider withShutdownFailure() {
    return new ThreadShutdownStatusProvider(SHUTDOWN_FAILURE_STATUS);
  }

  /**
   * Returns whether the status-code for this provider-instance is {@link #SHUTDOWN_SUCCESS_STATUS}.
   *
   * @return Whether the status-code for this provider-instance is {@link #SHUTDOWN_SUCCESS_STATUS}.
   * @since 1.0.0
   */
  public boolean shutdownSuccess() {
    return this.status == SHUTDOWN_SUCCESS_STATUS;
  }

  /**
   * Returns whether the status-code for this provider-instance is {@link #SHUTDOWN_IMMEDIATE_STATUS}.
   *
   * @return Whether the status-code for this provider-instance is {@link #SHUTDOWN_IMMEDIATE_STATUS}.
   * @since 1.0.0
   */
  public boolean shutdownImmediate() {
    return this.status == SHUTDOWN_IMMEDIATE_STATUS;
  }

  /**
   * Returns whether the status-code for this provider-instance is {@link #SHUTDOWN_FAILURE_STATUS}.
   *
   * @return Whether the status-code for this provider-instance is {@link #SHUTDOWN_FAILURE_STATUS}.
   * @since 1.0.0
   */
  public boolean shutdownFailure() {
    return this.status == SHUTDOWN_FAILURE_STATUS;
  }
}
