package me.hope.core;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class PluginLogger {
    private final Logger logger;
    private final File file;
    private static FileHandler fileHandler;
    private final Date date = new Date();
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
                fileHandler.setLevel(Level.ALL);
                fileHandler.setEncoding("UTF-8");
                fileHandler.setFormatter(new Formatter() {
                                             @Override
                                             public String format(LogRecord record) {
                                                 date.setTime(record.getMillis());
                                                 return String.format("%1$tc %2$s %3$s \n",date,record.getLevel(),record.getMessage());
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
        logger.info(string);
    }
    public void sendErrorMessage(String string){
        logger.warning(string);
    }

}
