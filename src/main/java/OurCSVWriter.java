import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.*;

public class OurCSVWriter{

//    private String fileName;
    private Path path;

    public OurCSVWriter(String fileName){
//        this.fileName = fileName;
        this.path = Paths.get(fileName);
    }

    public void setPath(String path){
        this.path = Paths.get(path);
    }

    public void write(int eval, double fitness, double metric){
        String line = eval + "," + fitness +"," + metric + "\n";
        try {
            Files.write(path, line.getBytes(), APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear(){
        try {
            Files.write(path, "N,fitness,metric\n".getBytes(), TRUNCATE_EXISTING, CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
