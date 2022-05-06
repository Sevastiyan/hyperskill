package encryptdecrypt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private static String dInput;
    private static String alg;
    private static String text;
    private static String operator;
    private static int key;
    private static boolean data;
    private static boolean in;
    private static String outputFile;

    public static void main(String[] args) {

        //boolean data = false;
        //boolean in = false;

        //Mode mode = new Mode();
        //mode.setMode(new Shift());

        // Loop through the command line arguments and find the right ones.
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-mode": {
                    operator = args[i + 1];
                    break;
                }
                case "-data": {
                    dInput = args[i + 1];
                    data = true;
                    break;
                }
                case "-key": {
                    key = Integer.parseInt(args[i + 1]);
                    break;
                }
                case "-in":{
                    String pathToFile = args[i + 1];
                    try {
                        text = readFileAsString(pathToFile);
                    } catch (IOException e) {
                        System.out.println("Cannot read file: " + e.getMessage());
                    }
                    in = true;
                    break;
                }
                case "-out": {
                    outputFile = args[i + 1];
                    break;
                }
                case "-alg": {
                    //System.out.println("alg");
                    alg = args[i+1];
                    //System.out.println(alg);
                    break;
                }
                default:
            }
        }
        
        // Ask the factory to initiate the right Algorithm
        Algorithm algorithm = new AlgFactory().create(alg);


        // Initiate the program in the right order.
        switch (operator) {
            case "enc":
                if (in && data) {
                    System.out.println(algorithm.encode(dInput, key));
                }
                else if (in) {
                    textEncryption(outputFile, algorithm.encode(text, key));
                }
                break;
            case "dec":
                if (in && data) {
                    System.out.println(algorithm.decode(dInput, key));
                }
                else if (in) {
                    textDecryption(outputFile, algorithm.decode(text, key));
                }
                break;
            default:
                System.out.println(algorithm.encode(dInput, key));
        }
    }
    
    // Read and write files.
    // TODO. can be separated from the main class.
    private static void textDecryption(String outputFile, String decryption) {
        File file = new File(outputFile);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(decryption);
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
    }

    private static void textEncryption(String outputFile, String encryption) {
        File file = new File(outputFile);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(encryption);
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
    }


    private static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

}

// Factory Implementation
class AlgFactory {
    static Algorithm create(String mode) {
        switch (mode) {
            case "shift":
                return new Shift();
            case "unicode":
                return new Unicode();
            default:
                return null;
        }
    }
}

// Strategy implementation
/*class Mode {
    private Algorithm mode;

    void setMode(Algorithm mode) {
        this.mode = mode;
    }

    String encode(String input, int key) {
        return this.mode.encode(input, key);
    }

    String decode(String input, int key) {
        return this.mode.encode(input, key);
    }
}*/

interface Algorithm {

    String encode(String input, int key);
    String decode(String input, int key);
}

class Shift implements Algorithm {

    @Override
    public String encode(String input, int key) {
        StringBuilder output = new StringBuilder();
        int len = input.length();
        char c;
        for (int i = 0; i < len; i++) {
            c = input.charAt(i);
            if (Character.isLetter(c)) {
                c = (char) (input.charAt(i) + key);
                if ((Character.isLowerCase(input.charAt(i)) && c > 'z') || (Character.isUpperCase(input.charAt(i)) && c > 'Z')) {
                    c = (char) (input.charAt(i) - (26 - key));
                }
            }
            output.append(c);
        }
        return output.toString();
    }

    @Override
    public String decode(String input, int key) {
        StringBuilder output = new StringBuilder();
        int len = input.length();
        char c;
        for (int i = 0; i < len; i++) {
            c = input.charAt(i);
            if (Character.isLetter(c)) {
                c = (char) (input.charAt(i) - key);
                if ((Character.isLowerCase(input.charAt(i)) && c < 'a') || (Character.isUpperCase(input.charAt(i)) && c < 'A')) {
                    c = (char) (input.charAt(i) + (26 - key));
                }
            }
            output.append(c);
        }
        return output.toString();
    }
}

class Unicode implements Algorithm {

    @Override
    public String encode(String input, int key) {
        StringBuilder output = new StringBuilder();
        int len = input.length();
        for (int i = 0; i < len; ++i) {
            char ch = input.charAt(i);
            ch = (char)(ch + key);
            output.append(ch);
        }
        return output.toString();
    }

    @Override
    public String decode(String input, int key) {
        StringBuilder output = new StringBuilder();
        int len = input.length();
        for (int i = 0; i < len; ++i) {
            char ch = input.charAt(i);
            ch = (char)(ch - key);
            output.append(ch);
        }
        return output.toString();
    }
}
