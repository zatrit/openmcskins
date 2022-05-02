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
import net.zatrit.openmcskins.mixin.PlayerSkinProviderAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static void resizeAndSaveImage(@NotNull BufferedImage source, File output, int targetWidth, int targetHeight) throws IOException {
        // https://stackoverflow.com/a/5194876/12245612
        BufferedImage target = new BufferedImage(targetWidth, targetHeight, source.getType());

        // https://stackoverflow.com/a/14424956/12245612
        int width = source.getWidth();
        int height = source.getHeight();
        int[] sourceBuffer = source.getRaster().getPixels(0, 0, width, height, (int[]) null);
        target.getRaster().setPixels(0, 0, width, height, sourceBuffer);

        ImageIO.write(target, "png", output);

        target.flush();
        source.flush();
    }

    public static @Nullable Identifier capeFromUrl(String url) throws IOException {
        InputStream stream = new URL(url).openStream();
        String hash = OpenMCSkins.getHashFunction().hashUnencodedChars(url).toString();
        File cacheFile = Path.of(((PlayerSkinProviderAccessor) MinecraftClient.getInstance().getSkinProvider()).getSkinCacheDir().getPath(), hash.substring(0, 2), hash).toFile();
        cacheFile.getParentFile().mkdirs();
        NativeImage image;

        if (!cacheFile.isFile()) {
            BufferedImage source = ImageIO.read(stream);

            int height = 16;

            while (height < source.getHeight() || height * 2 < source.getWidth()) height *= 2;

            NetworkUtils.resizeAndSaveImage(source, cacheFile, height * 2, height);
        }

        image = NativeImage.read(new FileInputStream(cacheFile));
        if (image == null)
            return null;
        NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
        return MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("skin", texture);
    }
}
