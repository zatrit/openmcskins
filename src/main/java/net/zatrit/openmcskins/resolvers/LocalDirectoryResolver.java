package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.*;
import java.util.Map;
import java.util.Objects;

public class LocalDirectoryResolver extends AbstractResolver<LocalDirectoryResolver.PlayerData> {
    private final File directory;

    public LocalDirectoryResolver(File directory) {
        this.directory = directory;
    }

    public LocalDirectoryResolver(String s) {
        this(new File(s));
    }

    @Override
    public PlayerData resolvePlayer(GameProfile profile) throws FileNotFoundException {
        return new PlayerData(profile.getName());
    }

    public class PlayerData extends AbstractResolver.IndexedPlayerData<File> {
        public PlayerData(String name) throws FileNotFoundException {
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
                    if (metadataFile.exists()) {
                        Map<String, String> metadata = GSON.<Map<String, String>>fromJson(new FileReader(metadataFile), Map.class);
                        if (metadata.containsKey("model")) this.setModel(metadata.get("model"));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
        }

        @Override
        public Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            try {
                FileInputStream stream = new FileInputStream(textures.get(type));
                NativeImageBackedTexture texture = new NativeImageBackedTexture(NativeImage.read(stream));

                return MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("skin", texture);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
