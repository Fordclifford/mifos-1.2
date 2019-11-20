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
package org.apache.fineract.accounting.budget.domain;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.budget.api.BudgetJsonInputParams;
import org.apache.fineract.accounting.glaccount.api.GLAccountJsonInputParams;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import software.amazon.ion.Decimal;

@Entity
@Table(name = "gl_acc_budget")
public class Budget extends AbstractPersistableCustom<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private GLAccount account;

    @Column(name = "min_amount", nullable = true)
    private BigDecimal minAmount;


    @Column(name = "max_amount", nullable = false)
    private BigDecimal maxAmount;
    
    @Column(name = "disabled", nullable = false)
    private Boolean disabled;


    protected Budget() {
        //
    }

 

    public Budget(final GLAccount account, final BigDecimal minAmount, final BigDecimal maxAmount,final Boolean disabled) {
		this.account = account;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.disabled = disabled;
	}



	public static Budget fromJson(final GLAccount account, final JsonCommand command) {
      final BigDecimal minAmount = command.bigDecimalValueOfParameterNamed(BudgetJsonInputParams.MIN_AMOUNT.getValue());
      final Boolean disabled = command.booleanObjectValueOfParameterNamed(BudgetJsonInputParams.DISABLED.getValue());
      final BigDecimal maxAmount = command.bigDecimalValueOfParameterNamed(BudgetJsonInputParams.MAX_AMOUNT.getValue());
        return new Budget(account, minAmount, maxAmount, disabled);
    }

    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(15);
        handlePropertyUpdate(command, actualChanges, BudgetJsonInputParams.ACCOUNT_ID.getValue(), 0L);
        handlePropertyUpdate(command, actualChanges, BudgetJsonInputParams.MAX_AMOUNT.getValue(), this.maxAmount);
        handlePropertyUpdate(command, actualChanges, BudgetJsonInputParams.MIN_AMOUNT.getValue(), this.minAmount);
        handlePropertyUpdate(command, actualChanges, BudgetJsonInputParams.DISABLED.getValue(), this.disabled);
        
        return actualChanges;
    }





    private void handlePropertyUpdate(final JsonCommand command, final Map<String, Object> actualChanges, final String paramName,
            final BigDecimal propertyToBeUpdated) {
        if (command.isChangeInBigDecimalParameterNamed(paramName, propertyToBeUpdated)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            // now update actual property
            if (paramName.equals(BudgetJsonInputParams.MIN_AMOUNT.getValue())) {
                this.minAmount = newValue;
            } else if (paramName.equals(BudgetJsonInputParams.MAX_AMOUNT.getValue())) {
                this.maxAmount = newValue;
            } 
        }
        
    }

    private void handlePropertyUpdate(final JsonCommand command, final Map<String, Object> actualChanges, final String paramName,
            final Long propertyToBeUpdated) {
        if (command.isChangeInLongParameterNamed(paramName, propertyToBeUpdated)) {
            final Long newValue = command.longValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            // now update actual property
            if (paramName.equals(BudgetJsonInputParams.ACCOUNT_ID.getValue())) {
                // do nothing as this is a nested property
            }
        }
    }
    
    private void handlePropertyUpdate(final JsonCommand command, final Map<String, Object> actualChanges, final String paramName,
            final Boolean propertyToBeUpdated) {
        if (command.isChangeInBooleanParameterNamed(paramName, propertyToBeUpdated)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            // now update actual property
            if (paramName.equals(BudgetJsonInputParams.DISABLED.getValue())) {
                // do nothing as this is a nested property
            }
        }
    }


    public void updateParentAccount(final GLAccount parentAccount) {
        this.account = parentAccount;
      
    }


	public BigDecimal getMinAmount() {
		return minAmount;
	}



	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	
	public Boolean gedDisabled() {
		return disabled;
	}

  

    
}