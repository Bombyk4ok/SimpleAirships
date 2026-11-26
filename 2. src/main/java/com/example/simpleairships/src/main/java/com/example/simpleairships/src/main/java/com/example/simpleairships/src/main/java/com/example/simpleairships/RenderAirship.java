package com.example.simpleairships;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;

public class RenderAirship extends Render<EntityAirship> {

    public RenderAirship(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityAirship entity, double x, double y, double z, float entityYaw, float partialTicks) {
        NBTTagList blocks = entity.getSavedBlocks();
        if (blocks.tagCount() == 0) return;

        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        this.bindEntityTexture(entity);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5, 0.0, -0.5);

        for (int i = 0; i < blocks.tagCount(); i++) {
            NBTTagCompound tag = blocks.getCompoundTagAt(i);
            int dx = tag.getInteger("dx");
            int dy = tag.getInteger("dy");
            int dz = tag.getInteger("dz");
            IBlockState state = Block.getStateById(tag.getInteger("stateId"));

            if (state.getRenderType() == EnumBlockRenderType.MODEL) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(dx, dy, dz);
                dispatcher.renderBlockBrightness(state, 1.0F);
                GlStateManager.popMatrix();
            }
        }

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAirship entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
