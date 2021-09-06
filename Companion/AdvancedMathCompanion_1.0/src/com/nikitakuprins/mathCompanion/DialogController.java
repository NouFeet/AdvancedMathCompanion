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
package com.nikitakuprins.mathCompanion;

import com.nikitakuprins.mathCompanion.datamodel.DataSource;
import com.nikitakuprins.mathCompanion.datamodel.Expression;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DialogController {

    @FXML
    private TextField expressionField;

    public TextField getExpressionField() {
        return expressionField;
    }

    public void setExpressionField(String str) {
        this.expressionField.setText(str);
    }

    public Expression processItem() {
        String expression = expressionField.getText();

        if (Calculations.isValidFormat(expression)) {
            int typeId = Calculations.getExpressionTypeId(expression);
            DataSource instance = DataSource.getInstance();
            BigDecimal answer = Calculations.calculate(expression);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Date date = Date.valueOf(LocalDateTime.now().format(dateTimeFormatter));

            Expression newExpression = new Expression();
            newExpression.setExpression(expression);
            newExpression.setAnswer(answer);
            newExpression.setDate(date);
            newExpression.setTypeId(typeId);
            newExpression.setType(instance.queryTypeById(typeId));
            newExpression.setComplexity(instance.queryComplexityByTypeId(typeId));
            return newExpression;
        }
        return null;
    }
}
