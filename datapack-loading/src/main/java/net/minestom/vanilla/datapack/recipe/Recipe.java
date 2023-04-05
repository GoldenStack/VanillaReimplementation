package net.minestom.vanilla.datapack.recipe;

import com.squareup.moshi.JsonReader;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import net.minestom.vanilla.datapack.DatapackLoader;
import net.minestom.vanilla.datapack.json.JsonUtils;
import net.minestom.vanilla.datapack.json.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public interface Recipe {

    @NotNull NamespaceID type();

    @Nullable String group();

    static Recipe fromJson(JsonReader reader) throws IOException {
        String src = reader.peekJson().nextSource().readUtf8();
        return JsonUtils.unionNamespaceStringType(reader, "type", Map.ofEntries(
            Map.entry("minecraft:blasting", DatapackLoader.moshi(Blasting.class)),
            Map.entry("minecraft:campfire_cooking", DatapackLoader.moshi(CampfireCooking.class)),
            Map.entry("minecraft:crafting_shaped", DatapackLoader.moshi(Shaped.class)),
            Map.entry("minecraft:crafting_shapeless", DatapackLoader.moshi(Shapeless.class)),
            Map.entry("minecraft:crafting_special_armordye", DatapackLoader.moshi(Special.ArmorDye.class)),
            Map.entry("minecraft:crafting_special_bannerduplicate", DatapackLoader.moshi(Special.BannerDuplicate.class)),
            Map.entry("minecraft:crafting_special_bookcloning", DatapackLoader.moshi(Special.BookCloning.class)),
            Map.entry("minecraft:crafting_special_firework_rocket", DatapackLoader.moshi(Special.FireworkRocket.class)),
            Map.entry("minecraft:crafting_special_firework_star", DatapackLoader.moshi(Special.FireworkStar.class)),
            Map.entry("minecraft:crafting_special_firework_star_fade", DatapackLoader.moshi(Special.FireworkStarFade.class)),
            Map.entry("minecraft:crafting_special_mapcloning", DatapackLoader.moshi(Special.MapCloning.class)),
            Map.entry("minecraft:crafting_special_mapextending", DatapackLoader.moshi(Special.MapExtending.class)),
            Map.entry("minecraft:crafting_special_repairitem", DatapackLoader.moshi(Special.RepairItem.class)),
            Map.entry("minecraft:crafting_special_shielddecoration", DatapackLoader.moshi(Special.ShieldDecoration.class)),
            Map.entry("minecraft:crafting_special_shulkerboxcoloring", DatapackLoader.moshi(Special.ShulkerBoxColoring.class)),
            Map.entry("minecraft:crafting_special_tippedarrow", DatapackLoader.moshi(Special.TippedArrow.class)),
            Map.entry("minecraft:crafting_special_suspiciousstew", DatapackLoader.moshi(Special.SuspiciousStew.class)),
            Map.entry("minecraft:smelting", DatapackLoader.moshi(Smelting.class)),
            Map.entry("minecraft:smithing", DatapackLoader.moshi(Smithing.class)),
            Map.entry("minecraft:smoking", DatapackLoader.moshi(Smoking.class)),
            Map.entry("minecraft:stonecutting", DatapackLoader.moshi(Stonecutting.class))
        ));
    }

    interface CookingRecipe extends Recipe {
        @NotNull List<Ingredient> ingredient();
        @NotNull Material result();
        double experience();
        @Optional Integer cookingTime();
    }

    interface Ingredient {
        static Ingredient fromJson(JsonReader reader) throws IOException {
            return JsonUtils.<Ingredient>typeMap(reader, Map.of(
                    JsonReader.Token.BEGIN_ARRAY, json -> {
                        Stream.Builder<Single> items = Stream.builder();
                        json.beginArray();
                        while (json.peek() != JsonReader.Token.END_ARRAY) {
                            items.add(DatapackLoader.moshi(Single.class).apply(json));
                        }
                        json.endArray();
                        return new Multi(items.build().toList());
                    },
                    JsonReader.Token.BEGIN_OBJECT, DatapackLoader.moshi(Single.class)
            ));
        }

        interface Single extends Ingredient {
            static Single fromJson(JsonReader reader) throws IOException {
                JsonReader peek = reader.peekJson();
                peek.beginObject();
                boolean isTag = JsonUtils.hasProperty(peek, "tag");
                if (isTag) {
                    return DatapackLoader.moshi(Tag.class).apply(reader);
                }
                boolean isItem = JsonUtils.hasProperty(peek, "item");
                if (isItem) {
                    return DatapackLoader.moshi(Item.class).apply(reader);
                }
                throw new IOException("Ingredient must have either tag or item");
            }
        }

        record Item(Material item) implements Single {
        }

        record Tag(String tag) implements Single {
        }

        record Multi(List<Single> items) implements Ingredient {
        }
    }

    record Result(Material item, @Optional Integer count) {
    }

    record Blasting(String group,
                    JsonUtils.SingleOrList<Ingredient> ingredient, Material result, double experience, @Optional Integer cookingTime) implements CookingRecipe {
        @Override
        public @NotNull NamespaceID type() {
            return NamespaceID.from("minecraft:blasting");
        }
    }

    record CampfireCooking(String group,
                           JsonUtils.SingleOrList<Ingredient> ingredient, Material result, double experience, @Optional Integer cookingTime) implements CookingRecipe {
        @Override
        public @NotNull NamespaceID type() {
            return NamespaceID.from("minecraft:campfire_cooking");
        }
    }

    record Shaped(String group,
                          List<String> pattern, Map<Character, Ingredient> key, Result result) implements Recipe {
        @Override
        public @NotNull NamespaceID type() {
            return NamespaceID.from("minecraft:crafting_shaped");
        }
    }

    record Shapeless(String group,
                     JsonUtils.SingleOrList<Ingredient> ingredients, Result result) implements Recipe {
        @Override
        public @NotNull NamespaceID type() {
            return NamespaceID.from("minecraft:crafting_shapeless");
        }
    }

    sealed interface Special extends Recipe {

        record ArmorDye(String group) implements Special {
            @Override
            public @NotNull NamespaceID type() {
                return NamespaceID.from("minecraft:crafting_special_armordye");
            }
        }

        record BannerDuplicate(String group) implements Special {
            @Override
            public @NotNull NamespaceID type() {
                return NamespaceID.from("minecraft:crafting_special_bannerduplicate");
            }
        }

        record BookCloning(String group) implements Special {
            @Override
            public @NotNull NamespaceID type() {
                return NamespaceID.from("minecraft:crafting_special_bookcloning");
            }
        }

        record FireworkRocket(String group) implements Special {
            @Override
            public @NotNull NamespaceID type() {
                return NamespaceID.from("minecraft:crafting_special_firework_rocket");
            }
        }

        record FireworkStar(String group) implements Special {
            @Override
            public @NotNull NamespaceID type() {
                return NamespaceID.from("minecraft:crafting_special_firework_star");
            }
        }

        record FireworkStarFade(String group) implements Special {
            @Override
            public @NotNull NamespaceID type() {
                return NamespaceID.from("minecraft:crafting_special_firework_star_fade");
            }
        }

        record MapCloning(String group) implements Special {
            @Override
            public @NotNull NamespaceID type() {
                return NamespaceID.from("minecraft:crafting_special_mapcloning");
            }
        }

        record MapExtending(String group) implements Special {
            @Override
            public @NotNull NamespaceID type() {
                return NamespaceID.from("minecraft:crafting_special_mapextending");
            }
        }

        record RepairItem(String group) implements Special {
            @Override
            public @NotNull NamespaceID type() {
                return NamespaceID.from("minecraft:crafting_special_repairitem");
            }
        }

        record ShieldDecoration(String group) implements Special {
            @Override
            public @NotNull NamespaceID type() {
                return NamespaceID.from("minecraft:crafting_special_shielddecoration");
            }
        }

        record ShulkerBoxColoring(String group) implements Special {
            @Override
            public @NotNull NamespaceID type() {
                return NamespaceID.from("minecraft:crafting_special_shulkerboxcoloring");
            }
        }

        record TippedArrow(String group) implements Special {
            @Override
            public @NotNull NamespaceID type() {
                return NamespaceID.from("minecraft:crafting_special_tippedarrow");
            }
        }

        record SuspiciousStew(String group) implements Special {
            @Override
            public @NotNull NamespaceID type() {
                return NamespaceID.from("minecraft:crafting_special_suspiciousstew");
            }
        }
    }

    record Smelting(String group,
                    JsonUtils.SingleOrList<Ingredient> ingredient, Material result, double experience, @Optional Integer cookingTime) implements CookingRecipe {
        @Override
        public @NotNull NamespaceID type() {
            return NamespaceID.from("minecraft:smelting");
        }
    }

    record Smithing(String group,
                    Ingredient.Single base, Ingredient.Single addition, Ingredient.Item result) implements Recipe {
        @Override
        public @NotNull NamespaceID type() {
            return NamespaceID.from("minecraft:smithing");
        }
    }

    record Smoking(String group,
                   JsonUtils.SingleOrList<Ingredient> ingredient, Material result, double experience, @Optional Integer cookingTime) implements CookingRecipe {
        @Override
        public @NotNull NamespaceID type() {
            return NamespaceID.from("minecraft:smoking");
        }
    }

    record Stonecutting(String group,
                        JsonUtils.SingleOrList<Ingredient> ingredient, Material result, int count) implements Recipe {
        @Override
        public @NotNull NamespaceID type() {
            return NamespaceID.from("minecraft:stonecutting");
        }
    }
}