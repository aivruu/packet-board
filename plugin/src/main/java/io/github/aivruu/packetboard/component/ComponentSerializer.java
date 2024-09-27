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
package io.github.aivruu.packetboard.component;

import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public enum ComponentSerializer implements TypeSerializer<Component> {
  INSTANCE;

  private static final Component EMPTY_COMPONENT = Component.empty();

  @Override
  public Component deserialize(final Type type, final ConfigurationNode target) {
    return (target.getString() == null)
      ? EMPTY_COMPONENT : ComponentParserUtils.apply(target.getString());
  }

  @Override
  public void serialize(final Type type, @Nullable final Component obj, final ConfigurationNode target) throws SerializationException {
    if (obj == null) {
      target.raw(null);
      return;
    }
    target.set(String.class, ComponentParserUtils.serializeToPlain(obj));
  }
}
