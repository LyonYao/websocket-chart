package com.sample;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.mysql.jdbc.Driver;

@Controller
public class TestController {
	@RequestMapping(value="/")
	public  String  index(HttpServletRequest req){
		req.setAttribute("userId",UUID.randomUUID().toString());
		return "index";
	}
	@RequestMapping(value="test")
	public @ResponseBody String  test(MultipartFile file){
		System.out.println(file.getOriginalFilename());
		return file.getOriginalFilename();
	}
	@RequestMapping(value="/ss/{name}")
	public ModelAndView test(@PathVariable String name) throws ClassNotFoundException, SQLException{
		Class.forName(Driver.class.getName());
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/116114_micro?characterEncoding=UTF-8", "root","root");
		CallableStatement prepareCall = connection.prepareCall("SELECT id,nick_name FROM `rp_app_mpuser`");
		ResultSet executeQuery = prepareCall.executeQuery();
		List<String> updates=new ArrayList<String>();
		List<String> nickNames=new ArrayList<String>();
		while(executeQuery.next()){
			Long id=executeQuery.getLong(1);
			String match=executeQuery.getString(2);
			
			System.out.println(match);
			nickNames.add(match);
		}
		return new ModelAndView(name,"nicks",nickNames);
	}
	
}
