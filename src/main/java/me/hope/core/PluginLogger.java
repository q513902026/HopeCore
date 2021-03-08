package me.hope.core;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class PluginLogger {
    private Logger logger;
    private File file;
    private static FileHandler fileHandler;
    private Date date = new Date();
    private final boolean debugMode = false;
    public PluginLogger(Logger logger,File file){
        this.logger = logger;
        this.file = file;
        fileHandler = createFileLog();

    }
    private FileHandler createFileLog(){
        if (fileHandler == null ){
            try {
                fileHandler =  new FileHandler(file.getAbsolutePath(),true);
                fileHandler.setLevel(Level.INFO);
                fileHandler.setFormatter(new Formatter() {
                                             @Override
                                             public String format(LogRecord record) {
                                                 date.setTime(record.getMillis());
                                                 return String.format("%1$tc %2$s %3$s \n\t",date,record.getLevel(),record.getMessage());
                                             }
                                         });
            logger.addHandler(fileHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileHandler;
    }
    public void sendConsoleMessage(String string){
        if (debugMode) logger.info(string);
    }
    public void sendErrorMessage(String string){
        logger.warning(string);
    }

}
