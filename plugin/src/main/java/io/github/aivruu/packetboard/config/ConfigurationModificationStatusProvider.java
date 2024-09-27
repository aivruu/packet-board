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
package io.github.aivruu.packetboard.config;

import org.jetbrains.annotations.Nullable;

public record ConfigurationModificationStatusProvider<C>(byte status, @Nullable ConfigurationProvider<C> result) {
  public static final byte MODIFIED_STATUS = 0;
  public static final byte FAILED_ON_MODIFICATION_STATUS = 1;

  public static <C> ConfigurationModificationStatusProvider<C> modified(final ConfigurationProvider<C> result) {
    return new ConfigurationModificationStatusProvider<>(MODIFIED_STATUS, result);
  }

  public static <C> ConfigurationModificationStatusProvider<C> failedOnModification() {
    return new ConfigurationModificationStatusProvider<>(FAILED_ON_MODIFICATION_STATUS, null);
  }

  public boolean modifiedStatus() {
    return this.status == MODIFIED_STATUS;
  }

  public boolean failedOnModificationStatus() {
    return this.status == FAILED_ON_MODIFICATION_STATUS;
  }
}
