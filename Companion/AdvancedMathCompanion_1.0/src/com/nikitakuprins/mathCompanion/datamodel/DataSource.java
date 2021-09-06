/*
 * Copyright 2021 Nikita Kuprins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nikitakuprins.mathCompanion.datamodel;

import com.nikitakuprins.mathCompanion.Calculations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DataSource {

    private static final String SCHEMA_NAME = "calculator";
    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/" + SCHEMA_NAME + "?user=root&password=p6zinamu";

    private static final String TABLE_EXPRESSIONS = "expressions";
    private static final String COLUMN_EXPRESSIONS_ID = "expression_id";
    private static final String COLUMN_EXPRESSIONS_EXPRESSION = "expression";
    private static final String COLUMN_EXPRESSIONS_ANSWER = "answer";
    private static final String COLUMN_EXPRESSIONS_DATE = "date";
    private static final String COLUMN_EXPRESSIONS_TYPE = "type";

    private static final String TABLE_TYPES = "types";
    private static final String COLUMN_TYPES_ID = "type_id";
    private static final String COLUMN_TYPES_TYPE = "type";
    private static final String COLUMN_TYPES_COMPLEXITY = "complexity";

    private static final String TABLE_COMPLEXITIES = "complexities";
    private static final String COLUMN_COMPLEXITIES_ID = "complexity_id";
    private static final String COLUMN_COMPLEXITIES_NAME = "complexity_name";
    private static final String COLUMN_COMPLEXITIES_DESCRIPTION = "description";

    private static final String EXPRESSIONS_VIEW = "expression_list";

    private static final String CREATE_VIEW_FOR_EXPRESSIONS = "CREATE OR REPLACE VIEW " + EXPRESSIONS_VIEW + " AS (" +
            "SELECT " + COLUMN_EXPRESSIONS_ID + ", " + COLUMN_EXPRESSIONS_EXPRESSION +
            ", " + COLUMN_EXPRESSIONS_ANSWER + ", " + COLUMN_EXPRESSIONS_DATE + ", " +
            TABLE_TYPES + '.' + COLUMN_TYPES_TYPE + ", " + TABLE_COMPLEXITIES + '.' + COLUMN_COMPLEXITIES_NAME +
            ", " + TABLE_COMPLEXITIES + '.' + COLUMN_COMPLEXITIES_DESCRIPTION +
            " FROM " + TABLE_EXPRESSIONS +
            " INNER JOIN " + TABLE_TYPES + " ON " + TABLE_TYPES + '.' + COLUMN_TYPES_ID + " = " + TABLE_EXPRESSIONS +
            '.' + COLUMN_EXPRESSIONS_TYPE +
            " INNER JOIN " + TABLE_COMPLEXITIES + " ON " + TABLE_TYPES + '.' + COLUMN_TYPES_COMPLEXITY + " = " + TABLE_COMPLEXITIES +
            '.' + COLUMN_COMPLEXITIES_ID +
            " ORDER BY " + COLUMN_EXPRESSIONS_ID + ')';

    private static final String QUERY_VIEW_FOR_EXPRESSIONS = "SELECT *" + " FROM " + EXPRESSIONS_VIEW;

    private static final String QUERY_COMPLEXITY_BY_TYPE_ID = "SELECT " + COLUMN_COMPLEXITIES_NAME + ", " +
            COLUMN_COMPLEXITIES_DESCRIPTION + " FROM " + TABLE_COMPLEXITIES + " INNER JOIN " +
            TABLE_TYPES + " ON " + COLUMN_TYPES_COMPLEXITY + " = " + COLUMN_COMPLEXITIES_ID +
            " WHERE " + COLUMN_TYPES_ID + " = ?";

    private static final String QUERY_TYPE_BY_ID = "SELECT " + COLUMN_TYPES_TYPE + " FROM " + TABLE_TYPES +
            " WHERE " + COLUMN_TYPES_ID + " = ?";
    private static final String INSERT_EXPRESSION = "INSERT INTO " + TABLE_EXPRESSIONS + " VALUES " +
            "( DEFAULT, ?, ?, ? )";

    private static final String DELETE_EXPRESSION = "DELETE " + TABLE_EXPRESSIONS +
            " FROM " + TABLE_EXPRESSIONS +
            " WHERE " + TABLE_EXPRESSIONS + '.' + COLUMN_EXPRESSIONS_ID + " = ?";

    private static final String UPDATE_EXPRESSION = "UPDATE " + TABLE_EXPRESSIONS + " SET " +
            COLUMN_EXPRESSIONS_EXPRESSION + " = ?" + ", " + COLUMN_EXPRESSIONS_ANSWER + " = ?" +
            " WHERE " + COLUMN_EXPRESSIONS_ID + " = ?";


    private PreparedStatement queryComplexityByTypeId;
    private PreparedStatement queryTypeByTypeId;
    private PreparedStatement insertIntoExpression;
    private PreparedStatement deleteExpression;
    private PreparedStatement updateExpression;

    private Connection conn;
    private static final DataSource instance = new DataSource();
    private final ObservableList<Expression> dataItems = FXCollections.observableArrayList();

    public static DataSource getInstance() {
        return instance;
    }
    public ObservableList<Expression> getDataItems() {
        return dataItems;
    }

    private int getIndexOfItem(int id) {
        for (int i = 0; i < dataItems.size(); i++) {
            if (dataItems.get(i).getPrimaryKey() == id) {
                return i;
            }
        }
        return -1;
    }

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            queryComplexityByTypeId = conn.prepareStatement(QUERY_COMPLEXITY_BY_TYPE_ID);
            insertIntoExpression = conn.prepareStatement(INSERT_EXPRESSION, Statement.RETURN_GENERATED_KEYS);
            queryTypeByTypeId = conn.prepareStatement(QUERY_TYPE_BY_ID);
            deleteExpression = conn.prepareStatement(DELETE_EXPRESSION);
            updateExpression = conn.prepareStatement(UPDATE_EXPRESSION);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        try {
            PreparedStatement[] preparedStatements = new PreparedStatement[] {
                    queryComplexityByTypeId, insertIntoExpression,
                    queryTypeByTypeId, deleteExpression, updateExpression
            };
            for (PreparedStatement p : preparedStatements) {
                if (p != null) {
                    p.close();
                }
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createViewForExpressions() {
        try(Statement statement = conn.createStatement()) {
            statement.execute(CREATE_VIEW_FOR_EXPRESSIONS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void queryViewForExpressions() {
        try(Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(QUERY_VIEW_FOR_EXPRESSIONS)) {
            while (resultSet.next()) {
                Expression expression = new Expression();
                expression.setPrimaryKey(resultSet.getInt(1));
                expression.setExpression(resultSet.getString(2));
                expression.setAnswer(resultSet.getBigDecimal(3));
                expression.setDate(resultSet.getDate(4));
                expression.setType(resultSet.getString(5));
                expression.setComplexity(resultSet.getString(6) + " | " + resultSet.getString(7));
                dataItems.add(expression);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String queryComplexityByTypeId(int id) {
        try {
            queryComplexityByTypeId.setInt(1, id);
            ResultSet resultSet = queryComplexityByTypeId.executeQuery();
            StringBuilder sb = new StringBuilder();
            if (resultSet.next()) {
                sb.append(resultSet.getString(1));
                sb.append(" | ");
                sb.append(resultSet.getString(2));
            }
            return sb.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String queryTypeById(int id) {
        try {
            queryTypeByTypeId.setInt(1, id);
            ResultSet resultSet = queryTypeByTypeId.executeQuery();

            String str = "";
            if (resultSet.next()) {
                str = resultSet.getString(1);
            }
            return str;
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }


    public void insertExpression(Expression expression) {

        try {
            conn.setAutoCommit(false);

            insertIntoExpression.setString(1, expression.getExpression());
            insertIntoExpression.setBigDecimal(2, expression.getAnswer());
            insertIntoExpression.setDate(3, expression.getDate());

            int affectedRows = insertIntoExpression.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert expression");
            }

            ResultSet generatedKeys = insertIntoExpression.getGeneratedKeys();
            int key;
            if (generatedKeys.next()) {
                key = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Couldn't get id ");
            }

            dataItems.add(expression);

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Couldn't reset auto-commit! " + e.getMessage());
            }
        }
    }

    public void deleteExpression(int id) {
        try {
            deleteExpression.setInt(1, id);

            if (deleteExpression.executeUpdate() != 2) {
                throw new SQLException("Delete failed to execute");
            } else {
                dataItems.remove(getIndexOfItem(id));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateExpression(Expression oldItem, Expression newItem) {
        try {
            int id = oldItem.getPrimaryKey();
            newItem.setPrimaryKey(id);
            updateExpression.setString(1, newItem.getExpression());
            updateExpression.setBigDecimal(2, newItem.getAnswer());
            updateExpression.setInt(3, id);

            if (updateExpression.executeUpdate() != 1) {
                throw new SQLException("Couldn't update the row");
            } else {
                dataItems.set(getIndexOfItem(id), newItem);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
