package biz.psidium.tujavas.c333broker.bd;


import net.sourceforge.pbeans.annotations.PersistentClass;

@PersistentClass(
		   table="cuentas",
		   autoIncrement=true,
		   idField="CuentaID"
		 )
public class Cuentas {
	private String shortCode;
	private int smsMO;
	private int smsMT;
	
	public Cuentas() {
		this.shortCode = "";
		this.smsMO = 0;
		this.smsMT = 0;
		
	}

	public Cuentas(String shortCode, int smsMO, int smsMT) {
		this.shortCode = shortCode;
		this.smsMO = smsMO;
		this.smsMT = smsMT;
	}
	
	public String getShortCode() {
		return shortCode;
	}
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	public int getSmsMO() {
		return smsMO;
	}
	public void setSmsMO(int smsMO) {
		this.smsMO = smsMO;
	}
	public int getSmsMT() {
		return smsMT;
	}
	public void setSmsMT(int smsMT) {
		this.smsMT = smsMT;
	}
	
}

