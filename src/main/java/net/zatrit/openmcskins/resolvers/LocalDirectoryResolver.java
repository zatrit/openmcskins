package net.zatrit.openmcskins.resolvers;

import com.google.common.hash.Hashing;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.CRC32;

public class LocalDirectoryResolver extends AbstractResolver<LocalDirectoryResolver.PlayerData> {
    private final File directory;

    @Override
    public PlayerData resolvePlayer(@NotNull PlayerInfo playerInfo) throws FileNotFoundException {
        return new PlayerData(playerInfo.getProfile().getName());
    }

    @Override
    public String getName() {
        return directory.getAbsolutePath();
    }

    public LocalDirectoryResolver(File directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory.getAbsolutePath();
    }

    public class PlayerData extends AbstractResolver.PlayerData {
        protected Map<MinecraftProfileTexture.Type, File> textures = new HashMap<>();
        private static final CRC32 CRC_32 = new CRC32();

        @Override
        public ResourceLocation downloadTexture(MinecraftProfileTexture.Type type) {
            try {
                FileInputStream stream = new FileInputStream(textures.get(type));
                DynamicTexture texture = new DynamicTexture(NativeImage.read(stream));

                stream = new FileInputStream(textures.get(type));
                String hash = String.valueOf(Hashing.crc32().hashBytes(stream.readAllBytes()));
                ResourceLocation resourceLocation = new ResourceLocation("skins/" + hash);
                stream.close();

                Minecraft.getInstance().getTextureManager().register(resourceLocation, texture);
                return resourceLocation;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public boolean hasTexture(MinecraftProfileTexture.Type type) {
            return textures.containsKey(type);
        }

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
                    if (metadataFile.exists())
                        this.model = GSON.<Map<String, String>>fromJson(new FileReader(metadataFile), Map.class).getOrDefault("model", "default");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
        }


    }
}
