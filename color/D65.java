import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

class D65{
    HashMap<Integer, Double> spectral_dist;
    D65(){
        spectral_dist = new HashMap<>();
        try {
            Path path = Paths.get("./D65.dat");
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String l: lines){
                String[] l_arr = l.split(" ");
                int lam = Integer.parseInt(l_arr[0]);
                double sd = Double.parseDouble(l_arr[1]);
                spectral_dist.put(lam, sd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 波長に対応する分光密度を線形補間で求める
     */
    public double get(double lam){
        double k1 = 5.0d * Math.floor(lam/5.0d);
        double k2 = k1 + 5.0d;
        assert 300d < k1 && k1 < 830d;
        assert 300d < k2 && k2 < 830d;
        double v1 = spectral_dist.get((int) Math.round(k1));
        double v2 = spectral_dist.get((int) Math.round(k2));
        double v_lerp = v1 + (v2 - v1) * (lam - k1) / (k2 - k1);
        
        return v_lerp;
    }
}