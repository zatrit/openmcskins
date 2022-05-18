package net.zatrit.openmcskins.loader;

import com.google.gson.internal.LinkedTreeMap;
import net.dorianpb.cem.internal.file.JemFile;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;

public class CosmeticsLoader {
    public static Map<String, List<CosmeticsItem>> COSMETICS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static @NotNull CosmeticsItem getCosmetics(@NotNull Identifier textureId, Identifier modelId, @NotNull LinkedTreeMap<String, Object> jemData, String modelType) throws Exception {
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

        return new CosmeticsLoader.CosmeticsItem(textureId, modelParts, attaches);
    }

    private static ModelPart getPartByIndex(@NotNull CemModelRegistry registry, @NotNull List<LinkedTreeMap<String, Object>> models, int index) {
        return registry.getEntryByPartName((String) models.get(index).get("part")).getModel();
    }

    public record CosmeticsItem(Identifier texture, List<ModelPart> parts, List<String> attaches) {
    }
}
