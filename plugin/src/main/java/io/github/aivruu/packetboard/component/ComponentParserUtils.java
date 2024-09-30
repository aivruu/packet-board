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
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ComponentParserUtils {
  private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
  public static final PlainTextComponentSerializer PLAIN_TEXT_COMPONENT_SERIALIZER
    = PlainTextComponentSerializer.plainText();
  public static Component apply(final String text, final TagResolver... placeholders) {
    return MINI_MESSAGE.deserialize(text, placeholders);
  }

  public static Component apply(final String[] textContent, final TagResolver... placeholders) {
    final var componentBuilder = Component.text();
    for (final var iterated : textContent) {
      componentBuilder.append(MINI_MESSAGE.deserialize(iterated, placeholders)).append(Component.newline());
    }
    return componentBuilder.build();
  }

  public static String serializeToPlain(final Component text) {
    return PLAIN_TEXT_COMPONENT_SERIALIZER.serialize(text);
  }
}
