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

public record ThreadShutdownStatusProvider(byte status) {
  public static final byte SHUTDOWN_SUCCESS_STATUS = 0;
  public static final byte SHUTDOWN_IMMEDIATE_STATUS = 2;
  public static final byte SHUTDOWN_FAILURE_STATUS = 3;

  public static ThreadShutdownStatusProvider withShutdownSuccess() {
    return new ThreadShutdownStatusProvider(SHUTDOWN_SUCCESS_STATUS);
  }

  public static ThreadShutdownStatusProvider withShutdownImmediate() {
    return new ThreadShutdownStatusProvider(SHUTDOWN_IMMEDIATE_STATUS);
  }

  public static ThreadShutdownStatusProvider withShutdownFailure() {
    return new ThreadShutdownStatusProvider(SHUTDOWN_FAILURE_STATUS);
  }

  public boolean shutdownSuccess() {
    return this.status == SHUTDOWN_SUCCESS_STATUS;
  }

  public boolean shutdownImmediate() {
    return this.status == SHUTDOWN_IMMEDIATE_STATUS;
  }

  public boolean shutdownFailure() {
    return this.status == SHUTDOWN_FAILURE_STATUS;
  }
}
