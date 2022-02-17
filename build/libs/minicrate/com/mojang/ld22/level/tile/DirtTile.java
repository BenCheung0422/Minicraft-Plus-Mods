// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.mojang.ld22.level.tile;

import com.mojang.ld22.sound.Sound;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.gfx.Screen;

public class DirtTile extends Tile
{
    public DirtTile(final int id) {
        super(id);
    }
    
    @Override
    public void render(final Screen screen, final Level level, final int x, final int y) {
        final int col = Color.get(level.dirtColor, level.dirtColor, level.dirtColor - 111, level.dirtColor - 111);
        screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0);
        screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0);
        screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0);
        screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0);
    }
    
    @Override
    public void tick(final Level level, final int xt, final int yt) {
        if (this.random.nextInt(100) != 0) {
            return;
        }
        if (level.isSurface() != 1) {
            return;
        }
        level.setTile(xt, yt, DirtTile.grass, 0);
        level.setFire(xt, yt, 0);
    }
    
    @Override
    public boolean mayPass(final Level level, final int x, final int y, final Entity e) {
        return e.canWalk();
    }
    
    @Override
    public boolean interact(final Level level, final int xt, final int yt, final Player player, final Item item, final int attackDir) {
        if (item instanceof ToolItem) {
            final ToolItem tool = (ToolItem)item;
            if (tool.type == ToolType.shovel && player.payStamina(4 - tool.level)) {
                level.setTile(xt, yt, Tile.hole, 0);
                level.setFire(xt, yt, 0);
                level.add(new ItemEntity(new ResourceItem(Resource.dirt), xt * 16 + this.random.nextInt(10) + 3, yt * 16 + this.random.nextInt(10) + 3));
                Sound.monsterHurt.play();
                return true;
            }
            if (tool.type == ToolType.hoe && player.payStamina(4 - tool.level)) {
                level.setTile(xt, yt, Tile.farmland, 0);
                level.setFire(xt, yt, 0);
                Sound.monsterHurt.play();
                return true;
            }
            if (tool.type == ToolType.staff && player.payStamina(4 - tool.level)) {
                level.setTile(xt, yt, Tile.flower, 0);
                level.setFire(xt, yt, 0);
                Sound.monsterHurt.play();
                return true;
            }
            if (tool.type == ToolType.wand && player.payStamina(4 - tool.level)) {
                level.setTile(xt, yt, Tile.freshWater, 0);
                level.setFire(xt, yt, 0);
                Sound.monsterHurt.play();
                return true;
            }
        }
        return false;
    }
}
