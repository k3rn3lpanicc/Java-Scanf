public class Main {

    public static void main(String[] args) throws Exception {
        Clib.disableWarning();
        Double a = Clib.Cdouble();
        Double b = Clib.Cdouble();
        Double c = Clib.Cdouble();
        Clib.scanf("(%Dx^2)+(%Dx)+(%D)=0" , a,b,c);
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);

    }
}
