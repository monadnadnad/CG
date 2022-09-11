import colour
import colour.plotting as cpl
import pandas as pd
import matplotlib.pyplot as plt
from pathlib import Path

def read_pigment(fname):
    df = pd.read_csv(fname,
        delimiter="[\t\s]+", skiprows=2,
        header=None, engine="python")
    sd_dict = dict(zip(df[0], df[1]))
    sd = colour.SpectralDistribution(sd_dict)
    return sd

def pigment_to_XYZ(fname):
    sd = read_pigment(fname)
    cmfs = colour.MSDS_CMFS['CIE 1931 2 Degree Standard Observer']
    illuminant = colour.SDS_ILLUMINANTS['D65']
    return colour.sd_to_XYZ(sd, cmfs, illuminant, method="Integration")

def D65_sRGB():
    ls = range(380, 781)
    sd_dict = dict(zip(ls, [1]*len(ls)))
    sd = colour.SpectralDistribution(sd_dict)
    cmfs = colour.MSDS_CMFS['CIE 1931 2 Degree Standard Observer']
    illuminant = colour.SDS_ILLUMINANTS['D65']
    XYZ = colour.sd_to_XYZ(sd, cmfs, illuminant, method="Integration")
    return colour.XYZ_to_sRGB(XYZ)

def main():
    # pigmentsの点をplot
    (fig, ax) = cpl.plot_RGB_colourspaces_in_chromaticity_diagram_CIE1931(
        ["sRGB"], standalone=False, diagram_opacity=0.5)
    for p in Path("PigmentData").iterdir():
        XYZ = pigment_to_XYZ(p)
        x, y = colour.XYZ_to_xy(XYZ)
        textpos = (-50, 30)
        if p.name == "33plot": textpos = (-75, 15)
        if p.name == "41plot": textpos = (-25, 15)
        plt.annotate(p.name,
            xy=[x,y],
            xytext=textpos,
            textcoords="offset points",
            arrowprops=dict(arrowstyle="->"))
        plt.plot(x, y, "o", color="black", markersize=2.0)
    fig.show()
    #plt.waitforbuttonpress()
    plt.savefig("xy.png")

def main2():
    # pigments -> sRGB(8bits)
    RGB = D65_sRGB()
    r,g,b = RGB
    print("filename,X,Y,Z,R,G,B")
    for p in Path("PigmentData").iterdir():
        XYZ = pigment_to_XYZ(p)
        RGB = colour.XYZ_to_sRGB(XYZ)
        rr,gg,bb = RGB
        RGB8 = [round(255*rr/r),round(255*gg/g),round(255*bb/b)]
        # 変換不可能なものも8bitに無理やり変換する
        RGB8 = [min(255, max(0, x)) for x in RGB8]
        print(",".join([
            p.name,
            ",".join(map(str, XYZ)),
            ",".join(map(str, RGB8))
            ]))