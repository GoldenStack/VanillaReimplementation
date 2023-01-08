package net.minestom.vanilla.generation.util;

import net.minestom.server.coordinate.Vec;
import net.minestom.vanilla.generation.densityfunctions.DensityFunction;
import org.jetbrains.annotations.NotNull;

public interface DoubleStorage {

    double obtain(int x, int y, int z);

    static DoubleStorage from(DensityFunction densityFunction) {
        return (x, y, z) -> densityFunction.compute(new Vec(x, y, z));
    }

    /**
     * A storage that caches an exact, unique value for each 3d coordinate once.
     * @param original the original storage to cache
     * @return a new storage that caches the original
     */
    static @NotNull DoubleStorage exactCache(DoubleStorage original) {
        return new DoubleStorageExactImpl(original);
    }
}
