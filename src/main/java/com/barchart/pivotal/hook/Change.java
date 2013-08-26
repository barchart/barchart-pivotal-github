package com.barchart.pivotal.hook;


public class Change extends Any {

	public Integer id;
	public String name;
	public String kind;

	public String change_type;

	public Values new_values;
	public Values original_values;

}
