package net.zatrit.openmcskins.render;

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
import net.zatrit.openmcskins.loader.CosmeticsLoader;
import net.zatrit.openmcskins.loader.TextureLoader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CosmeticsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public CosmeticsFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Contract(pure = true)
    private static ModelPart getPartByName(@NotNull PlayerEntityModel<AbstractClientPlayerEntity> model, String name) {
        return switch (name) {
            case "head" -> model.head;
            case "body" -> model.body;
            case "leftArm" -> model.leftArm;
            case "leftLeg" -> model.leftLeg;
            case "rightArm" -> model.rightArm;
            case "rightLeg" -> model.rightLeg;
            default -> null;
        };
    }

    @KeepClassMember
    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, @NotNull AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!OpenMCSkins.getConfig().cosmetics || !OpenMCSkins.HAS_CEM_MOD || entity.getGameProfile() == null || entity.isInvisible())
            return;

        String name = entity.getEntityName();
        List<CosmeticsLoader.CosmeticsItem> items = CosmeticsLoader.COSMETICS.get(name);
        if (!CosmeticsLoader.COSMETICS.containsKey(name)) {
            TextureLoader.resolveCosmetics(entity.getGameProfile());
            return;
        }

        for (CosmeticsLoader.CosmeticsItem item : items) {
            VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(item.texture()));
            for (int i = 0; i < item.parts().size(); i++) {
                ModelPart part = item.parts().get(i);
                if (!part.visible) continue;
                ModelPart attachPart = getPartByName(getContextModel(), item.attaches().get(i));
                if (attachPart != null) part.copyTransform(attachPart);
                part.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV);
            }
        }
    }
}
