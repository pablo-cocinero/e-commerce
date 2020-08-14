package ar.com.gl.shop.product.repositoryimpl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ar.com.gl.shop.product.model.Stock;
import ar.com.gl.shop.product.repository.StockRepository;
import ar.com.gl.shop.product.repository.datasources.StockDatasource;

public class StockRepositoryImpl implements Serializable, StockRepository {

	private static final long serialVersionUID = 3876426318410983253L;
	private static StockRepositoryImpl INSTANCE;

	private Connection con;
	private Statement st;
	private ResultSet rs;
	private PreparedStatement pst;

	private StockRepositoryImpl() {
	}

	public static StockRepositoryImpl getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new StockRepositoryImpl();
		}
		return INSTANCE;
	}

	@Override
	public Stock save(Stock stock) {
		final String query = "INSERT INTO stock (quantity, locationCode, enabled) VALUES (?,?,?);";
		Stock stockSave = null;
		try {
			con = StockDatasource.getStockDatasource().getConnection();
			pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			pst.setDouble(1, stock.getQuantity());
			pst.setString(2, stock.getLocationCode());
			pst.setBoolean(3, stock.getEnabled());

			pst.executeUpdate();

			if (!rs.next()) {
				throw new SQLException("Registro no encontrado");
			}
			
			rs = pst.getGeneratedKeys();
			stockSave = getById((long) rs.getInt(1));
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				con.close();
				pst.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return stockSave;
	}

	@Override
	public Stock updateStock(Stock stock) {
		final String query = "UPDATE stock SET quantity=?, locationCode=? where id=?;";

		Stock stockSave = null;

		try {
			con = StockDatasource.getStockDatasource().getConnection();
			pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			pst.setDouble(1, stock.getQuantity());
			pst.setString(2, stock.getLocationCode());
			pst.setLong(3, stock.getId());

			pst.executeUpdate();

			if (!rs.next()) {
				throw new SQLException("Registro no encontrado");
			}
			
			stockSave = getById((long) rs.getInt(1));

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
				pst.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return stockSave;
	}

	@Override
	public void delete(Stock stock) {
		final String query = "DELETE FROM stock WHERE id=?;";
		try {
			con = StockDatasource.getStockDatasource().getConnection();
			pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			pst.setLong(6, stock.getId());

			rs = pst.executeQuery();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
				pst.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Stock getById(Long id) {
		final String query = "SELECT * FROM stock WHERE id=?;";
		Stock stock = null;
		try {
			con = StockDatasource.getStockDatasource().getConnection();
			pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			pst.setLong(1, id);

			
			if (!rs.next()) {
				throw new SQLException("Registros no encontrado");
			}
			
			rs = pst.executeQuery();
			stock = new Stock();
			stock.setId(rs.getLong("id"));
			stock.setLocationCode(rs.getString("locationCode"));
			stock.setQuantity(rs.getInt("quantity"));

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
				pst.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return stock;
	}
}
