package andre.test;

import java.io.File;

import org.apache.jackrabbit.core.RepositoryCopier;
import org.apache.jackrabbit.core.config.RepositoryConfig;

public class RepoCopier {

	public static void main(String[] args) {
		try {
		RepositoryConfig source = RepositoryConfig.install(new File("/Users/andre/ebrm_backup"));
		System.out.println(source.toString());
		RepositoryConfig target = RepositoryConfig.install(new File("/Users/andre/ebrm_standalone"));
		RepositoryCopier.copy(source, target);
		// TODO Auto-generated method stub
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
