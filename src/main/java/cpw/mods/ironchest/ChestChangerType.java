/*******************************************************************************
 * Copyright (c) 2012 cpw. All rights reserved. This program and the accompanying materials are made available under the
 * terms of the GNU Public License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors: cpw - initial API and implementation
 ******************************************************************************/
package cpw.mods.ironchest;

import static cpw.mods.ironchest.IronChestType.LEAD;
import static cpw.mods.ironchest.IronChestType.CRYSTAL;
import static cpw.mods.ironchest.IronChestType.TUNGSTEN;
import static cpw.mods.ironchest.IronChestType.STAINLESS;
import static cpw.mods.ironchest.IronChestType.BRONZE;
import static cpw.mods.ironchest.IronChestType.OBSIDIAN;
import static cpw.mods.ironchest.IronChestType.STEEL;
import static cpw.mods.ironchest.IronChestType.WOOD;

import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.registry.GameRegistry;

public enum ChestChangerType {

    BRONZESTAINLESS(BRONZE, STAINLESS, "bronzeStainlessUpgrade", "Iron to Stainless Chest Upgrade", "mmm", "msm", "mmm"),
    STAINLESSTUNGSTEN(STAINLESS, TUNGSTEN, "stainlessTungstenUpgrade", "Tungsten Steel to Diamond Chest Upgrade", "GGG", "msm", "GGG"),
    LEADSTEEL(LEAD, STEEL, "leadSteelUpgrade", "Lead to Steel Chest Upgrade", "mmm", "msm", "mmm"),
    STEELSTAINLESS(STEEL, STAINLESS, "steelStainlessUpgrade", "Steel to Stainless Chest Upgrade", "mGm", "GsG", "mGm"),
    LEADBRONZE(LEAD, BRONZE, "leadBronzeUpgrade", "Lead to Bronze Chest Upgrade", "mGm", "GsG", "mGm"),
    TUNGSTENCRYSTAL(TUNGSTEN, CRYSTAL, "tungstenCrystalUpgrade", "Tungsten Steel to Crystal Chest Upgrade", "GGG", "GOG", "GGG"),
    WOODBRONZE(WOOD, BRONZE, "woodBronzeUpgrade", "Normal chest to Bronze Chest Upgrade", "mmm", "msm", "mmm"),
    WOODLEAD(WOOD, LEAD, "woodLeadUpgrade", "Normal chest to Lead Chest Upgrade", "mmm", "msm", "mmm"),
    TUNGSTENOBSIDIAN(TUNGSTEN, OBSIDIAN, "tungstenObsidianUpgrade", "Tungsten Steel to Obsidian Chest Upgrade", "mmm", "mGm",
            "mmm");

    public final IronChestType source;
    public final IronChestType target;
    public final String itemName;
    public final String descriptiveName;

    public ItemChestChanger getItem() {
        return item;
    }

    private ItemChestChanger item;
    private final String[] recipe;

    private ChestChangerType(IronChestType source, IronChestType target, String itemName, String descriptiveName,
            String... recipe) {
        this.source = source;
        this.target = target;
        this.itemName = itemName;
        this.descriptiveName = descriptiveName;
        this.recipe = recipe;
    }

    public boolean canUpgrade(IronChestType from) {
        return from == this.source;
    }

    public int getTarget() {
        return this.target.ordinal();
    }

    public ItemChestChanger buildItem(Configuration cfg) {
        item = new ItemChestChanger(this);
        GameRegistry.registerItem(item, itemName);
        return item;
    }

    public void addRecipes() {

    }

    public static void buildItems(Configuration cfg) {
        for (ChestChangerType type : values()) {
            type.buildItem(cfg);
        }
    }

    public static void generateRecipes() {
        for (ChestChangerType item : values()) {
            item.addRecipes();
        }
    }
}
