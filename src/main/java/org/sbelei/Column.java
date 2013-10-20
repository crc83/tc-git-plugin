package org.sbelei;

import plugins.wdx.FieldValue;

public enum Column {
	
	BRANCH_NAME(0,"branch name", FieldValue.FT_STRING),
	ORIGIN_URL(1,"origin url", FieldValue.FT_STRING),
	UPSTREAM_URL(2,"upstream url", FieldValue.FT_STRING),
	COMMIT_SHA_SHORT(3,"sha", FieldValue.FT_STRING),
	COMMIT_MESSAGE(4,"message", FieldValue.FT_STRING),
	COMMIT_AUTHOR(5,"author", FieldValue.FT_STRING);
	
	private final int id;
	private final String fieldName;
	private final int fieldType;
	
	
	public String title() {
		return fieldName;
	}

	public int type() {
		return fieldType;
	}

	Column(int fieldNubmer, String fieldName, int fieldType){
		this.id = fieldNubmer;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
	}

	public int id() {
		return id;
	}

}
