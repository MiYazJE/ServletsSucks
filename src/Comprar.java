// package src;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.*;

class Pedidos {

	public HashMap<String, Integer> almacen;
	public int pedidos;
	
	public Pedidos() {
		this.almacen = new HashMap<>();
		this.pedidos = 0;
	}

	public void agregarPedido(String pedido) {
		if (this.almacen.containsKey(pedido)) {
			this.almacen.put(pedido, this.almacen.get(pedido) + 1);
		}
		else {
			this.almacen.put(pedido, 1);
		}
		this.pedidos++;
	}

}

public class Comprar extends HttpServlet {

    static int getIdUser(String nombre, String apellidos) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tienda", "tomcat", "tomcat");
            String query = "Select _idusuario From usuarios where nombre='"+nombre+"' and apellidos='"+apellidos+"'";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
        }
        catch(Exception e) {
            
        }
        return -1;
    }

    static boolean createOrder(String nombre, String apellidos, double precio) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tienda", "tomcat", "tomcat");
            
            int idUser = getIdUser(nombre, apellidos);
            
            String query = "Insert Into pedidos (_idusuario, precio) Values ('"+idUser+"', '"+precio+"')";
            PreparedStatement pst = conn.prepareStatement(query);

            pst.execute();
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    static boolean createUser(String nombre, String apellidos) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tienda", "tomcat", "tomcat");
            String query = "Insert Into usuarios (nombre, apellidos) Values ('"+nombre+"', '"+apellidos+"')";
            PreparedStatement pst = conn.prepareStatement(query);
            return pst.execute();
        }
        catch(Exception e) {
            return false;
        }
    }

    static boolean existsUser(String nombre, String apellidos) {

        try {

            Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tienda", "tomcat", "tomcat");
            String query = "Select _idusuario From usuarios where nombre='"+nombre+"' and apellidos='"+apellidos+"'";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        }
        catch(Exception e) {
            return false;
        }
    }

    static boolean almacenarPedido(HttpServletRequest req, HttpSession sesion) {
        
        try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tienda", "tomcat", "tomcat");

            String nombre = req.getParameter("nombre");
            String apellidos = req.getParameter("apellidos");

			conn.close();
			if (!existsUser(nombre, apellidos)) {
                createUser(nombre, apellidos);
            }
            
            return createOrder(nombre, apellidos, getPrecioPedido(sesion));
		}
		catch (Exception e) {
			return false;
		}

    }

    static int getPrecioPedido(HttpSession sesion) {

        int precio = 0;

		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tienda", "tomcat", "tomcat");
			PreparedStatement pst = conn.prepareStatement("Select * from juegos;");
			ResultSet rs = pst.executeQuery();

			// Mapeo el nombre del juego seguido de su precio
			HashMap<String, Double> precios = new HashMap<>();
			
			while (rs.next()) {
				precios.put(rs.getString(2), rs.getDouble(3));
			}

            HashMap<String, Integer> almacen = ((Pedidos)sesion.getAttribute("almacen")).almacen;
            
            for (Map.Entry<String, Integer> entry : almacen.entrySet()) {
                precio += entry.getValue() * precios.get(entry.getKey());
            }

			conn.close();
		}
		catch (Exception e) {

		}

        return precio;
	}

	static Double getPrecioPedido(HashMap<String, Integer> pedidos, HashMap<String, Double> precios) {
		
		Double precio = 0.0;

		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tienda", "tomcat", "tomcat");
			PreparedStatement pst = conn.prepareStatement("Select * from juegos;");
			ResultSet rs = pst.executeQuery();

			for (Map.Entry<String, Integer> entry : pedidos.entrySet()) {
				precio += (entry.getValue() * precios.get(entry.getKey()));
			}

			conn.close();
		}
		catch (Exception e) {

		}
		
		return precio;
	}

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        PrintWriter out = res.getWriter();
        HttpSession sesion = req.getSession(true);

        out.println("<html>");
        out.println("<meta>");
        out.println("<title>DATOS USUARIO</title>");
        out.println("<link rel='stylesheet' href=' " + req.getContextPath() + "/css/formUser.css'>");
        out.println("</meta>");
        out.println("<body>");

        out.println("<h1 class='margin'>Datos Cliente</h1>");
        out.println("<form action='comprar' method='POST'>");
        out.println("<input type='text' name='nombre' placeholder='Introduce tu nombre...'></input>");
        out.println("<input type='text' name='apellidos' placeholder='Introduce tus apellidos'></input>");
        out.println("<input type='submit' value='Realizar Pedido'></input>");
        out.println("</form>");

        out.println("</body>");
        out.println("</html>");
        
	}

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        PrintWriter out = res.getWriter();
        HttpSession sesion = req.getSession(true);
        
        out.println("<html>");
        out.println("<meta>");
        out.println("<title>DATOS USUARIO</title>");
        out.println("<link rel='stylesheet' href=' " + req.getContextPath() + "/css/formUser.css'>");
        out.println("</meta>");
        out.println("<body>");

        out.println("<a href='carrito'>Seguir Comprando</a>");

        if (almacenarPedido(req, sesion)) {
            out.println("<h1 class='center'>Pedido correctamente almacenado. Gracias por su compra.</h1>");
            sesion.invalidate();
        }
        else {
            out.println("<h1 class='center'>Problemas al gestionar el pedido.</h1>");
        }

        out.println("</body>");
        out.println("</html>");


    }

}
