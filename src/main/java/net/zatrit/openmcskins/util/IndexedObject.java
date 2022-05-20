package net.zatrit.openmcskins.util;

import net.zatrit.openmcskins.interfaces.Indexable;
import org.jetbrains.annotations.Contract;

public class IndexedObject implements Indexable {
    private int index;

    public final int getIndex() {
        return index;
    }

    @SuppressWarnings("unchecked")
    @Contract("_ -> this")
    public final <T extends IndexedObject> T withIndex(int index) {
        this.index = index;
        return (T) this;
    }
}
