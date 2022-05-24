package net.zatrit.openmcskins.mod.plugin;

import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.mod.OpenMCSkins;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.InputStream;
import java.util.*;

@KeepClass
public class ConditionalMixinPlugin implements IMixinConfigPlugin {
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
            Map<String, List<String>> requires = new HashMap<>();

            requires.put("all", new ArrayList<>());
            requires.put("any", new ArrayList<>());

            reader.accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    if (Objects.equals(descriptor, "Lnet/zatrit/openmcskins/annotation/RequiresMod;"))
                        return new AnnotationVisitor(Opcodes.ASM9) {
                            @Override
                            public AnnotationVisitor visitArray(String arrayName) {
                                return new AnnotationVisitor(Opcodes.ASM9) {
                                    @Override
                                    public void visit(String name, Object value) {
                                        requires.get(arrayName).add(String.valueOf(value));
                                    }
                                };
                            }
                        };
                    return super.visitAnnotation(descriptor, visible);
                }
            }, 0);

            boolean allMatch = requires.get("all").stream().allMatch(OpenMCSkins::isModLoaded);
            boolean anyMatch = requires.get("any").stream().anyMatch(OpenMCSkins::isModLoaded) || requires.get("any").size() == 0;
            return allMatch && anyMatch;
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
