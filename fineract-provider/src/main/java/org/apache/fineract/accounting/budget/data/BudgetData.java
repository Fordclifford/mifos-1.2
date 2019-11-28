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
package org.apache.fineract.accounting.budget.data;

import java.math.BigDecimal;
import org.joda.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Predicate.BooleanOperator;

import org.apache.fineract.accounting.common.AccountingEnumerations;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.domain.GLAccountType;
import org.apache.fineract.accounting.glaccount.domain.GLAccountUsage;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Immutable object representing a General Ledger Account
 * 
 * Note: no getter/setters required as google-gson will produce json from fields
 * of object.
 */
@SuppressWarnings("unused")
public class BudgetData {

 
   // private final GLAccountData account;
    private final String expenseAccountName;
    private final String liabilityAccountName;
    private final String assetAccountName;
   private transient Integer rowIndex;
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
    private final LocalDate createDate;
    private final Long year;



	public static BudgetData importInstance(BigDecimal amount,Long expenseAccountId ,Long liabilityAccountId ,Long assetAccountId, 
            Integer rowIndex,Boolean disabled, String name, String description,LocalDate fromDate,LocalDate toDate,Long year,LocalDate createDate){
        return new BudgetData(amount, expenseAccountId, liabilityAccountId, assetAccountId,rowIndex,disabled,name,description,fromDate,toDate,year,createDate);
    }


//
//
//private BudgetData(Long id,BigDecimal minValue, Long glId, BigDecimal maxValue , Boolean disabled) {
//	this.minValue=minValue;
//	this.glId=glId;
//	this.maxValue=maxValue;
//	this.disabled=disabled;
//	//this.account=null;
//	this.accountName=null;
//	this.id=id;
//	
//}







public BudgetData(String expenseAccountName, String liabilityAccountName, String assetAccountName, Long id,
		BigDecimal amount, Long expenseAccountId, Long liabilityAccountId, Long assetAccountId, Boolean disabled,
		String description, String name,LocalDate fromDate,LocalDate toDate,Long year,LocalDate createDate) {
	this.expenseAccountName = expenseAccountName;
	this.liabilityAccountName = liabilityAccountName;
	this.assetAccountName = assetAccountName;
	this.id = id;
	this.amount = amount;
	this.expenseAccountId = expenseAccountId;
	this.liabilityAccountId = liabilityAccountId;
	this.assetAccountId = assetAccountId;
	this.disabled = disabled;
	this.description = description;
	this.name = name;
	this.fromDate=fromDate;
	this.toDate=toDate;
	this.year=year;
	this.createDate=createDate;
}


private BudgetData(final BigDecimal amount,final Long expenseAccountId,final Long liabilityAccountId,final Long assetAccountId,
		final Integer rowIndex, final Boolean disabled, final String name, final String description,LocalDate fromDate,LocalDate toDate,Long year,LocalDate createDate) {
	this.amount = amount;
	this.expenseAccountId = expenseAccountId;
	this.liabilityAccountId = liabilityAccountId;
	this.assetAccountId = assetAccountId;
	this.disabled = disabled;
	this.description = description;
	this.name = name;
	this.rowIndex=rowIndex;
	this.expenseAccountName = null;
	this.liabilityAccountName = null;
	this.assetAccountName = null;
	this.id = null;
	this.fromDate=fromDate;
	this.toDate=toDate;
	this.year=year;
	this.createDate=createDate;
	
	// TODO Auto-generated constructor stub
}

private BudgetData(String liabilityAccountName, String assetAccountName, Long id,
		BigDecimal amount, Long expenseAccountId, Long liabilityAccountId, Long assetAccountId, Boolean disabled,
		String description, String name,String expenseAccountName,LocalDate fromDate,LocalDate toDate,Long year,LocalDate createDate) {
	this.expenseAccountName = expenseAccountName;
	this.liabilityAccountName = liabilityAccountName;
	this.assetAccountName = assetAccountName;
	this.id = id;
	this.amount = amount;
	this.expenseAccountId = expenseAccountId;
	this.liabilityAccountId = liabilityAccountId;
	this.assetAccountId = assetAccountId;
	this.disabled = disabled;
	this.description = description;
	this.name = name;
	this.fromDate=fromDate;
	this.toDate=toDate;
	this.year=year;
	this.createDate=createDate;
}







public static BudgetData sensibleDefaultsForNewBudgetCreation() {
	final String  expenseAccountName = null;
	final String liabilityAccountName = null;
	final String assetAccountName = null;
	final Long id = null;
	final BigDecimal amount = null;
	final  Long expenseAccountId = null;
	final Long liabilityAccountId = null;
	final Long assetAccountId = null;
	final Boolean  disabled = null;
	final String description = null;
	final String name = null; 
	 final LocalDate fromDate=null;
	final LocalDate toDate=null;
	final Long year=null;
	final LocalDate createDate=null;

    return new BudgetData(liabilityAccountName,assetAccountName,id,amount,expenseAccountId,liabilityAccountId,assetAccountId,disabled,description,name,expenseAccountName,fromDate,toDate,year,createDate);
}


public String getExpenseAccountName() {
	return expenseAccountName;
}


public String getLiabilityAccountName() {
	return liabilityAccountName;
}


public String getAssetAccountName() {
	return assetAccountName;
}


public Integer getRowIndex() {
	return rowIndex;
}


public Long getId() {
	return id;
}


public BigDecimal getAmount() {
	return amount;
}


public Long getExpenseAccountId() {
	return expenseAccountId;
}


public Long getLiabilityAccountId() {
	return liabilityAccountId;
}


public Long getAssetAccountId() {
	return assetAccountId;
}


public Boolean getDisabled() {
	return disabled;
}


public String getDescription() {
	return description;
}


public String getName() {
	return name;
}


	
   
    
  
//	private BudgetData(BigDecimal minValue, Long glId, BigDecimal maxValue) {
//	
//		this.minValue = minValue;
//		this.glId = null;
//		this.maxValue = maxValue;
//		this.accountName = null;
//		}
//
// 
//
//    public BudgetData(BigDecimal minValue, Long glId, BigDecimal maxValue, Integer rowIndex) {
//		
//    	this.minValue = minValue;
//		this.glId = null;
//		this.maxValue = maxValue;
//		this.accountName = null;
//	}



//	public static GLAccountData sensibleDefaultsForNewGLAccountCreation(final Integer glAccType) {
//        final Long id = null;
//        final String name = null;
//        final Long parentId = null;
//        final String glCode = null;
//        final boolean disabled = false;
//        final boolean manualEntriesAllowed = true;
//        final EnumOptionData type;
//        if (glAccType != null && glAccType >= GLAccountType.getMinValue() && glAccType <= GLAccountType.getMaxValue()) {
//            type = AccountingEnumerations.gLAccountType(glAccType);
//        } else {
//            type = AccountingEnumerations.gLAccountType(GLAccountType.ASSET);
//        }
//        final EnumOptionData usage = AccountingEnumerations.gLAccountUsage(GLAccountUsage.DETAIL);
//        final String description = null;
//        final String nameDecorated = null;
//        final CodeValueData tagId = null;
//        final Long organizationRunningBalance = null;
//
//        return new GLAccountData(id, name, parentId, glCode, disabled, manualEntriesAllowed, type, usage, description, nameDecorated,
//                tagId, organizationRunningBalance);
//    }




}

