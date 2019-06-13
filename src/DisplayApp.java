import java.util.Properties;

public class DisplayApp {

    private static final String JAVA_LIBRARY_PATH = "java.library.path";

	public static void main(String[] args) {

        Properties properties = System.getProperties();
        // Java 8
        properties.forEach((k, v) -> System.out.println(k + ":" + v));

        // Classic way to loop a map
        //for (Map.Entry<Object, Object> entry : properties.entrySet()) {
        //    System.out.println(entry.getKey() + " : " + entry.getValue());
        //}

        // No good, output is truncated, long lines end with ...
        //properties.list(System.out);
        
        System.out.println("--------------");
        String values = System.getProperty(JAVA_LIBRARY_PATH);
        System.out.println(values);
        
//        values += ";C:\\Program Files\\tibco\\ftl\\5.4\\bin";
//        System.setProperty(JAVA_LIBRARY_PATH, values);        
//        
//        System.out.println("--------------");
//        String newValues = System.getProperty(JAVA_LIBRARY_PATH);
//        System.out.println(newValues);
    }

}