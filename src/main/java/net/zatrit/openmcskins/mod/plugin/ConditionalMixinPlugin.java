package net.zatrit.openmcskins.mod.plugin;

import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.annotation.KeepClassMember;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

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
    public boolean shouldApplyMixin(String targetClassName, @NotNull String mixinClassName) {
        try {
            final String classFile = "/" + mixinClassName.replace('.', '/') + ".class";
            final InputStream classStream = getClass().getResourceAsStream(classFile);
            assert classStream != null;
            final ClassReader reader = new ClassReader(classStream);
            final Map<String, List<String>> requires = new HashMap<>();

            reader.accept(new ClassVisitor(Opcodes.ASM9) {
                @KeepClassMember
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    if (Objects.equals(descriptor, "Lnet/zatrit/openmcskins/annotation/RequiresMod;"))
                        return new AnnotationVisitor(Opcodes.ASM9) {
                            @KeepClassMember
                            @Override
                            public AnnotationVisitor visitArray(String arrayName) {
                                return new AnnotationVisitor(Opcodes.ASM9) {
                                    @KeepClassMember
                                    @Override
                                    public void visit(String name, Object value) {
                                        requires.computeIfAbsent(arrayName, k -> new ArrayList<>()).add(String.valueOf(value));
                                    }
                                };
                            }
                        };
                    return super.visitAnnotation(descriptor, visible);
                }
            }, 0);

            // There is a computeIfAbsent, because it's not working without it
            final TriFunction<Map<String, List<String>>, String, Function<List<String>, Boolean>, Boolean> mapValueMatches = (m, k, f) -> !m.containsKey(k) || f.apply(m.computeIfAbsent(k, k2 -> new ArrayList<>()));

            final boolean allMatch = mapValueMatches.apply(requires, "all", l -> l.stream().allMatch(OpenMCSkins::isModLoaded));
            final boolean anyMatch = mapValueMatches.apply(requires, "any", l -> l.stream().anyMatch(OpenMCSkins::isModLoaded));

            return allMatch && anyMatch;
        } catch (IOException ex) {
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
