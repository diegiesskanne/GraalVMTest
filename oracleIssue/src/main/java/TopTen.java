
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TopTen {
    private static final Pattern pattern = Pattern.compile("[^a-zA-Z]");

    // /usr/bin/time ...
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        if(args[0].equals("foo") || args[0].equals("bar")){
            Reflect ref = new Reflect();
            System.out.println(Arrays.toString(args));
            ref.doStuff(args);
        }else {
            Arrays.stream(args)
                    .flatMap(TopTen::fileLines)
                    .flatMap(line -> Arrays.stream(line.split("\\b")))
                    .map(TopTen::replace)
                    .filter(word -> word.length() > 0)
                    .map(String::toLowerCase)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .sorted((a, b) -> -a.getValue().compareTo(b.getValue()))
                    .limit(10)
                    .forEach(e -> System.out.format("%s = %d%n", e.getKey(), e.getValue()));
        }

    }

    private static String replace(String word) {
        // word.replaceAll("[^a-zA-Z]", "");
        Matcher matcher = pattern.matcher(word);
        return matcher.replaceAll("");
    }

    private static Stream<String> fileLines(String path) {
        try {
            return Files.lines(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class MoreReflect{

        int x;
        int y = 4;
        static int z;

        public MoreReflect(int x, int y){
            x = 3;
            this.y = y;
        }

        public static void printsomestuff(){
            z = 5;
            System.out.println("Here we go again " + z);

        }

    }



    public static class Reflect {

        static int[] testarray = new int[2];
        long l = 245;

        public static void foo(Integer b) {
            b += 3;
            System.out.println("foo is now running " + b);
        }

        public static void bar(Integer c) {
            //int c = 2;
            testarray[1] = 8;
            c += 2;
            System.out.println("bar is now runnin " + c);
        }


        public void doStuff(String[] bargs) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

            //try {
                if(bargs.length == 0) {
                    System.out.println("bin drin");
                    bargs = new String[1];
                    bargs[0] = "foo";
                }
            //} catch (NullPointerException e){
              //      e.printStackTrace();
                //}

            System.out.println("Das ist es " + Arrays.toString(bargs));

            for (String arg : bargs) {
                try {
                    // Reflection
                    Reflect.class.getMethod(arg, Integer.class).invoke(null, 4);
                } catch (ReflectiveOperationException ex) {
                    ex.printStackTrace();
                    System.out.println("Exception running " + arg + ": " + ex.getClass().getSimpleName());
                }
            }
            testarray[0] = 6;
            System.out.println(Arrays.toString(testarray));

        }
    }
}
