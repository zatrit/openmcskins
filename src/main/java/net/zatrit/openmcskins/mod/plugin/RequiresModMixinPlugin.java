package net.zatrit.openmcskins.mod.plugin;

import net.fabricmc.loader.api.FabricLoader;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.annotation.KeepClassMember;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@KeepClass
public class RequiresModMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        try {
            String classFile = "/" + mixinClassName.replace('.', '/') + ".class";
            InputStream classStream = getClass().getResourceAsStream(classFile);
            assert classStream != null;
            ClassReader reader = new ClassReader(classStream);
            String[] requiredMod = new String[1];

            reader.accept(new ClassVisitor(Opcodes.ASM9) {
                @KeepClassMember
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    if (Objects.equals(descriptor, "Lnet/zatrit/openmcskins/annotation/RequiresMod;"))
                        return new AnnotationVisitor(Opcodes.ASM9) {
                            @KeepClassMember
                            @Override
                            public void visit(String name, Object value) {
                                requiredMod[0] = String.valueOf(value);
                            }
                        };
                    else return super.visitAnnotation(descriptor, visible);
                }
            }, 0);

            return FabricLoader.getInstance().isModLoaded(requiredMod[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return Collections.emptyList();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
