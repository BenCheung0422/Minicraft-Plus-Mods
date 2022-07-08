package minicraft.core;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.jar.Manifest;

import org.json.JSONArray;
import org.json.JSONObject;

import minicraft.core.io.ClassLoader;
import minicraft.core.io.FileHandler;
import minicraft.core.io.InputHandler;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.saveload.Version;
import minicraft.screen.Display;

public class Mods extends Game {
    private Mods() {}
    public static ArrayList<Mods.Mod> Mods = new ArrayList<>();
    public static final Version VERSION = new Version("0.3.0");
    public static ArrayList<Display> guiDisplays = new ArrayList<>();
	public static ArrayList<Item> registeredItems = new ArrayList<>();

	public static void registerItem(Item item) {
		registeredItems.add(item);
		Items.add(item);
	}

    public static void init() {}

    static {
        File[] mods = FileHandler.readModsFolder();
        if (mods != null) {
            ClassLoader loader = new ClassLoader();
            for (int a = 0; a<mods.length; a++) {
                Entry<Entry<Class<?>, Manifest>, JSONObject> modP = loader.loadJar(mods[a]);
                Mod modObj = new Mod(modP.getKey().getKey(), modP.getKey().getValue());
                modObj.Info = modP.getValue();
                if (modObj.Info.getString("name") == null) System.out.println("mod.json name not found.");
                if (modObj.Info.getString("description") == null) System.out.println("mod.json description not found.");
                Mods.add(modObj);
            }
            int count = 0;
            boolean success = true;
            for (int i = 0; i<Mods.size(); i++) {
                if (count > Mods.size()*Mods.size()) {
                    System.out.println("Dependency structure too complex.");
                    success = false;
                    break;
                }
                int index = i;
                JSONArray deps = Mods.get(i).Info.optJSONArray("depencencies");
                if (deps != null) {
                    for (int j = 0; j<deps.length(); j++) {
                        String n = deps.getString(j);
                        int jdx = Mods.indexOf(Mods.stream().filter(m -> m.Info.getString("name").equals(n)).findAny().orElse(null));
                        if (jdx == -1) {
                            System.out.println("Dependency not found: "+deps.getString(j));
                            success = false;
                            break;
                        }
                        index = jdx;
                    }
                    Mods.add(index, Mods.remove(i));
                }
                count++;
            }
            if (success) Mods.forEach(m -> m.startLoading());
        }
    }

    static void renderGui() {
        guiDisplays.forEach(m -> m.render(Renderer.screen));
    }
    static void tick(InputHandler input) {
        guiDisplays.forEach(m -> m.tick(input));
    }

    public static class Mod {
        public JSONObject Info;
        public Manifest manifest;
        private Class<?> mainClass;
        public Mod(Class<?> m, Manifest man) {
            manifest = man;
            mainClass = m;
        }

        public void startLoading() {
            try {
                mainClass.getDeclaredMethod("entry").invoke(null, new Object[0]);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
