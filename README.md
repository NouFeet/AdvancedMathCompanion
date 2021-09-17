# AdvancedMathCompanion

### What are AdvancedCompanion_1.0 and AdvancedCompanion_2.0 versions in Companion folder?

> The 2.0 version uses stored procedure, while 1.0 not. I created two versions, just for potfolio to show that I have enough skills to do it in the both ways.

### How to run?
- First, the file DBSchema contain the schema of MySQL database. Create the database by it.
- After that, you have to download/add JavaFX-11, JUnit 5 and mysql connector.

- Then, you have to change connection string by providing the valid username, password and host for your database in
> `src/com/nikitakuprins/mathCompanion/datamodel/DataSource.java`

```java 
private static final String CONNECTION_STRING = "jdbc:mysql://HOST/" + SCHEMA_NAME + "?user=USER&password=PASSWORD";
``` 

### What the application does?

- Calculator
- Arithmetic expressions solver
- Adding, editing or deleting list of expressions

### What was used?

- Java 11
- SQL
- JavaFX 11
- JUnit 5
- JDBC

### Previous version of project

[CasualMathCompanion](https://github.com/NouFeet/CasualMathCompanion)
