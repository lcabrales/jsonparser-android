import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides the simple parsed output in plain text from a JSON object, such as the class variables
 * with appropriate names (camelCase) and a fromJson() method to parse the received JSON object
 * into an instance of the class.
 * 
 * @author Lucas De Morais Cabrales
 * @since 2018-01-18
 */
public class JSONParser {
	
	/**
	 * Name of the Java class.
	 */
	private String className;
	/**
	 * Java class variables.
	 */
	private ArrayList<Variable> variables;
	
	/**
	 * Simple constructor, receives the Java class name and initializes the variables array.
	 * 
	 * @param className Java class name.
	 */
	public JSONParser(String className) {
		this.className = className;
		variables = new ArrayList<>();
	}
	
	/**
	 * Retrieves the Java class variables in plain text.
	 * 
	 * @return Class variables in plain text.
	 */
	public String getVariablesDefinition() {
		String vDefinition = "";
		
		for (Variable v : variables) {
			vDefinition += "public " + v.getType().getName() 
					+ " " + v.getName() + ";";
		}
		
		return vDefinition;
	}
	
	/**
	 * Retrieves the fromJson() method definition in plain text.
	 * 
	 * @param parsedFields Each line of parsed JSON field into the respective Java class
	 * variable in plain text.
	 * @return fromJson() method definition in plain text.
	 */
	public String getMethodDefinition(String parsedFields) {
		return "public static " + className + " fromJson(JSONObject jObj) { " 
				+ className + " obj = new " + className + "(); try { " + parsedFields + 
				"} catch (JSONException e) { Debug.error(TAG, e); } return obj;} ";
	}
	
	/**
	 * Generates the line of code to parse each field of the JSON object into its associated
	 * class variable.
	 * 
	 * @param line JSON object field line. E.g.: "key" : "value",
	 * @param idFieldName JSON field name for the id value
	 * @return Line of code containing the parsed JSON field into the associated class variable.
	 */
	public String parseLine(String line, String idFieldName) {
		String result = "";
		
		String fieldName = getFieldName(line); //JSON field name
		String variableName = fieldName.equals(idFieldName) ? "id" : getVariableName(fieldName); //class variable converted to camelCase.
		ValueType valueType = getFieldValueType(line); //implicitly detects the variable type.
		String valueTypeParsed = getFieldValueTypeParsed(valueType); //retrieves the code to parse the variable type.
		
		variables.add(new Variable(valueType, variableName));

		result = "obj." + variableName;
		
		if (valueType == ValueType.BOOLEAN) {
			//parsing line of code is simplified
			result += " = !jObj.isNull(" + getParsedFieldName(fieldName) +") && ";
		} else {
			//standard code
			result += " = jObj.isNull(" + getParsedFieldName(fieldName) +") ? ";
		}
		
		result += valueTypeParsed + getParsedFieldName(fieldName) + ");";
		
		return result;
	}
	
	/**
	 * Retrieves the JSON field name from its field line.
	 * 
	 * @param line JSON field line.
	 * @return JSON field name.
	 */
	private String getFieldName(String line) {
		Pattern p = Pattern.compile("\"([^\"]*)\""); //Gets String between quotes characters
		Matcher m = p.matcher(line);
		
		while (m.find()) {
		  return m.group(1).trim();
		}
		
		return "";
	}
	
	/**
	 * Converts the JSON field name (normally in PascalCase) into camelCase.
	 * 
	 * @param fieldName JSON field name
	 * @return Variable name in camelCase format.
	 */
	public static String getVariableName(String fieldName) {
		return fieldName.substring(0, 1).toLowerCase() 
				+ fieldName.substring(1, fieldName.length());
	}
	
	/**
	 * Surrounds the JSON field name with double quotes in order to generate the parsing line of code.
	 * 
	 * @param fieldName JSON field name.
	 * @return JSON field name surrounded with double quotes.
	 */
	private String getParsedFieldName(String fieldName) {
		return "\"" + fieldName + "\"";
	}
	
	/**
	 * <p>
	 * Implicitly detects the JSON field variable type, takes certain parameters into account:
	 * </p>
	 * <ul>
	 * <li>Contains double quotes or is {@value null} = String</li>
	 * <li>Contains {@value true} or {@value false} = Boolean</li>
	 * <li>It is not any of the previous and it contains a floating point = Double<lip>
	 * <li>It is not any of the above
	 * </ul>
	 * 
	 * @param line
	 * @return The type of the variable in a ValueType value.
	 */
	private ValueType getFieldValueType(String line) {
		String value = line.substring(line.indexOf(':'), line.length()).trim();
		
		if (value.contains("\"") || value.contains("null")){
			return ValueType.STRING;
		} else if (value.contains("true") || value.contains("false")) {
			return ValueType.BOOLEAN;
		} else if (value.contains(".")){
			return ValueType.DOUBLE;
		} else {
			return ValueType.INTEGER;
		}
	}
	
	/**
	 * Retrieves the appropriate method for parsing each ValueType in plain text.
	 * 
	 * @param type value of ValueType, type of the variable.
	 * @return appropriate parsing substring in plain text
	 */
	private String getFieldValueTypeParsed(ValueType type) {

		switch(type) {
			case STRING:
				return "\"\" : jObj.getString(";
			case BOOLEAN:
				return "jObj.getBoolean(";
			case DOUBLE: 
				return "0D : jObj.getDouble(";
			case INTEGER:
			default:
				return "0 : jObj.getInt(";
		}
	}
	
	/**
	 * Retrieves all of the class variables
	 * 
	 * @return Class variables
	 */
	public ArrayList<Variable> getVariables(){
		return variables;
	}
}
