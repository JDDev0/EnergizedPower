package me.jddev0.ep.config;

import com.google.common.io.Files;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Config {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final List<ConfigValue<?>> configValues = new LinkedList<>();

    private final File configFile;

    private final String headerComment;

    private boolean loaded = false;

    public Config(File configFile, String headerComment) {
        this.configFile = configFile;
        this.headerComment = headerComment;
    }

    public boolean isRegistered(String key) {
        return configValues.stream().anyMatch(cv -> cv.getKey().equals(key));
    }

    public boolean isRegistered(ConfigValue<?> configValue) {
        return configValues.contains(configValue);
    }

    public <T> ConfigValue<T> register(ConfigValue<T> configValue) {
        String key = configValue.getKey();
        if(isRegistered(key))
            throw new RuntimeException("Config value conflict for key \"" + key + "\"");

        configValues.add(configValue);

        return configValue;
    }

    public void unregister(ConfigValue<?> configValue) {
        String key = configValue.getKey();
        if(!isRegistered(key))
            throw new RuntimeException("Config value was not registered for key \"" + key + "\"");

        configValues.removeIf(cv -> cv.getKey().equals(key));
    }

    @SuppressWarnings("unchecked")
    public <T> ConfigValue<T> getConfigValue(String key) {
        Optional<ConfigValue<?>> configValueOptional = configValues.stream().filter(cv -> cv.getKey().equals(key)).findAny();
        if(configValueOptional.isEmpty())
            throw new RuntimeException("Config value was not registered for key \"" + key + "\"");

        return (ConfigValue<T>)configValueOptional.get();
    }

    public static boolean isCharAtIndexEscaped(String str, int index) {
        if(str == null || str.length() <= index || index < 0)
            return false;

        for(int i = index - 1;i >= 0;i--)
            if(str.charAt(i) != '\\')
                return (index - i) % 2 == 0;

        return index % 2 == 1;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void read() throws ConfigValidationException, IOException {
        loaded = true;

        if(!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();

            writeDefault();

            configValues.forEach(ConfigValue::manuallyLoaded);

            return;
        }

        try(BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            int lineNumber = 0;
            String line;
            while((line = br.readLine()) != null) {
                lineNumber++;

                if(line.contains("#")) {
                    int commentStartIndex = -1;
                    while((commentStartIndex = line.indexOf('#', commentStartIndex + 1)) > -1)
                        if(!isCharAtIndexEscaped(line, commentStartIndex))
                            break;

                    if(commentStartIndex > -1)
                        line = line.substring(0, commentStartIndex);
                }

                line = line.trim();

                //Parse escape chars
                StringBuilder lineBuilder = new StringBuilder(line);
                int unescapeIndex = line.length();
                while(unescapeIndex > 0) {
                    if(isCharAtIndexEscaped(line, unescapeIndex)) {
                        //Special escape chars
                        switch(lineBuilder.charAt(unescapeIndex)) {
                            case 'n' -> lineBuilder.replace(unescapeIndex, unescapeIndex + 1, "\n");
                            case 't' -> lineBuilder.replace(unescapeIndex, unescapeIndex + 1, "\t");
                        }

                        lineBuilder.deleteCharAt(unescapeIndex - 1);

                        unescapeIndex--;
                    }

                    unescapeIndex--;
                }
                line = lineBuilder.toString();

                if(line.isEmpty())
                    continue;

                String[] tokens = line.split(" = ", 2);
                if(tokens.length != 2) {
                    String errorTxt = "Configuration file \"" + configFile + "\" has an invalid format at line " +
                            lineNumber + ": \" = \" expected => Using default values.";
                    LOGGER.error(errorTxt);

                    throw new ConfigValidationException(errorTxt);
                }

                String key = tokens[0];
                if(!isRegistered(key)) {
                    LOGGER.warn("Configuration file \"" + configFile + "\" contains an invalid key at line " +
                            lineNumber + ": \"" + key + "\" => Ignoring.");

                    continue;
                }

                ConfigValue<?> configValue = getConfigValue(key);

                String value = tokens[1];
                try {
                    configValue.read(value);
                }catch(ConfigValidationException e) {
                    LOGGER.warn("Configuration file \"" + configFile + "\" contains an invalid value at line " +
                            lineNumber + ": for \"" + key + "\" value \"" + value + "\" (" + e.getMessage() +
                            ") => Using default value \"" + configValue.getDefaultValue() + "\".");
                }
            }

            //Write new config values if any
            if(configValues.stream().anyMatch(cv -> !cv.isLoaded())) {
                makeBackup();

                write();

                configValues.stream().filter(cv -> !cv.isLoaded()).forEach(ConfigValue::manuallyLoaded);
            }
        }
    }

    public void write() throws IOException {
        writeInternally(ConfigValue::write);
    }

    public void writeDefault() throws IOException {
        writeInternally(ConfigValue::writeDefault);
    }

    private void writeInternally(Function<ConfigValue<?>, String> configValueSerializer) throws IOException {
        if(!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
        }

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(configFile))) {
            bw.write("# === " + headerComment + " ===");
            bw.newLine();

            for(ConfigValue<?> configValue:configValues) {
                bw.newLine();

                String comment = configValue.getComment();
                if(comment != null) {
                    bw.write("# " + comment);
                    bw.newLine();
                }

                String defaultValue = configValue.writeDefault();

                defaultValue = defaultValue.
                        replaceAll("\\\\", "\\\\").
                        replaceAll("#", "\\\\#").
                        replaceAll("\\n", "\\\\n").
                        replaceAll("\\t", "\\\\t");

                bw.write("# [Default value]: " + defaultValue);
                bw.newLine();

                List<String> commentLines = configValue.getValidationCommentLines();
                for(String commentLine:commentLines) {
                    bw.write("# [Validation]: " + commentLine);
                    bw.newLine();
                }

                String rawValue = configValueSerializer.apply(configValue);

                rawValue = rawValue.
                        replaceAll("\\\\", "\\\\").
                        replaceAll("#", "\\\\#").
                        replaceAll("\\n", "\\\\n").
                        replaceAll("\\t", "\\\\t");

                bw.write(configValue.getKey() + " = " + rawValue);
                bw.newLine();
            }

            bw.flush();
        }
    }

    public void makeBackup() throws IOException {
        String fileName = configFile.getName();

        File backupConfigFile = new File(configFile.getParentFile(), fileName + ".5.bak");
        if(backupConfigFile.exists())
            backupConfigFile.delete();

        //Move newer backup files to higher numbers
        for(int i = 5;i > 1;i--) {
            backupConfigFile = new File(configFile.getParentFile(), fileName + "." + i + ".bak");
            File newerBackupConfigFile = new File(configFile.getParentFile(), fileName + "." + (i - 1) + ".bak");

            if(newerBackupConfigFile.exists())
                newerBackupConfigFile.renameTo(backupConfigFile);
        }

        backupConfigFile = new File(configFile.getParentFile(), fileName + ".1.bak");
        Files.copy(configFile, backupConfigFile);
    }
}
