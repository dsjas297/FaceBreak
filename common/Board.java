package common;

import java.util.ArrayList;

public class Board {
	private int ownerID;
	private String ownerName;
	private Integer[] regions;
	
	public Board(int ownerID){
		this.ownerID = ownerID;		
	}
	public Board(String ownerName){
		this.ownerName = ownerName;		
	}
	public Board(int ownerID, Integer[] regions){
		this.ownerID = ownerID;
		this.regions = regions;
	}
	public Board(String ownerName, Integer[] regions){
		this.ownerName = ownerName;	
		this.regions = regions;
	}
	public void setOwnerID(int ownerID){
		this.ownerID = ownerID;		
	}
	public void setOwnerName(String ownerName){
		this.ownerName = ownerName;		
	}
	public void setRegions(Integer[] regions){
		this.regions = regions;
	}
	
	public int getOwnerID(){
		return this.ownerID;		
	}
	public String getOwnerName(){
		return this.ownerName;		
	}
	public Integer[] getRegions(){
		return this.regions;
	}
	
}
