package net.zatrit.openmcskins.io.skins;

import com.google.gson.internal.LinkedTreeMap;
import net.dorianpb.cem.internal.file.JemFile;
import net.dorianpb.cem.internal.models.CemModelRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CosmeticsParser {
    private CosmeticsParser() {
    }

    @Contract("_, _, _, _ -> new")
    @SuppressWarnings("unchecked")
    public static @NotNull CosmeticsItem parseJemCosmeticItem(@NotNull Identifier textureId, Identifier modelId, @NotNull LinkedTreeMap<String, Object> jemData, String modelName) throws Exception {
        final ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        jemData.put("texture", textureId.toString());
        final List<LinkedTreeMap<String, Object>> models = ((List<LinkedTreeMap<String, Object>>) jemData.get("models"));
        final List<String> attaches = models.stream().map(model -> (String) model.get("attachTo")).toList();

        models.forEach(model -> model.put("part", modelName + models.indexOf(model)));
        models.forEach(model -> {
            List<LinkedTreeMap<String, Object>> submodels = (List<LinkedTreeMap<String, Object>>) model.get("submodels");
            if (submodels != null && submodels.size() > 1) for (int i = 0; i < submodels.size(); i++)
                submodels.get(i).putIfAbsent("id", ((String) submodels.get(i).get("id")) + i);
        });

        final JemFile jemFile = new JemFile(jemData, modelId, resourceManager);
        final CemModelRegistry registry = new CemModelRegistry(jemFile);

        final List<ModelPart> modelParts;

        if (models.size() == 1) modelParts = Collections.singletonList(getPartByIndex(registry, models, 0));
        else {
            List<ModelPart> list = new ArrayList<>();
            int bound = models.size();
            for (int i = 0; i < bound; i++) {
                ModelPart partByIndex = getPartByIndex(registry, models, i);
                if (partByIndex != null) {
                    list.add(partByIndex);
                }
            }
            modelParts = list;
        }

        return new CosmeticsItem(textureId, modelParts, attaches);
    }

    private static ModelPart getPartByIndex(@NotNull CemModelRegistry registry, @NotNull List<LinkedTreeMap<String, Object>> models, int index) {
        return registry.getEntryByPartName((String) models.get(index).get("part")).getModel();
    }

    public record CosmeticsItem(Identifier texture, List<ModelPart> parts, List<String> attaches) {
    }
}
