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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
import org.apache.fineract.accounting.glaccount.exception.GLAccountDisableException;
import org.apache.fineract.accounting.glaccount.exception.GLAccountInvalidClassificationException;
import org.apache.fineract.accounting.glaccount.exception.GLAccountNotFoundException;
import org.apache.fineract.accounting.glaccount.service.GLAccountReadPlatformService;
import org.apache.fineract.accounting.glaccount.service.GLAccountReadPlatformServiceImpl;
import org.apache.fineract.accounting.journalentry.data.JournalEntryAssociationParametersData;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class BudgetReadPlatformServiceImpl implements BudgetReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final GLAccountRepository glAccountRepository;
    //private final static GLAccountRepository lglAccountRepository;
    private final static String nameDecoratedBaseOnHierarchy = "concat(substring('........................................', 1, ((LENGTH(hierarchy) - LENGTH(REPLACE(hierarchy, '.', '')) - 1) * 4)), name)";

    @Autowired
    public BudgetReadPlatformServiceImpl(final RoutingDataSource dataSource,GLAccountRepository glAccountRepository ) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.glAccountRepository=glAccountRepository;
    }
    
     private static final class BudgetMapper implements RowMapper<BudgetData> {
    	  private final GLAccountRepository glAccountRepository;

      //  private final JournalEntryAssociationParametersData associationParametersData;
    	  @Autowired
    	    public BudgetMapper(GLAccountRepository glAccountRepository ) {    	       
    	        this.glAccountRepository=glAccountRepository;
    	    }

//     		public BudgetMapper() {
//		// TODO Auto-generated constructor stub
//	}

			public String schema() {
            StringBuilder sb = new StringBuilder();
            sb.append(" b.id,b.disabled,b.create_date as createDate,b.year, b.expense_account_id as expenseAccountId,b.asset_account_id as assetAccountId,b.liability_account_id as liabilityAccountId, b.amount, b.description,b.name,(SELECT NAME FROM acc_gl_account g WHERE g.id=b.liability_account_id) AS liabilityAccountName,(SELECT NAME FROM acc_gl_account g WHERE g.id=b.expense_account_id) AS expenseAccountName,(SELECT NAME FROM acc_gl_account g WHERE g.id=b.asset_account_id) AS assetAccountName,b.from_date as fromDate,b.to_date as toDate ");
//            if (this.associationParametersData.isRunningBalanceRequired()) {
//                sb.append(",gl_j.organization_running_balance as organizationRunningBalance ");
//            }
            sb.append(" from gl_acc_budget b ");
//            if (this.associationParametersData.isRunningBalanceRequired()) {
//                sb.append("left outer Join acc_gl_journal_entry gl_j on gl_j.account_id = gl.id");
//            }
            return sb.toString();
        }

        @Override
        public BudgetData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

           final Long id = rs.getLong("id");
           final Long expenseAccountId = JdbcSupport.getLong(rs, "expenseAccountId");
           final Long year = JdbcSupport.getLong(rs, "year");
           final Long assetAccountId = JdbcSupport.getLong(rs, "assetAccountId");
           final Long liabilityAccountId = JdbcSupport.getLong(rs, "liabilityAccountId");
           final BigDecimal amount = rs.getBigDecimal("amount");
           final String name = rs.getString("name"); 
           final String description = rs.getString("description"); 
           final Boolean disabled = rs.getBoolean("disabled");     
                   
           final LocalDate createDate = JdbcSupport.getLocalDate(rs, "createDate");
           final LocalDate fromDate = JdbcSupport.getLocalDate(rs, "fromDate");
           final LocalDate toDate = JdbcSupport.getLocalDate(rs, "toDate");
                    
           final String assetAccountName = rs.getString("assetAccountName"); 
           final String expenseAccountName = rs.getString("expenseAccountName"); 
           final String liabilityAccountName = rs.getString("liabilityAccountName"); 
         
        //   final GLAccount gl= glAccountRepository.findOne(expenseAccountId);
          // final String assetAccountName = glAccountRepository.getOne(assetAccountId).getName();
          // System.out.println("assetAcc"+assetAccountName);
          
            return new BudgetData(expenseAccountName, liabilityAccountName, assetAccountName, id, amount, expenseAccountId, liabilityAccountId, assetAccountId, disabled, description, name,fromDate,toDate,year,createDate);
        
    }
    }

    @Override
    public List<BudgetData> retrieveAll(String searchParam) {
       


        final BudgetMapper rm = new BudgetMapper(glAccountRepository);
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
           
            if (StringUtils.isNotBlank(searchParam)) {
               
                sql += " ( b.name like '%"+searchParam+"%'or b.description like '%"+searchParam+"%') ";
                paramaterArray[arrayPos] = searchParam;
                arrayPos = arrayPos + 1;
                paramaterArray[arrayPos] = searchParam;    
                arrayPos = arrayPos + 1;
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
        System.out.println(sql);

        final Object[] finalObjectArray = Arrays.copyOf(paramaterArray, arrayPos);
        return this.jdbcTemplate.query(sql.toString(), rm, new Object[] {});
    }

    @Override
    public BudgetData retrieveBudgetById(final long budgetId) {
        try {

            final BudgetMapper rm = new BudgetMapper(glAccountRepository);
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
        	
        //	System.out.println("db acccount Id "+accountId);
        	
        	
            final GLAccount glAccount = this.glAccountRepository.findOne(accountId);
            if (glAccount == null ) { throw new GLAccountNotFoundException(accountId); }
            


            final BudgetMapper rm = new BudgetMapper(glAccountRepository);
            final StringBuilder sql = new StringBuilder();
            sql.append("select ").append(rm.schema());
           
            sql.append("where b.expense_account_id = ?");
           // System.out.println("query"+sql.toString());
            
            final BudgetData glAccountData = this.jdbcTemplate.queryForObject(sql.toString(), rm, new Object[] { accountId });

            return glAccountData;
        } catch (final EmptyResultDataAccessException e) {
            throw new BudgetNotFoundException(accountId);
        }
    }
    
    @Override
    public BudgetData retrieveByAsetAccountId(final long accountId,Long year) {
        try {
        	
        //	System.out.println("db acccount Id "+accountId);
        	
        	
            final GLAccount glAccount = this.glAccountRepository.findOne(accountId);
            if (glAccount == null ) { throw new GLAccountNotFoundException(accountId); }
            


            final BudgetMapper rm = new BudgetMapper(glAccountRepository);
            final StringBuilder sql = new StringBuilder();
            sql.append("select ").append(rm.schema());
           
            sql.append("where b.asset_account_id = ? and year = ? ");
           // System.out.println("query"+sql.toString());
            
            final BudgetData glAccountData = this.jdbcTemplate.queryForObject(sql.toString(), rm, new Object[] { accountId,year });

            return glAccountData;
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public BudgetData getByAsetAccountId(final long accountId,Long year,Long budgetId) {
        try {
        	
        //	System.out.println("db acccount Id "+accountId);
        	
//        	
//            final GLAccount glAccount = this.glAccountRepository.findOne(accountId);
//            if (glAccount == null ) { throw new GLAccountNotFoundException(accountId); }
//            
//            
//            String sql = "select count(*) from gl_acc_budget acc where acc.asset_account_id = "+accountId+ " and year="+y;
//    		int count = this.jdbcTemplate.queryForObject(sql, Integer.class);
//    		if(count>0){
//    			throw new GLAccountDisableException();
//    		}
//            


            final BudgetMapper rm = new BudgetMapper(glAccountRepository);
            final StringBuilder sql = new StringBuilder();
            sql.append("select ").append(rm.schema());
           
            sql.append("where b.asset_account_id = ? and year = ? and b.id!= ? ");
           
            final BudgetData glAccountData = this.jdbcTemplate.queryForObject(sql.toString(), rm, new Object[] { accountId,year,budgetId });

            return glAccountData;
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public BudgetData getExpenseAccountById(final long accountId) {
        try {
        	
        	final BudgetMapper rm = new BudgetMapper(glAccountRepository);
            final StringBuilder sql = new StringBuilder();
            sql.append("select ").append(rm.schema());
           
            sql.append("where b.expense_account_id = ?");
            
            final BudgetData glAccountData = this.jdbcTemplate.queryForObject(sql.toString(), rm, new Object[] { accountId });

            return glAccountData;
        } catch (final EmptyResultDataAccessException e) {
           return null;
        }
    }
    
    
    @Override
    public BudgetData getAccountById(final long accountId) {
        try {
        	
        	
//            final GLAccount glAccount = this.glAccountRepository.findOne(accountId);
//            if (glAccount == null ) { throw new GLAccountNotFoundException(accountId); }
//            


            final BudgetMapper rm = new BudgetMapper(glAccountRepository);
            final StringBuilder sql = new StringBuilder();
            sql.append("select ").append(rm.schema());
           
            sql.append("where b.asset_account_id = ? order by id desc limit 1");
           //System.out.println("query"+sql.toString());
            
            final BudgetData glAccountData = this.jdbcTemplate.queryForObject(sql.toString(), rm, new Object[] { accountId });

            return glAccountData;
        } catch (final EmptyResultDataAccessException e) {
           return null;
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