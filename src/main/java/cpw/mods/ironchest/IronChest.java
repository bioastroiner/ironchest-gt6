/*******************************************************************************
 * Copyright (c) 2012 cpw. All rights reserved. This program and the accompanying materials are made available under the
 * terms of the GNU Public License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors: cpw - initial API and implementation
 ******************************************************************************/
package cpw.mods.ironchest;

import cpw.mods.fml.common.Loader;
import gregapi.code.ModData;
import gregapi.data.CS;
import gregapi.data.MT;
import gregapi.data.OD;
import gregapi.data.OP;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.CR;
import gregapi.util.ST;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(
        modid = "IronChest",
        name = "Iron Chests",
        version = IronChest.VERSION,
        dependencies = "required-after:Forge@[10.10,);required-after:FML@[7.2,)")
public class IronChest {

    public static BlockIronChest ironChestBlock;
    @SidedProxy(clientSide = "cpw.mods.ironchest.client.ClientProxy", serverSide = "cpw.mods.ironchest.CommonProxy")
    public static CommonProxy proxy;
    @Instance("IronChest")
    public static IronChest instance;
    public static boolean CACHE_RENDER = true;
    public static boolean OCELOTS_SITONCHESTS = true;
    public static final String VERSION = "GRADLETOKEN_VERSION";
    public static boolean TRANSPARENT_RENDER_INSIDE = true;
    public static double TRANSPARENT_RENDER_DISTANCE = 128D;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        try {
            cfg.load();
            ChestChangerType.buildItems(cfg);
            CACHE_RENDER = cfg.get(Configuration.CATEGORY_GENERAL, "cacheRenderingInformation", true).getBoolean(true);
            OCELOTS_SITONCHESTS = cfg.get(Configuration.CATEGORY_GENERAL, "ocelotsSitOnChests", true).getBoolean(true);
            TRANSPARENT_RENDER_INSIDE = cfg.get("general", "transparentRenderInside", true).getBoolean(true);
            TRANSPARENT_RENDER_DISTANCE = cfg.get("general", "transparentRenderDistance", 128D).getDouble(128D);
        } catch (Exception e) {
            FMLLog.log(Level.ERROR, e, "IronChest has a problem loading its configuration");
        } finally {
            if (cfg.hasChanged()) cfg.save();
        }
        ironChestBlock = new BlockIronChest();
        GameRegistry.registerBlock(ironChestBlock, ItemIronChest.class, "BlockIronChest");
        PacketHandler.INSTANCE.ordinal();
    }

    @EventHandler
    public void load(FMLInitializationEvent evt) {
        for (IronChestType typ : IronChestType.values()) {
            if (typ.name().equals("STEEL")) {
                GameRegistry.registerTileEntityWithAlternatives(
                        typ.clazz,
                        "IronChest." + typ.name(),
                        typ.name(),
                        "SILVER",
                        "IronChest.SILVER");
            }
            else if (typ.name().equals("BRONZE")) {
                GameRegistry.registerTileEntityWithAlternatives(
                        typ.clazz,
                        "IronChest." + typ.name(),
                        typ.name(),
                        "IRON",
                        "IronChest.IRON");
            }
            else if (typ.name().equals("TUNGSTEN")) {
                GameRegistry.registerTileEntityWithAlternatives(
                        typ.clazz,
                        "IronChest." + typ.name(),
                        typ.name(),
                        "DIAMOND",
                        "IronChest.DIAMOND");
            }
            else if (typ.name().equals("STAINLESS")) {
                GameRegistry.registerTileEntityWithAlternatives(
                        typ.clazz,
                        "IronChest." + typ.name(),
                        typ.name(),
                        "GOLD",
                        "IronChest.GOLD");
            }
            else if (typ.name().equals("LEAD")) {
                GameRegistry.registerTileEntityWithAlternatives(
                        typ.clazz,
                        "IronChest." + typ.name(),
                        typ.name(),
                        "COPPER",
                        "IronChest.COPPER");
            }
            else {
                GameRegistry.registerTileEntityWithAlternatives(typ.clazz, "IronChest." + typ.name(), typ.name());
            }
            proxy.registerTileEntitySpecialRenderer(typ);
        }
        OreDictionary.registerOre("chestWood", Blocks.chest);
        IronChestType.registerBlocksAndRecipes(ironChestBlock);
        //ChestChangerType.generateRecipes();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
        proxy.registerRenderInformation();
        // if (OCELOTS_SITONCHESTS)
        // {
        // MinecraftForge.EVENT_BUS.register(new OcelotsSitOnChestsHandler());
        // }
        MinecraftForge.EVENT_BUS.register(this);

        if(Loader.isModLoaded("gregtech")){
            OreDictMaterial[] mats = new OreDictMaterial[]{MT.Bronze,MT.StainlessSteel,MT.TungstenSteel,MT.Pb,MT.Steel};
            for (int i = 0; i < mats.length; i++) {
                ItemStack res = ST.make(new ModData("IronChest","Iron Chest"),"BlockIronChest",1,i);
                CR.shaped(res,CR.DEF,"PhP","RSR","PPP",'P', OP.plateDouble.mat(mats[i],1),'S', OP.stickLong.mat(mats[i],1),'R', OP.ring.mat(mats[i],1));
            }
            for (int i = 0; i < ChestChangerType.values().length; i++) {
                ChestChangerType t = ChestChangerType.values()[i];
                switch(t){
                    case BRONZESTAINLESS:
                        CR.shapeless(ST.make(new ModData("IronChest","Iron Chest"),t.itemName, 1, CS.W),new Object[]{ST.make(
                                new ModData("IronChest", "Iron Chest"),
                                "BlockIronChest", 1, 1)
                        ,OP.plate.mat(MT.Bronze,1)});
                        break;
                    case STAINLESSTUNGSTEN:
                        CR.shapeless(ST.make(new ModData("IronChest","Iron Chest"),t.itemName, 1, CS.W),new Object[]{ST.make(
                                new ModData("IronChest", "Iron Chest"),
                                "BlockIronChest", 1, 2)
                                ,OP.plate.mat(MT.StainlessSteel,1)});
                        break;
                    case LEADSTEEL:
                        CR.shapeless(ST.make(new ModData("IronChest","Iron Chest"),t.itemName, 1, CS.W),new Object[]{ST.make(
                                new ModData("IronChest", "Iron Chest"),
                                "BlockIronChest", 1, 4)
                                ,OP.plate.mat(MT.Pb,1)});
                        break;
                    case STEELSTAINLESS:
                        CR.shapeless(ST.make(new ModData("IronChest","Iron Chest"),t.itemName, 1, CS.W),new Object[]{ST.make(
                                new ModData("IronChest", "Iron Chest"),
                                "BlockIronChest", 1, 1)
                                ,OP.plate.mat(MT.Steel,1)});
                        break;
                    case LEADBRONZE:
                        CR.shapeless(ST.make(new ModData("IronChest","Iron Chest"),t.itemName, 1, CS.W),new Object[]{ST.make(
                                new ModData("IronChest", "Iron Chest"),
                                "BlockIronChest", 1, 0)
                                ,OP.plate.mat(MT.Pb,1)});
                        break;
                    case TUNGSTENCRYSTAL:
                        CR.shaped(ST.make(new ModData("IronChest","Iron Chest"),t.itemName, 1, CS.W),CR.DEF,"PhP","RSR","PPP",'P', OP.plate.mat(MT.Glass,1),'S', OP.stick.mat(MT.Steel,1),'R', OP.ring.mat(MT.Steel,1));

//                        CR.shapeless(ST.make(new ModData("IronChest","Iron Chest"),t.itemName, 1, CS.W),new Object[]{ST.make(
//
//                                ,OP.plate.mat(MT.Glass,1)});
                        break;
                    case WOODBRONZE:
                        CR.shapeless(ST.make(new ModData("IronChest","Iron Chest"),t.itemName, 1, CS.W),new Object[]{ST.make(
                                new ModData("IronChest", "Iron Chest"),
                                "BlockIronChest", 1, 0)
                                ,OP.plate.mat(MT.Wood,1)});
                        break;
                    case WOODLEAD:
                        CR.shapeless(ST.make(new ModData("IronChest","Iron Chest"),t.itemName, 1, CS.W),new Object[]{ST.make(
                                new ModData("IronChest", "Iron Chest"),
                                "BlockIronChest", 1, 3)
                                ,OP.plate.mat(MT.Wood,1)});
                        break;
                    case TUNGSTENOBSIDIAN:
//                        CR.shapeless(ST.make(new ModData("IronChest","Iron Chest"),t.itemName, 1, CS.W),new Object[]{ST.make(
//                                new ModData("IronChest", "Iron Chest"),
//                                "BlockIronChest", 1, 6)
//                                ,OP.plate.mat(MT.Bronze,1)});
                                break;
                }
            }

        }
    }

    @EventHandler
    public void modsLoaded(FMLPostInitializationEvent evt) {

    }

    // cpw.mods.fml.common.registry.GameRegistry#registerTileEntityWithAlternatives
    @Mod.EventHandler
    public void missingMapping(FMLMissingMappingsEvent event) {
//        for (FMLMissingMappingsEvent.MissingMapping mapping : event.getAll()) {
//            if (mapping.type == GameRegistry.Type.BLOCK) {
//                switch (mapping.name) {
////                    case "IronChest:copperSilverUpgrade":
////                        mapping.remap(GameRegistry.findBlock("IronChest", "leadSteelUpgrade"));
////                        break;
////                    case "IronChest:silverGoldUpgrade":
////                        mapping.remap(GameRegistry.findBlock("IronChest", "steelStainlessUpgrade"));
////                        break;
////                    case "IronChest:woodIronUpgrade":
////                        mapping.remap(GameRegistry.findBlock("IronChest", "woodBronzeUpgrade"));
////                        break;
////                    case "IronChest:ironGoldUpgrade":
////                        mapping.remap(GameRegistry.findBlock("IronChest", "bronzeStainlessUpgrade"));
////                        break;
////                    default:
//                }
//            } else if (mapping.type == GameRegistry.Type.ITEM) {
//                switch (mapping.name) {
////                    case "IronChest:copperSilverUpgrade":
////                        mapping.remap(GameRegistry.findItem("IronChest", "leadSteelUpgrade"));
////                        break;
////                    case "IronChest:silverGoldUpgrade":
////                        mapping.remap(GameRegistry.findItem("IronChest", "steelStainlessUpgrade"));
////                        break;
////                    case "IronChest:woodIronUpgrade":
////                        mapping.remap(GameRegistry.findBlock("IronChest", "woodBronzeUpgrade"));
////                        break;
////                    case "IronChest:ironGoldUpgrade":
////                        mapping.remap(GameRegistry.findBlock("IronChest", "bronzeStainlessUpgrade"));
////                        break;
//                    default:
//                }
//            }
//        }
    }
}
