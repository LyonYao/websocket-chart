package com.sample;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(value="members")
public class Room {
	private final String roomId;
	private Set<Member> members=new HashSet<>();
	private String roomName;
	
	public Room(String roomId) {
		super();
		this.roomId = roomId;
	}
	public String getRoomId() {
		return roomId;
	}
	public void addMember(Member member){
		this.members.add(member);
	}
	public void removeMember(Member member){
		this.members.remove(member);
	}
	public Set<Member>  getMembers() {
		return members;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	
}
