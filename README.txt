Command to start a mysql db that uses a docker host filesystem (/Users/andre/mysql_ebrm) to store the data
docker run --name ebrmDB -v /Users/andre/mysql_ebrm:/var/lib/mysql -p 3306:3306 -e MYSQL_DATABASE=ebrmDB -e MYSQL_ROOT_PASSWORD=secret -e MYSQL_USER=ebrm -e MYSQL_PASSWORD=jackrabbit -d mysql:latest 
then just use 'docker stop ebrmDB' or 'docker start ebrmDB' to stop / start the DB instance.
To wipe clean the data, just stop the docker instance, remove everything under the /Users/andre/mysql_ebrm folder and then 'docker start ebrmDB' again

Command to start a jackrabbit standalone cli, pointing to a repository.xml on /Users/andre/ebrm_standalone folder:
java -Xmx256m -classpath /Users/andre/mysql_jdbc_driver/mysql-connector-java-5.1.39-bin.jar:jackrabbit-standalone-2.10.1.jar org.apache.jackrabbit.standalone.Main --cli file:///Users/andre/ebrm_standalone

ebrm/repository.xml - jackrabbit repository.xml to be used by wildfly
ebrm_standalone - jackrabbit repository.xml to be used by jackrabbit standalone for the CLI access
wildfly/jackrabbit-jca-2.12.0.rar - RAR file to be deployed on wildfly
wildfly/standalone.xml - wildfly config file, should be stored under <wildfly_home>/standalone/configuration
wildfly/jcr/main/* - custom module definition that needs to be added to wildfly's modules folder (under javax)
wildfly/mysql/main/* - custom module definition for mysql jdbc driver that needs to be added to wildfly's modules folder (under com)