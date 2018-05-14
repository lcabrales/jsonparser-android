import java.util.Scanner;

/**
 * <p>
 * Parses a complete formatted JSON object (starting and ending with brackets)
 * Generates a java object model class with the fromJson() and database definitions (optional).
 * </p>
 * <p>
 * This class defines the main functionality of the Console Application.
 * </p>
 * <p>
 * Program input:
 * <ul> 
 * <li>Class name</li>
 * <li>Whether to generate database definitions</li>
 * <li>Properly formatted unnamed JSON object (starting and ending with brackets, one field per line).</li>
 * </ul>
 * </p>
 * <p>
 * Program output:
 * <ul>
 * <li>Full Java class definition in plain text, including the class variables and 
 * a method to parse the JSON object.</li>
 * <li>May or may not include database definitions, depends on input.</li>
 * </ul>
 * </p>
 * 
 * @author Lucas De Morais Cabrales (Hypernova Labs - Panama)
 * @category AndroidTools
 * @version 2.0.0
 * @since 2018-01-18
 */
public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Scanner in = new Scanner(System.in);
		
		System.out.print("Class Name: ");
		String className = in.nextLine(); //retrieves Java class name.
		
		System.out.print("Database? (Y/N): ");
		//whether the program outputs the database methods
		boolean isDatabase = in.nextLine().equalsIgnoreCase("Y"); 
		
		System.out.print("Master? (Y/N): ");
		//whether the program outputs the database methods
		boolean isMaster = in.nextLine().equalsIgnoreCase("Y");
		
		System.out.print("Specify filter field (optional): ");
		//whether the program outputs the database methods
		String filterFieldName = in.nextLine();
		
		System.out.print("Specify ID field (optional): ");
		//whether the program outputs the database methods
		String idFieldName = in.nextLine();
		
		JSONParser parser = new JSONParser(className);
		
		//JSON object HAS to be formatted in order for this to work.
		System.out.print("JSON: ");
		
		String parsedFields = "";
		String currentLine = in.nextLine().trim();
		
		//Parse all of the JSON lines.
		while (!currentLine.contains("}")){
			if (!currentLine.equals("{")) {
				parsedFields += parser.parseLine(currentLine, idFieldName);
			}
			
			currentLine = in.nextLine().trim();
		}
		
		String json = parser.getMethodDefinition(parsedFields); //fromJson() method
		String variables = parser.getVariablesDefinition(); //class variables
		
		ClassCreator creator;
		
		if (isDatabase) {
			//database definition is included
			
			DatabaseGenerator dbGenerator = new DatabaseGenerator(className, parser.getVariables());
			String tableHeaders = dbGenerator.getTableDefinition();
			String database = dbGenerator.getMethodsDefinition(isMaster, idFieldName, filterFieldName);
			
			creator = new ClassCreator(className, tableHeaders, variables, json, database);
		} else
			creator = new ClassCreator(className, variables, json);
		
		String completeClass = creator.getClassDefinition(); //full Java class definition
		
		System.out.println();
		System.out.println("======== COPY BELOW =======");
		System.out.println();
		System.out.println(completeClass);
		System.out.println();
		System.out.println("======== COPY ABOVE =======");
		
		in.close();
	}

}
