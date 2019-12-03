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
package org.apache.fineract.portfolio.paymenttype.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetailRepository;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentReadPlatformServiceImpl implements PaymentReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PaymentDetailRepository paymentRepository;
    private final static String nameDecoratedBaseOnHierarchy = "concat(substring('........................................', 1, ((LENGTH(hierarchy) - LENGTH(REPLACE(hierarchy, '.', '')) - 1) * 4)), name)";

    @Autowired
    public PaymentReadPlatformServiceImpl(final RoutingDataSource dataSource,PaymentDetailRepository paymentRepository ) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.paymentRepository=paymentRepository;
    }
    
     private static final class PaymentMapper implements RowMapper<PaymentDetailData> {

      //  private final JournalEntryAssociationParametersData associationParametersData;

//        public BudgetMapper() {
//           
//        }


        public String schema() {
            StringBuilder sb = new StringBuilder();
            sb.append(" p.id as id,p.payment_type_id as paymentType,p.account_number as accountNumber, p.check_number as checkNumber,p.receipt_number as receiptNumber, p.bank_number as bankNumber,p.routing_code as routingCode ");
//            if (this.associationParametersData.isRunningBalanceRequired()) {
//                sb.append(",gl_j.organization_running_balance as organizationRunningBalance ");
//            }
            sb.append(" from m_payment_detail p left join m_loan_transaction lt on lt.payment_detail_id = p.id ");
//            if (this.associationParametersData.isRunningBalanceRequired()) {
//                sb.append("left outer Join acc_gl_journal_entry gl_j on gl_j.account_id = gl.id");
//            }
            return sb.toString();
        }

        @Override
        public PaymentDetailData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long paymentType = rs.getLong("paymentType");
            final Long id = rs.getLong("id");
            final String accountNumber = rs.getString("accountNumber");
            final String checkNumber = rs.getString("checkNumber");
            final String bankNumber = rs.getString("bankNumber");
            final String receiptNumber = rs.getString("receiptNumber"); 
            final String routingCode = rs.getString("routingCode"); 
           
           
           
            return new PaymentDetailData(id, paymentType, accountNumber, checkNumber, routingCode, receiptNumber, bankNumber);
        
    }
    }

 
  
	@Override
	public List<PaymentDetailData> findByReceiptNumber(String receiptNumber) {
		 final PaymentMapper mapper = new PaymentMapper();
		
       final String sql = "Select " + mapper.schema() + " WHERE lt.is_reversed='0' and p.receipt_number = ?";
      // System.out.println(sql);
       return this.jdbcTemplate.query(sql, mapper, new Object[] { receiptNumber });

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