package declutterapp;

/**
 * The Main Class.
 * @author adam
 */
public class DeclutterApp {

    /** @param blargs the command line arguments */
    public static void main(String[] blargs){
        App app = new App(200); //Initialize with 200 tracks
        app.launch();
    }
}
