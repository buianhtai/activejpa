/**
 * 
 */
package org.activejpa.entity.testng;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.testng.IObjectFactory;

import net.bytebuddy.agent.ByteBuddyAgent;

/**
 * @author ganeshs
 *
 */
public class DomainClassObjectFactory implements IObjectFactory {
	
	private static final long serialVersionUID = 1L;
	
	private ClassLoader loader;
	
	public DomainClassObjectFactory(Set<String> ignoredPackages) throws Exception {
	    ByteBuddyAgent.install();
		Class<?> clazz = Class.forName("org.activejpa.enhancer.ModelClassLoader");
		Constructor<?> constructor = clazz.getConstructor(ClassLoader.class, Set.class);
		loader = (ClassLoader) constructor.newInstance(Thread.currentThread().getContextClassLoader(), ignoredPackages);
		Thread.currentThread().setContextClassLoader(loader);
	}
	
	public DomainClassObjectFactory() throws Exception {
		this(new HashSet<>(Arrays.asList("org.xml.")));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object newInstance(Constructor constructor, Object... params) {
		Class<?> clazz = constructor.getDeclaringClass();
		try {
			clazz = loader.loadClass(clazz.getName());
			constructor = clazz.getConstructor(constructor.getParameterTypes());
			return constructor.newInstance(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
