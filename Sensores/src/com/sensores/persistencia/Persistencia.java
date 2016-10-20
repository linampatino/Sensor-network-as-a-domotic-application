package com.sensores.persistencia;

import java.util.List;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;

import com.sensores.modelo.*;
import com.sensores.persistencia.HibernateSessionFactory;

public abstract class Persistencia {

	public static boolean guardar(Object obj) {

		try {
			HibernateSessionFactory.getSession().beginTransaction();
			HibernateSessionFactory.getSession().save(obj);
			HibernateSessionFactory.getSession().getTransaction().commit();
			return true;
		}/*
			 * catch(ConstraintViolationException e){
			 * System.out.println("Excepcion: ConstraintViolationException");
			 * return false; }
			 */
		catch (HibernateException hbe) {
			hbe.printStackTrace();
		}

		return false;

	}
	
	public static boolean actualizar(Object obj) {

		try {
			HibernateSessionFactory.getSession().beginTransaction();
			HibernateSessionFactory.getSession().update(obj);
			HibernateSessionFactory.getSession().getTransaction().commit();
			return true;
		} catch (HibernateException hbe) {
			hbe.printStackTrace();
		}

		return false;

	}

	public static boolean eliminar(Object obj) {
		try {
			HibernateSessionFactory.getSession().beginTransaction();
			HibernateSessionFactory.getSession().delete(obj);
			HibernateSessionFactory.getSession().getTransaction().commit();
			return true;
		} catch (HibernateException hbe) {
			hbe.printStackTrace();
		}
		return false;
	}

	public static int contarActualizaciones(){
		
		HibernateSessionFactory.getSession().setCacheMode(CacheMode.REFRESH);
		Query consulta = HibernateSessionFactory.getSession().createQuery(
		"select count(*) from Actualizacion").setCacheable(false);
		return ((Integer)consulta.uniqueResult()).intValue();
	}
	
	public static Alerta cargarAlerta(int id) {
		Alerta alerta = null;
		try {
			alerta = (Alerta) HibernateSessionFactory.getSession().load(
					Alerta.class, new Integer(id));
		} catch (HibernateException hbe) {
			hbe.printStackTrace();
		}
		return alerta;

	}

	public static Celular cargarCelular(int id) {
		Celular celular = null;
		try {
			celular = (Celular) HibernateSessionFactory.getSession().load(
					Celular.class, new Integer(id));
		} catch (HibernateException hbe) {
			hbe.printStackTrace();
		}
		return celular;

	}

	public static Estado cargarEstado(int id) {
		Estado estado = null;
		try {
			estado = (Estado) HibernateSessionFactory.getSession().load(
					Estado.class, new Integer(id));
		} catch (HibernateException hbe) {
			hbe.printStackTrace();
		}
		return estado;

	}

	public static Sensor cargarSensor(int id) {
		Sensor sensor = null;
		try {
			sensor = (Sensor) HibernateSessionFactory.getSession().load(
					Sensor.class, new Integer(id));
		} catch (HibernateException hbe) {
			hbe.printStackTrace();
		}
		return sensor;

	}

	public static EstadosEP cargarEstadoEP(int id) {
		EstadosEP estado = null;
		try {
			estado = (EstadosEP) HibernateSessionFactory.getSession().load(
					EstadosEP.class, new Integer(id));
		} catch (HibernateException hbe) {
			hbe.printStackTrace();
		}
		return estado;

	}

	public static List cargarAlertas() {

		Query consulta = HibernateSessionFactory.getSession().createQuery(
				"from Alerta");
		return consulta.list();
	}
	
	public static List cargarEventos() {
		//Devuelve la lista de todos los eventos para los cuales no se han generado alertas
		HibernateSessionFactory.getSessionFactory().openStatelessSession();
		HibernateSessionFactory.getSessionFactory(). evict(Evento.class);
		HibernateSessionFactory.getSession().flush();
		HibernateSessionFactory.getSession().setCacheMode(CacheMode.REFRESH);
		Query consulta = HibernateSessionFactory.getSession().createQuery(
				"from Evento e where e.mensaje=?").setInteger(0, 0).setCacheable(false);
		HibernateSessionFactory.getSession().evict(consulta.list());
		return consulta.list();
	}
	
	public static List cargarCelulares() {
		Query consulta = HibernateSessionFactory.getSession().createQuery(
				"from Celular");
		return consulta.list();
	}

	public static List cargarEstadosEP(int ep) {
		Query consulta = HibernateSessionFactory.getSession().createQuery(
				"from EstadosEP where ep=?").setInteger(0, ep);
		return consulta.list();
	}

	public static EstadosEP crearEstadoEP(int ep, int estado) {
		Query consulta = HibernateSessionFactory.getSession().createQuery(
				"from EstadosEP where ep=? and id_estado=?").setInteger(0, ep)
				.setInteger(1, estado);
		;
		return (EstadosEP) consulta.uniqueResult();
	}

	public static List cargarEPS() {
		Query consulta = HibernateSessionFactory.getSession().createQuery(
				"from Sensor");
		return consulta.list();
	}

	public static List cargarActualizaciones() {
		
		HibernateSessionFactory.getSessionFactory().openStatelessSession();
		HibernateSessionFactory.getSessionFactory(). evict(Actualizacion.class);
		HibernateSessionFactory.getSession().flush();
		HibernateSessionFactory.getSession().setCacheMode(CacheMode.REFRESH);
		
		Query consulta = HibernateSessionFactory.getSession().createQuery(	
			"from Actualizacion").setCacheable(false);
		
		return consulta.list();
	}

	public static boolean existeCelular(String celular) {
		Query consulta = HibernateSessionFactory.getSession().createQuery(
				"select count(us) from Celular us where us.celular= ? ")
				.setString(0, celular);
		int resul = ((Integer) consulta.uniqueResult()).intValue();
		if (resul > 0)
			return true;
		else
			return false;
	}

	public static boolean existeEPEvento(int ep) {
		//System.out.println("Id sensor en existeEPEvento "+ep);
		Query consulta = HibernateSessionFactory.getSession().createQuery(
				"select count(us) from Evento us where us.ep= ? ").setInteger(
				0, ep);
		int resul = ((Integer) consulta.uniqueResult()).intValue();
		if (resul > 0)
			return true;
		else
			return false;
	}
	
	public static boolean existeActualizacionEP(int ep) {
		Query consulta = HibernateSessionFactory.getSession().createQuery(
				"select count(us) from Actualizacion us where us.ep= ? ").setInteger(
				0, ep);
		int resul = ((Integer) consulta.uniqueResult()).intValue();
		if (resul > 0)
			return true;
		else
			return false;
	}
	
	public static boolean existeAlarma(int idCelular, int estadoEP) {
		System.out.println("Validando que exista la alarma");
		Query consulta = HibernateSessionFactory.getSession().createQuery("select count(*) from Alerta us where us.idEstadoEp= ? and us.idCelular = ?").setInteger(0, estadoEP).setInteger(1,idCelular);
		int resul = ((Integer) consulta.uniqueResult()).intValue();
		if (resul > 0)
			return true;
		else
			return false;
	}

	public static Evento cargarEventoSensor(int ep) {
		HibernateSessionFactory.getSessionFactory().openStatelessSession();
		HibernateSessionFactory.getSessionFactory(). evict(Evento.class);
		HibernateSessionFactory.getSession().flush();
		HibernateSessionFactory.getSession().setCacheMode(CacheMode.REFRESH);
		Evento evento = null;
		try {
			Query consulta = HibernateSessionFactory.getSession().createQuery(
					"from Evento us where us.ep= ? ").setInteger(0, ep).setCacheable(false);
			evento = (Evento) consulta.uniqueResult();
			HibernateSessionFactory.getSession().evict(evento);
		} catch (HibernateException hbe) {
			hbe.printStackTrace();
		}
		return evento;

	}
	
	public static Evento cargarEvento(int id) {
		HibernateSessionFactory.getSessionFactory().openStatelessSession();
		HibernateSessionFactory.getSessionFactory(). evict(Evento.class);
		HibernateSessionFactory.getSession().flush();
		HibernateSessionFactory.getSession().setCacheMode(CacheMode.REFRESH);
		Evento evento = null;
		try {
			evento = (Evento) HibernateSessionFactory.getSession().load(
					Evento.class, new Integer(id));
			HibernateSessionFactory.getSession().evict(evento);
		} catch (HibernateException hbe) {
			hbe.printStackTrace();
		}
		return evento;

	}

	public static Estado cargarEstadoCadena(String estado) {
		HibernateSessionFactory.getSessionFactory().openStatelessSession();
		HibernateSessionFactory.getSessionFactory(). evict(Estado.class);
		HibernateSessionFactory.getSession().flush();
		HibernateSessionFactory.getSession().setCacheMode(CacheMode.REFRESH);
		Estado est = null;
		try {
			Query consulta = HibernateSessionFactory.getSession().createQuery(
					"from Estado us where us.estado= ? ").setString(0,
					estado.toUpperCase()).setCacheable(false);
			est = (Estado) consulta.uniqueResult();
			HibernateSessionFactory.getSession().evict(est);
		} catch (HibernateException hbe) {
			hbe.printStackTrace();
		}
		return est;

	}
	
	public static List cargarSensores(){
		HibernateSessionFactory.getSessionFactory().openStatelessSession();
		HibernateSessionFactory.getSessionFactory(). evict(Sensor.class);
		HibernateSessionFactory.getSession().flush();
		HibernateSessionFactory.getSession().setCacheMode(CacheMode.REFRESH);
		
		Query consulta = HibernateSessionFactory.getSession().createQuery(	
			"from Sensor").setCacheable(false);
		HibernateSessionFactory.getSession().evict(consulta.list());
		return consulta.list();
	}
	
	public static List cargarNodos(){
		HibernateSessionFactory.getSessionFactory().openStatelessSession();
		HibernateSessionFactory.getSessionFactory(). evict(Sensor.class);
		HibernateSessionFactory.getSession().flush();
		HibernateSessionFactory.getSession().setCacheMode(CacheMode.REFRESH);
		
		Query consulta = HibernateSessionFactory.getSession().createSQLQuery(	
		"select distinct (sen.mac) from sensores sen").setCacheable(false);
		HibernateSessionFactory.getSession().evict(consulta.list());
		return consulta.list();
	}
	
	public static Sensor buscarSensor(String mac,int ep){
		HibernateSessionFactory.getSessionFactory().openStatelessSession();
		HibernateSessionFactory.getSessionFactory(). evict(Sensor.class);
		HibernateSessionFactory.getSession().flush();
		HibernateSessionFactory.getSession().setCacheMode(CacheMode.REFRESH);
		Sensor sensor = null;
		try {
			Query consulta = HibernateSessionFactory.getSession().createQuery(
					"from Sensor us where us.mac= ? and us.ep= ?").setString(0,mac).setInteger(1, ep).setCacheable(false);
			sensor = (Sensor) consulta.uniqueResult();
			HibernateSessionFactory.getSession().evict(sensor);
		} catch (HibernateException hbe) {
			hbe.printStackTrace();
		}
		return sensor;
	}
	
	public static List cargarIdsEventos(int ep) {
		//Entrega los ids de los eventos para los sensores que estan asociados
		HibernateSessionFactory.getSessionFactory().openStatelessSession();
		HibernateSessionFactory.getSession().flush();
		HibernateSessionFactory.getSession().setCacheMode(CacheMode.REFRESH);
		
		Query consulta = HibernateSessionFactory.getSession().createSQLQuery(	
			"select ev.id from eventos ev, sensores sen where ev.id_sensor = sen.id and sen.ep = ? and sen.asociado = ?").setInteger(0, ep).setString(1, "1").setCacheable(false);
		HibernateSessionFactory.getSession().evict(consulta.list());
		
		return consulta.list();
	}
	
	public static void desasociarMac(String mac){
		HibernateSessionFactory.getSessionFactory().openStatelessSession();
		HibernateSessionFactory.getSession().flush();
		HibernateSessionFactory.getSession().setCacheMode(CacheMode.REFRESH);
		
		Query consulta = HibernateSessionFactory.getSession().createQuery(	
			"update Sensor set asociado = ?  where mac = ?").setString(0, "0").setString(1, mac).setCacheable(false);
		consulta.executeUpdate();
		
	}
	
	public static void asociarMac(String mac){
		HibernateSessionFactory.getSessionFactory().openStatelessSession();
		HibernateSessionFactory.getSession().flush();
		HibernateSessionFactory.getSession().setCacheMode(CacheMode.REFRESH);
		
		Query consulta = HibernateSessionFactory.getSession().createQuery(	
		"update Sensor set asociado = ?  where mac = ?").setString(0, "1").setString(1, mac).setCacheable(false);
		consulta.executeUpdate();
		
	}
}
