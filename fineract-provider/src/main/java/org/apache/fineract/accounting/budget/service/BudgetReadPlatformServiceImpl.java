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
package org.apache.fineract.accounting.budget.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.budget.api.BudgetJsonInputParams;
import org.apache.fineract.accounting.budget.data.BudgetData;
import org.apache.fineract.accounting.budget.domain.Budget;
import org.apache.fineract.accounting.budget.exception.BudgetNotFoundException;
import org.apache.fineract.accounting.common.AccountingEnumerations;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.data.GLAccountDataForLookup;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepository;
import org.apache.fineract.accounting.glaccount.domain.GLAccountType;
import org.apache.fineract.accounting.glaccount.domain.GLAccountUsage;
import org.apache.fineract.accounting.glaccount.exception.GLAccountInvalidClassificationException;
import org.apache.fineract.accounting.glaccount.exception.GLAccountNotFoundException;
import org.apache.fineract.accounting.journalentry.data.JournalEntryAssociationParametersData;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class BudgetReadPlatformServiceImpl implements BudgetReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final GLAccountRepository glAccountRepository;
    private final static String nameDecoratedBaseOnHierarchy = "concat(substring('........................................', 1, ((LENGTH(hierarchy) - LENGTH(REPLACE(hierarchy, '.', '')) - 1) * 4)), name)";

    @Autowired
    public BudgetReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.glAccountRepository=null;
    }
    
     private static final class BudgetMapper implements RowMapper<BudgetData> {

      //  private final JournalEntryAssociationParametersData associationParametersData;

//        public BudgetMapper() {
//           
//        }

        public String schema() {
            StringBuilder sb = new StringBuilder();
            sb.append(" b.id as id,b.disabled, b.account_id as accountId,gl.name as accountName, b.min_amount as minAmount, b.max_amount as maxAmount");
//            if (this.associationParametersData.isRunningBalanceRequired()) {
//                sb.append(",gl_j.organization_running_balance as organizationRunningBalance ");
//            }
            sb.append(" from gl_acc_budget b inner join acc_gl_account gl on gl.id=b.account_id ");
//            if (this.associationParametersData.isRunningBalanceRequired()) {
//                sb.append("left outer Join acc_gl_journal_entry gl_j on gl_j.account_id = gl.id");
//            }
            return sb.toString();
        }

        @Override
        public BudgetData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final BigDecimal maxValue = rs.getBigDecimal("maxAmount");
            final Long accountId = JdbcSupport.getLong(rs, "accountId");
            final BigDecimal minValue = rs.getBigDecimal("minAmount");
            final String accountName = rs.getString("accountName"); 
            final Boolean disabled = rs.getBoolean("disabled"); 
           
           
            return new BudgetData(id,minValue, accountId, maxValue,accountName,disabled);
        
    }
    }

    @Override
    public List<BudgetData> retrieveAll(final Integer accountId,String searchParam) {
       


        final BudgetMapper rm = new BudgetMapper();
        String sql = "select " + rm.schema();
        // append SQL statement for fetching account totals
//        if (associationParametersData!=null) {
//            if (associationParametersData.isRunningBalanceRequired()) {
//                sql = sql + " and gl_j.id in (select t1.id from (select t2.account_id, max(t2.id) as id from "
//                        + "(select id, max(entry_date) as entry_date, account_id from acc_gl_journal_entry where is_running_balance_calculated = 1 "
//                        + "group by account_id desc, id) t3 inner join acc_gl_journal_entry t2 on t2.account_id = t3.account_id and t2.entry_date = t3.entry_date "
//                        + "group by t2.account_id desc) t1)";
//            }
//        }
        final Object[] paramaterArray = new Object[3];
        int arrayPos = 0;
        boolean filtersPresent = false;
        if ( StringUtils.isNotBlank(searchParam)) {
            filtersPresent = true;
            sql += " where";
        }

        if (filtersPresent) {
            boolean firstWhereConditionAdded = false;
            if (accountId != null) {
                sql += " account_id like ?";
                paramaterArray[arrayPos] = accountId;
                arrayPos = arrayPos + 1;
                firstWhereConditionAdded = true;
            }
            if (StringUtils.isNotBlank(searchParam)) {
                if (firstWhereConditionAdded) {
                    sql += " and ";
                }
                sql += " ( disabled like %?% or min_value like %?% )";
                paramaterArray[arrayPos] = searchParam;
                arrayPos = arrayPos + 1;
                paramaterArray[arrayPos] = searchParam;
                arrayPos = arrayPos + 1;
                firstWhereConditionAdded = true;
            }
//            if (usage != null) {
//                if (firstWhereConditionAdded) {
//                    sql += " and ";
//                }
//                if (GLAccountUsage.HEADER.getValue().equals(usage)) {
//                    sql += " account_usage = 2 ";
//                } else if (GLAccountUsage.DETAIL.getValue().equals(usage)) {
//                    sql += " account_usage = 1 ";
//                }
//                firstWhereConditionAdded = true;
//            }
//            if (manualTransactionsAllowed != null) {
//                if (firstWhereConditionAdded) {
//                    sql += " and ";
//                }
//
//                if (manualTransactionsAllowed) {
//                    sql += " manual_journal_entries_allowed = 1";
//                } else {
//                    sql += " manual_journal_entries_allowed = 0";
//                }
//                firstWhereConditionAdded = true;
//            }
//            if (disabled != null) {
//                if (firstWhereConditionAdded) {
//                    sql += " and ";
//                }
//
//                if (disabled) {
//                    sql += " disabled = 1";
//                } else {
//                    sql += " disabled = 0";
//                }
//                firstWhereConditionAdded = true;
//            }
        }

        sql+=" ORDER BY b.id ASC";

        final Object[] finalObjectArray = Arrays.copyOf(paramaterArray, arrayPos);
        return this.jdbcTemplate.query(sql, rm, finalObjectArray);
    }

    @Override
    public BudgetData retrieveBudgetById(final long budgetId) {
        try {

            final BudgetMapper rm = new BudgetMapper();
            final StringBuilder sql = new StringBuilder();
            sql.append("select ").append(rm.schema());
           
            sql.append("where b.id = ?");
            
            final BudgetData glAccountData = this.jdbcTemplate.queryForObject(sql.toString(), rm, new Object[] { budgetId });

            return glAccountData;
        } catch (final EmptyResultDataAccessException e) {
            throw new BudgetNotFoundException(budgetId);
        }
    }
    


    
    @Override
    public BudgetData retrieveAccountById(final long accountId) {
        try {
        	
        	
            final GLAccount glAccount = this.glAccountRepository.findOne(accountId);
            if (glAccount == null) { throw new GLAccountNotFoundException(accountId); }
            


            final BudgetMapper rm = new BudgetMapper();
            final StringBuilder sql = new StringBuilder();
            sql.append("select ").append(rm.schema());
           
            sql.append("where b.account_id = ?");
            
            final BudgetData glAccountData = this.jdbcTemplate.queryForObject(sql.toString(), rm, new Object[] { accountId });

            return glAccountData;
        } catch (final EmptyResultDataAccessException e) {
            throw new BudgetNotFoundException(accountId);
        }
    }

	@Override
	public BudgetData retrieveNewBudgetDetails() {
		 return BudgetData.sensibleDefaultsForNewBudgetCreation();
	}

//    @Override
//    public List<GLAccountData> retrieveAllEnabledDetailGLAccounts(final GLAccountType accountType) {
//        return retrieveAllGLAccounts(accountType.getValue(), null, GLAccountUsage.DETAIL.getValue(), null, false,
//                new JournalEntryAssociationParametersData());
//    }

//    @Override
//    public List<GLAccountData> retrieveAllEnabledDetailGLAccounts() {
//        return retrieveAllGLAccounts(null, null, GLAccountUsage.DETAIL.getValue(), null, false, new JournalEntryAssociationParametersData());
//    }

//    private static boolean checkValidGLAccountType(final int type) {
//        for (final GLAccountType accountType : GLAccountType.values()) {
//            if (accountType.getValue().equals(type)) { return true; }
//        }
//        return false;
//    }

//    private static boolean checkValidGLAccountUsage(final int type) {
//        for (final GLAccountUsage accountUsage : GLAccountUsage.values()) {
//            if (accountUsage.getValue().equals(type)) { return true; }
//        }
//        return false;
//    }

//    @Override
//    public GLAccountData retrieveNewBudgetDetails(final Integer type) {
//        return GLAccountData.sensibleDefaultsForNewGLAccountCreation(type);
//    }

//    @Override
//    public List<GLAccountData> retrieveAllEnabledHeaderGLAccounts(final GLAccountType accountType) {
//        return retrieveAllGLAccounts(accountType.getValue(), null, GLAccountUsage.HEADER.getValue(), null, false,
//                new JournalEntryAssociationParametersData());
//    }

//    @Override
//    public List<GLAccountDataForLookup> retrieveAccountsByTagId(final Long ruleId, final Integer transactionType) {
//        final GLAccountDataLookUpMapper mapper = new GLAccountDataLookUpMapper();
//        final String sql = "Select " + mapper.schema() + " where rule.id=? and tags.acc_type_enum=?";
//        return this.jdbcTemplate.query(sql, mapper, new Object[] { ruleId, transactionType });
//    }

//    private static final class GLAccountDataLookUpMapper implements RowMapper<GLAccountDataForLookup> {
//
//        public String schema() {
//            return " gl.id as id, gl.name as name, gl.gl_code as glCode from acc_accounting_rule rule join acc_rule_tags tags on tags.acc_rule_id = rule.id join acc_gl_account gl on gl.tag_id=tags.tag_id";
//        }
//
//        @Override
//        public GLAccountDataForLookup mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
//            final Long id = JdbcSupport.getLong(rs, "id");
//            final String name = rs.getString("name");
//            final String glCode = rs.getString("glCode");
//            return new GLAccountDataForLookup(id, name, glCode);
//        }
//
//    }
}