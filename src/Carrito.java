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

public class Carrito extends HttpServlet {

    public void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) throws ServletException, IOException {
        
        PrintWriter salida   = respuesta.getWriter();
        HttpSession misesion = peticion.getSession(true);

		int totalNumeroArticulos = 0;
		String juego = peticion.getParameter("juego");

		if (juego != null) {
			if (misesion.getAttribute("almacen") == null) {
				Pedidos pedidos = new Pedidos();
				pedidos.agregarPedido(juego);
				misesion.setAttribute("almacen", pedidos);
			}
			else {
				Pedidos pedidos = (Pedidos)misesion.getAttribute("almacen");
				pedidos.agregarPedido(juego);
				misesion.setAttribute("almacen", pedidos);
			}
		}

		if (((Pedidos)misesion.getAttribute("almacen")) != null) {
			totalNumeroArticulos = ((Pedidos)misesion.getAttribute("almacen")).pedidos;
		}

		salida.println("<!DOCTYPE html>"); 
		salida.println("<html><head>");

		salida.println("<title> CARRITO </title></head>");

		salida.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"//fonts.googleapis.com/css?family=Mate+SC\" />");
		salida.println("<link rel=\"stylesheet\" href=\""+peticion.getContextPath()+"/css/style.css\" type=\"text/css\">");
		
		salida.println("<body>");

		salida.println("<header>");
		salida.println("<h1 class=\"titulo\"> JUEGOS </h1>");
		salida.println("<a class=\"enlaceDetalles\" href='pasarela'> Elementos en la Cesta (" + totalNumeroArticulos + ") </a>");
		salida.println("</header");

		salida.println("<main>");

		salida.println("<table>");

		// Construccion de la tabla
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tienda", "tomcat", "tomcat");
			PreparedStatement pst = conn.prepareStatement("Select * from juegos;");
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()){
				salida.println("<tr>");

				salida.println("<td>");
				salida.println("<img class=\"figuras\" src=\"" + peticion.getContextPath()+"/imgs/"+rs.getString(4)+"\">");
				salida.println("</td>");
						
				
				salida.println("<td>");
				salida.println(rs.getString(2));
				salida.println("</td>");

				salida.println("<td>");
				salida.println(rs.getString(3) + " $");
				salida.println("</td>");

				salida.println("<td>");
				salida.println("<form action='carrito' method=\"GET\">");
				salida.println("<input class=\"carrito\" type=\"submit\" name='juego' value='"+rs.getString(2)+"'></input>");
				salida.println("</form>");
				salida.println("</td>");
				
				salida.println("</tr>");

			}
			
			conn.close();
			
			} catch(ClassNotFoundException | SQLException e) {
				salida.println(e.toString());
			}

		salida.println("</table>");

		salida.println("</main>");
		
		salida.println("<form class='loginVendedor' action='vendedor' method='POST'>");
		salida.println("<input name='usuario' type='text' placeholder='nombre...'></input>");
		salida.println("<input name='password' type='password' placeholder='password...'></input>");
		salida.println("<input type='submit' value='Entrar'></input>");
		salida.println("</form>");

		salida.println("</body></html>");        
    }
}
