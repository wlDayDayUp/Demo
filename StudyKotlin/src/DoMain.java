import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DoMain {

    public static void main(String[] args) {

        List<String> ls = new ArrayList<>();
        ls.add("00000");
        System.out.println(ls);
        ls.add(0, "1111111");
        System.out.println(ls);
        Collections.reverse(ls);
        System.out.println(ls);

    }
}
