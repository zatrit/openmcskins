package net.zatrit.openmcskins.config;

import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.annotation.KeepClassMember;

@KeepClass
public enum CosmeticaMode {
    @KeepClassMember
    NO_THIRD_PARTY,
    @KeepClassMember
    ALLOW_THIRD_PARTY
}
