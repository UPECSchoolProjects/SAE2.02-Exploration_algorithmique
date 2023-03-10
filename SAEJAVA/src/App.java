import java.io.File;
import Conversion.ConversionFactory;
import Conversion.IConverter;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        IConverter cf = ConversionFactory.getConverter(new File("test.pdf"));

        cf.convert();
    }
}
