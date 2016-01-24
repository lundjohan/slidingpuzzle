package com.slidingPuzzleGoogle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.*;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class SlidingPuzzleGoogleServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		/*
		 * GET JSON-STRING FROM CLIENT
		 */
		System.out.println("I doPost 1");
		List<Integer> nrs = new ArrayList<Integer>();
		BufferedReader reader = req.getReader();
		String jsonString = reader.readLine();
		reader.close();
		try {
			JSONObject jsonObj = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObj.getJSONArray("nrsArray");
			for (int i = 0; i < jsonArray.length(); i++)
				nrs.add(jsonArray.getInt(i));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		/*
		 * USE OTHER JAVA CLASSES
		 */
		System.out.println("I doPost 2");
		Facade facade = new Facade();
		List<Character> toMoveList = facade.solve(nrs);	//returen �r (List<String>)nrs.clone() se s. 111
		
	
		/*
		 * SEND JSON-STRING TO CLIENT
		 */
		System.out.println("I doPost 3");
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		PrintWriter out = resp.getWriter();
		JSONObject jObj = new JSONObject();
		
	
		try {
			jObj.put("toMoveArray",toMoveList);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//JSONArray jArr = jObj.getJSONArray("toMoveArray");
		
		out.print(jObj.toString());
		System.out.println("I doPost 4");
	}
}
