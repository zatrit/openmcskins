package net.zatrit.openmcskins.render;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.annotation.KeepClassMember;
import net.zatrit.openmcskins.io.skins.CosmeticsParser;
import net.zatrit.openmcskins.io.skins.Loaders;
import net.zatrit.openmcskins.io.skins.loader.CosmeticsLoader;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CosmeticsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private static final Map<String, List<CosmeticsParser.CosmeticsItem>> playerCosmetics = new HashMap<>(256);
    private static final boolean hasCem = FabricLoader.getInstance().isModLoaded("cem");

    public CosmeticsFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    public static void clear() {
        playerCosmetics.clear();
    }

    @KeepClassMember
    @Override
    public void render(MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers,
                       int light,
                       @NotNull AbstractClientPlayerEntity entity,
                       float limbAngle,
                       float limbDistance,
                       float tickDelta,
                       float animationProgress,
                       float headYaw,
                       float headPitch) {
        if (OpenMCSkins.getConfig().cosmetics && hasCem && entity.getGameProfile() != null && !entity.isInvisible()) {
            final List<CosmeticsParser.CosmeticsItem> items = playerCosmetics.computeIfAbsent(entity.getEntityName(),
                    k -> {
                        List<CosmeticsParser.CosmeticsItem> cosmeticsItems = new ArrayList<>();
                        Loaders.COSMETICS.getHandler().loadAsync(entity.getGameProfile(),
                                (CosmeticsLoader.CosmeticsResolveCallback) cosmeticsItems::addAll);
                        return cosmeticsItems;
                    });

            for (CosmeticsParser.CosmeticsItem item : items) {
                final VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(item.texture()));
                final PlayerEntityModel<AbstractClientPlayerEntity> model = getContextModel();
                for (int i = 0; i < item.parts().size(); i++) {
                    final ModelPart part = item.parts().get(i);
                    if (!part.visible) {
                        continue;
                    }
                    final ModelPart attachPart = switch (item.attaches().get(i)) {
                        case "head" -> model.head;
                        case "body" -> model.body;
                        case "leftArm" -> model.leftArm;
                        case "leftLeg" -> model.leftLeg;
                        case "rightArm" -> model.rightArm;
                        case "rightLeg" -> model.rightLeg;
                        default -> null;
                    };
                    if (attachPart != null) {
                        part.copyTransform(attachPart);
                    }
                    part.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV);
                }
            }
        }

    }
}
