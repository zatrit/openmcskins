package net.zatrit.openmcskins.resolvers;

import com.google.common.hash.Hashing;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Map;
import java.util.Objects;

public class LocalDirectoryResolver extends AbstractResolver<LocalDirectoryResolver.IndexedPlayerData> {
    private final File directory;

    public LocalDirectoryResolver(File directory) {
        this.directory = directory;
    }

    @Override
    public IndexedPlayerData resolvePlayer(@NotNull PlayerListEntry player) throws FileNotFoundException {
        return new IndexedPlayerData(player.getProfile().getName());
    }

    @Override
    public String getName() {
        return directory.getAbsolutePath();
    }

    public String getDirectory() {
        return directory.getAbsolutePath();
    }

    public class IndexedPlayerData extends AbstractResolver.IndexedPlayerData<File> {
        public IndexedPlayerData(String name) throws FileNotFoundException {
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
                        this.textures.put(type, textureFile);
                    }

                    File metadataTypeDirectory = new File(metadataDirectory, typeName);
                    File metadataFile = new File(metadataTypeDirectory, name + ".json");
                    if (metadataFile.exists())
                        this.model = GSON.<Map<String, String>>fromJson(new FileReader(metadataFile), Map.class).getOrDefault("model", "default");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
        }

        @Override
        public Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            try {
                FileInputStream stream = new FileInputStream(textures.get(type));
                NativeImageBackedTexture texture = new NativeImageBackedTexture(NativeImage.read(stream));

                stream = new FileInputStream(textures.get(type));
                String hash = String.valueOf(Hashing.crc32().hashBytes(stream.readAllBytes()));
                Identifier identifier = new Identifier("skins/" + hash);
                stream.close();

                MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, texture);
                return identifier;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
