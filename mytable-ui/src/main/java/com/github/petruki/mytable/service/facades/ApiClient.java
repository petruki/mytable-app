package com.github.petruki.mytable.service.facades;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Jersey is required:
 * 
 *	<dependency>
 *	    <groupId>org.glassfish.jersey.core</groupId>
 *	    <artifactId>jersey-client</artifactId>
 *	</dependency>
 *		
 *	<dependency>
 *	    <groupId>org.glassfish.jersey.inject</groupId>
 *	    <artifactId>jersey-hk2</artifactId>
 *	</dependency>
 *	
 *	<dependency>
 *	    <groupId>org.glassfish.jersey.media</groupId>
 *	    <artifactId>jersey-media-json-jackson</artifactId>
 *	</dependency>
 */

/**
 * Simple HTTP Request handler that abstract client requests and response transformations.
 * 
 * @author Roger Floriano (petruki) - https://github.com/petruki
 * @since 2021-02-27
 */
public class ApiClient<T> {
	
	private static final String HEADER_ACCEPT = "accept";
	private static final String HEADER_JSON = "application/json";
	private static final String URL_PATTERN = "%s/%s";
	
	private final Client client;
	private final Class<T> classType;
	private final String apiUrl;
	
	public ApiClient(String apiUrl, Class<T> classType) {
		this.client = ClientBuilder.newClient();
		this.apiUrl = apiUrl;
		this.classType = classType;
	}

	/**
	 * @param <Y> runtime return type
	 * @param clazz Type of the list to be returned
	 * @return Generic list type
	 */
	private <Y> GenericType<List<Y>> getListType(final Class<Y> clazz) {
	    ParameterizedType genericType = new ParameterizedType() {
	        
	    	@Override
	    	public Type[] getActualTypeArguments() {
	            return new Type[]{ clazz };
	        }

	        @Override
	        public Type getRawType() {
	            return List.class;
	        }

	        @Override
	        public Type getOwnerType() {
	            return List.class;
	        }
	    };
	    
	    return new GenericType<List<Y>>(genericType) { };
	}
	
	/**
	 * @param <Y> runtime return type
	 * @param clazz Type of the list to be returned
	 * @return List of a given object type
	 */
	public <Y> List<Y> doGetAll(Class<Y> clazz) {
		try {
			final WebTarget myResource = client.target(
					String.format("%s/", apiUrl));
			
			final Response response = myResource
				.request(MediaType.APPLICATION_JSON)
				.header(HEADER_ACCEPT, HEADER_JSON)
				.get();
		
			
			if (response.getStatus() == 200) {
				return response.readEntity(getListType(clazz));
			}
			
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * @return List of the object type defined when initialized the ApiClient
	 */
	public List<T> doGetAll() {
		return doGetAll(classType);
	}
	
	/**
	 * @param <Y> runtime return type
	 * @param queryParams Can extend resource url and add params to the URL
	 * 	e.g. /list?sort=true
	 * @param clazz Type of the object to be returned
	 * @return Single object
	 */
	public <Y> Y doGetOne(String queryParams, Class<Y> clazz) {
		try {
			final WebTarget myResource = client.target(
					String.format(URL_PATTERN, apiUrl, queryParams));
			
			final Response response =  myResource
				.request(MediaType.APPLICATION_JSON)
				.header(HEADER_ACCEPT, HEADER_JSON)
				.get();
			
			if (response.getStatus() == 200) {
				return response.readEntity(clazz);
			}
			
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * @param queryParams Can extend resource url and add params to the URL
	 * @return Single object
	 */
	public T doGetOne(String queryParams) {
		return doGetOne(queryParams, classType);
	}
	
	/**
	 * @param <Y> runtime return type
	 * @param body Body request
	 * @param queryParams Can extend resource url and add params to the URL
	 * @param clazz Type of the object to be returned
	 * @return Single object created
	 */
	public <Y> Y doPost(Object body, String queryParams, Class<Y> clazz) {
		try {
			final WebTarget myResource = client.target(
					String.format(URL_PATTERN, apiUrl, queryParams));
			
			final Response response =  myResource
				.request(MediaType.APPLICATION_JSON)
				.header(HEADER_ACCEPT, HEADER_JSON)
				.post(Entity.entity(body, MediaType.APPLICATION_JSON));
			
			if (response.getStatus() == 201) {
				return response.readEntity(clazz);
			}
			
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * @param body Body request
	 * @param queryParams Can extend resource url and add params to the URL
	 * @return Single object created
	 */
	public T doPost(Object body, String queryParams) {
		return doPost(body, queryParams, classType);
	}
	
	/**
	 * @param <Y> runtime return type
	 * @param body Body request
	 * @param queryParams Can extend resource url and add params to the URL
	 * @param clazz Type of the object to be returned
	 * @return Single object updated
	 */
	public <Y> Y doPut(Object body, String queryParams, Class<Y> clazz) {
		try {
			final WebTarget myResource = client.target(
					String.format(URL_PATTERN, apiUrl, queryParams));
			
			final Response response = myResource
				.request(MediaType.APPLICATION_JSON)
				.header(HEADER_ACCEPT, HEADER_JSON)
				.put(Entity.entity(body, MediaType.APPLICATION_JSON));
			
		
			if (response.getStatus() == 200) {
				return response.readEntity(clazz);
			}
			
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * @param body Body request
	 * @param queryParams Can extend resource url and add params to the URL
	 * @return Single object updated
	 */
	public T doPut(Object body, String queryParams) {
		return doPut(body, queryParams, classType);
	}
	
	/**
	 * @param <Y> runtime return type
	 * @param queryParams Can extend resource url and add params to the URL
	 * @param clazz Type of the object to be returned
	 * @return Single object deleted
	 */
	public <Y> Y doDelete(String queryParams, Class<Y> clazz) {
		try {
			final WebTarget myResource = client.target(String.format(
					URL_PATTERN, apiUrl, queryParams));
			
			final Response response = myResource
				.request(MediaType.APPLICATION_JSON)
				.header(HEADER_ACCEPT, HEADER_JSON)
				.delete();
			
			if (response.getStatus() == 200) {
				return response.readEntity(clazz);
			}
			
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		return null;
	}
	
	/**
	 * @param queryParams Can extend resource url and add params to the URL
	 * @return Single object deleted
	 */
	public T doDelete(String queryParams) {
		return doDelete(queryParams, classType);
	}
	
}
