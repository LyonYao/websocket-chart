package com.sample;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ServerEndpoint(value = "/websocket/{user}")
public class MyServerEndpoint {

	private Session session;
	private String user;

	@OnOpen
	public void open(Session session, @PathParam(value = "user") String user) {
		this.session = session;
		this.user = user;
		RoomManager.getManager().inter(user, new Member(this, user));
		System.out.println("*** WebSocket opened from sessionId "
				+ session.getId());
	}

	@OnMessage
	public void inMessage(String message) {
		try {
			Message readValue = new ObjectMapper().readValue(message,
					Message.class);
			switch (readValue.getAction()) {
			case "message":
				messageBroad(readValue);
				break;
			case "inter-room":
				interRoom(readValue);
				break;
			case "add-room":
				addRoom(readValue);
				break;
			case "refresh-room":
				refreshRoom();
			default:
				break;
			}

		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	private void refreshRoom() {
		Message m1 = new Message();
		m1.setAction("refresh-room");
		RoomManager manager = RoomManager.getManager();
		try {
			m1.setData(new ObjectMapper().writeValueAsString(manager.allRooms()));
			getSession().getBasicRemote()
			.sendText(new ObjectMapper().writeValueAsString(m1));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
	}
	SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
	private void addRoom(Message readValue) {
		Room room = new Room(UUID.randomUUID().toString());
		room.setRoomName(readValue.getData());
		RoomManager manager = RoomManager.getManager();
		manager.addRoom(room.getRoomId(), room);
		Message m1 = new Message();
		m1.setAction("refresh-room");
		try {
			m1.setData(new ObjectMapper().writeValueAsString(manager.allRooms()));
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		manager.allRooms();
		for (Member m : manager.allMemebers()) {
			try {
				m.getServerEndpoint().getSession().getBasicRemote()
						.sendText(new ObjectMapper().writeValueAsString(m1));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void interRoom(Message readValue) {
		RoomManager.getManager().interRoom(getUser(), readValue.getData());
		Message m1 = new Message();
		m1.setAction("inter-room");
		m1.setData(readValue.getData());
		Member member = RoomManager.getManager().getMember(getUser());
		try {
			member.getServerEndpoint()
			.getSession()
			.getBasicRemote()
			.sendText(new ObjectMapper().writeValueAsString(m1));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void messageBroad(Message readValue) {
		Member member = RoomManager.getManager().getMember(getUser());
		Message m1 = new Message();
		m1.setAction("message");
		m1.setData("<p class='well well-sm'>"+"<span class='atTime'>"+format.format(new Date())+":</span><br/><span class='message'>"+readValue.getData()+"</span></p>");
		if (member.getRoom() != null) {
			for (Member m : member.getRoom().getMembers()) {
				try {
					m.getServerEndpoint()
							.getSession()
							.getBasicRemote()
							.sendText(new ObjectMapper().writeValueAsString(m1));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@OnClose
	public void end() {
		RoomManager.getManager().leaveRoom(getUser());
		RoomManager.getManager().out(getUser());
	}

	@OnError
	public void error(Session session, Throwable t) {
		t.printStackTrace();
		RoomManager.getManager().leaveRoom(getUser());
		RoomManager.getManager().out(getUser());
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}