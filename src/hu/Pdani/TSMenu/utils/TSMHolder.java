package hu.Pdani.TSMenu.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class TSMHolder implements InventoryHolder {

    private Inventory inventory = null;

    public TSMHolder(){
    }

    public TSMHolder setInventory(Inventory inventory){
        this.inventory = inventory;
        return this;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
