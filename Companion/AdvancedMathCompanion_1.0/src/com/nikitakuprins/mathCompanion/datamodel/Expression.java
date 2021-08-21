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

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.sql.Date;

public class Expression implements ExpressionTypes, Complexities {
    private final SimpleIntegerProperty primaryKey;
    private final SimpleStringProperty expression;
    private final SimpleObjectProperty<BigDecimal> answer;
    private final SimpleObjectProperty<Date> date;

    private final SimpleStringProperty type;

    private final SimpleStringProperty complexity;

    public Expression() {
        this.primaryKey = new SimpleIntegerProperty();
        this.expression = new SimpleStringProperty();
        this.answer = new SimpleObjectProperty<>();
        this.date = new SimpleObjectProperty<>();
        this.type = new SimpleStringProperty();
        this.complexity = new SimpleStringProperty();
    }

    public int getPrimaryKey() {
        return primaryKey.get();
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey.set(primaryKey);
    }

    public String getExpression() {
        return expression.get();
    }

    public SimpleStringProperty expressionProperty() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression.set(expression);
    }

    public BigDecimal getAnswer() {
        return answer.get();
    }

    public SimpleObjectProperty<BigDecimal> answerProperty() {
        return answer;
    }

    public void setAnswer(BigDecimal answer) {
        this.answer.set(answer);
    }

    public SimpleObjectProperty<Date> dateProperty() {
        return date;
    }

    public Date getDate() {
        return date.get();
    }

    public void setDate(Date date) {
        this.date.set(date);
    }

    @Override
    public SimpleStringProperty typeProperty() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type.set(type);
    }

    @Override
    public SimpleStringProperty complexityProperty() {
        return complexity;
    }

    @Override
    public void setComplexity(String complexity) {
        this.complexity.set(complexity);
    }
}
