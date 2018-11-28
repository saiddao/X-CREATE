package it.cnr.isti.labse.xcreate.comboTest;
/*
 * Copyright 2004-2005 CSIRO e-Health Research Centre (http://e-hrc.net).
 * All rights reserved. Use is subject to license terms and conditions.
 * $Id: codetemplates.xml,v 1.2 2005/11/15 22:24:01 mcb037 Exp $
 */


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Vector;

public class ComboTester {
    
    public static final String POSTGRES = "org.postgresql.Driver";
    public static final String MYSQL = "com.mysql.jdbc.Driver";
    public static final String SQLSERVER = "net.sourceforge.jtds.jdbc.Driver";
    public static final String MSACCESS = "sun.jdbc.odbc.JdbcOdbcDriver";
    private static final Set<String> DATABASES = new HashSet<String>();
    // FIXME AGGIUNTO DA SAID -- CONTIENE IL RISULTATO -- TUTTE LE COMBINAZIONI
    private Vector<String> combinazioni;
    private Hashtable<String, String[]> combinationsAsHashTable;
    
    static {
      DATABASES.add(POSTGRES);
      DATABASES.add(MYSQL);
      DATABASES.add(SQLSERVER);
      DATABASES.add(MSACCESS);
    };

    private static final Map<String, String> DATABASE_AUTOINCREMENT_TYPE = new HashMap<String, String>();
    static {
      DATABASE_AUTOINCREMENT_TYPE.put(POSTGRES, "serial");
      DATABASE_AUTOINCREMENT_TYPE.put(MYSQL, "integer auto_increment");
      DATABASE_AUTOINCREMENT_TYPE.put(SQLSERVER, "integer identity");
      DATABASE_AUTOINCREMENT_TYPE.put(MSACCESS, "autoincrement");
    }

    private final String url;
    private final String username;
    private final String password;
    private final String driver;
    private final String[] variableNames;
    private final String[][] variables;
    private final int strength;
    //private final BufferedWriter fileWriter;
    
    private String[][] combinations;
    private String[] combinationTableNames;
    private Map<String, Integer> variableMap = new HashMap<String, Integer>();
    private Connection connection;
    
    public ComboTester(final String url, final String username, final String password, final String driver, 
            final String[] variableNames, final String[][] variables, 
            final int strength/*, final String outputFilename*/) throws IOException {
        this.url = url;
        this.username = username;
        this.password = password;
        this.driver = driver;
        this.variableNames = variableNames;
        this.variables = variables;
        this.strength = strength;
        //this.fileWriter = new BufferedWriter(new FileWriter(outputFilename));
        
        // FIXME
        this.combinazioni = new Vector<String>();
        this.combinationsAsHashTable = new Hashtable<String, String[]>();
    }
    // FIXME -- MODIFICATO -- DA void a Vector<String>
    public Vector<String> go() throws SQLException, ClassNotFoundException, IOException {
        if (!DATABASES.contains(driver)) {
            throw new RuntimeException("Unknown driver: " + driver);
        }
        if (strength < 1 || strength > variableNames.length) {
            throw new RuntimeException("Strength must be between 1 and number of variables.");
        }

        Class.forName(driver);
        //System.out.println("caricato driver : "+driver.toString());
        connection = DriverManager.getConnection(url, username, password);
        //System.out.println("effettuata connessione : "+connection.toString());
        connection.setAutoCommit(false);

        createVariableMetaTable();
        createVariableTables();
        createTestCases();
        createCombinations();
        
        // prendere il tempo
        
        /*
        long inizio;
        long fine;
        
        for (int i = combinationTableNames.length; i > combinationTableNames.length/2; i--) {
            inizio = System.currentTimeMillis();
        
            trial(i);
        	
            fine = System.currentTimeMillis();
            System.out.println("Tempo di esecuzione con threshold "+i+" : "+(fine+inizio));
            
            reset();
        }
        
        */
        /*
         * EFFETTUA SOLO UN'ESECUSIONE DELL'ALGORITMO DI GENERAZIONE
         * DELLE COMBINAZIONI PER UN VALORE DI SOGLIA (THRESHOLD)
         * MASSIMO.
         * DA ESPERIMENTI EFFETTUATI PARE CHE QUESTO NUMERO GARANTISCA 
         * -> IL TEMPO DI RIPOSTA MINIMO
         * -> LA CARDINALITA' DELL'INSIEME DELLE COMBINAZIONI MINIMA
         * combinationTableNames.length
         * 
         */
        trial(combinationTableNames.length);
        reset();
       // fileWriter.close();
        dropCombinations();
        dropVariableTables();
        dropTable("variable");
        dropTable("test_cases");

        connection.commit();
        connection.close();
        return this.combinazioni;
    }
    
    private void trial(int startingThreshold) throws IOException, SQLException {
        System.out.println("Threshold = " + startingThreshold);
        //writeFileHeader();
        int totalTestCaseCount = 0;
        int combosLeft = 0;
        for (int i = startingThreshold; i > 0; i--) {
            totalTestCaseCount += run(i);
            combosLeft = getRemainingComboCount();
            if (combosLeft == 0) {
                break;
            }
        }
        
        if (combosLeft > 0) {
            throw new RuntimeException("Error: " + combosLeft + " combinations not accounted for.");
        }
        System.out.println("Total test cases = " + totalTestCaseCount + "\n");
        //fileWriter.write(totalTestCaseCount + " test cases\n\n");
    }

    private void reset() throws SQLException {
        connection.createStatement().execute("update test_cases set match_count = 0");
        for (int i = 0; i < combinationTableNames.length; i++) {
            connection.createStatement().execute("update " + combinationTableNames[i] + " set match_count = 0");
        }
    }
    
    ///////////////////////////////////////////////////////////
    //
    // Create working tables
    //
    ///////////////////////////////////////////////////////////
    /*
    private void writeFileHeader() throws IOException {
        for (int i = 0; i < variableNames.length; i++) {
            if (i > 0) {
               // fileWriter.write("\t");
            }
            //fileWriter.write(variableNames[i]);
        }
        //fileWriter.newLine();
    }
    */
    private void createVariableMetaTable() throws SQLException {
        dropTable("variable");
        connection.createStatement().execute("create table variable (name varchar(100))");
        for (int i = 0; i < variableNames.length; i++) {
            variableMap.put(variableNames[i], new Integer(i));
            connection.createStatement().execute("insert into variable values ('" + variableNames[i] + "')");
        }
    }
    
    private void createVariableTables() throws SQLException {
        for (int i = 0; i < variableNames.length; i++) {
            dropTable(String.valueOf(variableNames[i]));
        	//connection.createStatement().execute("create table " + variableNames[i] + "(var_value varchar(100))");
            connection.createStatement().execute("create table " + variableNames[i] + " (var_value varchar(100))");
            for (int j = 0; j < variables[i].length; j++) {
                connection.createStatement().execute("insert into " + variableNames[i] + " values ('" + variables[i][j] + "')");
            }
        }
    }
    
    private void createTestCases() throws SQLException {
        dropTable("test_cases");

        StringBuffer sb = new StringBuffer("create table test_cases (");
        for (int i = 0; i < variableNames.length; i++) {
            sb.append(variableNames[i] + " varchar(100)");
            if (i < variableNames.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(", match_count integer, pk_col " + DATABASE_AUTOINCREMENT_TYPE.get(driver) + " primary key)");
        connection.createStatement().execute(sb.toString());
        
        if (MSACCESS.equals(driver)) {
            sb = new StringBuffer("insert into test_cases (");
            for (int i = 0; i < variableNames.length; i++) {
                sb.append(variableNames[i] + ", ");
            }
            sb.append("match_count) select ");
            for (int i = 0; i < variableNames.length; i++) {
                sb.append(variableNames[i]);
                sb.append(".var_value, ");
            }
            sb.append("0 from ");
        } else if (MYSQL.equals(driver)) {
            sb = new StringBuffer("insert into test_cases select *, 0, null from ");
        } else {
            sb = new StringBuffer("insert into test_cases select *, 0 from ");
        }
        for (int i = 0; i < variableNames.length; i++) {
            sb.append(variableNames[i]);
            if (i < variableNames.length - 1) {
                sb.append(", ");
            }
        }
        connection.createStatement().execute(sb.toString());
    }
    
    private void createCombinations() throws SQLException {
        StringBuffer selectClause = new StringBuffer("");
        StringBuffer fromClause = new StringBuffer("");
        StringBuffer whereClause = new StringBuffer("");
        StringBuffer orderByClause = new StringBuffer("");
        for (int i = 0; i < strength; i++) {
            if (i > 0) {
                selectClause.append(", ");
                fromClause.append(", ");
                orderByClause.append(", ");
            }
            if (i > 0) {
                whereClause.append(i == 1 ? " where " : " and ");
                whereClause.append("v" + (i-1) + ".name < v" + i + ".name");
            }
            selectClause.append("v" + i + ".name");
            fromClause.append("variable v" + i);
            orderByClause.append("v" + i + ".name");
        }
        
        ResultSet rs = connection.createStatement().executeQuery("select " + selectClause + " from " + fromClause 
                + whereClause + " order by " + orderByClause);
        List<String[]> l = new ArrayList<String[]>();
        while (rs.next()) {
            String[] row = new String[strength];
            for (int i = 0; i < strength; i++) {
                row[i] = rs.getString(i+1);
            }
            l.add(row);
        }
        rs.close();
        
        combinations = new String[l.size()][strength];
        combinationTableNames = new String[l.size()];
        for (int i = 0; i < l.size(); i++) {
            combinations[i] = (String[])l.get(i);
            StringBuffer tableName = new StringBuffer("c_");
            StringBuffer columns = new StringBuffer();
            StringBuffer msaccessSelectClause = new StringBuffer();
            StringBuffer insertClause = new StringBuffer();
            for (int j = 0; j < strength; j++) {
                if (j > 0) {
                    tableName.append("_");
                    columns.append(", ");
                    insertClause.append(", ");
                }
                tableName.append(combinations[i][j]);
                columns.append(combinations[i][j] + " varchar(100)");
                insertClause.append(combinations[i][j]);
                msaccessSelectClause.append(combinations[i][j] + ".var_value, ");
            }
            dropTable(tableName.toString());
            connection.createStatement().execute("create table " + tableName + "(" + columns + ", match_count integer, pk_col " 
                + DATABASE_AUTOINCREMENT_TYPE.get(driver) + " primary key)");
            combinationTableNames[i] = tableName.toString();
            String insertStatement;
            if (MSACCESS.equals(driver)) {
                insertStatement = 
                    "insert into " + tableName + "(" + insertClause + ", match_count) select " + msaccessSelectClause + " 0 from " + insertClause;
            } else if (MYSQL.equals(driver)) {
                insertStatement = "insert into " + tableName + " select *, 0, null from " + insertClause;
            } else {
                insertStatement = "insert into " + tableName + " select *, 0 from " + insertClause;
            }
            connection.createStatement().execute(insertStatement);
        }
    }
    
    /*
     * Look for candidate test cases
     */
    private int run(int threshold) throws SQLException, IOException {
        StringBuffer sb = new StringBuffer("select count(*) from (");
        for (int i = 0; i < combinations.length; i++) {
            if (i > 0) {
                sb.append(" union all ");
            }
            sb.append("select * from " + combinationTableNames[i]);
            for (int j = 0; j < combinations[i].length; j++) {
                sb.append(j == 0 ? " where match_count = 0 and " : " and ");
                sb.append(combinations[i][j] + " = ?");
            }
        }
        sb.append(") subq");
        PreparedStatement stmt = connection.prepareStatement(sb.toString());

        Statement testCasesStmt = connection.createStatement();
        if (MSACCESS.equals(driver)) {
            testCasesStmt = connection.createStatement();
        } else {
            testCasesStmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            testCasesStmt.setFetchSize(1000);
        }
        ResultSet rs = testCasesStmt.executeQuery("select test_cases.* from test_cases where match_count = 0 order by pk_col");
        
        int testCaseCount = 0;
        while (rs.next()) {
            String[] resultSetRow = new String[variableNames.length];
            int paramCount = 1;
            for (int i = 0; i < combinations.length; i++) {
                for (int j = 0; j < combinations[i].length; j++) {
                    int index = ((Integer)variableMap.get(combinations[i][j])).intValue();
                    String val = resultSetRow[index];
                    if (val == null) {
                      val = rs.getString(index + 1);
                      resultSetRow[index] = val;
                    }
                    stmt.setString(paramCount++, val);
                }
            }
            
            ResultSet rs2 = stmt.executeQuery();
            rs2.next();
            int matchCount = rs2.getInt(1);
            rs2.close();
            
            if (matchCount >= threshold) {
                if (MSACCESS.equals(driver)) {
                    int pkVal = rs.getInt(variableNames.length + 2);
                    connection.createStatement().execute("update test_cases set match_count = " + matchCount + " where pk_col = " + pkVal);
                } else {
                    rs.updateInt(variableNames.length + 1, matchCount);
                    rs.updateRow();
                }
                testCaseCount++;
                
                boolean[] used = new boolean[variables.length];
                
                for (int i = 0; i < combinations.length; i++) {
                    sb = new StringBuffer("select match_count, pk_col from " + combinationTableNames[i]);
                    for (int j = 0; j < combinations[i].length; j++) {
                        sb.append(j == 0 ? " where " : " and ");
                        sb.append(combinations[i][j] + " = ?");
                    }
                    PreparedStatement selectStmt = connection.prepareStatement(sb.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                    int selectParamCount = 1;
                    for (int j = 0; j < combinations[i].length; j++) {
                        int index = ((Integer)variableMap.get(combinations[i][j])).intValue();
                        selectStmt.setString(selectParamCount++, resultSetRow[index]);
                    }
                    ResultSet comboRS = selectStmt.executeQuery();
                    comboRS.next();
                    int oldMatchCount = comboRS.getInt(1);
                    if (oldMatchCount == 0) {
                        for (int j = 0; j < combinations[i].length; j++) {
                            int index = ((Integer)variableMap.get(combinations[i][j])).intValue();
                            used[index] = true;
                        }
                    }
                    if (MSACCESS.equals(driver)) {
                        int pkVal = comboRS.getInt(2);
                        connection.createStatement().execute(
                            "update " + combinationTableNames[i] + " set match_count = " + (oldMatchCount + 1) + " where pk_col = " + pkVal);
                    } else {
                        comboRS.updateInt(1, oldMatchCount + 1);
                        comboRS.updateRow();
                    }
                    comboRS.close();
                }
                //FIXME Said ..
                String[] stringForHashTable = new String[variableNames.length]; 
                

                StringBuffer row = new StringBuffer();
                for (int i = 0; i < variableNames.length; i++) {
                    if (i > 0) {
                        row.append("\t");
                    }
                    /* FIXME COMMEMTATO DA SAID
                     * non vi e' la necessita' di marcare le tuple 
                     * utilizzate piu' volte
                     * 
                    if (!used[i]) {
                        row.append("~");
                    }
                    */
                    stringForHashTable[i] = resultSetRow[i];
                    row.append(resultSetRow[i]);
                }
                
                // FIXME said .. AGGANCIARE UNA CODA
                //fileWriter.write(row.toString());
                //fileWriter.newLine();
          //      System.out.println(row + " => " + matchCount);
             // FIXME said .. 
                this.combinazioni.add(row.toString());
                this.combinationsAsHashTable.put(row.toString(), stringForHashTable);
                
            }
        }
        rs.close();
        return testCaseCount;
    }
    
    private int getRemainingComboCount() throws SQLException {
        int total = 0;
        for (int i = 0; i < combinationTableNames.length; i++) {
            ResultSet resultSet = connection.createStatement().executeQuery(
                    "select count(*) from " + combinationTableNames[i] + " where match_count = 0");
            resultSet.next();
            
            int count = resultSet.getInt(1);
          //  System.out.println("\n\nselect count(*) "+count+"\nfrom " + combinationTableNames[i] + " \nwhere match_count = 0");
            resultSet.close();
            total += count;
        }
        return total;
    }
    

    ////////////////////////////////////////////////////////
    //
    // Drop working tables
    //
    ////////////////////////////////////////////////////////
    
    private void dropCombinations() throws SQLException {
        for (int i = 0; i < combinationTableNames.length; i++) {
            dropTable(combinationTableNames[i]);
        }
    }
    
    private void dropVariableTables() throws SQLException {
        for (int i = 0; i < variableNames.length; i++) {
            dropTable(variableNames[i]);
        }        
    }

    private void dropTable(String name) throws SQLException {
    	if (POSTGRES.equals(driver)) {
            connection.createStatement().execute("drop table if exists " + name);
        } else {
            try {
                connection.createStatement().execute("drop table " + name);
                //System.out.println("drop : "+ name);
            } catch (SQLException e) {
                //ignore
            	//System.out.println("drop Exception");
            }
        }
    }

    private void printCombos() {
        for (int i = 0; i < combinations.length; i++) {
            for (int j = 0; j < combinations[i].length; j++) {
                System.out.print(combinations[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("total = " + combinations.length);
    }

    
    /*
     * FIXME Aggiunto da said 
     * Retituisce il risultato come hashTable
     */
    public Hashtable<String, String[]> getCombinationsAsHashTable(){
    	return this.combinationsAsHashTable;
    }
}
