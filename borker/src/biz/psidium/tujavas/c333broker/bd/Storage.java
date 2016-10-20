package biz.psidium.tujavas.c333broker.bd;
import javax.servlet.http.HttpServlet;

import net.sourceforge.pbeans.Store;
import net.sourceforge.pbeans.StoreException;
import net.sourceforge.pbeans.data.GenericDataSource;
import net.sourceforge.pbeans.servlet.ServletAccess;

public class Storage {
	
	private static Store uniqueDataBase = null;;
	
	public static Store getStorage(HttpServlet papa) throws StoreException{
		if (uniqueDataBase == null)
			uniqueDataBase = ServletAccess.getStore(papa, "broker1");
		papa.log("Conectado a la Base de Datos");
		return uniqueDataBase;
	}
	
	public static Store getStorage(String driverClassName, String bdUrl) throws StoreException, IllegalAccessException {
		if (uniqueDataBase == null)
			uniqueDataBase = new Store(new GenericDataSource(driverClassName, bdUrl));
		return uniqueDataBase;
	}

}
