/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.accounting.budget.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.fineract.accounting.budget.api.BudgetJsonInputParams;
import org.apache.fineract.accounting.budget.domain.Budget;
import org.apache.fineract.accounting.glaccount.api.GLAccountJsonInputParams;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.domain.GLAccountType;
import org.apache.fineract.accounting.glaccount.domain.GLAccountUsage;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;

/**
 * Immutable command for adding a general Ledger Account
 */
public class BudgetCommand {

    @SuppressWarnings("unused")

    private final Long id;
    private final BigDecimal minValue;
    private final BigDecimal maxValue;
    private final Long accountId;
    private final Boolean disabled;
  
   



	public BudgetCommand(Long id, BigDecimal minValue, BigDecimal maxValue, Long accountId,Boolean disabled
			) {
		
		this.id = id;
		this.minValue = minValue;		
		this.maxValue = maxValue;
		this.accountId = accountId;
		this.disabled=disabled;
	
	}

	public void validateForCreate() {

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("Budget");

        baseDataValidator.reset().parameter(BudgetJsonInputParams.MAX_AMOUNT.getValue()).value(this.maxValue).notBlank().notLessThanMin(0);

        baseDataValidator.reset().parameter(BudgetJsonInputParams.MIN_AMOUNT.getValue()).value(this.minValue).notBlank().notLessThanMin(0)
                .notExceedingLengthOf(45);
        
        baseDataValidator.reset().parameter(BudgetJsonInputParams.DISABLED.getValue()).value(this.disabled).notBlank().notNull().validateForBooleanValue();
        baseDataValidator.reset().parameter(BudgetJsonInputParams.ACCOUNT_ID.getValue()).value(this.accountId).ignoreIfNull()
                .integerGreaterThanZero();


        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    public void validateForUpdate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("GLAccount");

        baseDataValidator.reset().parameter(BudgetJsonInputParams.MAX_AMOUNT.getValue()).value(this.maxValue).notBlank().notLessThanMin(0);

        baseDataValidator.reset().parameter(BudgetJsonInputParams.MIN_AMOUNT.getValue()).value(this.minValue).notBlank().notLessThanMin(0)
                .notExceedingLengthOf(45);

        baseDataValidator.reset().parameter(BudgetJsonInputParams.ACCOUNT_ID.getValue()).value(this.accountId).ignoreIfNull()
                .integerGreaterThanZero();
        baseDataValidator.reset().anyOfNotNull(this.accountId, this.maxValue, this.minValue);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }

    }

  }

