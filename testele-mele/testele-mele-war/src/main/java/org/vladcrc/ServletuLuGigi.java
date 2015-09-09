package org.vladcrc;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletuLuGigi extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        Enumeration<String> attributeNames = req.getAttributeNames();
        System.out.println("attributeNames" + attributeNames);
        
        Enumeration<String> parameterNames = req.getParameterNames();
        System.out.println("parameterNames" + parameterNames);
        
        StringBuffer buf = new StringBuffer();
        buf.append("<html>");
        buf.append("<head><title>Titlu lu Peshte</title></head>");
        buf.append("<body><h1>Cefa Mahareeeeeeee!!!</h1></body>");
        buf.append("</html>");

        resp.getOutputStream().println(buf.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        Enumeration<String> attributeNames = req.getAttributeNames();
        System.out.println("attributeNames" + attributeNames);
        
        Enumeration<String> parameterNames = req.getParameterNames();
        System.out.println("parameterNames" + parameterNames);
        
        
//		String method = req.getMethod();
//		if (method.equals("POST")) {
//			BufferedReader reader = req.getReader();
//			Gson gson = new Gson();
//
//			MyView myViewObject = gson.fromJson(reader, MyView.class);
//			System.out.println("myViewObject=" + myViewObject);
//		}

		//////////////////////////////////////////////////
        
        StringBuffer buf = new StringBuffer();
        buf.append("<html>");
        buf.append("<head><title>Titlu lu Peshte</title></head>");
        buf.append("<body><h1>Cefa Mahareeeeeeee!!!</h1></body>");
        buf.append("</html>");

        resp.getOutputStream().println(buf.toString());
    }

}
