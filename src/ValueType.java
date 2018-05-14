/**
 * Defines all of the supported variable types.
 * 
 * @author Lucas De Morais Cabrales
 * @since 2018-01-18
 */
public enum ValueType {
	STRING("String"),
	INTEGER("Integer"),
	BOOLEAN("Boolean"),
	DOUBLE("Double");
	
	String name;
	
	ValueType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
