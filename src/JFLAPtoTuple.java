import java.util.Collections;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.DocumentBuilder;  
import org.w3c.dom.Document;  
import org.w3c.dom.NodeList;  
import org.w3c.dom.Node;  
import org.w3c.dom.Element;  
import java.io.FileWriter;
import java.io.File;  
import java.io.IOException;

public class JFLAPtoTuple {
    public static boolean convert(String filepath) {
        boolean result = false;
        // Verify file
        if(isValidFile(filepath)) {
            // Create doc
            Document doc = createDoc(filepath);

            if(doc != null) {
                doc.getDocumentElement().normalize();
                NodeList nodeStates = doc.getElementsByTagName("state");
                NodeList transitions = doc.getElementsByTagName("transition"); 

                // adds one more state as the trap state
                int statesCount = nodeStates.getLength() + 1;
                ArrayList<Integer> finalStates = getFinalStates(nodeStates);
                ArrayList<Character> alphabet = getAlphabet(transitions);
                int[][] table = createTranTable(nodeStates, alphabet, transitions);
                // Write to new .txt file
                result = writeToFile(filepath, statesCount, finalStates, alphabet, table);
            }
        }
        
        return result;
    }

    private static boolean isValidFile(String filepath) {
        return filepath.substring(filepath.lastIndexOf(".")).equals(".jff");
    }

    private static Document createDoc(String filepath) {
        try {
            File file = new File(filepath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);

            return doc;
        } catch (Exception e) {
            return null;
        }
    }

    private static ArrayList<Integer> getFinalStates(NodeList states) {
        ArrayList<Integer> finalStates = new ArrayList<>();

        for(int i = 0; i < states.getLength(); i++) {
            Node state = states.item(i);
            Element element = (Element) state;
            NodeList finalElem = element.getElementsByTagName("final");

            if(finalElem.getLength() > 0) {
                finalStates.add(Integer.parseInt(element.getAttribute("id")));
            }
        }

        Collections.sort(finalStates);
        return finalStates;
    }

    private static ArrayList<Character> getAlphabet(NodeList transitions) {
        ArrayList<Character> alphabet = new ArrayList<>();

        for(int i = 0; i < transitions.getLength(); i++) {
            Node transition = transitions.item(i);
            Element element = (Element) transition;
            
            // gets the char at the transition's read tag
            String inputText = element.getElementsByTagName("read").item(0).getTextContent();
            if(inputText.startsWith("[") && inputText.endsWith("]")) {
                // add every value in the range to the alphabet
                inputText = inputText.substring(1, inputText.length() - 1);
                char value = inputText.charAt(0);
                char end = inputText.charAt(inputText.length() - 1);

                while(value <= end) {
                    if(!alphabet.contains(value)) alphabet.add(value);
                    value++;
                }
            } else {
                if(!alphabet.contains(inputText.charAt(0))) alphabet.add(inputText.charAt(0));
            }
        }

        Collections.sort(alphabet);
        return alphabet;
    }

    private static int[][] createTranTable(NodeList states, ArrayList<Character> alphabet, NodeList transitions) {
        // we add a new row for the trap state
        int rows = states.getLength() + 1;
        int columns = alphabet.size();
        int[][] table = new int[rows][columns];

        // initialize with -1
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                table[i][j] = -1;
            }
        }

        // fills the table with the transitions from the file
        for(int i = 0; i < transitions.getLength(); i++) {
            Node transition = transitions.item(i);
            Element element = (Element) transition;

            int row = Integer.parseInt(element.getElementsByTagName("from").item(0).getTextContent());
            int dest = Integer.parseInt(element.getElementsByTagName("to").item(0).getTextContent());
            String inputText = element.getElementsByTagName("read").item(0).getTextContent();

            if(inputText.startsWith("[") && inputText.endsWith("]")) {
                // make a range of values for the regex entry
                inputText = inputText.substring(1, inputText.length() - 1);
                char value = inputText.charAt(0);
                char end = inputText.charAt(inputText.length() - 1);

                while(value <= end) {
                    table[row][alphabet.indexOf(value++)] = dest;
                }
            } else {
                table[row][alphabet.indexOf(inputText.charAt(0))] = dest;
            }
        }

        // if we have elements with -1, then we're missing a trap state
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                if(table[i][j] == -1) {
                    table[i][j] = rows;
                }
            }
        }

        return table;
    }

    public static boolean writeToFile(String filepath, int states, ArrayList<Integer> finalStates, ArrayList<Character> alphabet, int[][] table) {
        String newFilepath = filepath.substring(0, filepath.lastIndexOf(".")).concat("_tuple.txt");
        String finalStatesStr = "";
        String alphabetStr = "";
        String tableStr = "";

        for(int i=0; i < finalStates.size()-1; i++) {
            String state = finalStates.get(i).toString();
            finalStatesStr += state + ",";
        }
        finalStatesStr += finalStates.get(finalStates.size()-1) + "\n";

        for(int i=0; i < alphabet.size()-1; i++) {
            String symbol = alphabet.get(i).toString();
            alphabetStr += symbol + ",";
        }
        alphabetStr += alphabet.get(alphabet.size()-1) + "\n";

        for(int i=0; i < table.length; i++) {
            for(int j=0; j < table[0].length-1; j++) {
                tableStr += table[i][j] + ",";
            }
            if(i != table.length-1) {
                tableStr += table[i][table[0].length-1] + "\n";
            } else {
                tableStr += table[i][table[0].length-1];
            }
        }

        try {
            FileWriter fw = new FileWriter(newFilepath);
            fw.write(alphabetStr);
            fw.write(states + "\n");
            fw.write(finalStatesStr);
            fw.write(tableStr);
            fw.close();
            return true;
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
    }
}
