package net.zatrit.openmcskins.config;

import com.google.common.hash.HashFunction;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.annotation.KeepClassMember;

import static com.google.common.hash.Hashing.*;

public enum HashingAlgorithm {

    @KeepClassMember
    CRC32(crc32()),
    @KeepClassMember
    SHA1(sha1()),
    @KeepClassMember
    SHA384(sha384());

    private final HashFunction function;

    HashingAlgorithm(HashFunction function) {
        this.function = function;
    }

    public HashFunction getFunction() {
        return function;
    }
}
