package vicente.rocka.util.JSON;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.JSONObject;

import java.util.Map;

public class ItemJSON {

    public static JSONObject itemStackToJSON(ItemStack itemSell) {
        JSONObject itemJSON = new JSONObject();

        itemJSON.put("v", Bukkit.getUnsafe().getDataVersion());
        itemJSON.put("type", itemSell.getType().name());
        itemJSON.put("amount", itemSell.getAmount());

        if (itemSell.getItemMeta() instanceof SkullMeta) {

            SkullMeta skullMeta = (SkullMeta) itemSell.getItemMeta();
            itemJSON.put("meta", skullMeta.serialize());

        } else if (itemSell.getItemMeta() instanceof BlockStateMeta) {

            BlockStateMeta blockStateMeta = (BlockStateMeta) itemSell.getItemMeta();
            itemJSON.put("meta", blockStateMeta.serialize());

        } else {
            itemJSON.put("meta", itemSell.getItemMeta().serialize());
        }
        return itemJSON;
    }

    public static ItemStack JSONToItem(JSONObject itemJSON) {

        Map<String, Object> map_item = itemJSON.toMap();
        Map<String, Object> map_meta_item = itemJSON.getJSONObject("meta").toMap();

        map_meta_item.put("==", "ItemMeta");

        ConfigurationSerializable meta = ConfigurationSerialization.deserializeObject(map_meta_item);
        ItemStack item = ItemStack.deserialize(map_item);

        item.setItemMeta((ItemMeta) meta);

        return item;
    }

}

