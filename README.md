# AdvancedMathCompanion

### What is AdvancedCompanion_1.0 and AdvancedCompanion_2.0 versions in Companion folder?

> The 2.0 version uses stored procedure, while 1.0 not. I created two versions, just for potfolio to show that I have enough skills to do it in the both ways.

### How to run?
> First, the file DBSchema contain the schema of MySQL database. Create the database by it.
> Second, you have to change connection string in
> `AdvancedMathCompanion/Companion/AdvancedMathCompanion_version/src/com/nikitakuprins/mathCompanion/datamodel/DataSource.java`

```java 
private static final String CONNECTION_STRING = "jdbc:mysql://HOST/" + SCHEMA_NAME + "?user=USER&password=PASSWORD";
```
> Provide the valid username and password for your database 

### What the application does?

- Calculator
- Arithmetic expressions solver
- Adding, editing or deleting list of expressions
