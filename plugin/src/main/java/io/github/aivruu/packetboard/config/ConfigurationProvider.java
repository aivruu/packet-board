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

import io.github.aivruu.packetboard.serializer.ComponentSerializer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public record ConfigurationProvider<C>(C configModel, HoconConfigurationLoader configLoader, Class<C> configClass) {
  public ConfigurationModificationStatusProvider<@Nullable C> reload() {
    try {
      final var loadedConfigNode = this.configLoader.load();
      // We give the updated config-model for the configuration-provider constructor.
      return ConfigurationModificationStatusProvider.modified(new ConfigurationProvider<>(
        loadedConfigNode.get(this.configClass), this.configLoader, configClass));
    } catch (final ConfigurateException exception) {
      exception.printStackTrace();
      return ConfigurationModificationStatusProvider.failedOnModification();
    }
  }

  public static <C> ConfigurationProvider<C> of(Path fileRoute, final String fileName, final Class<C> configClass) {
    final var loader = HoconConfigurationLoader.builder()
      .prettyPrinting(true)
      .defaultOptions(opts -> opts
        .serializers(builderConsumer -> builderConsumer.register(Component.class, ComponentSerializer.INSTANCE))
        .header("""
          PacketBoard | Packet-level customizable and animated scoreboard plugin for modern servers.
          - It have support for PlaceholderAPI, and MiniMessage formatting.""")
      .shouldCopyDefaults(true))
      .path(fileRoute = fileRoute.resolve(fileName))
      .build();
    try {
      final var loadedConfigNode = loader.load();
      final var configModel = loadedConfigNode.get(configClass);
      // Check if file already was created to avoid overwriting it with default configuration-values.
      if (Files.notExists(fileRoute)) {
        loadedConfigNode.set(configClass, configModel);
        loader.save(loadedConfigNode);
      }
      return new ConfigurationProvider<>(configModel, loader, configClass);
    } catch (final ConfigurateException exception) {
      exception.printStackTrace();
      return null;

    }
  }
}
