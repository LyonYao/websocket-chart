package com.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomManager {
	private final Map<String,Room> rooms=new HashMap<String, Room>();
	private final Map<String,Member> userMember=new HashMap<String, Member>();
	private final static RoomManager INSTANCE=new RoomManager();
	
	private RoomManager() {
		super();
	}
	public static RoomManager getManager(){
		return INSTANCE;
	}
	public void addRoom(String roomId,Room room){
		if(room==null){
			return;
		}
		rooms.put(roomId, room);
	}
	public void removeRoom(String roomId) {
		rooms.remove(roomId);
	}
	public void inter(String user,Member member){
		System.out.println(user+"=>enter");
		userMember.put(user, member);
	}
	public void interRoom(String user,String roomId){
		System.out.println(user+"=>enter room=>"+roomId);
		Member member = userMember.get(user);
		Room room = rooms.get(roomId);
		if(member!=null&&room!=null){
			member.setRoom(room);
			room.addMember(member);
		}
		userMember.put(user, member);
	}
	public void leaveRoom(String user){
		System.out.println(user+"=>leave room.");
		Member remove = userMember.get(user);
		if(remove!=null&&remove.getRoom()!=null){
			remove.getRoom().removeMember(remove);
		}
	}
	public void out(String user){
		userMember.remove(user);
	}
	public Member getMember(String user){
		return userMember.get(user);
	}
	public List<Member> allMemebers() {
		return new ArrayList<Member>(userMember.values());
	}
	public List<Room> allRooms() {
		return new ArrayList<Room>(rooms.values());
		
	}
}
