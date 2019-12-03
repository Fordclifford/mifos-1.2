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
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
    @JoinColumn(name = "liability_account_id")
    private GLAccount liabilityAccountId;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_account_id")
    private GLAccount expenseAccountId;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_account_id")
    private GLAccount assetAccountId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;


    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", nullable = false)
    private String description;
    
   
    @Column(name = "disabled", nullable = false)
    private Boolean disabled;
    
    @Column(name = "from_date")
    @Temporal(TemporalType.DATE)
    private Date fromDate;
    
    @Column(name = "to_date")
    @Temporal(TemporalType.DATE)
    private Date toDate;
    
    @Column(name = "create_date")
    @Temporal(TemporalType.DATE)
    private Date createDate;
    
    @Column(name = "year")
    @Temporal(TemporalType.DATE)
    private Long year;


    protected Budget() {
        //
    }
    
  
   
    
	public Budget(GLAccount liabilityAccountId, GLAccount expenseAccountId, GLAccount assetAccountId, BigDecimal amount,
			String name, String description, Boolean disabled, Date fromDate, Date toDate, Date createDate, Long year) {
		
		this.liabilityAccountId = liabilityAccountId;
		this.expenseAccountId = expenseAccountId;
		this.assetAccountId = assetAccountId;
		this.amount = amount;
		this.name = name;
		this.description = description;
		this.disabled = disabled;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.createDate = createDate;
		this.year = year;
	}




	public static Budget fromJson(final GLAccount liabilityAccountId,final GLAccount expenseAccountId,final GLAccount assetAccountId, JsonCommand command) {
      final BigDecimal amount = command.bigDecimalValueOfParameterNamed(BudgetJsonInputParams.AMOUNT.getValue());
      final Boolean disabled = command.booleanObjectValueOfParameterNamed(BudgetJsonInputParams.DISABLED.getValue());
       final String description = command.stringValueOfParameterNamed(BudgetJsonInputParams.DESCRIPTION.getValue());
       final Long year = command.longValueOfParameterNamed(BudgetJsonInputParams.YEAR.getValue());
       
      final String name = command.stringValueOfParameterNamed(BudgetJsonInputParams.NAME.getValue());
      final Date fromDate = command.DateValueOfParameterNamed(BudgetJsonInputParams.FROM_DATE.getValue());
      final Date toDate = command.DateValueOfParameterNamed(BudgetJsonInputParams.TO_DATE.getValue());
      final Date createdate = command.DateValueOfParameterNamed(BudgetJsonInputParams.CREATE_DATE.getValue());
      
      
        return new Budget(liabilityAccountId,expenseAccountId,assetAccountId,amount,name,description,disabled, fromDate, toDate,createdate,year);
    }

    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(15);
        handlePropertyUpdate(command, actualChanges, BudgetJsonInputParams.ASSET_ACCOUNT_ID.getValue(), 0L);
        handlePropertyUpdate(command, actualChanges, BudgetJsonInputParams.LIABILITY_ACCOUNT_ID.getValue(), 0L);
        handlePropertyUpdate(command, actualChanges, BudgetJsonInputParams.EXPENSE_ACCOUNT_ID.getValue(), 0L);
        handlePropertyUpdate(command, actualChanges, BudgetJsonInputParams.NAME.getValue(), this.name);
        handlePropertyUpdate(command, actualChanges, BudgetJsonInputParams.DESCRIPTION.getValue(), this.description);
        handlePropertyUpdate(command, actualChanges, BudgetJsonInputParams.FROM_DATE.getValue(), this.fromDate);
        handlePropertyUpdate(command, actualChanges, BudgetJsonInputParams.TO_DATE.getValue(), this.toDate);
        handlePropertyUpdate(command, actualChanges, BudgetJsonInputParams.CREATE_DATE.getValue(), this.createDate);
        handlePropertyUpdate(command, actualChanges, BudgetJsonInputParams.YEAR.getValue(), 0L);
        handlePropertyUpdate(command, actualChanges, BudgetJsonInputParams.AMOUNT.getValue(), this.amount);
         handlePropertyUpdate(command, actualChanges, BudgetJsonInputParams.DISABLED.getValue(), this.disabled);
        
        return actualChanges;
    }






	private void handlePropertyUpdate(final JsonCommand command, final Map<String, Object> actualChanges, final String paramName,
            final BigDecimal propertyToBeUpdated) {
        if (command.isChangeInBigDecimalParameterNamed(paramName, propertyToBeUpdated)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            // now update actual property
            if (paramName.equals(BudgetJsonInputParams.AMOUNT.getValue())) {
                this.amount = newValue;
            }
        }
        
    }
    
    private void handlePropertyUpdate(final JsonCommand command, final Map<String, Object> actualChanges, final String paramName,
            final Date propertyToBeUpdated) {
        if (command.isChangeInDateParameterNamed(paramName, propertyToBeUpdated)) {
            final Date newValue = command.DateValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            // now update actual property
            if (paramName.equals(BudgetJsonInputParams.FROM_DATE.getValue())) {
                this.fromDate = newValue;
            }
            if (paramName.equals(BudgetJsonInputParams.TO_DATE.getValue())) {
                this.toDate = newValue;
            }
        }
        
    }

    private void handlePropertyUpdate(final JsonCommand command, final Map<String, Object> actualChanges, final String paramName,
            final Long propertyToBeUpdated) {
        if (command.isChangeInLongParameterNamed(paramName, propertyToBeUpdated)) {
            final Long newValue = command.longValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            // now update actual property
            if (paramName.equals(BudgetJsonInputParams.YEAR.getValue())) {
            	this.year=newValue;
               
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
               this.disabled=newValue;
            }
        }
    }
    
    
    private void handlePropertyUpdate(final JsonCommand command, final Map<String, Object> actualChanges, final String paramName,
            final String propertyToBeUpdated) {
        if (command.isChangeInStringParameterNamed(paramName, propertyToBeUpdated)) {
            final String newValue = command.stringValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            // now update actual property
            if (paramName.equals(BudgetJsonInputParams.DESCRIPTION.getValue())) {
            	this.description=newValue;
                // do nothing as this is a nested property
            }else if (paramName.equals(BudgetJsonInputParams.NAME.getValue())) {
            	this.name=newValue;
                // do nothing as this is a nested property
            }
        }
    }


    public void updateAssetAccount(final GLAccount assetAccount) {
        this.assetAccountId = assetAccount;
      
    }
    
    public void updateExpenseAccount(final GLAccount expenseAccountId) {
        this.expenseAccountId = expenseAccountId;
      
    }
    
    public void updateLiabilityAccount(final GLAccount liabilityAccountId) {
        this.liabilityAccountId = liabilityAccountId;
      
    }




	public GLAccount getLiabilityAccountId() {
		return liabilityAccountId;
	}




	public void setLiabilityAccountId(GLAccount liabilityAccountId) {
		this.liabilityAccountId = liabilityAccountId;
	}




	public GLAccount getExpenseAccountId() {
		return expenseAccountId;
	}




	public void setExpenseAccountId(GLAccount expenseAccountId) {
		this.expenseAccountId = expenseAccountId;
	}




	public GLAccount getAssetAccountId() {
		return assetAccountId;
	}




	public void setAssetAccountId(GLAccount assetAccountId) {
		this.assetAccountId = assetAccountId;
	}




	public BigDecimal getAmount() {
		return amount;
	}




	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}




	public String getName() {
		return name;
	}




	public void setName(String name) {
		this.name = name;
	}




	public String getDescription() {
		return description;
	}




	public void setDescription(String description) {
		this.description = description;
	}




	public Boolean getDisabled() {
		return disabled;
	}




	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

    

  

    
}