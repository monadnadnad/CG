import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

class Pigment {
    HashMap<Double, Double> reflectance;
    TreeSet<Double> measured_lam;
    double min_lam;
    double max_lam;
    Pigment(Path path){
        reflectance = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            lines.remove(0);
            lines.remove(0);
            for (String l: lines){
                String[] l_arr = l.split(" ");
                double lam = Double.parseDouble(l_arr[0]);
                double ref = Double.parseDouble(l_arr[1]);
                reflectance.put(lam, ref);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        measured_lam = new TreeSet<>(reflectance.keySet());
        min_lam = measured_lam.first();
        max_lam = measured_lam.last();
    }
    /**
     * 波長に対応する分光反射率を線形補間で求める
     */
    public double get(double lam){
        if (lam <= min_lam) return reflectance.get(min_lam);
        if (lam >= max_lam) return reflectance.get(max_lam);
        double k1 = measured_lam.headSet(lam).last();
        double k2 = measured_lam.tailSet(lam).first();
        double v1 = reflectance.get(k1);
        double v2 = reflectance.get(k2);
        double v_lerp = v1 + (v2 - v1) * (lam - k1) / (k2 - k1);
        return v_lerp;
    }
}
