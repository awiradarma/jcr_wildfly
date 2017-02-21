package andre.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.apache.jackrabbit.JcrConstants;

@Path("terms")
public class NodeResource {
	
	@Resource(lookup = "java:/MySqlDS")
	private DataSource ds;

	{
		// Initialize the repository
		Context ctx;
        Session session = null;
        Node root;
		try {
			ctx = new InitialContext();
			Repository repository = (Repository) ctx.lookup("java:/jca/DocumentStore") ;
			session = repository.login( 
					new SimpleCredentials("admin", "admin".toCharArray())); 
			root = session.getRootNode();
			Node terms = root.getNode("terms");
		}
		catch (PathNotFoundException e) {
			try {
				session.getRootNode().addNode("terms");
				session.save();
			} catch (ItemExistsException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (PathNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (VersionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ConstraintViolationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (LockException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (RepositoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			session.logout();
		}
	}
	
	@GET
	@Produces("application/json")
	public Response getNodes() throws Exception {
		List<BusinessTerm> terms = new ArrayList<BusinessTerm>();
		
		Context ctx;
        Session session = null;
		try {
			ctx = new InitialContext();
			Repository repository = (Repository) ctx.lookup("java:/jca/DocumentStore") ;
			session = repository.login( 
					new SimpleCredentials("admin", "admin".toCharArray())); 
			VersionManager vm = session.getWorkspace().getVersionManager();
			Version v;
			Node rootTerms = session.getRootNode().getNode("terms");
			
			NodeIterator nodes = rootTerms.getNodes();
			for (; nodes.hasNext();) {
				Node child = nodes.nextNode();
				System.out.println(child.getName());
				//v = vm.getBaseVersion(child.getPath());
				BusinessTerm term = new BusinessTerm();
				term.setTermID(child.getName());
				//term.setDescription(v.getFrozenNode().getProperty("value").getString());
				term.setDescription(child.getProperty("value").getString());
				terms.add(term);
			}				
		} catch (Exception e) {
			e.printStackTrace();
		} finally { 
			if (session != null) {
				session.logout(); 
			} 
		}
		
		GenericEntity<List<BusinessTerm>> list = new GenericEntity<List<BusinessTerm>> (terms) {};
		return Response.ok(list).build();
	}
	
	@GET
	@Path("{termID}/")
	@Produces("application/json")
	public BusinessTerm getNode(@PathParam("termID") String termID) throws Exception {
		
//		String result = fetchFromDB(termID);
		
		Context ctx;
        Session session = null;
        String description = "NOT FOUND";
		try {
			ctx = new InitialContext();
			Repository repository = (Repository) ctx.lookup("java:/jca/DocumentStore") ;
			session = repository.login( 
					new SimpleCredentials("admin", "admin".toCharArray())); 
			VersionManager vm = session.getWorkspace().getVersionManager();
			Version v;
			Node rootTerms = session.getRootNode().getNode("terms");
			//String output = "";
			System.out.println(">>>> RETRIEVE " + termID);
			Node child;
			child = rootTerms.getNode(termID);
			//v = vm.getBaseVersion(child.getPath());
			description = child.getProperty("value").getString();
			//description = v.getFrozenNode().getProperty("value").getString();
			VersionIterator vi = vm.getVersionHistory(child.getPath()).getAllLinearVersions();
			if (vi.hasNext()) vi.skip(1); // skip the jcr:rootVersion
			while (vi.hasNext()) {
				Version ver = vi.nextVersion();
				//System.out.println("Name: " + ver.getName() + ", Identifier: " + ver.getIdentifier() + ", jcr:lastModified: " + child.getProperty("jcr:lastModified").getString());						
				System.out.println("Name: " + ver.getName() + ", Identifier: " + ver.getIdentifier() + ", Value: " + ver.getFrozenNode().getProperty("value").getString() + ", frozen_jcr:created: " + ver.getFrozenNode().getProperty("jcr:created").getString() + ", frozen_lastModified: " + ver.getFrozenNode().getProperty("jcr:lastModified").getString() + ", jcr:lastModified: " + child.getProperty("jcr:lastModified").getString());						
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally { 
			if (session != null) {
				session.logout(); 
			} 
		}
		
		BusinessTerm term = new BusinessTerm();
		term.setDescription(description);
		term.setTermID(termID);
	
		return term;
	}

//	private String fetchFromDB(String termID) throws SQLException {
//		Connection c = ds.getConnection();
//		PreparedStatement p = c.prepareStatement("select description from terms where id = ?");
//		p.setString(1, termID);
//		ResultSet rs = p.executeQuery();
//		String result;
//		if (rs.next()) {
//			result = rs.getString("description");
//		} else {
//			result = "not found!!";
//		}
//		c.close();
//		return result;
//	}
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response createProductInJSON(BusinessTerm term) {

		
		Context ctx;
        Session session = null;
//        String description = null;
		try {
			ctx = new InitialContext();
			Repository repository = (Repository) ctx.lookup("java:/jca/DocumentStore") ;
			session = repository.login( 
					new SimpleCredentials("admin", "admin".toCharArray())); 
			VersionManager vm = session.getWorkspace().getVersionManager();
			Version v;
			Node rootTerms = session.getRootNode().getNode("terms");
			Node child;
			try {
				child = rootTerms.getNode(term.getTermID());
				System.out.println(">>>>> UPDATE " + term.getTermID());
				vm.checkpoint(child.getPath());
				//output = "Updated node " + nodeName;
				child.setProperty("value", term.getDescription());
				child.setProperty("jcr:lastModified",Calendar.getInstance());
				session.save();
				v = vm.checkin(child.getPath());
				System.out.println("UPDATED successfully");
			} catch (PathNotFoundException e) {
				System.out.println(">>>>>> INSERT " + term.getTermID());
				child = rootTerms.addNode(term.getTermID());
				child.addMixin(JcrConstants.MIX_VERSIONABLE);
				child.addMixin("mix:created");
				child.addMixin("mix:lastModified");
				//output = "Created node " + nodeName;
				session.save();
				vm.checkout(child.getPath());
				child.setProperty("value", term.getDescription());
				session.save();
				v = vm.checkin(child.getPath());
				System.out.println("INSERTED successfully");
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally { 
			if (session != null) {
				session.logout(); 
			} 
		}

		
		//String result = "Term created : " + term.toString();
		return Response.status(201).entity(term).build();

	}

}
