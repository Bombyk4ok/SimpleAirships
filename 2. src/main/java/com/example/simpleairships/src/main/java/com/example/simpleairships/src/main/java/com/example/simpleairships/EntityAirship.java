package com.example.simpleairships;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class EntityAirship extends Entity {
    private static final DataParameter<NBTTagCompound> SHIP_BLOCKS = EntityDataManager.createKey(EntityAirship.class, DataSerializers.COMPOUND_TAG);
    private int balloonCount = 1;
    private int engineCount = 0;

    public EntityAirship(World worldIn) {
        super(worldIn);
        this.setSize(1.5F, 1.5F);
        this.preventEntitySpawning = true;
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(SHIP_BLOCKS, new NBTTagCompound());
    }

    public void setShipData(NBTTagList blocks, int balloons, int engines) {
        NBTTagCompound wrapper = new NBTTagCompound();
        wrapper.setTag("Blocks", blocks);
        this.dataManager.set(SHIP_BLOCKS, wrapper);
        this.balloonCount = balloons;
        this.engineCount = engines;
    }

    public NBTTagList getSavedBlocks() {
        return this.dataManager.get(SHIP_BLOCKS).getTagList("Blocks", Constants.NBT.TAG_COMPOUND);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        Entity passenger = this.getControllingPassenger();
        if (passenger instanceof EntityLivingBase) {
            EntityLivingBase driver = (EntityLivingBase) passenger;
            this.rotationYaw = driver.rotationYaw;

            float forward = driver.moveForward;
            float strafe = driver.moveStrafing;
            double speed = 0.25 + (this.engineCount * 0.12);

            double rad = Math.toRadians(this.rotationYaw);
            double mx = (-MathHelper.sin((float) rad) * forward + MathHelper.cos((float) rad) * strafe) * speed;
            double mz = (MathHelper.cos((float) rad) * forward + MathHelper.sin((float) rad) * strafe) * speed;

            double my = 0.0;
            if (driver.rotationPitch < -25.0F) {
                my = 0.15 + (this.balloonCount * 0.03);
            } else if (driver.rotationPitch > 25.0F) {
                my = -0.2;
            }

            this.motionX = mx;
            this.motionY = my;
            this.motionZ = mz;
        } else {
            this.motionX *= 0.8;
            this.motionY = 0.0;
            this.motionZ *= 0.8;

            if (!this.world.isRemote && this.ticksExisted > 10) {
                disassembleAirship();
            }
        }

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
    }

    private void disassembleAirship() {
        NBTTagList savedBlocks = getSavedBlocks();
        BlockPos basePos = new BlockPos(Math.floor(this.posX), Math.floor(this.posY), Math.floor(this.posZ));
        for (int i = 0; i < savedBlocks.tagCount(); i++) {
            NBTTagCompound tag = savedBlocks.getCompoundTagAt(i);
            int dx = tag.getInteger("dx");
            int dy = tag.getInteger("dy");
            int dz = tag.getInteger("dz");
            IBlockState state = Block.getStateById(tag.getInteger("stateId"));

            BlockPos targetPos = basePos.add(dx, dy, dz);
            if (this.world.isAirBlock(targetPos)) {
                this.world.setBlockState(targetPos, state);
            }
        }
        this.setDead();
    }

    @Override
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.dataManager.set(SHIP_BLOCKS, compound);
        this.balloonCount = compound.getInteger("Balloons");
        this.engineCount = compound.getInteger("Engines");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setTag("Blocks", getSavedBlocks());
        compound.setInteger("Balloons", this.balloonCount);
        compound.setInteger("Engines", this.engineCount);
    }
}
