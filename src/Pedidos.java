package src;
import java.util.*;

public class Pedidos {

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