/**
 * POJO for the generated class variables.
 * 
 * @author Lucas De Morais Cabrales
 * @since 2018-01-18
 */
public class Variable {
	/**
	 * Type of the variable.
	 */
	private ValueType type;
	/**
	 * Java variable name (camelCase).
	 */
	private String name;
	/**
	 * Java database name (snake_case).
	 */
	private String dbName;
	
	/**
	 * Constructor of the Variable class.
	 * 
	 * @param type value of ValueType.
	 * @param name Java variable name (camelCase).
	 */
	public Variable(ValueType type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public ValueType getType() {
		return type;
	}
	
	public void setType(ValueType type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDBName() {
		return dbName;
	}
	
	public void setDBName(String dbName) {
		this.dbName = dbName;
	}
}
