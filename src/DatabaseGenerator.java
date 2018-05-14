import java.util.ArrayList;

/**
 * Provides the needed database definitions in plain text from the provided class variables.
 * 
 * @author Lucas De Morais Cabrales
 * @since 2018-01-18
 */
public class DatabaseGenerator {
	
	/**
	 * Java class name.
	 */
	private String className;
	/**
	 * List of class variables.
	 */
	private ArrayList<Variable> variables;
	
	/**
	 * Constructor of the class.
	 * 
	 * @param className Java class name.
	 * @param variables List of class variables.
	 */
	public DatabaseGenerator(String className, ArrayList<Variable> variables) {
		this.className = className;
		this.variables = variables;
	}
	
	/**
	 * Retrieves the database headers containing the required constants for the DB creation, 
	 * including the command to create the table in SQLite.
	 * 
	 * @return Database constants in plain text.
	 */
	public String getTableDefinition() {
		return "public static final String KEY = \"" + className + "Key\";"
				+ "public static final String TABLE_NAME = \"" + className.toLowerCase() + "\";"
				+ "public static final String TABLE = \"create table \" + TABLE_NAME + \n"
				+ getVariablesDefinition();
	}
	
	/**
	 * Retrieves the database variable definitions for the create table command.
	 * 
	 * @return Database variables in plain text.
	 */
	private String getVariablesDefinition() {
		Variable first = variables.get(0);
		first.setDBName(getVariableDBName(first.getName()));
		String vDefinition = "\"(" + first.getDBName()
			+ " " + getVariableDBType(first.getType()) + ",\" + \n";
		
		for (int i = 1; i < variables.size() - 1; i++) {
			Variable v = variables.get(i);
			
			v.setDBName(getVariableDBName(v.getName()));
			vDefinition +=  "\"" + v.getDBName() + " " + getVariableDBType(v.getType()) + ",\" +";
			vDefinition += "\n";
		}
		
		Variable last = variables.get(variables.size() - 1);
		last.setDBName(getVariableDBName(last.getName()));
		vDefinition += "\"" + last.getDBName()
		+ " " + getVariableDBType(last.getType()) + ")\";";
		
		return vDefinition;
	}
	
	/**
	 * Converts a Java class variable (camelCase) into a database variable (snake_case).
	 * Basically replaces every upper case character with an underscore character followed by the lower case version.
	 * 
	 * @param name Java variable name.
	 * @return Variable name in snake_case.
	 */
	private String getVariableDBName(String name) {
		if (name.equals(name.toLowerCase()))
			return name;
		else {
			char[] array = name.toCharArray();
			int countUpperCase = 0;
			//counts how many upper case characters are there.
			for (int i = 0; i < array.length; i++) {
				char ch = array[i];
				if (Character.isUpperCase(ch)) 
					countUpperCase++;
			}
			
			char[] newArray = new char[array.length + countUpperCase];
			int newIndex = 0;
			//replaces all of the upper case characters with their lower case version.
			for (int i = 0; i < array.length; i++) {
				char ch = array[i];
				
				if (Character.isUpperCase(ch)){
					newArray[newIndex] = '_';
					newIndex++;
				}
				
				newArray[newIndex] = Character.toLowerCase(ch);
				newIndex++;
			}
			
			return new String(newArray);
		}
	}
	
	/**
	 * Retrieves the SQLite variant of the Java variable type.
	 * 
	 * @param type value of ValueType.
	 * @return SQLite column type.
	 */
	private String getVariableDBType(ValueType type) {
		
		switch(type) {
			case STRING:
				return "text";
			case DOUBLE: 
				return "float";
			case BOOLEAN:
			case INTEGER:
			default:
				return "integer";
		}
	}
	
	/**
	 * Retrieves the compilation of every database method in plain text.
	 * 
	 * @param isMaster whether to include specific methods for a master/form entity
	 * @param JSON field name for the entity ID, if empty, takes the first field as the ID
	 * @return All of the database methods in plain text.
	 */
	public String getMethodsDefinition(boolean isMaster, String idFieldName, String filterFieldName) {
		String getObjByIdMethod = isMaster ? getGetObjByIdMethod(idFieldName) : "";
		String getNameListMethod = isMaster ? getGetNameListMethod() : "";
		
		return "\n//region Database\n" + getAddObjMethod() + " " + getGetObjMethod(filterFieldName) + " " + getObjByIdMethod
				+ getGetListMethod() + " " + getNameListMethod + getIsEmptyMethod() + " "
				+ getDeleteTableMethod() + "\n\t//endregion\n\n";
	}
	
	/**
	 * Retrieves the simple addObj() method in plain text.
	 * 
	 * @return addObj() in plain text.
	 */
	private String getAddObjMethod() {
		return "public static void addObj(" + className + " obj) { try { "
				+ "ContentValues values = new ContentValues();" + getVariablesAddObjDefinition()
				+ "StorageManager.getDb().insert(TABLE_NAME, \"\", values);" 
				+ "} catch (Exception e) { Debug.error(TAG, e); } }";
	}
	
	/**
	 * Retrieves the lines of code regarding the addition of the column values using ContentValues.
	 * 
	 * @return Lines of code in plain text.
	 */
	private String getVariablesAddObjDefinition() {
		String vDefinition = "";
		
		for (Variable v : variables) {
			vDefinition += "values.put(\"" + v.getDBName() + "\", obj." + v.getName() + ");";
		}
		
		return vDefinition;
	}
	
	/**
	 * Retrieves the simple getObj(String) in plain text with a whereClause if specified.
	 * Only works for String fields.
	 * 
	 * @param fieldName name of the field regarding the whereClause (optional).
	 * @return getObj(String) in plain text if a whereClause has been specified.
	 */
	private String getGetObjMethod(String fieldName) {
		String todoWhereClause = "\n\t//TODO add whereClause\n";
		String whereClause = "";
		String methodParam = "";
				
		if (fieldName.length() > 0) {
			Variable var = new Variable(ValueType.STRING, JSONParser.getVariableName(fieldName));
			var.setDBName(getVariableDBName(var.getName()));
			
			todoWhereClause = "";
			whereClause = "+\" where " + var.getDBName() + "='\" + " + var.getName() + "+ \"' COLLATE NOCASE\"";
			methodParam = var.getType().getName() + " " + var.getName();
		}
		
		return "public static " + className + " getObj(" + methodParam + ") { " + todoWhereClause
				+ className + " obj = new " + className + "(); try { "
				+ "String query = \"select * from \" + TABLE_NAME" + whereClause + ";"
				+ "Cursor c = StorageManager.getDb().rawQuery(query, null);"
				+ "c.moveToFirst();"
				+ "if (c.getCount() > 0) {" + getVariablesSelectDefinition() + " } c.close(); }"
				+ "catch (Exception e) { Debug.error(TAG, e); } return obj; }";
	}
	
	/**
	 * Retrieves the getObj() definition in plain text without a whereClause specified.
	 * 
	 * @return getObj() in plain text.
	 */
	private String getGetObjMethod() {
		return getGetObjMethod("");
	}
	
	/**
	 * Retrieves the getObjById(String) method definition in plain text.
	 * Only works for the id field
	 * 
	 * @param idFieldName JSON field name for the entity ID, if empty, takes the first field as the ID
	 * @return
	 */
	private String getGetObjByIdMethod(String idFieldName) {
		String todoWhereClause = "\n\t//TODO add whereClause\n";
		String whereClause = "";
		String methodParam = "";	
		
		if (idFieldName.length() > 0) {
			
			//every time that this is used, idFieldName is not empty, therefore, it will always be replaced by "id"
			Variable var = new Variable(ValueType.STRING, "id");
			var.setDBName("id"); 	
			
			todoWhereClause ="";
			whereClause = "+\" where " + var.getDBName() + "='\" + " + var.getName() + "+ \"' COLLATE NOCASE\"";
			methodParam = var.getType().getName() + " " + var.getName();
		}
		
		return "public static " + className + " getObjById(" + methodParam + ") { " + todoWhereClause
				+ className + " obj = new " + className + "(); try { "
				+ "String query = \"select * from \" + TABLE_NAME" + whereClause + ";"
				+ "Cursor c = StorageManager.getDb().rawQuery(query, null);"
				+ "c.moveToFirst();"
				+ "if (c.getCount() > 0) {" + getVariablesSelectDefinition() + " } c.close(); }"
				+ "catch (Exception e) { Debug.error(TAG, e); } return obj; }";
	}
	
	/**
	 * Retrieves the getNameList(ArrayList<?>) method in plain text with the specified class.
	 * 
	 * @return getNameList(ArrayList<?>) method in plain text.
	 */
	private String getGetNameListMethod() {
		return "public static ArrayList<String> getNameList(ArrayList<" + className +"> list) {" +
				"ArrayList<String> nameList = new ArrayList<>(); for (" + className + " obj : list) {" + 
				"nameList.add(obj.description);}return nameList;}";
	}
	
	/**
	 * Retrieves the lines of code regarding the query of the SQLite columns according to their column type.
	 * 
	 * @return Lines of code in plain text.
	 */
	private String getVariablesSelectDefinition() {
		String vDefinition = "";
		
		for (Variable v : variables) {
			vDefinition += "obj." + v.getName() + " = c." + getVariablesSelectType(v.getType()) 
			+ "(c.getColumnIndex(\"" + v.getDBName() + "\"))";
			
			if (v.getType().equals(ValueType.BOOLEAN))
				vDefinition += " > 0";
			
			vDefinition += ";";
		}
		
		return vDefinition;
	}
	
	/**
	 * Retrieves the appropriate Cursor method depending on the column type.
	 * 
	 * @param type column type.
	 * @return Cursor method in plain text.
	 */
	private String getVariablesSelectType(ValueType type) {
		
		switch(type) {
			case STRING:
				return "getString";
			case DOUBLE: 
				return "getDouble";
			case BOOLEAN:
			case INTEGER:
			default:
				return "getInt";
		}
	}
	
	/**
	 * Retrieves the getList() method definition in plain text.
	 * 
	 * @return getList() in plain text.
	 */
	private String getGetListMethod() {
		return "public static ArrayList<" + className + "> getList() {" 
				+ "ArrayList<" + className +"> list = new ArrayList<>(); try { "
				+ "String query = \"select * from \" + TABLE_NAME;"
				+ "Cursor c = StorageManager.getDb().rawQuery(query, null);"
				+ "c.moveToFirst();"
				+ "while (!c.isAfterLast()) {" + className + " obj = new " + className + "();" 
				+ getVariablesSelectDefinition() + " list.add(obj); c.moveToNext(); }"
				+ "c.close(); }"
				+ "catch (Exception e) { Debug.error(TAG, e); } return list; }";
	}
	
	/**
	 * Retrieves a simple isEmpty() method definition is plain text.
	 * 
	 * @return isEmpty() in plain text.
	 */
	private String getIsEmptyMethod() {
		return " public static boolean isEmpty() { "
				+ "String query = \"select count(*) from \" + TABLE_NAME;"
				+ "int count; try { Cursor c = StorageManager.getDb().rawQuery(query, null);"
				+ "c.moveToFirst(); count = c.getInt(0); c.close();"
				+ "} catch (Exception e) { count = 0; Debug.error(TAG, e); } return count == 0; }";
	}
	
	/**
	 * Retrieves a simple deleteTable() method definition in plain text.
	 * 
	 * @return deleteTable() in plain text.
	 */
	private String getDeleteTableMethod() {
		return "public static void deleteTable() { try {"
				+ "StorageManager.getDb().delete(TABLE_NAME, null, null);"
				+ "} catch (Exception e) { Debug.error(TAG, e); } }";
	}
}
