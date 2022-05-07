package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.openmcskins.resolvers.data.AnimatedPlayerData;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.Objects;

public class LocalDirectoryResolver implements Resolver<LocalDirectoryResolver.PlayerData> {
    private final File directory;

    public LocalDirectoryResolver(File directory) {
        this.directory = directory;
    }

    public LocalDirectoryResolver(String s) {
        this(new File(s));
    }

    @Override
    public PlayerData resolvePlayer(@NotNull GameProfile profile) throws FileNotFoundException {
        return new PlayerData(profile.getName());
    }

    public class PlayerData extends AnimatedPlayerData {
        public PlayerData(String name) throws FileNotFoundException {
            super();
            File texturesDirectory = new File(directory, "textures");
            File metadataDirectory = new File(directory, "metadata");

            if (!texturesDirectory.exists()) throw new FileNotFoundException(texturesDirectory.getAbsolutePath());

            File[] subdirectories = texturesDirectory.listFiles(File::isDirectory);
            for (File subdirectory : Objects.requireNonNull(subdirectories))
                try {
                    String typeName = texturesDirectory.toPath().relativize(subdirectory.toPath()).toFile().getName();
                    MinecraftProfileTexture.Type type = MinecraftProfileTexture.Type.valueOf(typeName.toUpperCase());
                    File textureFile = new File(subdirectory, name + ".png");
                    if (textureFile.exists()) {
                        this.textures.put(type, textureFile.toURI().toString());
                    }

                    File metadataTypeDirectory = new File(metadataDirectory, typeName);
                    File metadataFile = new File(metadataTypeDirectory, name + ".json");
                    if (metadataFile.exists()) {
                        Map<String, ?> metadata = GSON.<Map<String, String>>fromJson(new FileReader(metadataFile), Map.class);
                        if (metadata.containsKey("model")) this.setModel(String.valueOf(metadata.get("model")));
                        if (metadata.containsKey("animated"))
                            this.setAnimated(type, (boolean) metadata.get("animated"));
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
