import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CIEXYZMain {
    public static void main(String[] args){
        D65 d65 = new D65();
        CIEXYZ1931 ciexyz1931 = new CIEXYZ1931();
        System.out.println("filename,X,Y,Z,R,G,B");
        try(Stream<Path> stream = Files.list(Paths.get("PigmentData"))){
            stream.forEach(path -> {
                Pigment pigment = new Pigment(path);
                Double[] XYZ = ciexyz1931.getXYZ(pigment, d65);
                Double[] sRGB = ciexyz1931.getsRGB(pigment, d65);
                System.out.println(
                    path.getFileName()+","+
                    String.join(",", Arrays.stream(XYZ).map(v -> v.toString()).collect(Collectors.toList()))+","+
                    String.join(",", Arrays.stream(sRGB).map(v-> v.toString()).collect(Collectors.toList()))
                );
            });
        } catch(IOException e) {
            System.out.println(e);
        }
    }
}