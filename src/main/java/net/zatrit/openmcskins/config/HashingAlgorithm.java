package net.zatrit.openmcskins.config;

import com.google.common.hash.HashFunction;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.annotation.KeepClassMember;

import static com.google.common.hash.Hashing.*;

@KeepClass
public enum HashingAlgorithm {
    CRC32(crc32()),
    SHA1(sha1()),
    SHA384(sha384());

    private final HashFunction function;

    HashingAlgorithm(HashFunction function) {
        this.function = function;
    }

    public HashFunction getFunction() {
        return function;
    }
}
