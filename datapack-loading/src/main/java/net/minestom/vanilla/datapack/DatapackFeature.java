package net.minestom.vanilla.datapack;

import io.github.pesto.MojangDataFeature;
import io.github.pesto.files.ByteArray;
import io.github.pesto.files.FileSystem;
import net.minestom.server.utils.NamespaceID;
import net.minestom.vanilla.VanillaReimplementation;
import net.minestom.vanilla.logging.Loading;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class DatapackFeature implements VanillaReimplementation.Feature {

    private @Nullable Datapack datapack;

    @Override
    public void hook(@NotNull HookContext context) {

        Loading.start("Fetching from Mojang data");
        @NotNull MojangDataFeature data = context.vri().feature(MojangDataFeature.class);
        Loading.finish();

        Loading.start("Loading vanilla datapack");
        FileSystem<ByteArray> fs = data.getLatest().join();
        datapack = Datapack.loadByteArray(fs);
        Loading.finish();
    }

    public Datapack vanilla() {
        Objects.requireNonNull(datapack, "Datapack not loaded yet");
        return datapack;
    }

    @Override
    public @NotNull NamespaceID namespaceId() {
        return NamespaceID.from("vri:datapack");
    }

    @Override
    public @NotNull Set<Class<? extends VanillaReimplementation.Feature>> dependencies() {
        return Set.of(MojangDataFeature.class);
    }
}