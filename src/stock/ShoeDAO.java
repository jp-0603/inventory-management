package stock;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ShoeDAO {
	private final String url;
	private final String username;
	private final String password;
	
	public ShoeDAO(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public Shoe getShoe(int id) throws SQLException {
		final String sql = "SELECT * FROM shoes WHERE shoe_id = ?";
		
		Shoe shoe = null;
		Connection conn = getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		pstmt.setInt(1, id);
		ResultSet rs = pstmt.executeQuery();
		
		if (rs.next()) {
			String name = rs.getString("name");
			String description = rs.getString("description");
			double price = rs.getDouble("price");
			int quantity = rs.getInt("quantity");
			
			shoe = new Shoe(id, name, description, price, quantity);
		}
		
		rs.close();
		pstmt.close();
		conn.close();
		
		return shoe;
	}
	
	public List<Shoe> getShoes() throws SQLException {
		final String sql = "SELECT * FROM shoes ORDER BY shoe_id ASC";
		
		List<Shoe> shoes = new ArrayList<Shoe>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		while (rs.next()) {
			int id = rs.getInt("shoe_id");
			String name = rs.getString("name");
			String description = rs.getString("description");
			double price = rs.getDouble("price");
			int quantity = rs.getInt("quantity");
			
			shoes.add(new Shoe(id, name, description, price, quantity));
		}
		
		rs.close();
		stmt.close();
		conn.close();
		
		return shoes;
	}
	
	public boolean insertShoe(String name, String description, double price, int quantity) throws SQLException {       
		final String sql = "INSERT INTO shoes (name, description, price, quantity) " +
			"VALUES (?, ?, ?, ?)";
		
        Connection conn = getConnection();        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1, name);
        pstmt.setString(2, description);
        pstmt.setDouble(3, price);
        pstmt.setInt(4, quantity);
        int affected = pstmt.executeUpdate();
        
        pstmt.close();
        conn.close();
        
        return affected == 1;
    }
	
    public boolean updateShoe(Shoe shoe) throws SQLException {
    	final String sql = "UPDATE shoes SET name = ?, description= ?, price = ?, quantity = ? " +
    		"WHERE shoe_id = ?";
    			
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
                
        pstmt.setString(1, shoe.getName());
        pstmt.setString(2, shoe.getDescription());
        pstmt.setDouble(3, shoe.getPrice());
        pstmt.setInt(4, shoe.getQuantity());
        pstmt.setInt(5, shoe.getId());
        int affected = pstmt.executeUpdate();
        
        pstmt.close();
        conn.close();
        
        return affected == 1;
    }
	
    public boolean deleteShoe(Shoe shoe) throws SQLException {
    	final String sql = "DELETE FROM shoes WHERE shoe_id = ?";
    	
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setInt(1, shoe.getId());
        int affected = pstmt.executeUpdate();
        
        pstmt.close();
        conn.close();
        
        return affected == 1;
    }
    
    public List<Shoe> searchShoes(String type, String entry) throws SQLException {
    	String sql = "";
    	Connection conn = getConnection();
    	PreparedStatement pstmt = null;
    	
    	if (type.equals("price")) {
    		double amount = 0;
    		try {
    			amount = Double.parseDouble(entry);
    		} catch (NumberFormatException e) {
    			
    		}
    		sql = "SELECT * FROM shoes WHERE price = ?";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setDouble(1,  amount);
    		
    	} else if (type.equals("description")) {
    		sql = "SELECT * FROM shoes WHERE description = ?";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, entry);
    		
    	} else if (type.equals("name")) {
    		sql = "SELECT * FROM shoes WHERE name = ?";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, entry);
    	}
    	
    	List<Shoe> shoes = new ArrayList<Shoe>();
    	ResultSet rs = pstmt.executeQuery();
		
		while (rs.next()) {
			int id = rs.getInt("shoe_id");
			String name = rs.getString("name");
			String description = rs.getString("description");
			double price = rs.getDouble("price");
			int quantity = rs.getInt("quantity");
			
			shoes.add(new Shoe(id, name, description, price, quantity));
		}
		
		rs.close();
		pstmt.close();
		conn.close();
		
		return shoes;
    	
    }
    

	private Connection getConnection() throws SQLException {
		final String driver = "com.mysql.cj.jdbc.Driver";
		
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return DriverManager.getConnection(url, username, password);
	}
}