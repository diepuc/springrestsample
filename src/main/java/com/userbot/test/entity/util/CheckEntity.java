package com.userbot.test.entity.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.RollbackException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.StringTokenizer;

public abstract class CheckEntity {

	protected static Logger logger = LoggerFactory.getLogger(CheckEntity.class);
	
	protected abstract void checkStrings() throws RollbackException;
	
	protected void checkStrings(Object entity) throws RollbackException{
		if (entity != null){
			for (Field f : entity.getClass().getDeclaredFields()){
				try {
					f.setAccessible(true);
				} catch (Exception e) {}
				
				if (f.getType().getName().equals(String.class.getName())){
					Integer len = null;
					
					for (Annotation a: f.getAnnotations()){
						if (a.annotationType().getName().equals(Lob.class.getName())) {
							len = null;
							break;
						}
						if (a.annotationType().getName().equals(Column.class.getName())) {
							String an = a.toString();
							an = an.substring(Column.class.getName().length() + 2, an.length() - 1);
							StringTokenizer st1 = new StringTokenizer(an, ",");
							
							while (st1 != null && st1.hasMoreTokens()){
								StringTokenizer st2 = new StringTokenizer(st1.nextToken(), "=");
								if (st2 != null && st2.hasMoreTokens() && st2.nextToken().trim().equals("length")){
									if (st2.hasMoreTokens()){
										try {
											len = Integer.parseInt(st2.nextToken());
										} catch (Exception e) {
											len = null;
										}
										break;
									}
								}
							}
						}
					}
					if (len != null && len > 0){
						int lenDato = 0;
						try {
							lenDato = ((String)f.get(entity)).length();
						} catch (Exception e) {}
						
						if (lenDato > len){
							logger.error("String too big " + f.getName() + " MaxLength=" + len);
							throw new RollbackException("String too big " + f.getName() + " MaxLength=" + len);
						}
					}
				}
			}
		}
	}
	
}
