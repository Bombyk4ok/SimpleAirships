package com.example.simpleairships;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.*;

public class BlockHelm extends Block {

    public BlockHelm() {
        super(Material.WOOD);
        setRegistryName(SimpleAirshipsMod.MODID, "helm_block");
        setUnlocalizedName(SimpleAirshipsMod.MODID + ".helm_block");
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        setHardness(2.0F);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote && hand == EnumHand.MAIN_HAND) {
            assembleAirship(worldIn, pos, playerIn);
        }
        return true;
    }

    private void assembleAirship(World world, BlockPos helmPos, EntityPlayer player) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(helmPos);
        visited.add(helmPos);

        int balloons = 0;
        int engines = 0;

        while (!queue.isEmpty() && visited.size() < 128) {
            BlockPos current = queue.poll();
            Block block = world.getBlockState(current).getBlock();

            if (block == SimpleAirshipsMod.BALLOON_BLOCK) balloons++;
            if (block == SimpleAirshipsMod.ENGINE_BLOCK) engines++;

            for (EnumFacing dir : EnumFacing.values()) {
                BlockPos neighbor = current.offset(dir);
                if (!visited.contains(neighbor)) {
                    IBlockState neighborState = world.getBlockState(neighbor);
                    Block nBlock = neighborState.getBlock();
                    if (nBlock != Blocks.AIR && nBlock != Blocks.DIRT && nBlock != Blocks.GRASS && nBlock != Blocks.STONE && nBlock != Blocks.BEDROCK && nBlock != Blocks.WATER && nBlock != Blocks.FLOWING_WATER) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }

        if (balloons < 1) {
            player.sendMessage(new TextComponentString("Нужен хотя бы 1 Воздушный баллон!"));
            return;
        }

        NBTTagList blockList = new NBTTagList();
        for (BlockPos p : visited) {
            IBlockState st = world.getBlockState(p);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("dx", p.getX() - helmPos.getX());
            tag.setInteger("dy", p.getY() - helmPos.getY());
            tag.setInteger("dz", p.getZ() - helmPos.getZ());
            tag.setInteger("stateId", Block.getStateId(st));
            blockList.appendTag(tag);
            world.setBlockToAir(p);
        }

        EntityAirship airship = new EntityAirship(world);
        airship.setPosition(helmPos.getX() + 0.5, helmPos.getY() + 0.5, helmPos.getZ() + 0.5);
        airship.setShipData(blockList, balloons, engines);
        world.spawnEntity(airship);
        player.startRiding(airship);
    }
}
