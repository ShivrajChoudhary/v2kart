package in.com.v2kart.dataimport.commands;

/**
 * Interface exposing csv import functionalities as commands. Each command refers to single instance of csv import process. It exposes the
 * execute interface which initialize the csv import process and persist them in to Hybris platform.
 * 
 */
public interface ImportCommand {

    /**
     * Command execution return true if there is any update performed
     * 
     * @throws Exception
     */
    boolean execute() throws Exception;
}
