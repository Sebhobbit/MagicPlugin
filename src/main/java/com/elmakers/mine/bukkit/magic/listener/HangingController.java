package com.elmakers.mine.bukkit.magic.listener;

import com.elmakers.mine.bukkit.api.block.UndoList;
import com.elmakers.mine.bukkit.magic.MagicController;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

import java.util.logging.Level;

public class HangingController implements Listener {
    private final MagicController controller;

    public HangingController(MagicController controller) {
        this.controller = controller;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakEvent event) {
        final Hanging entity = event.getEntity();
        if (!entity.isValid()) return;
        try {
            final BlockFace attachedFace = entity.getAttachedFace();
            Location location = entity.getLocation();
            location = location.getBlock().getRelative(attachedFace).getLocation();
            UndoList undoList = controller.getPendingUndo(location);
            if (undoList != null) {
                event.setCancelled(true);
                undoList.damage(entity, 0);
            }
        } catch (Exception ex) {
            controller.getLogger().log(Level.WARNING, "Failed to handle HangingBreakEvent", ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        Entity breakingEntity = event.getRemover();
        if (breakingEntity == null) return;

        Hanging entity = event.getEntity();
        UndoList undoList = controller.getEntityUndo(breakingEntity);
        if (undoList != null)
        {
            undoList.damage(entity, 0);

            // Prevent item drops, but still remove it
            // Else it'll probably just break again.
            event.setCancelled(true);
        }
    }
}