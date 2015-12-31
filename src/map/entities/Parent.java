package map.entities;

import com.baidu.mapapi.model.LatLng;

public class Parent extends EntityBase{
	
	private int code;
	private int parentCode;
	private int level;
	private String name;
	private float latitude;
	private  float longitude;
	public Parent(int code, int parentCode, int level, String name,
			 float latitude,  float longitude) {
		super();
		this.code = code;
		this.parentCode = parentCode;
		this.level = level;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public Parent(){}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public int getParentCode() {
		return parentCode;
	}
	public void setParentCode(int parentCode) {
		this.parentCode = parentCode;
	}
	public int  getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public  float getLatitude() {
		return latitude;
	}
	public void setLatitude( float latitude) {
		this.latitude = latitude;
	}
	public  float getLongitude() {
		return longitude;
	}
	public void setLongitude( float longitude) {
		this.longitude = longitude;
	}
	@Override
	public String toString() {
		return "Parent [code=" + code + ", parentCode=" + parentCode
				+ ", level=" + level + ", name=" + name + ", latitude="
				+ latitude + ", longitude=" + longitude + "]";
	}
	
	
	
	
	

}
