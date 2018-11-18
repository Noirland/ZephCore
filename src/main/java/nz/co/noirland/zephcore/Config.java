package nz.co.noirland.zephcore;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;

public abstract class Config {

    protected final String file;
    protected final File configFile;
    protected FileConfiguration config;

    protected abstract Plugin getPlugin();

    protected abstract Debug getDebug();

    public Config(String path, String file) {
        this.file = file;
        configFile = new File(getPlugin().getDataFolder(), path + File.separator + file);
        load();

    }

    public Config(String file) {
        this.file = file;
        configFile = new File(getPlugin().getDataFolder(), file);
        load();
    }

    public Config(File file) {
        this.file = file.getName();
        configFile = file;
        load();
    }

    protected void load() {
        if(!configFile.exists()) {
            createFile();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void save() {
        try{
            config.save(configFile);
        }catch(IOException e) {
            getDebug().warning("Unable to write to file " + configFile.getPath(), e);
        }
    }

    protected void createFile() {
        if(!configFile.getParentFile().mkdirs()) {
            getDebug().warning("Could not create plugin config directory!");
            return;
        }

        InputStream iStream = getResource();

        if(iStream == null) {
            getDebug().warning("File missing from jar: " + file);
            return;
        }

        OutputStream oStream = null;

        try {
            oStream = new FileOutputStream(configFile);

            int read;
            byte[] bytes = new byte[1024];

            while((read = iStream.read(bytes)) != -1) {
                oStream.write(bytes, 0, read);
            }

        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }finally{

            if(oStream != null) {
                try {
                    oStream.close();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                iStream.close();
            }catch(IOException e) {
                e.printStackTrace();
            }

        }

    }

    protected InputStream getResource() {
        return getPlugin().getResource(file);
    }



}

