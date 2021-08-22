package io.degeus.recipeappapi.db;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * To run:
 * <ul><li>
 *  <pre><code>
 *  docker run --rm -p5432:5432 --name recipedb-postgres -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=recipedb postgres
 *  </code></pre>
 *  </li>
 * </ul>
 */
public class DatabaseAdmin {

    public static final String LIQUIBASE_CHANGELOG_MASTER_LOCATION = "db/changelog/changelog-master.xml";
    public static final String LIQUIBASE_CHANGELOG_IAM_LOCATION = "db/changelog/changelog-iam.xml";
    public static final String LIQUIBASE_CHANGELOG_RCP_LOCATION = "db/changelog/changelog-rcp.xml";

    public static final String SCHEMA_JDBC_URL = "SCHEMA_JDBC_URL";
    public static final String SCHEMA_JDBC_USER = "SCHEMA_JDBC_USER";
    public static final String SCHEMA_JDBC_PASSWORD = "SCHEMA_JDBC_PASSWORD";
    public static final String LIQUIBASE_SCHEMA_CHANGELOG_TABLENAME = "LIQUIBASE_SCHEMA_CHANGELOG_TABLENAME";
    public static final String LIQUIBASE_SCHEMA_CHANGELOGLOCK_TABLENAME = "LIQUIBASE_SCHEMA_CHANGELOGLOCK_TABLENAME";
    public static final String DATABASE_PROPERTIES_FILE = "database.properties";

    public static void main(String[] args) throws LiquibaseException, SQLException, IOException {

        Properties databaseProperties = loadDatabaseProperties(DATABASE_PROPERTIES_FILE);
        doUpdateMasterChanges(databaseProperties);
        doUpdateSchema("iam", LIQUIBASE_CHANGELOG_IAM_LOCATION, databaseProperties);
        //doUpdateSchema("rcp", LIQUIBASE_CHANGELOG_RCP_LOCATION, databaseProperties);
    }

    private static void doUpdateSchema(String schema, String changelogFile, Properties databaseProperties) throws LiquibaseException, SQLException  {

        System.out.println("Updating " + schema + " schema " + " with changelogfile " + changelogFile + "...");
        final String databaseUrl = databaseProperties.getProperty(SCHEMA_JDBC_URL.replace("SCHEMA", schema.toUpperCase()));
        final String databaseUsername = databaseProperties.getProperty(SCHEMA_JDBC_USER.replace("SCHEMA", schema.toUpperCase()));
        final String databasePassword = databaseProperties.getProperty(SCHEMA_JDBC_PASSWORD.replace("SCHEMA", schema.toUpperCase()));

        System.out.println("Opening connection for " + schema + " static data changes..");
        System.out.println("Using databaseUrl: " + databaseUrl);
        System.out.println("Using databaseUsername: " + databaseUsername);
        System.out.println("Using databasePassword: " + (databasePassword == null ? "null!" : databasePassword.substring(0, Math.min(3,databasePassword.length()))));

        Properties props = new Properties();
        props.setProperty("user", databaseUsername);
        props.setProperty("password", databasePassword);
        Connection connection = DriverManager.getConnection(databaseUrl, props);

        final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        System.out.println("Opening connection for changes - done.");

        final String databaseChangeLogTableName = databaseProperties.getProperty(LIQUIBASE_SCHEMA_CHANGELOG_TABLENAME.replace("SCHEMA", schema.toUpperCase()));
        final String databaseChangeLogLockTableName = databaseProperties.getProperty(LIQUIBASE_SCHEMA_CHANGELOGLOCK_TABLENAME.replace("SCHEMA", schema.toUpperCase()));
        database.setDefaultSchemaName(schema.toUpperCase());
        database.setDatabaseChangeLogTableName(databaseChangeLogTableName);
        database.setDatabaseChangeLogLockTableName(databaseChangeLogLockTableName);

        final Liquibase liquibase = new Liquibase(
                changelogFile,
                new ClassLoaderResourceAccessor(), database);

        injectAllEnvironmentVariables(liquibase, databaseProperties);

        System.out.println("Applying changeset..");
        liquibase.update(new Contexts(), new LabelExpression());
        System.out.println("Applying changeset - done.");

        System.out.println("Done - Updating " + schema + " schema " + " with changelogfile " + changelogFile + "...");
    }

    private static Properties loadDatabaseProperties(String env) throws IOException {

        Properties prop = new Properties();
        try (InputStream input = ClassLoader.getSystemResourceAsStream(env)){
            prop.load(input);
            Map<String, String> environmentVars = System.getenv();
            prop.putAll(environmentVars); //insert (and thus overrides if already present)
            return prop;
        }
    }


    /** changes the master */
    private static void doUpdateMasterChanges(Properties databaseProperties) throws SQLException, LiquibaseException {

        final String databaseUrl = databaseProperties.getProperty("MASTER_JDBC_URL");
        final String databaseUsername = databaseProperties.getProperty("MASTER_JDBC_USER");
        final String databasePassword = databaseProperties.getProperty("MASTER_JDBC_PASSWORD");
        final String dbName = databaseProperties.getProperty("DB_NAME");

        System.out.println("Opening connection for master changes..");
        System.out.println("Using databaseUrl: " + databaseUrl);
        System.out.println("Using databaseUsername: " + databaseUsername);
        System.out.println("Using databasePassword: " + (databasePassword == null ? "null!" : databasePassword.substring(0, Math.min(3,databasePassword.length()))));
        System.out.println("Using DB_NAME: " + dbName);

        Properties props = new Properties();
        props.setProperty("user", databaseUsername);
        props.setProperty("password", databasePassword);
        props.setProperty("DB_NAME", dbName);
        Connection connection = DriverManager.getConnection(databaseUrl, props);

        final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        System.out.println("Opening connection for master changes - done.");

        final String databaseChangeLogTableName = databaseProperties.getProperty("MASTER_DATABASE_CHANGE_LOG_TABLE_NAME");
        final String databaseChangeLogLockTableName = databaseProperties.getProperty("MASTER_DATABASE_CHANGE_LOG_LOCK_TABLE_NAME");
        database.setDefaultSchemaName("public"); //shared and available at t = 0 (thus before creating a schema with liquibase)
        database.setDatabaseChangeLogTableName(databaseChangeLogTableName);
        database.setDatabaseChangeLogLockTableName(databaseChangeLogLockTableName);

        final Liquibase liquibase = new Liquibase(
                LIQUIBASE_CHANGELOG_MASTER_LOCATION,
                new ClassLoaderResourceAccessor(), database);

        injectAllEnvironmentVariables(liquibase,databaseProperties);

        System.out.println("Applying changeset..");
        liquibase.update(new Contexts(), new LabelExpression());
        System.out.println("Applying changeset - done.");

        System.out.println("finished updating master database.");
    }

    /** injects all properties into the liquibase' changelogparameter context with same key, value */
    private static void injectAllEnvironmentVariables(final Liquibase liquibase, Properties allProps) {
        allProps.forEach((key, value) -> {
            String keyAsStr = key.toString();
            String valueAsStr = value.toString();
            liquibase.setChangeLogParameter(keyAsStr, value);
            if (keyAsStr.contains("_MGR_PASSWORD") || keyAsStr.contains("_APP_PASSWORD")) {
                System.out.println("Using _MGR_PASSWORD: [" + keyAsStr + "] with value [" +  valueAsStr.substring(0, Math.min(3,valueAsStr.length())) + "***]");
                System.out.println("Using _PASSWORD: ["  + keyAsStr + "] with value [" +   valueAsStr.substring(0, Math.min(3,valueAsStr.length()))+ "***]");
            }
        });
    }

}
