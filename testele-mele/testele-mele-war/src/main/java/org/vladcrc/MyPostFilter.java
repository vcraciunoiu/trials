package org.vladcrc;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

public class MyPostFilter implements Filter {

	private FilterConfig filterConfig = null;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String method = httpRequest.getMethod();
		if (method.equals("POST")) {
			BufferedReader reader = request.getReader();
			Gson gson = new Gson();

			MyView myViewObject = gson.fromJson(reader, MyView.class);
			request.setAttribute("view", myViewObject);
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		this.filterConfig = null;
	}

}
