package dam2.add.p8;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.google.gson.Gson;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Operaciones {
	Base64 base64 = new Base64();

	private static Logger log = Logger.getLogger(Operaciones.class);

	public void usuario() {
		PropertyConfigurator.configure("./properties/log4j.properties");
		Scanner sc = new Scanner(System.in);

		System.out.println("Acceso");
		System.out.println("******");
		System.out.println("Usuario");
		String user = sc.nextLine();
		System.out.println("Contrasena");
		String passDC = sc.nextLine();

		String pass = new String(base64.encode(passDC.getBytes()));

		boolean existe = compruebaUsuario(user, pass);

		log.debug(existe);

		if (existe) {
			System.out.println("Bienvenido " + user);
			log.info("Logueado correctamente: " + user);
		} else {
			System.out.println("Usuario incorrecto");
			log.info("Intento de logueo: " + user);
		}

	}

	public void registroUsuario() {
		PropertyConfigurator.configure("./properties/log4j.properties");
		Scanner sc = new Scanner(System.in);

		System.out.println("Formulario de registro");
		System.out.println("**********************");
		System.out.println("Nombre de usuario");
		String user = sc.nextLine();
		System.out.println("Contrasena");
		String passDC = sc.nextLine();
		System.out.println("Repita la contrasena");
		String pass2 = sc.nextLine();
		System.out.println("Escoja el id de su provincia con 2 cifras. por ejemplo 01.");

		Provincias[] provincias = leerProvincias();

		for (Provincias provincia : provincias) {
			String name = provincia.getNm();
			String id = provincia.getId();
			System.out.println(id + ".- " + name);
		}

		String codigo = sc.nextLine();
		String loc = escogerProvincia(codigo);

		String pass = new String(base64.encode(passDC.getBytes()));

		if (passDC.equals(pass2)) {
			Usuario u = new Usuario(9, user, pass, loc, 3);
			log.debug(u);
			Operaciones.altaCuenta(u);
		} else {
			System.out.println("La contrasena no coincide, introduzca los datos otra vez");
			registroUsuario(); // algoritmo recursivo
		}

	}

	public Boolean compruebaUsuario(String user, String pass) {
		boolean resultado = false;

		List<Usuario> results = leerBDD();

		for (Usuario usuario : results) {
			if (usuario.getUser().equals(user) && usuario.getBloq() != 0) {
				if (usuario.getUser().equals(user) && usuario.getPass().equals(pass)) {
					if (usuario.getUser().equals("Admin") && usuario.getPass().equals(pass)) {
						adminOps();
					}
					resultado = true;
					results.clear();
					return resultado;
				} else {
					if (usuario.getUser().equals(user) && !usuario.getPass().equals(pass)) {
						int id = usuario.getId();
						int bloq = usuario.getBloq();
						intentosClave(id, user, bloq);
						results.clear();
						return resultado;
					}
				}
			} else if (usuario.getUser().equals(user) && usuario.getBloq() == 0) {
				System.out.println("Usuario bloqueado, contacte con el administrador");
				log.info(user + " bloqueado");
				results.clear();
				return resultado;
			}
		}
		results.clear();
		return resultado;
	}

	public void intentosClave(Usuario u) {
		intentosClave(u.getId(), u.getUser(), u.getBloq());
	}

	public void intentosClave(int id, String user, int bloq) {
		PropertyConfigurator.configure("./properties/log4j.properties");
		Session session = HibernateManager.getSessionFactory().openSession();
		session.beginTransaction();
		Usuario u = recuperarUsuarios(id);

		log.debug(u);

		int resultado = bloq - 1;
		u.setBloq(resultado);
		session.update(u);
		session.getTransaction().commit();
		System.out.println("Le quedan: " + u.getBloq() + " intentos");
		log.info(user + " le quedan " + u.getBloq() + " intentos");
		
		session.close();

	}

	public static void altaCuenta(Usuario u) {
		altaCuenta(u.getId(), u.getUser(), u.getPass(), u.getLocation());
	}

	public static void altaCuenta(int id, String user, String pass, String loc) {
		PropertyConfigurator.configure("./properties/log4j.properties");
		Session session = HibernateManager.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Usuario u = null;

		u = new Usuario(id, user, pass, loc, 3);

		log.debug(u);

		int i = (int) session.save(u);
		System.out.println("Usuario registrado");
		tx.commit();
		session.close();
	}

	public void adminOps() {
		List<Usuario> results = leerBDD();

		Scanner sc = new Scanner(System.in);

		System.out.println("Panel de administrador");
		System.out.println("**********************");

		System.out.println("Estos son los usuarios bloqueados");

		for (Usuario usuario : results) {
			System.out.println(usuario.getId() + ".- " + usuario.getUser() + " intentos: " + usuario.getBloq());
		}

		System.out.println("Introduce el ID del usuario que quieras desbloquear");
		int id = sc.nextInt();

		desbloquearCuenta(id);
	}

	public void desbloquearCuenta(Usuario u) {
		desbloquearCuenta(u.getId());

	}

	public void desbloquearCuenta(int id) {
		PropertyConfigurator.configure("./properties/log4j.properties");
		Session session = HibernateManager.getSessionFactory().openSession();
		session.beginTransaction();
		Usuario u = recuperarUsuarios(id);

		log.debug(u);

		if (u == null) {
			System.out.println("no existe el usuario");
		} else {
			u.setBloq(3);
			session.update(u);
			session.getTransaction().commit();
			System.out.println("Reestablecido los intentos del usuario " + id);
			log.info("Reestablecido los intentos del usuario " + id);
		}
		session.close();
	}

	public List<Usuario> leerBDD() {
		PropertyConfigurator.configure("./properties/log4j.properties");
		Session session = HibernateManager.getSessionFactory().openSession();
		session.beginTransaction();

		String query = "SELECT u FROM Usuario u";

		log.debug(query);

		Query q = session.createQuery(query);
		List<Usuario> results = q.list();

		session.close();

		return results;
	}

	public static Usuario recuperarUsuarios(int id) {
		Session session = HibernateManager.getSessionFactory().openSession();
		session.beginTransaction();

		Usuario u = (Usuario) session.get(Usuario.class, id);

		log.debug(u);

		if (u == null) {
			System.out.println("No existe el usuario");
		}
		session.close();
		return u;
	}

	// TODO
	// crear array para almacenar las provincinas ordenarlo y servirlo

	public static Provincias[] leerProvincias() {
		String url = "https://raw.githubusercontent.com/IagoLast/pselect/master/data/provincias.json";
		String cadenaJson = leerUrl(url);
		Provincias[] provincias = new Gson().fromJson(cadenaJson, Provincias[].class);

		return provincias;
	}

	public String escogerProvincia(String codigo) {
		Provincias[] provincias = leerProvincias();
		String seleccion = "";

		for (Provincias provincia : provincias) {
			if (provincia.getId().equals(codigo)) {
				seleccion = provincia.getNm();
				return seleccion;
			}
		}
		return seleccion;
	}

	public static String leerUrl(String sUrl) {
		PropertyConfigurator.configure("./properties/log4j.properties");
		String output = "";

		try {
			URL url = new URL(sUrl);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			StringBuilder sb = new StringBuilder();
			int cp;
			while ((cp = br.read()) != -1) {
				sb.append((char) cp);
			}
			output = sb.toString();

			conn.disconnect();
		} catch (Exception e) {
			log.error(e.toString());
			System.out.println(e.getMessage());
		}

		return output;
	}

}
