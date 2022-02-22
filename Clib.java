import sun.misc.Unsafe;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Clib {
    public static void disableWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);

            Class cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Exception e) {
            // ignore
        }
    }
    static Integer Cint(){
        return new Integer(0);
    }
    static Double Cdouble(){
        return new Double(0);
    }
    static String Cstring(){
        return new String("");
    }
    static Float Cfloat(){
        return new Float(0.0f);
    }
    static Character Cchar(){
        return new Character(' ');
    }
    static Long Clong(){
        return new Long(0);
    }

    private static void changeValueByReference(Object src, Object newValue) {
        try {
            if (!(src instanceof String || src instanceof Boolean || src instanceof Character
                    || src instanceof Integer || src instanceof Long
                    || src instanceof Float || src instanceof Double
                    || src instanceof Byte || src instanceof Short)) {
                ArrayList<Field> fields = new ArrayList<>(
                        src.getClass().getFields().length + src.getClass().getDeclaredFields().length + 1);
                fields.addAll(Arrays.asList(src.getClass().getFields()));
                fields.addAll(Arrays.asList(src.getClass().getDeclaredFields()));
                Method method = Object.class.getDeclaredMethod("clone");
                method.setAccessible(true);
                for (Field f : fields) {
                    if ((f.getModifiers() | Modifier.STATIC) == f.getModifiers())
                        continue;
                    f.setAccessible(true);
                    try {
                        f.set(src, method.invoke(f.get(newValue)));
                    } catch (Exception ignore) {
                        f.set(src, f.get(newValue));
                    }
                }
                return;
            }
            Field field = src.getClass().getDeclaredField("value"), accessible;
            int modifiers = 0;
            try {
                accessible = field.getClass().getDeclaredField("modifiers");
                modifiers = accessible.getInt(field);
                accessible.setInt(field, Modifier.PUBLIC);
            } catch (Exception ignore) {
                accessible = null;
            }
            field.setAccessible(true);
            if (src.getClass().isAssignableFrom(newValue.getClass())) {
                field.set(src, field.get(newValue));
            } else {
                if (src instanceof String)
                    field.set(src, field.get(newValue = newValue.toString()));
                else if (src instanceof Character)
                    field.setChar(src, (char) ((Number) newValue).intValue());
                else if (src instanceof Boolean)
                    field.setBoolean(src, ((Number) newValue).intValue() != 0);
                else if (src instanceof Integer)
                    field.setInt(src, ((Number) newValue).intValue());
                else if (src instanceof Float)
                    field.setFloat(src, ((Number) newValue).floatValue());
                else if (src instanceof Double)
                    field.setDouble(src, ((Number) newValue).doubleValue());
                else if (src instanceof Long)
                    field.setLong(src, ((Number) newValue).longValue());
                else if (src instanceof Short)
                    field.setShort(src, ((Number) newValue).shortValue());
                else
                    field.setByte(src, ((Number) newValue).byteValue());
            }
            if (accessible != null)
                accessible.setInt(field, modifiers);
            if (src instanceof String) {
                // Clear cache of hashCode
                field = src.getClass().getDeclaredField("hash");
                field.setAccessible(true);
                field.set(src, 0);
                // Update string coder
                field = src.getClass().getDeclaredField("coder");
                field.setAccessible(true);
                field.set(src, field.get(newValue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static int countChar(String str, char c)
    {
        int count = 0;
        for(int i=0; i < str.length(); i++)
        {    if(str.charAt(i) == c)
            count++;
        }
        return count;
    }
    /**
     * C-Style Scanf using reflection \
     *
     * types : \
     *
     *
     * Ineger : %d \
     * Double : %D \
     * String : %S \
     * Character : %c \
     * Long : %u \
     * Float : %f \
     *
     * @throws Exception if the pattern doesn't match the inputline
     */
    public static void scanf(String format , Object... arr) throws Exception {
        Scanner sc2 = new Scanner(System.in);
        String input = sc2.nextLine();
        String[] inputs = new String[countChar(format , '%')];
        Character[] types = new Character[countChar(format , '%')];
        int ind = 0;
        int formatind = 0;
        int inputind = 0;
        int typesind = 0;
        while(true){
            if(ind>=input.length()||formatind>=format.length())
                break;
            if(input.charAt(ind)==format.charAt(formatind)) {
                formatind++;
            }
            else if(format.charAt(formatind)=='%'){
                types[typesind] = format.charAt(formatind+1);
                typesind++;
                formatind+=2;
                StringBuilder inp = new StringBuilder();
                while(input.charAt(ind)!=format.charAt(formatind)){
                    inp.append(input.charAt(ind));
                    ind++;
                }
                inputs[inputind] = inp.toString();
                inputind++;
                ind--;
            }
            ind++;
        }
        for(int i = 0; i<arr.length;i++){
            Object src = arr[i];
            if (!(src instanceof String || src instanceof Boolean || src instanceof Character
                    || src instanceof Integer || src instanceof Long
                    || src instanceof Float || src instanceof Double
                    || src instanceof Byte || src instanceof Short)) {
                throw new Exception("Type mismatch");
            }
            switch (types[i]){
                case 'd':
                    changeValueByReference(arr[i], (Integer.parseInt(inputs[i])));
                    break;
                case 'f':
                    changeValueByReference(arr[i], (Float.parseFloat(inputs[i])));
                    break;
                case 'D':
                    changeValueByReference(arr[i], (Double.parseDouble(inputs[i])));
                    break;
                case 's':
                    changeValueByReference(arr[i], inputs[i]);
                    break;

                case 'c':
                    changeValueByReference(arr[i], inputs[i].charAt(0));
                    break;

                case 'u':
                    changeValueByReference(arr[i], (Long.parseLong(inputs[i])));
                    break;
                default:
                    throw new Exception("type not found : %"+types[i]);

            }
        }
    }
}
