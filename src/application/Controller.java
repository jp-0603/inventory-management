package application;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import stock.Shoe;
import stock.ShoeDAO;

@SuppressWarnings("serial")
public class Controller extends HttpServlet {
	
	private ShoeDAO dao;
	
	public void init( ) {
		final String url = getServletContext().getInitParameter("JDBC-URL");
		final String username = getServletContext().getInitParameter("JDBC-USERNAME");
		final String password = getServletContext().getInitParameter("JDBC-PASSWORD");
		
		dao = new ShoeDAO(url, username, password);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String action = request.getServletPath();
		
		try {
			switch (action) {
			case "/add": //intentionally fall through
			case "/edit": showEditForm(request, response); break;
			case "/insert": insertShoe(request, response); break;
			case "/update": updateShoe(request, response); break;
			case "/search": searchShoes(request, response); break;
			default: viewShoes(request, response); break;
			}
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}

	private void viewShoes(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
		final String action = request.getParameter("action") != null
				? request.getParameter("action")
				: "null";
		List<Shoe> shoes = dao.getShoes();
		request.setAttribute("shoes", shoes);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("stock.jsp");
		dispatcher.forward(request, response);
	}
	
	private void insertShoe(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		double price = Double.parseDouble(request.getParameter("price"));
		int quantity = Integer.parseInt(request.getParameter("quantity"));
		
		dao.insertShoe(name,  description,  price,  quantity);
		response.sendRedirect(request.getContextPath() + "/");
	}
	
	private void updateShoe(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
		final String action = request.getParameter("action") != null
			? request.getParameter("action")
			: request.getParameter("submit").toLowerCase();
		final int id = Integer.parseInt(request.getParameter("id"));
		
		Shoe shoe = dao.getShoe(id);
		switch (action) {
			case "purchase": shoe.buyMe(); break;
			case "save":
				String name = request.getParameter("name");
				String description = request.getParameter("description");
				double price = Double.parseDouble(request.getParameter("price"));
				int quantity = Integer.parseInt(request.getParameter("quantity"));
				
				shoe.setName(name);
				shoe.setDescription(description);
				shoe.setPrice(price);
				shoe.setQuantity(quantity);
				break;
			case "delete": deleteProduct(id, request, response); return;
		}
		dao.updateShoe(shoe);
		
		response.sendRedirect(request.getContextPath() + "/");
	}
	
	private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
		try {
			final int id = Integer.parseInt(request.getParameter("id"));
			
			Shoe shoe = dao.getShoe(id);
			request.setAttribute("shoe", shoe);
		} catch (NumberFormatException e) {
			
		} finally {
			RequestDispatcher dispatcher = request.getRequestDispatcher("shoeform.jsp");
			dispatcher.forward(request, response);
		}
	}
	
	private void deleteProduct(final int id, HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
		dao.deleteShoe(dao.getShoe(id));
		
		response.sendRedirect(request.getContextPath() + "/");
	}
	
	private void searchShoes(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
		final String type = request.getParameter("search_attribute") != null
				? request.getParameter("search_attribute")
				: null;
				
		final String entry = request.getParameter("search_bar") != null
				? request.getParameter("search_bar").toLowerCase()
				: "";
		
		List<Shoe> results = dao.searchShoes(type, entry);
		
		request.setAttribute("shoes", results);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("stock.jsp");
		dispatcher.forward(request,  response);
	}
}