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
package org.apache.fineract.accounting.budget.exception;

import org.apache.fineract.accounting.glaccount.domain.GLAccountType;
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * A {@link RuntimeException} thrown when trying to map invalid parents to a GL
 * account
 */
public class BudgetInvalidAccountTypeException extends AbstractPlatformDomainRuleException {

    public BudgetInvalidAccountTypeException(final long glAccountId,String type,String required) {
        super("error.msg.glaccount.type.invalid", "The account with id " + glAccountId
                + " is a "+ type +" account and cannot be used as a "+ required, glAccountId);
    }
}