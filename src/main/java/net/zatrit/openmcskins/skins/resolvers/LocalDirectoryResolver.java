package net.zatrit.openmcskins.skins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.skins.resolvers.handler.AnimatedPlayerHandler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.Objects;

public class LocalDirectoryResolver implements Resolver<LocalDirectoryResolver.PlayerHandler> {
    private final File directory;

    public LocalDirectoryResolver(File directory) {
        this.directory = directory;
    }

    public LocalDirectoryResolver(String s) {
        this(new File(s));
    }

    @Override
    public PlayerHandler resolvePlayer(@NotNull GameProfile profile) throws FileNotFoundException {
        return new PlayerHandler(profile.getName());
    }

    @Override
    public boolean requiresUUID() {
        return false;
    }

    public class PlayerHandler extends AnimatedPlayerHandler {
        public PlayerHandler(String name) throws FileNotFoundException {
            final File texturesDirectory = new File(directory, "textures");
            final File metadataDirectory = new File(directory, "metadata");

            if (!texturesDirectory.exists()) {
                throw new FileNotFoundException(texturesDirectory.getAbsolutePath());
            }

            final File[] subdirectories = texturesDirectory.listFiles(File::isDirectory);
            for (File subdirectory : Objects.requireNonNull(subdirectories))
                try {
                    final String typeName = texturesDirectory.toPath().relativize(subdirectory.toPath()).toFile().getName();
                    final MinecraftProfileTexture.Type type = MinecraftProfileTexture.Type.valueOf(typeName.toUpperCase());
                    final File textureFile = new File(subdirectory, name + ".png");
                    if (textureFile.exists()) {
                        this.textures.put(type, textureFile.toURI().toString());
                    }

                    final File metadataTypeDirectory = new File(metadataDirectory, typeName);
                    final File metadataFile = new File(metadataTypeDirectory, name + ".json");
                    if (metadataFile.exists()) {
                        final Map<String, ?> metadata = GSON.<Map<String, String>>fromJson(new FileReader(metadataFile),
                                Map.class);
                        if (metadata.containsKey("model")) {
                            this.setModel(String.valueOf(metadata.get("model")));
                        }
                        if (metadata.containsKey("animated")) {
                            this.setAnimated(type, (boolean) metadata.get("animated"));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
        }

        @Override
        protected boolean cacheEnabled() {
            return false;
        }
    }
}
