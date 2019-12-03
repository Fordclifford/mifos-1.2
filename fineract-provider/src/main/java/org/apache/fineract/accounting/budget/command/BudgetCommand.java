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
import org.apache.fineract.accounting.journalentry.api.JournalEntryJsonInputParams;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.joda.time.LocalDate;

/**
 * Immutable command for adding a general Ledger Account
 */
public class BudgetCommand {

    @SuppressWarnings("unused")

    private final Long id;
    private final BigDecimal amount;
    private final Long expenseAccountId;
    private final Long liabilityAccountId;
    private final Long assetAccountId;
    private final Boolean disabled;
    private final String description;
    private final String name;
    
    private final LocalDate fromDate;
    private final LocalDate toDate;
	private LocalDate createdate;
	private Long year;

    
    public BudgetCommand(Long id,BigDecimal amount, Long expenseAccountId, Long liabilityAccountId,
			Long assetAccountId, Boolean disabled, String description, String name, LocalDate fromDate, LocalDate toDate,LocalDate createDate,Long year) {
		
		this.amount = amount;
		this.id=id;
		this.expenseAccountId = expenseAccountId;
		this.liabilityAccountId = liabilityAccountId;
		this.assetAccountId = assetAccountId;
		this.disabled = disabled;
		this.description = description;
		this.name = name;
		this.fromDate = fromDate;
		this.createdate = createDate;
		this.toDate = toDate;
		this.year=year;
	}    
    

	public void validateForCreate() {

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("Budget");

        baseDataValidator.reset().parameter(BudgetJsonInputParams.AMOUNT.getValue()).value(this.amount).notBlank().notLessThanMin(0);

        baseDataValidator.reset().parameter(BudgetJsonInputParams.NAME.getValue()).value(this.name).notBlank().notLessThanMin(0)
                .notExceedingLengthOf(45);
        baseDataValidator.reset().parameter("fromDate").value(this.fromDate).notBlank();
        baseDataValidator.reset().parameter("disabled").value(this.disabled).notBlank();

        baseDataValidator.reset().parameter("toDate").value(this.toDate).notBlank();
        baseDataValidator.reset().parameter("createdate").value(this.createdate).notBlank();
        
        baseDataValidator.reset().parameter("description").value(this.description).ignoreIfNull().notExceedingLengthOf(500);

        
      //  baseDataValidator.reset().parameter(BudgetJsonInputParams.DISABLED.getValue()).value(this.disabled).notBlank();
        baseDataValidator.reset().parameter(BudgetJsonInputParams.LIABILITY_ACCOUNT_ID.getValue()).value(this.liabilityAccountId).notBlank()
                .integerGreaterThanZero();
        
        baseDataValidator.reset().parameter(BudgetJsonInputParams.YEAR.getValue()).value(this.year).notBlank()
        .integerGreaterThanZero();
        baseDataValidator.reset().parameter(BudgetJsonInputParams.EXPENSE_ACCOUNT_ID.getValue()).value(this.expenseAccountId).notBlank()
        .integerGreaterThanZero();
        baseDataValidator.reset().parameter(BudgetJsonInputParams.ASSET_ACCOUNT_ID.getValue()).value(this.assetAccountId).notBlank()
        .integerGreaterThanZero();

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    

	public void validateForUpdate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("Budget");
        baseDataValidator.reset().parameter(BudgetJsonInputParams.AMOUNT.getValue()).value(this.amount).ignoreIfNull().notLessThanMin(0);

        baseDataValidator.reset().parameter(BudgetJsonInputParams.NAME.getValue()).value(this.name).ignoreIfNull().notLessThanMin(0)
                .notExceedingLengthOf(45);
        baseDataValidator.reset().parameter("fromDate").value(this.fromDate).ignoreIfNull();
        baseDataValidator.reset().parameter("disabled").value(this.disabled).ignoreIfNull();

        baseDataValidator.reset().parameter("toDate").value(this.toDate).ignoreIfNull();
        baseDataValidator.reset().parameter("createdate").value(this.createdate).ignoreIfNull();
        
        baseDataValidator.reset().parameter("description").value(this.description).ignoreIfNull().notExceedingLengthOf(500);

        
      //  baseDataValidator.reset().parameter(BudgetJsonInputParams.DISABLED.getValue()).value(this.disabled).notBlank();
        baseDataValidator.reset().parameter(BudgetJsonInputParams.LIABILITY_ACCOUNT_ID.getValue()).value(this.liabilityAccountId).ignoreIfNull()
                .integerGreaterThanZero();
        
        baseDataValidator.reset().parameter(BudgetJsonInputParams.YEAR.getValue()).value(this.year).ignoreIfNull()
        .integerGreaterThanZero();
        baseDataValidator.reset().parameter(BudgetJsonInputParams.EXPENSE_ACCOUNT_ID.getValue()).value(this.expenseAccountId).ignoreIfNull()
        .integerGreaterThanZero();
        baseDataValidator.reset().parameter(BudgetJsonInputParams.ASSET_ACCOUNT_ID.getValue()).value(this.assetAccountId).ignoreIfNull()
        .integerGreaterThanZero();
        baseDataValidator.reset().anyOfNotNull(this.amount, this.fromDate, this.toDate, this.year, this.description, this.disabled,this.expenseAccountId,this.assetAccountId,this.liabilityAccountId);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }

    }

  }

