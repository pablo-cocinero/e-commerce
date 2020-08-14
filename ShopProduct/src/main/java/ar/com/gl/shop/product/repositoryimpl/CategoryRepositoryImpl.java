package ar.com.gl.shop.product.repositoryimpl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ar.com.gl.shop.product.exceptions.ItemNotFound;
import ar.com.gl.shop.product.model.Category;
import ar.com.gl.shop.product.repository.CategoryRepository;
import ar.com.gl.shop.product.repository.datasources.CategoryDatasource;

public class CategoryRepositoryImpl implements Serializable, CategoryRepository {

	private static final long serialVersionUID = 3876426318410983253L;
	private static CategoryRepositoryImpl INSTANCE;

	private Connection con;
	private Statement st;
	private ResultSet rs;
	private PreparedStatement pst;
	
	private CategoryRepositoryImpl() {
	}

	public static CategoryRepositoryImpl getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CategoryRepositoryImpl();
		}
		return INSTANCE;
	}

	@Override
	public Category create(Category category) {
		final String query = "INSERT INTO category (name, description, enabled) VALUES (?,?,?);";
		Category categorySave = null;
		try {
			con = CategoryDatasource.getCategoryDatasource().getConnection();
			pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			pst.setString(1, category.getName());
			pst.setString(2, category.getDescription());
			pst.setBoolean(3, category.getEnabled());

			pst.executeUpdate();
			
			rs = pst.getGeneratedKeys();
		
			if (!rs.next()) {
				throw new ItemNotFound();	
			} 
			
			categorySave = getById((long) rs.getInt(1));
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}catch (ItemNotFound e) {
			System.out.println(e.getMessage());
		}
		finally {
			try {
				con.close();
				pst.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return categorySave;
	}

	@Override
	public Category delete(Category category) {
		final String query = "DELETE FROM category WHERE id=? ;";
		try {

			con = CategoryDatasource.getCategoryDatasource().getConnection();
			pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			pst.setLong(1, category.getId());
			
			pst.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				con.close();
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}			
		}
		return category;
	}

	@Override
	public Category update(Category category) {
		final String query = "UPDATE category SET name=?, description=?, enabled=? where id=?;";
		Category categorySave = null;
		try {
			con = CategoryDatasource.getCategoryDatasource().getConnection();
			pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			pst.setString(1, category.getName());
			pst.setString(2, category.getDescription());
			pst.setBoolean(3, category.getEnabled());
			pst.setLong(4, category.getId());

			Integer rs = pst.executeUpdate();
			
			if (rs.equals(0)) {
				throw new ItemNotFound();	
			}
			
			categorySave = getById(category.getId());

		} catch (SQLException e) {
			e.printStackTrace();
		}catch (ItemNotFound e) {
			System.out.println(e.getMessage());
		} 
		finally {
			try {
				con.close();
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return categorySave;

	}

	@Override
	public List<Category> findAll() {
		final String query = "SELECT * FROM category;";
		List<Category> categories = new ArrayList<Category>();
		try {
			con = CategoryDatasource.getCategoryDatasource().getConnection();
			st = con.createStatement();
			rs = st.executeQuery(query);

			if (!rs.next()) {
				throw new ItemNotFound();
			} 
			do {
				Category category = new Category();
				category.setId(rs.getLong("id"));
				category.setName(rs.getString("name"));
				category.setDescription(rs.getString("description"));
				category.setEnabled(rs.getBoolean("enabled"));
				categories.add(category);
			}while (rs.next());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (ItemNotFound e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				con.close();
				st.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return categories;
	}

	@Override
	public Category getById(Long id) {
		final String query = "SELECT * FROM category WHERE id=?;";
		Category category = null;
		try {
			con = CategoryDatasource.getCategoryDatasource().getConnection();
			st = con.createStatement();
			pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			pst.setLong(1, id);

			rs = pst.executeQuery();

			if (!rs.next()) {
				throw new ItemNotFound();
			} 
			
			category = new Category();
			category.setId(rs.getLong("id"));
			category.setName(rs.getString("name"));
			category.setDescription(rs.getString("description"));
			category.setEnabled(rs.getBoolean("enabled"));

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ItemNotFound e) {
			System.out.println(e.getMessage());
		}
		finally {
			try {
				con.close();
				pst.close();
				rs.close();
			} catch (SQLException e) {
				e.getMessage();
				e.printStackTrace();
			}
		}
		return category;
	}

}