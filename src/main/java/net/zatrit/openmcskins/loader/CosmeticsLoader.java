package net.zatrit.openmcskins.loader;

import com.google.gson.internal.LinkedTreeMap;
import com.mojang.authlib.GameProfile;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.dorianpb.cem.internal.file.JemFile;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.resolvers.PlayerCosmeticsResolver;
import net.zatrit.openmcskins.resolvers.Resolver;
import net.zatrit.openmcskins.resolvers.handler.PlayerCosmeticsHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class CosmeticsLoader {
    public static final Map<String, List<CosmeticsItem>> PLAYER_COSMETICS = new TreeMap<>();
    public static final Map<String, CosmeticsItem> COSMETICS = new HashMap<>(16);

    @SuppressWarnings("unchecked")
    public static void loadSingleCosmeticItem(@NotNull Identifier textureId, Identifier modelId, @NotNull LinkedTreeMap<String, Object> jemData, String modelType) throws Exception {
        if (COSMETICS.containsKey(modelType))
            return;

        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        jemData.put("texture", textureId.toString());
        List<LinkedTreeMap<String, Object>> models = ((List<LinkedTreeMap<String, Object>>) jemData.get("models"));
        List<String> attaches = models.stream().map(model -> (String) model.get("attachTo")).toList();

        models.forEach(model -> model.put("part", modelType + models.indexOf(model)));
        models.forEach(model -> {
            List<LinkedTreeMap<String, Object>> submodels = (List<LinkedTreeMap<String, Object>>) model.get("submodels");
            if (submodels != null && submodels.size() > 1) for (int i = 0; i < submodels.size(); i++)
                submodels.get(i).put("id", ((String) submodels.get(i).get("id")) + i);
        });

        JemFile jemFile = new JemFile(jemData, modelId, resourceManager);
        CemModelRegistry registry = new CemModelRegistry(jemFile);

        List<ModelPart> modelParts;

        if (models.size() == 1) modelParts = Collections.singletonList(getPartByIndex(registry, models, 0));
        else
            modelParts = IntStream.range(0, models.size()).boxed().map(i -> getPartByIndex(registry, models, i)).filter(Objects::nonNull).toList();

        CosmeticsItem item = new CosmeticsItem(textureId, modelParts, attaches);
        COSMETICS.put(modelType, item);
    }

    private static ModelPart getPartByIndex(@NotNull CemModelRegistry registry, @NotNull List<LinkedTreeMap<String, Object>> models, int index) {
        return registry.getEntryByPartName((String) models.get(index).get("part")).getModel();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void resolveCosmetics(@NotNull GameProfile profile) {
        final List<? extends Resolver<?>> hosts = OpenMCSkins.getResolvers();
        PLAYER_COSMETICS.put(profile.getName(), new ArrayList<>());

        Flowable.range(0, hosts.size()).parallel().runOn(Schedulers.io()).sequential().timeout(OpenMCSkins.getConfig().resolvingTimeout, TimeUnit.SECONDS).subscribe(i -> {
            if (hosts.get(i) instanceof PlayerCosmeticsResolver) try {
                PlayerCosmeticsHandler handler = (PlayerCosmeticsHandler) hosts.get(i).resolvePlayer(profile);
                List<String> cosmetics = handler.downloadCosmetics();
                cosmetics.forEach(item -> PLAYER_COSMETICS.get(profile.getName()).add(COSMETICS.get(item)));
            } catch (NullPointerException ignored) {
            }
        }, OpenMCSkins::handleError);
    }

    public static void clear() {
        PLAYER_COSMETICS.clear();
        COSMETICS.clear();
    }

    public record CosmeticsItem(Identifier texture, List<ModelPart> parts, List<String> attaches) {
    }
}
