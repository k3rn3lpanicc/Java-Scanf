# Java-Scanf
C-Style Scanf implementation using reflection in java

## Types :
 Most of them are C-Style
 %d : Integer , %f : Float , %D : Double , %u : Long , %s : String , %c : Character

## Usage : 
```Java
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
```
Input:
```
(1.3x^2)+(-2x)+(1)=0
```
Output:
```
1.3
-2.0
1.0
```
