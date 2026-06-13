package com.hihelloy.invincible.shop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class WeaponGuideBook {

    public static ItemStack create() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.title(Component.text("OmniMC Weapon Guide"));
        meta.author(Component.text("The Forge"));
        meta.pages(List.of(
        cover(),
        shopPage(),
        forgePage(),
        swordsPage(),
        gauntletsPage1(),
        gauntletsPage2(),
        ornamentsPage(),
        buffsPage()
        ));
        book.setItemMeta(meta);
        return book;
    }

    private static Component cover() {
        return line("  §5§l⚒ OmniMC\n")
        .append(line(" §8Weapon Guide\n\n\n"))
        .append(line("  §o§7\"Power forged\n"))
        .append(line("  §o§7 through purpose\"\n\n\n"))
        .append(line("§8— Steps —\n"))
        .append(line("§71. Buy ingredients\n"))
        .append(line("§72. Forge the weapon"));
    }

    private static Component shopPage() {
        return line("§5§l== The Shop ==\n\n")
        .append(line("§8Type §b/inv forge\n"))
        .append(line("§8to open the\n"))
        .append(line("§8Ingredient Shop.\n\n"))
        .append(line("§8Spend §6Stat Points\n"))
        .append(line("§8to buy ingredients.\n\n"))
        .append(line("§8Earn points by\n"))
        .append(line("§8defeating enemies.\n\n"))
        .append(line("§7Ingredients:\n"))
        .append(line("§8Iron Core §720sp\n"))
        .append(line("§5Arcane Crystal §730sp\n"))
        .append(line("§bTech Chip §730sp\n"))
        .append(line("§8Shadow Metal §735sp\n"))
        .append(line("§6Beast Essence §735sp\n"))
        .append(line("§dVoid Fragment §750sp\n"))
        .append(line("§4Viltrumite Alloy §760sp"));
    }

    private static Component forgePage() {
        return line("§5§l== The Forge ==\n\n")
        .append(line("§8Find the §5OmniMC\n"))
        .append(line("§5Forge §8station\n"))
        .append(line("§8in the world.\n\n"))
        .append(line("§8Bring your\n"))
        .append(line("§8ingredients and\n"))
        .append(line("§8select a recipe\n"))
        .append(line("§8to craft your\n"))
        .append(line("§8weapon.\n\n"))
        .append(line("§7Example:\n"))
        .append(line("§cConquest's Blade\n"))
        .append(line("§8· 3x Iron Core\n"))
        .append(line("§8· 2x Arcane Crystal\n"))
        .append(line("§8· 1x Viltrumite Alloy\n"))
        .append(line("§8· 8 seconds to forge"));
    }

    private static Component swordsPage() {
        return line("§5§l== Swords ==\n\n")
        .append(weapon("Conquest's Blade", "§c", "3×Iron + 2×Arcane\n+ 1×Viltrumite"))
        .append(weapon("Immortal's Edge", "§e", "2×Viltrumite\n+ 3×Arcane"))
        .append(weapon("Thragg War Claw", "§5", "3×Viltrumite\n+ 2×Beast"))
        .append(weapon("GDA Shock Baton", "§b", "3×Iron + 2×Tech"))
        .append(weapon("Darkwing Blade", "§8", "2×Shadow + 2×Iron"))
        .append(weapon("Rex Gauntlets", "§a", "3×Tech + 1×Arcane"));
    }

    private static Component gauntletsPage1() {
        return line("§5§l== Gauntlets I ==\n\n")
        .append(weapon("Invincible Fists", "§b", "3×Viltrumite\n+ 2×Arcane"))
        .append(weapon("Beast Claws", "§6", "3×Beast + 2×Iron"))
        .append(weapon("Seismic Gauntlets", "§b", "4×Viltrumite\n+ 2×Arcane"))
        .append(weapon("World Breaker", "§4", "5×Viltrumite\n+ 3×Arcane"));
    }

    private static Component gauntletsPage2() {
        return line("§5§l== Gauntlets II ==\n\n")
        .append(weapon("Monster Girl", "§a", "3×Beast + 2×Arcane"))
        .append(weapon("Density Fists", "§7", "4×Iron + 2×Arcane"))
        .append(line("\n§5§l== Ornaments ==\n\n"))
        .append(weapon("Darkwing Batarang", "§8", "2×Shadow + 1×Tech"))
        .append(weapon("Atom Eve Bracelet", "§d", "3×Arcane + 2×Void"))
        .append(weapon("Angstrom Ring", "§5", "3×Void + 2×Arcane"))
        .append(weapon("Robot Control Unit", "§7", "4×Tech + 1×Arcane"));
    }

    private static Component ornamentsPage() {
        return line("§5§l== Weapon Buffs ==\n\n")
        .append(line("§8Weapons with the\n"))
        .append(line("§6INVINCIBLE ABILITY\n"))
        .append(line("§8stat will boost\n"))
        .append(line("§8your matching moves:\n\n"))
        .append(line("§cConquest's Blade\n"))
        .append(line("§8Blade Spin +dmg\n"))
        .append(line("§8Devastation +dmg\n\n"))
        .append(line("§bInvincible Fists\n"))
        .append(line("§8Atomic Punch +dmg\n"))
        .append(line("§8Combo Strike +dmg\n"))
        .append(line("§8Tremor Slam +dmg\n\n"))
        .append(line("§4World Breaker\n"))
        .append(line("§8Sonic Clap +dmg\n"))
        .append(line("§8Berserker Rage +dur"));
    }

    private static Component buffsPage() {
        return line("§5§l== More Buffs ==\n\n")
        .append(line("§dAtom Eve Bracelet\n"))
        .append(line("§8Pink Beam +dmg +rng\n"))
        .append(line("§8Atomic Burst +dmg\n\n"))
        .append(line("§5Angstrom Ring\n"))
        .append(line("§8Dimension Portal\n"))
        .append(line("§8Reality Tear +dmg\n\n"))
        .append(line("§6Beast Claws\n"))
        .append(line("§8Crushing Grip +dmg\n"))
        .append(line("§8Predator Leap\n\n"))
        .append(line("§7§l== Tips ==\n\n"))
        .append(line("§8Buffs from all\n"))
        .append(line("§8equipped gear stack!\n"))
        .append(line("§8Radius, damage,\n"))
        .append(line("§8duration all boost."));
    }

    private static Component line(String s) {
        return LegacyComponentSerializer.legacySection().deserialize(s);
    }

    private static Component weapon(String name, String color, String recipe) {
        Component c = line(color + "§l" + name + "\n");
        for (String r : recipe.split("\n")) {
            c = c.append(line("§8  " + r + "\n"));
        }
        return c.append(Component.empty());
    }
}