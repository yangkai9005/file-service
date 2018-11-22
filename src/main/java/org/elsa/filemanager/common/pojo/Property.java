package org.elsa.filemanager.common.pojo;

/**
 * 通用修改数据库的属性
 */
public class Property {

	private String name;

	private Object value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Property{" +
				"name='" + name + '\'' +
				", value=" + value +
				'}';
	}
}
