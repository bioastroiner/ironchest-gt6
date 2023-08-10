/*******************************************************************************
 * Copyright (c) 2012 cpw. All rights reserved. This program and the accompanying materials are made available under the
 * terms of the GNU Public License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors: cpw - initial API and implementation
 ******************************************************************************/
package cpw.mods.ironchest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.IIcon;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum IronChestType {

    BRONZE(54, 9, true, "Bronze Chest", "ironchest.png", 0, Arrays.asList("ingotBronze"),
            TileEntityBronzeChest.class, "mmmmPmmmm", "mGmG3GmGm"),
    STAINLESS(81, 9, true, "Stainless Steel Chest", "goldchest.png", 1, Arrays.asList("ingotSteelStainless"), TileEntityStainlessChest.class,
            "mmmmPmmmm", "mGmG4GmGm"),
    TUNGSTEN(108, 12, true, "Tungsten Steel Chest", "diamondchest.png", 2, Arrays.asList("ingotTungstenSteel"),
            TileEntityTungstenChest.class, "GGGmPmGGG", "GGGG4Gmmm"),
    LEAD(45, 9, false, "Lead Chest", "copperchest.png", 3, Arrays.asList("ingotLead"),
            TileEntityLeadChest.class, "mmmmCmmmm"),
    STEEL(72, 9, false, "Steel Chest", "silverchest.png", 4, Arrays.asList("ingotSteel"), TileEntitySteelChest.class,
            "mmmm3mmmm", "mGmG0GmGm"),
    CRYSTAL(108, 12, true, "Crystal Chest", "crystalchest.png", 5, Arrays.asList("blockGlass"),
            TileEntityCrystalChest.class, "GGGGPGGGG"),
    OBSIDIAN(108, 12, false, "Obsidian Chest", "obsidianchest.png", 6, Arrays.asList("obsidian"),
            TileEntityObsidianChest.class, "mmmm2mmmm"),
    DIRTCHEST9000(1, 1, false, "Dirt Chest 9000", "dirtchest.png", 7, Arrays.asList("dirt"), TileEntityDirtChest.class,
            Item.getItemFromBlock(Blocks.dirt), "mmmmCmmmm"),
    WOOD(0, 0, false, "", "", -1, Arrays.asList("plankWood"), null);

    final int size;
    private final int rowLength;
    public final String friendlyName;
    private final boolean tieredChest;
    private final String modelTexture;
    private final int textureRow;
    public final Class<? extends TileEntityBronzeChest> clazz;
    private final String[] recipes;
    private final ArrayList<String> matList;
    private final Item itemFilter;

    IronChestType(int size, int rowLength, boolean tieredChest, String friendlyName, String modelTexture,
                  int textureRow, List<String> mats, Class<? extends TileEntityBronzeChest> clazz, String... recipes) {
        this(size, rowLength, tieredChest, friendlyName, modelTexture, textureRow, mats, clazz, (Item) null, recipes);
    }

    IronChestType(int size, int rowLength, boolean tieredChest, String friendlyName, String modelTexture,
                  int textureRow, List<String> mats, Class<? extends TileEntityBronzeChest> clazz, Item itemFilter,
                  String... recipes) {
        this.size = size;
        this.rowLength = rowLength;
        this.tieredChest = tieredChest;
        this.friendlyName = friendlyName;
        this.modelTexture = modelTexture;
        this.textureRow = textureRow;
        this.clazz = clazz;
        this.itemFilter = itemFilter;
        this.recipes = recipes;
        this.matList = new ArrayList<String>();
        matList.addAll(mats);
    }

    public String getModelTexture() {
        return modelTexture;
    }

    public int getTextureRow() {
        return textureRow;
    }

    public static TileEntityBronzeChest makeEntity(int metadata) {
        // Compatibility
        int chesttype = validateMeta(metadata);
        if (chesttype == metadata) {
            try {
                return values()[chesttype].clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                // unpossible
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void registerBlocksAndRecipes(BlockIronChest blockResult) {
        Object previous = "chestWood";
        for (IronChestType typ : values()) {
            ItemStack chest = new ItemStack(blockResult, 1, typ.ordinal());
            if (typ.isValidForCreativeMode()) GameRegistry.registerCustomItemStack(typ.friendlyName, chest);
            if (typ.tieredChest) previous = chest;
        }
    }

    public static Object translateOreName(String mat) {
        if (mat.equals("obsidian")) {
            return Blocks.obsidian;
        } else if (mat.equals("dirt")) {
            return Blocks.dirt;
        }
        return mat;
    }

    public static void addRecipe(ItemStack is, Object... parts) {

    }

    public int getRowCount() {
        return size / rowLength;
    }

    public int getRowLength() {
        return rowLength;
    }

    public boolean isTransparent() {
        return this == CRYSTAL;
    }

    public List<String> getMatList() {
        return matList;
    }

    public static int validateMeta(int i) {
        if (i < values().length && values()[i].size > 0) {
            return i;
        } else {
            return 0;
        }
    }

    public boolean isValidForCreativeMode() {
        return validateMeta(ordinal()) == ordinal();
    }

    public boolean isExplosionResistant() {
        return this == OBSIDIAN;
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    @SideOnly(Side.CLIENT)
    public void makeIcons(IIconRegister par1IconRegister) {
        if (isValidForCreativeMode()) {
            icons = new IIcon[3];
            int i = 0;
            for (String s : sideNames) {
                if (name().equalsIgnoreCase("steel")) {
                    icons[i++] = par1IconRegister.registerIcon(String.format("ironchest:silver_%s", s));
                } else {
                    icons[i++] = par1IconRegister
                            .registerIcon(String.format("ironchest:%s_%s", name().toLowerCase(), s));
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side) {

        return icons[sideMapping[side]];
    }

    private static final String[] sideNames = { "top", "front", "side" };
    private static final int[] sideMapping = { 0, 0, 2, 1, 2, 2, 2 };

    public Slot makeSlot(IInventory chestInventory, int index, int x, int y) {
        return new ValidatingSlot(chestInventory, index, x, y, this);
    }

    public boolean acceptsStack(ItemStack itemstack) {
        return itemFilter == null || itemstack == null || itemstack.getItem() == itemFilter;
    }

    public void adornItemDrop(ItemStack item) {
        if (this == DIRTCHEST9000) {
            item.setTagInfo("dirtchest", new NBTTagByte((byte) 1));
        }
    }
}
