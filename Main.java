import java.io.File;
import java.util.ArrayList;

public class Main {

    public static ArrayList<File> getRelevantFiles(File directory){
        ArrayList<File> result = new ArrayList<File>();
        File[] files = directory.listFiles();
        if (files == null) return result;
        for (File f:files){
            if (f.getName().endsWith(".jack")){
                result.add(f);
            }
        }
        return result;
    }
    
    public static void main(String[] args) {
        if (args.length != 1){
            System.out.println("please put jack file or directory");
        }
        else {
            String fileOutPath = "";
            String fileInName = args[0];
            File input = new File(fileInName);
            File out;
            ArrayList<File> jackFiles = new ArrayList<File>();
            if (input.isFile()) {
                String path = input.getAbsolutePath();
                if (!path.endsWith(".jack")) {
                    throw new IllegalArgumentException("no jack file is supplied");
                }
                jackFiles.add(input);
            } 
            else if (input.isDirectory()) {
                jackFiles = getRelevantFiles(input);
                if (jackFiles.size() == 0) {
                    throw new IllegalArgumentException("No jack file is supplied in this folder");
                }
            }
            for (File f: jackFiles) {
                fileOutPath = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".")) + ".xml";
                out = new File(fileOutPath);
                CompilationEngine compilationEngine = new CompilationEngine(f,out);
                compilationEngine.compileClass();
            }
        }
    }

    
}