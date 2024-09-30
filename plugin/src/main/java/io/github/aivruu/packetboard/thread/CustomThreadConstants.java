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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CustomThreadConstants {
  // Used to define max-thread-pool size for the scoreboards' two threads.
  public static final ScheduledExecutorService THREAD_POOL_EXECUTOR = Executors.newScheduledThreadPool(2);
}
