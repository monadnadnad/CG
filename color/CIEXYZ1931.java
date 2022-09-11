import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

class CIEXYZ1931 {
    HashMap<Integer, Double> x;
    HashMap<Integer, Double> y;
    HashMap<Integer, Double> z;

    CIEXYZ1931(){
        x = new HashMap<>();
        y = new HashMap<>();
        z = new HashMap<>();
        try {
            Path path = Paths.get("./XYZ_CIE_2.dat");
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String l: lines){
                String[] l_arr = l.split("\t");
                int lam = Integer.parseInt(l_arr[0]);
                double xv = Double.parseDouble(l_arr[1]);
                double yv = Double.parseDouble(l_arr[2]);
                double zv = Double.parseDouble(l_arr[3]);
                x.put(lam, xv);
                y.put(lam, yv);
                z.put(lam, zv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 試料とD65光源から色彩値XYZを求める
     */
    public Double[] getXYZ(Pigment pigment, D65 d65){
        // 積分の計算
        double X = 0, Y = 0, Z = 0;
        int dl = 5;
        double k = 0;
        for (int l=380; l<=780; l+=dl) {
            X += d65.get(l) * pigment.get(l) * x.get(l);
            Y += d65.get(l) * pigment.get(l) * y.get(l);
            Z += d65.get(l) * pigment.get(l) * z.get(l);
            k += d65.get(l) * y.get(l);
        }
        // 幅と定数の乗算
        k = 100/(k*dl);
        X *= k*dl;
        Y *= k*dl;
        Z *= k*dl;
        Double[] ret = {X,Y,Z};
        return ret;
    }
    public Double[] getsRGB(Pigment pigment, D65 d65){
        Double[] XYZ = getXYZ(pigment, d65);
        double X = XYZ[0], Y = XYZ[1], Z = XYZ[2];
        double R = 3.2410*X - 1.5374*Y - 0.4986*Z;
        double G = -0.9692*X + 1.8760*Y + 0.0416*Z;
        double B = 0.0556*X - 0.2040*Y + 1.0507*Z;
        Double[] ret = {R,G,B};
        for (int i=0; i<3; i++){
            if (ret[i] <= 0.0031308) {
                ret[i] *= 12.92;
            } else {
                ret[i] = 1.055*Math.pow(ret[i], 1/2.4) - 0.055;
            }
        }
        return ret;
    }
}
