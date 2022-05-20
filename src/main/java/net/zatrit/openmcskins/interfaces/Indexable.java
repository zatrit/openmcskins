package net.zatrit.openmcskins.interfaces;

import net.zatrit.openmcskins.util.IndexedObject;

public interface Indexable {
    <T extends IndexedObject> T withIndex(int index);
    int getIndex();
}
