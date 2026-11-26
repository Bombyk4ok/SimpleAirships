package com.example.simpleairships;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = SimpleAirshipsMod.MODID, name = SimpleAirshipsMod.NAME, version = SimpleAirshipsMod.VERSION)
@Mod.EventBusSubscriber
public class SimpleAirshipsMod {
    public static final String MODID = "simpleairships";
    public static final String NAME = "Simple Airships";
    public static final String VERSION = "1.0";

    public static Block HELM_BLOCK;
    public static Block BALLOON_BLOCK;
    public static Block ENGINE_BLOCK;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        HELM_BLOCK = new BlockHelm();
        BALLOON_BLOCK = new Block(Material.CLOTH)
                .setRegistryName(MODID, "balloon_block")
                .setUnlocalizedName(MODID + ".balloon_block")
                .setCreativeTab(CreativeTabs.TRANSPORTATION)
                .setHardness(0.8F);
        ENGINE_BLOCK = new Block(Material.IRON)
                .setRegistryName(MODID, "engine_block")
                .setUnlocalizedName(MODID + ".engine_block")
                .setCreativeTab(CreativeTabs.TRANSPORTATION)
                .setHardness(2.0F);

        EntityRegistry.registerModEntity(
                new ResourceLocation(MODID, "airship"),
                EntityAirship.class,
                "airship",
                1,
                this,
                80,
                1,
                true
        );

        if (event.getSide().isClient()) {
            registerRenderers();
        }
    }

    @SideOnly(Side.CLIENT)
    private void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityAirship.class, RenderAirship::new);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(HELM_BLOCK, BALLOON_BLOCK, ENGINE_BLOCK);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ItemBlock(HELM_BLOCK).setRegistryName(HELM_BLOCK.getRegistryName()),
                new ItemBlock(BALLOON_BLOCK).setRegistryName(BALLOON_BLOCK.getRegistryName()),
                new ItemBlock(ENGINE_BLOCK).setRegistryName(ENGINE_BLOCK.getRegistryName())
        );
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent event) {
        registerItemModel(HELM_BLOCK);
        registerItemModel(BALLOON_BLOCK);
        registerItemModel(ENGINE_BLOCK);
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemModel(Block block) {
        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(block),
                0,
                new ModelResourceLocation(block.getRegistryName(), "inventory")
        );
    }
}
