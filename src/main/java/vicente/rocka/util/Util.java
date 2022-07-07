package vicente.rocka.util;

public class Util {
    public static boolean isInt(String s) {
        try{
            Integer.valueOf(s);
            return true;
        }catch(NumberFormatException e) {
            return false;
        }
    }
    public static int[] getPoints(int a, int b, int c, int a2, int b2, int c2) {
        int x, y,z,x2,y2,z2;
        int[] point = new int[6];
        if(a > a2 && c > c2) {//x2,y2,z2 es abajo izquierda
            x = a2;
            y = b2;
            z = c2;
            x2 = a;
            y2 = b;
            z2 = c;
            point[0] = x;
            point[1] = y;
            point[2] = z;
            point[3] = x2;
            point[4] = y2;
            point[5] = z2;
        }else if(a2 > a && c2 > c) {//x,y,z es abajo izquierda
            x = a;
            y = b;
            z = c;
            x2 = a2;
            y2 = b2;
            z2 = c2;
            point[0] = x;
            point[1] = y;
            point[2] = z;
            point[3] = x2;
            point[4] = y2;
            point[5] = z2;
        }else if(a2<a && c2 > c) {//x2,y2,z es abajo izquiera
            x = a2;
            y = b2;
            z = c;
            x2 = a;
            y2 = c;
            z2 = c2;
            point[0] = x;
            point[1] = y;
            point[2] = z;
            point[3] = x2;
            point[4] = y2;
            point[5] = z2;
        }else if(a2>a && c2 < c) {//x,y,z2 es abajo izquierda
            x = a;
            y = b;
            z = c2;
            x2 = a2;
            y2 = b2;
            z2 = c;
            point[0] = x;
            point[1] = y;
            point[2] = z;
            point[3] = x2;
            point[4] = y2;
            point[5] = z2;
        }
        return point;
    }
    public static double distaciaEntreDosPuntos(int x, int z, int x2, int z2) {
        return Math.sqrt(Math.pow((x2-x), 2)+(Math.pow((z2-z), 2)));
    }
}
