package net.minestom.vanilla.generation.noise;

import com.google.gson.JsonObject;
import net.minestom.vanilla.generation.Util;
import net.minestom.vanilla.generation.WorldgenContext;

public interface VerticalAnchor {

    int apply(WorldgenContext context);

    static VerticalAnchor fromJson(Object obj) {
        JsonObject json = Util.jsonObject(obj);
        if (json.has("absolute")) {
            return absolute(json.get("absolute").getAsInt());
        } else if (json.has("above_bottom")) {
            return aboveBottom(json.get("above_bottom").getAsInt());
        } else if (json.has("below_top")) {
            return belowTop(json.get("below_top").getAsInt());
        }
        return context -> 0;
    }

    static VerticalAnchor absolute(int value) {
        return context -> value;
    }

    static VerticalAnchor aboveBottom(int value) {
        return context -> context.minY() + value;
    }

    static VerticalAnchor belowTop(int value) {
        return context -> context.maxY() - value;
    }

}