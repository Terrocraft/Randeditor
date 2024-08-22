package de.Kingmine.randcustomizer.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

public class SConfig extends YamlConfiguration {
    private File file;
    private String name;
    public SConfig(File file, String name) {
        super();
        this.file = file;
        this.name = name;
        try {
            super.load(file);
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file , ex);
        }
    }

    public void setDefault(String a, Object b) {
        if(!isSet(a)) {
            set(a, b);
            save();
        }
    }

    public void save() {
        try {
            super.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        file.delete();
        ConfigUtil.cachemap.remove(name);
    }

    public File getFile() {
        return file;
    }
}
