package net.zatrit.openmcskins.operators;

import com.google.common.hash.HashFunction;

import static com.google.common.hash.Hashing.*;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public enum HashingAlgorithm {
    CRC32(crc32()),
    @Deprecated
    SHA1(sha1()),
    MURMUR3(murmur3_128()),
    SHA384(sha384());

    private final HashFunction function;

    HashingAlgorithm(HashFunction function) {
        this.function = function;
    }

    public HashFunction getFunction() {
        return function;
    }
}
