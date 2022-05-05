package net.zatrit.openmcskins.util;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.mixin.NativeImageAccessor;
import net.zatrit.openmcskins.mixin.PlayerSkinProviderAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class NetworkUtils {
    public static int getResponseCode(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            return connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static UUID getUUIDByName(@NotNull GameProfileRepository repo, String name) {
        AtomicReference<UUID> uuid = new AtomicReference<>();
        repo.findProfilesByNames(new String[]{name}, Agent.MINECRAFT, new ProfileLookupCallback() {
            @Override
            public void onProfileLookupSucceeded(GameProfile profile) {
                uuid.set(profile.getId());
                if (uuid.get() == null) uuid.set(UUID.randomUUID());
            }

            @Override
            public void onProfileLookupFailed(GameProfile profile, Exception exception) {
                uuid.set(UUID.randomUUID());
            }
        });

        while (uuid.get() == null) ;

        return uuid.get();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static @Nullable Identifier downloadAndResize(InputStream stream, String name, int width, int height) throws Exception {
        String hash = OpenMCSkins.getHashFunction().hashUnencodedChars(name).toString();
        File cacheFile = Path.of(((PlayerSkinProviderAccessor) MinecraftClient.getInstance().getSkinProvider()).getSkinCacheDir().getPath(), hash.substring(0, 2), hash).toFile();
        cacheFile.getParentFile().mkdirs();
        BufferedImage target;
        NativeImage nativeImage;

        if (!cacheFile.isFile()) {
            BufferedImage source = ImageIO.read(stream);

            int newHeight = height;

            while (newHeight < source.getHeight() || newHeight * (width / height) < source.getWidth()) newHeight *= 2;

            target = new BufferedImage(newHeight * 2, newHeight, BufferedImage.TYPE_INT_ARGB);

            Graphics2D graphics2D = (Graphics2D) target.getGraphics();
            graphics2D.drawImage(source, 0, 0, null);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ImageIO.write(target, "png", outputStream);
            byte[] bytes = outputStream.toByteArray();

            FileOutputStream cacheFileStream = new FileOutputStream(cacheFile);
            cacheFileStream.write(bytes);

            nativeImage = NativeImage.read(new ByteArrayInputStream(bytes));

            source.flush();
            target.flush();
            cacheFileStream.close();
            outputStream.close();
            graphics2D.dispose();
        } else nativeImage = NativeImage.read(new FileInputStream(cacheFile));

        if (NativeImageAccessor.class.cast(nativeImage).getPointer() == 0L) return null;
        NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);
        return MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("skin", texture);
    }
}
