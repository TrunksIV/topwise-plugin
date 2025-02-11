package com.topwise.plugin.emv.database;

import com.topwise.cloudpos.struct.BaseStruct;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * The base class of all table classes,and the field with @Column will also be added to the table of sub classes.
 * author caixh
 * */
public class BaseModel extends BaseStruct {
	/**primary key,INT,auto increment*/
	@Id
	@Column(name = "id")
	private int id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void check() throws Exception{
		Table table = (Table) getClass().getAnnotation(Table.class);
		String tableName = table.name();
		for (Field field :getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}
			Column column = (Column) field.getAnnotation(Column.class);
			String style = column.style();
			if("".equals(style)||"-,+".equals(style)){
				return;
			}
			boolean hasmin = false,hasmax = false;
			double min=0.0,max=0.0;
			if(!Pattern.compile("(\\-)?(\\d+(\\.\\d+)?)?\\,((\\+)|((\\-)?\\d+(\\.\\d+)?)?)").matcher(style).matches()){
				throw new Exception("The style format of the "+field.getName()+" field in the "+tableName+" table does not conform to \"min,max\"");
			}
			String sa[] = style.split(",");
			if(!sa[0].equals("-")){
				hasmin = true;
				min = Double.parseDouble(sa[0]);
			}
			if(!sa[1].equals("+")){
				hasmax = true;
				max = Double.parseDouble(sa[1]);
			}
			if(hasmin&&hasmax&&min>max){
				throw new Exception("In the style format of "+field.getName()+" field in "+tableName+" table, min > max");
			}
			if (byte[].class == field.getType()) {
				byte[] obj = (byte[]) field.get(this);
				if(hasmin&&obj.length<(int)min){
					throw new Exception("The length of "+field.getName()+" field in "+tableName+" table cannot be less than "+(int)min);
				}
				if(hasmax&&obj.length>(int)max){
					throw new Exception("The length of "+field.getName()+" field in "+tableName+" table cannot be greater than "+(int)max);
				}
			}
			else if (String.class == field.getType()) {
				String obj = field.get(this).toString();
				if(hasmin&&obj.length()<(int)min){
					throw new Exception("The length of "+field.getName()+" field in "+tableName+" table cannot be less than "+(int)min);
				}
				if(hasmax&&obj.length()>(int)max){
					throw new Exception("The length of "+field.getName()+" field in "+tableName+" table cannot be greater than "+(int)max);
				}
			}
			else if (Float.TYPE == field.getType()||Double.TYPE == field.getType()) {
				double obj = Double.parseDouble(field.get(this).toString());
				if(hasmin&&obj<min){
					throw new Exception("The value of "+field.getName()+" field in "+tableName+" table cannot be less than "+(int)min);
				}
				if(hasmax&&obj>max){
					throw new Exception("The value of "+field.getName()+" field in "+tableName+" table cannot be greater than "+(int)max);
				}
			}
			else if (Byte.TYPE == field.getType()||Short.TYPE == field.getType()||Integer.TYPE == field.getType()||Long.TYPE == field.getType()) {
				long obj = Long.parseLong(field.get(this).toString());
				if(hasmin&&obj<(long)min){
					throw new Exception("The value of "+field.getName()+" field in "+tableName+" table cannot be less than "+(int)min);
				}
				if(hasmax&&obj>(long)max){
					throw new Exception("The value of "+field.getName()+" field in "+tableName+" table cannot be greater than "+(int)max);
				}
			}
		}
	}
	
}
