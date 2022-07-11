package vicente.rocka.util;

import org.json.JSONObject;
import org.json.JSONStringer;
import vicente.rocka.villaregion.VillaRegion;
import net.md_5.bungee.api.ChatColor;


import org.bukkit.Bukkit;
import org.json.JSONArray;

import java.io.*;
import java.util.stream.Collectors;

public class JSONFile {

    public static VillaRegion plugin;
    /**
     * Metodo para crear un JSON de la carpeta
     * Importante! no poner la extencion .json
     * @author Rocka
     * @param name El nombre del archivo
     */
    private static void createJSONFile(String name) {
        File file = new File(plugin.getDataFolder(),name+".json");

        if(file.exists()) return;

        JSONArray myObj = new JSONArray();

        try(FileWriter fileCreation = new FileWriter(plugin.getDataFolder().getPath()+"/"+name+".json")) {

            fileCreation.write(myObj.toString());

        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"[VillageRegion / ERROR] The plugin cannot create "+name+".json file");
        }


    }

    private JSONArray json;
    private String path;
    /**
     * Metodo para obtener un JSON de la carpeta
     * @author Rocka
     * @param name El nombre del archivo
     */
    public JSONFile(String name) {
        JSONFile.createJSONFile(name);

        String sourceName = plugin.getDataFolder().getPath()+"/"+name+".json";

        try(BufferedReader reader = new BufferedReader(new FileReader(sourceName))) {

            String content = reader.lines().collect(Collectors.joining(System.lineSeparator()));

            JSONArray json = new JSONArray(content);

            this.setJson(json);
            this.setPath(sourceName);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo para obtener el JSON
     */
    public JSONArray getJson() {
        return json;
    }

    private void setJson(JSONArray json) {
        this.json = json;
    }

    public String getPath() {
        return path;
    }

    private void setPath(String path) {
        this.path = path;
    }

    public void saveJson() {
        try(FileWriter file = new FileWriter(this.getPath(),false);) {

            file.write(this.getJson().toString());

        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"[ERROR] Error al guardar "+this.getPath());
            throw new RuntimeException(e);
        }
    }
}
