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
import java.util.Collection;
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

    private final Long id;
    private final BigDecimal minValue;
    private final Long glId;
    private final BigDecimal maxValue;
   // private final GLAccountData account;
    private final String accountName;
    private final Boolean disabled;
   
    private transient Integer rowIndex;


	public static BudgetData importInstance(BigDecimal minValue,Long glId, BigDecimal maxValue, 
            Integer rowIndex,Boolean disabled){
        return new BudgetData(minValue, glId, maxValue, rowIndex,disabled);
    }


private BudgetData(final BigDecimal minValue,final Long glId, final BigDecimal maxValue,final Integer rowIndex,final Boolean disabled) {
	this.minValue = minValue;
	this.glId = glId;
	this.maxValue = maxValue;
	this.accountName = null;
	this.rowIndex=rowIndex;
	this.disabled=disabled;
	this.id=null;
}


private BudgetData(Long id,BigDecimal minValue, Long glId, BigDecimal maxValue , Boolean disabled) {
	this.minValue=minValue;
	this.glId=glId;
	this.maxValue=maxValue;
	this.disabled=disabled;
	//this.account=null;
	this.accountName=null;
	this.id=id;
	
}

public BudgetData(Long id,BigDecimal minValue, Long glId, BigDecimal maxValue,String accountName,Boolean disabled) {
	this.minValue=minValue;
	this.glId=glId;
	this.maxValue=maxValue;	
	this.disabled=disabled;
	this.accountName=accountName;
	this.id=id;
	
}





public Integer getRowIndex() {
	return rowIndex;
}


public void setRowIndex(Integer rowIndex) {
	this.rowIndex = rowIndex;
}


public BigDecimal getMinValue() {
	return minValue;
}


public Long getGlId() {
	return glId;
}

public Long getId() {
	return id;
}



public BigDecimal getMaxValue() {
	return maxValue;
}




public String getAccountName() {
	return accountName;
}



public static BudgetData sensibleDefaultsForNewBudgetCreation() {
    final Long id = null;
    final Long acccountId = null;
    final BigDecimal minValue = null;
    final BigDecimal maxValue = null;
    final String accountName = null;
    final Boolean disabled = null;
     

    return new BudgetData(id, minValue, acccountId, maxValue, accountName,disabled);
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

