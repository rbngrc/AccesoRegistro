package dam2.add.p8;

public class Usuario {
	private int id;
	private String user;
	private String pass;
	private String location;
	private int bloq;

	public Usuario() {
		super();
	}

	public Usuario(String user, String pass, String location, int bloq) {
		super();
		this.user = user;
		this.pass = pass;
		this.location = location;
		this.bloq = bloq;
	}

	public Usuario(int id, String user, String pass, String location, int bloq) {
		super();
		this.id = id;
		this.user = user;
		this.pass = pass;
		this.location = location;
		this.bloq = bloq;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getBloq() {
		return bloq;
	}

	public void setBloq(int bloq) {
		this.bloq = bloq;
	}

}
