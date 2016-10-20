package biz.psidium.tujavas.c333broker.bd;

import net.sourceforge.pbeans.annotations.PersistentClass;

@PersistentClass(
		   table="vasp",
		   autoIncrement=true,
		   idField="VaspID"  
		 )
public class VASP {
	private String nombre;
	private String contacto;
	private String shortCode;
	private String password;
	
	public VASP(String nombre, String contacto, String shortCode, String password) {
		this.nombre = nombre;
		this.contacto = contacto;
		this.shortCode = shortCode;
		this.password = password;
	}
	
	public VASP(){
		this.nombre = "";
		this.contacto = "";
		this.shortCode = "";
		this.password = "";
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getContacto() {
		return contacto;
	}
	public void setContacto(String contacto) {
		this.contacto = contacto;
	}
	public String getShortCode() {
		return shortCode;
	}
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	

}
